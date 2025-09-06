package com.smartcity.user.worker;

import com.smartcity.models.Worker;
import com.smartcity.models.WorkerRequest;
import com.smartcity.user.user.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface WorkerMapper {
    public static WorkerMapper INSTANCE = Mappers.getMapper(WorkerMapper.class);
    WorkerEntity toEntity(WorkerRequest workerRequest);
    @Mapping(source = "userEntity.id", target = "id")
    @Mapping(source = "userEntity.name", target = "name")
    @Mapping(source = "userEntity.email", target = "email")
    @Mapping(source = "userEntity.etag", target = "etag")
    @Mapping(source = "workerEntity.skills", target = "skills")
    @Mapping(source = "workerEntity.availability", target = "availability")
    Worker toModel(WorkerEntity workerEntity, UserEntity userEntity);
}

