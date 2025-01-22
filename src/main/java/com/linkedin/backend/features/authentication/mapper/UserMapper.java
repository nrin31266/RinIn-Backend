package com.linkedin.backend.features.authentication.mapper;

import com.linkedin.backend.features.authentication.dto.request.UpdateUserRequest;
import com.linkedin.backend.features.authentication.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    void updateUserProfile(@MappingTarget User user, UpdateUserRequest updateUserRequest);
}
