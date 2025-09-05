package com.smartcity.user.auth;

import com.smartcity.models.UserRequest;
import com.smartcity.models.WorkerRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public Mono<String> login(@RequestParam String email,@RequestParam String password) {
        return authService.login(email, password);
    }
    @PostMapping("/register")
    public Mono<String> register(@Valid @RequestBody UserRequest  userRequest){
        return authService.register(userRequest);
    }
    @PostMapping("/register/worker")
    public Mono<String> registerWorker(@Valid @RequestBody WorkerRequest workerRequest){
        return authService.registerWorker(workerRequest);
    }
}
