package com.smartcity.user.user;

import com.smartcity.models.User;
import com.smartcity.models.UserRequest;
import com.smartcity.user.user.exceptions.UserException;
import com.smartcity.user.village.VillageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.module.ResolutionException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final VillageService villageService;

    public Mono<String> register(UserRequest userRequest) {
        return userRepository.findByEmail(userRequest.getEmail()).flatMap(user -> Mono.error(new UserException("Email already in use"))).switchIfEmpty(Mono.defer(() -> {
            assert userRequest.getId() != null;
            assert userRequest.getVillageId() != null;
            UserEntity user = UserEntity.builder().id(userRequest.getId().toString()).name(userRequest.getName()).email(userRequest.getEmail()).villageId(String.valueOf(userRequest.getVillageId())).build();
            return r2dbcEntityTemplate.insert(UserEntity.class).using(user);
        })).cast(UserEntity.class).map(UserEntity::getId);
    }

    public Flux<User> getAllUsers() {
        return userRepository.findAll().flatMap(userEntity -> villageService.getVillageById(userEntity.getVillageId()).map(village -> UserMapper.INSTANCE.toModel(userEntity, village)));
    }

    public Mono<User> getUserById(String id) {
        return userRepository.findById(id).switchIfEmpty(Mono.error(new ResolutionException("User not found"))).flatMap(userEntity -> villageService.getVillageById(userEntity.getVillageId()).map(village -> UserMapper.INSTANCE.toModel(userEntity, village)));
    }

    public Mono<Boolean> existById() {
        return ReactiveSecurityContextHolder.getContext().map(securityContext -> securityContext.getAuthentication().getName()).flatMap(userRepository::existsById);
    }

    public Mono<User> updateUser(String id, UserRequest user) {
        return userRepository.findById(id).switchIfEmpty(Mono.error(new ResolutionException("User not found"))).flatMap(existingUser -> {
            existingUser.setName(user.getName());
            existingUser.setEmail(user.getEmail());
            return userRepository.save(existingUser);
        }).flatMap(userEntity -> villageService.getVillageById(userEntity.getVillageId()).map(village -> UserMapper.INSTANCE.toModel(userEntity, village)));
    }

    public Mono<Void> deleteUser(String id) {
        return userRepository.findById(id).switchIfEmpty(Mono.error(new ResolutionException("User not found"))).flatMap(user -> userRepository.deleteById(id));
    }


}
