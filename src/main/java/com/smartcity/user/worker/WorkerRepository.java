package com.smartcity.user.worker;

import com.smartcity.models.Worker;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface WorkerRepository extends ReactiveCrudRepository<WorkerEntity,String> {
//    @Query("SELECT u.id, u.name, u.email, u.role, u.etag, u.is_active as active , " +
//            "w.skills, w.availability " +
//            "FROM user u JOIN worker w ON u.id = w.user_id " +
//            "WHERE u.role = 'WORKER'")
//    Flux<Worker> findAllWorkers();
//
//    @Query("SELECT u.id, u.name, u.email, u.role, u.etag, u.is_active as active, " +
//            "w.skills, w.availability " +
//            "FROM user u JOIN worker w ON u.id = w.user_id " +
//            "WHERE u.id = :id AND u.role = 'WORKER'")
//    Mono<Worker> findByWorkerId(String id);

    Mono<WorkerEntity> findByEmail(String email);

}
