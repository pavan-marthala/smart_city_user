package com.smartcity.user.user;

import com.smartcity.models.User;
import com.smartcity.models.Village;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    @Mapping(source = "entity.id", target = "id")
    @Mapping(source = "entity.name", target = "name")
    User toModel(UserEntity entity, Village village);
    UserEntity toEntity(User user);
}
