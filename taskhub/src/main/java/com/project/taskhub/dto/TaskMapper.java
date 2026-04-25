package com.project.taskhub.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.project.taskhub.entity.Task;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {
    TaskResponseDTO toDTO(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "taskGroup", ignore = true)
    @Mapping(target = "ocorrencia", ignore = true)
    Task toEntity(TaskRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(TaskUpdateDTO dto, @MappingTarget Task task);
}
