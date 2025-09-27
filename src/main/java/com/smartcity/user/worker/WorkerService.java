package com.smartcity.user.worker;

import com.smartcity.models.Worker;
import com.smartcity.models.WorkerRequest;
import com.smartcity.user.shared.uils.UpdateHelper;
import com.smartcity.user.user.UserEntity;
import com.smartcity.user.user.UserRepository;
import com.smartcity.user.worker.exceptions.WorkerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
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
    private final R2dbcEntityTemplate r2dbcEntityTemplate;


    public Flux<Worker> getAllWorkers() {
        log.info("Fetching all workers");
        return workerRepository.findAll().map(WorkerMapper.INSTANCE::toModel);
    }
    public Mono<String> register (WorkerRequest workerRequest){
        log.info("adding new worker");
        return workerRepository.findByEmail(workerRequest.getEmail()).flatMap(workerEntity -> Mono.error(() -> new WorkerException("Worker already exist with this email"))).switchIfEmpty(Mono.defer(() -> {
            assert workerRequest.getId() != null;
            assert workerRequest.getVillageId() != null;
            WorkerEntity worker = WorkerEntity.builder().id(workerRequest.getId().toString()).name(workerRequest.getName()).email(workerRequest.getEmail()).availability(Boolean.TRUE.equals(workerRequest.getAvailability())).skills(workerRequest.getSkills()).villageId(String.valueOf(workerRequest.getVillageId())).build();
            return r2dbcEntityTemplate.insert(WorkerEntity.class).using(worker);
        })).cast(WorkerEntity.class).map(WorkerEntity::getId);
    }

    public Mono<Worker> getWorkerById(String id) {
        log.info("Fetching worker with id: {}", id);
        return getWorker(id).map(WorkerMapper.INSTANCE::toModel);
    }

    public Mono<Void> updateWorker(String id, WorkerRequest workerRequest) {
        log.info("Updating worker with id: {}", id);
        return userRepository.findById(id).switchIfEmpty(Mono.error(new RuntimeException("User not found with id: " + id))).flatMap(userEntity -> updateUserEntity(id, workerRequest, userEntity)).then();
    }

    private Mono<UserEntity> updateUserEntity(String id, WorkerRequest workerRequest, UserEntity userEntity) {
       UpdateHelper.updateIfNotNull(userEntity::setName, workerRequest.getName());
        UpdateHelper.updateIfNotNull(userEntity::setEmail, workerRequest.getEmail());
        return getWorker(id).flatMap(workerEntity -> updateWorker(workerRequest, workerEntity)).then(userRepository.save(userEntity));
    }

    private Mono<WorkerEntity> getWorker(String id) {
        return workerRepository.findById(id).switchIfEmpty(Mono.error(new RuntimeException("Worker not found with id: " + id)));
    }

    private Mono<WorkerEntity> updateWorker(WorkerRequest workerRequest, WorkerEntity workerEntity) {
        UpdateHelper.updateIfNotNull(workerEntity::setSkills, workerRequest.getSkills());
        UpdateHelper.updateIfNotNull(workerEntity::setAvailability, workerRequest.getAvailability());
        return workerRepository.save(workerEntity);
    }

    public Mono<Void> deleteWorker(String id) {
        log.info("Deleting worker with id: {}", id);
        return getWorker(id).flatMap(workerRepository::delete);
    }
}
