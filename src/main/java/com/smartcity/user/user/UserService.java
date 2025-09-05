package com.smartcity.user.user;

import com.smartcity.models.User;
import com.smartcity.models.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.module.ResolutionException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Flux<User> getAllUsers() {
        return userRepository.findByRole("USER").map(UserMapper.INSTANCE::toModel);
    }

    public Mono<User> getUserById(String id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResolutionException("User not found")))
                .map(UserMapper.INSTANCE::toModel);
    }

    public Mono<User> updateUser(String id, UserRequest user) {
        return userRepository.findById(id).switchIfEmpty(Mono.error(new ResolutionException("User not found"))).flatMap(existingUser -> {
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            return userRepository.save(existingUser);
        }).map(UserMapper.INSTANCE::toModel);
    }

    public Mono<Void> deleteUser(String id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResolutionException("User not found")))
                .flatMap(user -> userRepository.deleteById(id));
    }


}
