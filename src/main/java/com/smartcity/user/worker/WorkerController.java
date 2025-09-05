package com.smartcity.user.worker;

import com.smartcity.models.Worker;
import com.smartcity.models.WorkerRequest;
import com.smartcity.user.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/workers")
@RequiredArgsConstructor
public class WorkerController {
    private final WorkerService workerService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('DISPATCHER')")
    public Flux<Worker> getAllWorkers() {
        return workerService.getAllWorkers();
    }

    @GetMapping("/{id}")
    public Mono<Worker> getWorkerById(@PathVariable String id) {
        return workerService.getWorkerById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Worker not found with id: " + id)));
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<Void>> updateWorker(@PathVariable String id, @RequestBody WorkerRequest workerRequest) {
        return workerService.updateWorker(id, workerRequest)
                .then(Mono.just(ResponseEntity.status(202).build()));
    }
    @DeleteMapping("/{id}")
    public Mono<Void> deleteWorker(@PathVariable String id) {
        return workerService.deleteWorker(id);
    }

}
