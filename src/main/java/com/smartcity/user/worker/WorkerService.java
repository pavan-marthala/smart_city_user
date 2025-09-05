package com.smartcity.user.worker;

import com.smartcity.models.Worker;
import com.smartcity.models.WorkerRequest;
import com.smartcity.user.shared.uils.UpdateHelper;
import com.smartcity.user.user.UserEntity;
import com.smartcity.user.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkerService {
    private final WorkerRepository workerRepository;
    private final UserRepository userRepository;

    private static void checkRole(UserEntity userEntity,String message) {
        Optional.of(userEntity.getRole()).filter(role -> !"WORKER".equals(role)).ifPresent(role -> {
            throw new RuntimeException(message);
        });
    }

    public Flux<Worker> getAllWorkers() {
        log.info("Fetching all workers");
        return workerRepository.findAllWorkers();
    }

    public Mono<Worker> getWorkerById(String id) {
        log.info("Fetching worker with id: {}", id);
        return workerRepository.findByWorkerId(id);
    }

    public Mono<Void> updateWorker(String id, WorkerRequest workerRequest) {
        log.info("Updating worker with id: {}", id);
        return userRepository.findById(id).switchIfEmpty(Mono.error(new RuntimeException("User not found with id: " + id))).flatMap(userEntity -> updateUserEntity(id, workerRequest, userEntity)).then();
    }

    private Mono<UserEntity> updateUserEntity(String id, WorkerRequest workerRequest, UserEntity userEntity) {
        checkRole(userEntity,"User with id: " + id + " is not a worker");
        UpdateHelper.updateIfNotNull(userEntity::setName, workerRequest.getName());
        UpdateHelper.updateIfNotNull(userEntity::setEmail, workerRequest.getEmail());
        return workerRepository.findById(id).switchIfEmpty(Mono.error(new RuntimeException("Worker not found with id: " + id))).flatMap(workerEntity -> updateWorker(workerRequest, workerEntity)).then(userRepository.save(userEntity));
    }

    private Mono<WorkerEntity> updateWorker(WorkerRequest workerRequest, WorkerEntity workerEntity) {
        UpdateHelper.updateIfNotNull(workerEntity::setSkills, workerRequest.getSkills());
        UpdateHelper.updateIfNotNull(workerEntity::setAvailability, workerRequest.getAvailability());
        return workerRepository.save(workerEntity);
    }

    public Mono<Void> deleteWorker(String id) {
        log.info("Deleting worker with id: {}", id);
        return userRepository.findById(id).switchIfEmpty(Mono.error(new RuntimeException("User not found with id: " + id))).flatMap(userEntity -> {
            checkRole(userEntity,"User with id: " + id + " is not a worker");
            return userRepository.deleteById(id);
        });
    }
}
