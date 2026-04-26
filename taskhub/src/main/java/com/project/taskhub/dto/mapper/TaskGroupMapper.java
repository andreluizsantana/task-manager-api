package com.project.taskhub.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.project.taskhub.dto.response.TaskGroupResponseDTO;
import com.project.taskhub.entity.TaskGroup;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskGroupMapper {
    TaskGroupResponseDTO toDTO(TaskGroup taskgroup);

    @Mapping(target = "id", ignore = true)
    TaskGroup toEntity(TaskGroupResponseDTO dto);

}
