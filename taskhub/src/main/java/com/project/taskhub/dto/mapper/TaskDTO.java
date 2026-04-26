package com.project.taskhub.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.project.taskhub.dto.request.TaskRequestDTO;
import com.project.taskhub.dto.response.TaskResponseDTO;
import com.project.taskhub.dto.update.TaskUpdateDTO;
import com.project.taskhub.entity.Task;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskDTO {
    TaskResponseDTO toDTO(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "taskGroup", ignore = true)
    @Mapping(target = "ocorrencia", ignore = true)
    Task toEntity(TaskRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(TaskUpdateDTO dto, @MappingTarget Task task);
}
