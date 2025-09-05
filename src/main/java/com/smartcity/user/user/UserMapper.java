package com.smartcity.user.user;

import com.smartcity.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    User toModel(UserEntity entity);
    UserEntity toEntity(User model);
}
