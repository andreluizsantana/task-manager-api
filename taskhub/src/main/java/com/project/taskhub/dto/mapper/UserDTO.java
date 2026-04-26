package com.project.taskhub.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.project.taskhub.dto.request.RegisterUserRequestDTO;
import com.project.taskhub.dto.response.RegisterUserResponseDTO;
import com.project.taskhub.entity.User;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserDTO {

    RegisterUserResponseDTO toDTO(User user);

    RegisterUserResponseDTO toRegisterDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "inativo", ignore = true)
    User toEntity(RegisterUserRequestDTO dto);
}