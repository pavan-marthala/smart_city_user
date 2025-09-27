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
    Worker toModel(WorkerEntity workerEntity);
}

