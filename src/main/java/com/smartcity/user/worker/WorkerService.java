package com.smartcity.user.worker;

import com.smartcity.models.Worker;
import com.smartcity.models.WorkerRequest;
import com.smartcity.user.shared.uils.UpdateHelper;
import com.smartcity.user.user.UserEntity;
import com.smartcity.user.user.UserRepository;
import com.smartcity.user.village.VillageService;
import com.smartcity.user.worker.exceptions.WorkerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkerService {
    private final WorkerRepository workerRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final VillageService villageService;


    public Flux<Worker> getAllWorkers() {
        log.info("Fetching all workers");
        return workerRepository.findAll().flatMap(worker -> villageService.getVillageById(worker.getVillageId()).map(village -> WorkerMapper.INSTANCE.toModel(worker, village)));
    }

    public Mono<String> register(WorkerRequest workerRequest) {
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
        return getWorker(id).flatMap(worker -> villageService.getVillageById(worker.getVillageId()).map(village -> WorkerMapper.INSTANCE.toModel(worker, village)));
    }
    public Mono<Boolean> existById() {
        return ReactiveSecurityContextHolder.getContext().map(securityContext -> securityContext.getAuthentication().getName()).flatMap(workerRepository::existsById);
    }
    public Mono<Void> updateWorker(String id, WorkerRequest workerRequest) {
        log.info("Updating worker with id: {}", id);
        return getWorker(id).flatMap(workerEntity -> updateWorkerEntity( workerRequest, workerEntity)).then();
    }


    private Mono<WorkerEntity> getWorker(String id) {
        return workerRepository.findById(id).switchIfEmpty(Mono.error(new RuntimeException("Worker not found with id: " + id)));
    }

    private Mono<WorkerEntity> updateWorkerEntity(WorkerRequest workerRequest, WorkerEntity workerEntity) {
        UpdateHelper.updateIfNotNull(workerEntity::setName, workerRequest.getName());
        UpdateHelper.updateIfNotNull(workerEntity::setEmail, workerRequest.getEmail());
//        UpdateHelper.updateIfNotNull(workerEntity::setVillageId, workerRequest.getVillageId().toString());
        UpdateHelper.updateIfNotNull(workerEntity::setSkills, workerRequest.getSkills());
        UpdateHelper.updateIfNotNull(workerEntity::setAvailability, workerRequest.getAvailability());
        return workerRepository.save(workerEntity);
    }

    public Mono<Void> deleteWorker(String id) {
        log.info("Deleting worker with id: {}", id);
        return getWorker(id).flatMap(workerRepository::delete);
    }
}
