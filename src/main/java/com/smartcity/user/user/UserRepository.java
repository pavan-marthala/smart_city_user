package com.smartcity.user.user;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface UserRepository extends ReactiveCrudRepository<UserEntity, String> {
    Mono<UserEntity> findByEmail(String email);
}
