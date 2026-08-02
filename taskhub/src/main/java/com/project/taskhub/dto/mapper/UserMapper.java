package com.project.taskhub.dto.mapper;

import com.project.taskhub.dto.request.RegisterUserRequestDTO;
import com.project.taskhub.dto.response.RegisterUserResponseDTO;
import com.project.taskhub.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    RegisterUserResponseDTO toRegisterDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "inactive", ignore = true)
    User toEntity(RegisterUserRequestDTO dto);
}
