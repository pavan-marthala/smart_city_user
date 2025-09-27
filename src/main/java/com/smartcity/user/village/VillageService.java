package com.smartcity.user.village;

import com.smartcity.models.Village;
import com.smartcity.user.shared.jwt.JwtToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@Slf4j
public class VillageService {

    private final WebClient webClient;

    public VillageService(WebClient webClient, WebClient.Builder webClientBuilder, Environment environment) {
        this.webClient = webClientBuilder.baseUrl(Objects.requireNonNull(environment.getProperty("smart_city.services.location-service.url"))).build();
    }

    public Mono<Village> getVillageById(String id) {
        return getAuth().flatMap(token ->
                webClient.get().uri("/villages/{id}", id)
                        .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                        .retrieve()
                        .bodyToMono(Village.class));
    }
    private  Mono<String> getAuth() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> Mono.just(authentication)
                        .cast(JwtToken.class)
                        .map(JwtToken::getToken)
                        .switchIfEmpty(Mono.error(new RuntimeException("Invalid token."))));
    }
}
