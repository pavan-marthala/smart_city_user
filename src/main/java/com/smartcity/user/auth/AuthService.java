package com.smartcity.user.auth;

import com.smartcity.user.auth.exception.AuthException;
import com.smartcity.models.UserRequest;
import com.smartcity.models.WorkerRequest;
import com.smartcity.user.user.UserEntity;
import com.smartcity.user.user.UserRepository;
import com.smartcity.user.worker.WorkerEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token-expiration-seconds}")
    private long tokenExpiration;

    public Mono<String> login(String email, String password) {
        return userRepository.findByEmail(email).switchIfEmpty(Mono.error(new AuthException("User not found"))).flatMap(user -> {
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return Mono.error(new AuthException("Invalid credentials"));
            }
            return Mono.just(user);
        }).map(user -> generateToken(Map.of(), user));
    }

    public Mono<String> register(UserRequest userRequest) {
        return userRepository.findByEmail(userRequest.getEmail()).flatMap(user -> Mono.error(new AuthException("Email already in use"))).switchIfEmpty(Mono.defer(() -> {
            UserEntity user = UserEntity.builder().id(UUID.randomUUID().toString()).name(userRequest.getName()).email(userRequest.getEmail()).password(passwordEncoder.encode(userRequest.getPassword())).role("USER").isActive(true).build();
            return r2dbcEntityTemplate.insert(UserEntity.class).using(user);
        })).cast(UserEntity.class).map(user -> generateToken(Map.of(), user));
    }

    public Mono<String> registerWorker(WorkerRequest workerRequest) {
        return userRepository
                .findByEmail(workerRequest.getEmail())
                .flatMap(user -> Mono.error(new AuthException("Email already in use")))
                .switchIfEmpty(Mono.defer(() -> {
                    UserEntity user = UserEntity.builder().id(UUID.randomUUID().toString()).name(workerRequest.getName()).email(workerRequest.getEmail()).password(passwordEncoder.encode(workerRequest.getPassword())).role("WORKER").isActive(true).build();
                    return r2dbcEntityTemplate.insert(UserEntity.class).using(user);
                }))
                .cast(UserEntity.class)
                .flatMap(userEntity -> {
                    WorkerEntity worker = WorkerEntity.builder().userId(userEntity.getId()).skills(workerRequest.getSkills()).availability(Boolean.TRUE.equals(workerRequest.getAvailability())).build();
                   return r2dbcEntityTemplate.insert(WorkerEntity.class).using(worker)
                            .thenReturn(userEntity);
                })
                .map(user -> generateToken(Map.of("email",user.getEmail()), user));
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        long currentTimeMillis = System.currentTimeMillis();
        return Jwts.builder().claims(extraClaims).subject(userDetails.getUsername()).claim("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray()).issuedAt(new Date(currentTimeMillis)).expiration(new Date(currentTimeMillis + tokenExpiration * 1000)).signWith(getSigningKey(), Jwts.SIG.HS256).compact();
    }

    private SecretKey getSigningKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }
}
