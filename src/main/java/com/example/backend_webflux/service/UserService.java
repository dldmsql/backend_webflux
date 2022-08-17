package com.example.backend_webflux.service;

import com.example.backend_webflux.domain.User;
import com.example.backend_webflux.dto.AuthDto;
import com.example.backend_webflux.exception.ApiException;
import com.example.backend_webflux.exception.ApiExceptionEnum;
import com.example.backend_webflux.repository.UserRepository;
import com.example.backend_webflux.sercurity.JwtProvider;
import com.example.backend_webflux.sercurity.PwEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class UserService {

  private final UserRepository userRepository;
  private final JwtProvider jwtProvider;
  private final PwEncoder passwordEncoder;

  public Mono<User> create(User user) {
    return userRepository.findByName(user.getName())
        .defaultIfEmpty(user)
        .flatMap(result -> {
          if (result.getId() == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(result);
          } else {
            return Mono.error(new ApiException(ApiExceptionEnum.DUPLICATION_VALUE_EXCEPTION));
          }
        });
  }

  public Mono<User> getUserById(String id) {
    return userRepository.findById(id)
        .switchIfEmpty(Mono.error(new ApiException(ApiExceptionEnum.NOT_FOUND_EXCEPTION)));
  }

  public Flux<User> getAllUser() {
    return userRepository.findAll()
        .switchIfEmpty(Mono.error(new ApiException(ApiExceptionEnum.NOT_FOUND_EXCEPTION)));
  }

  public Mono<User> modify(String id, User user) {
    return userRepository.findById(id)
        .switchIfEmpty(Mono.error(new ApiException(ApiExceptionEnum.NOT_FOUND_EXCEPTION)))
        .flatMap(result -> userRepository.findByName(user.getName())
            .switchIfEmpty(Mono.defer(() -> {
              result.setName(user.getName());
              return userRepository.save(result);
            }))
            .flatMap(duplicated -> Mono.error(new ApiException(ApiExceptionEnum.DUPLICATION_VALUE_EXCEPTION)))
        );

  }

  public Mono<Void> delete(String id) {
    return userRepository.deleteById(id);
  }

  public Mono<String> getUserByName(AuthDto.Request dto) {
    return userRepository.findByName(dto.getName())
        .filter(user -> passwordEncoder.encode(dto.getPassword()).equals(user.getPassword()))
        .map(jwtProvider::generateToken)
        .switchIfEmpty(Mono.error(new ApiException(ApiExceptionEnum.UNAUTHORIZED_EXCEPTION)));
  }
}