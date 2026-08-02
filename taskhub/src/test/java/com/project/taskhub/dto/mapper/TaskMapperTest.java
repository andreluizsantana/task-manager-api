package com.project.taskhub.dto.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.taskhub.dto.request.TaskRequestDTO;
import com.project.taskhub.dto.response.TaskGroupResponseDTO;
import com.project.taskhub.dto.response.TaskResponseDTO;
import com.project.taskhub.dto.update.TaskUpdateDTO;
import com.project.taskhub.entity.Task;
import com.project.taskhub.entity.TaskGroup;
import com.project.taskhub.entity.enums.StatusTask;
import com.project.taskhub.entity.enums.TipoRecorrencia;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TaskMapperTest {

    private final TaskMapper taskMapper = new TaskMapperImpl();

    @Test
    @DisplayName("toEntity: maps request fields into a new Task")
    void toEntity_mapsRequestFields() {
        TaskRequestDTO dto =
                new TaskRequestDTO(
                        "Title",
                        "Description",
                        TipoRecorrencia.MENSAL,
                        12,
                        LocalDate.of(2026, 8, 1));

        Task task = taskMapper.toEntity(dto);

        assertThat(task.getTitle()).isEqualTo("Title");
        assertThat(task.getDescription()).isEqualTo("Description");
        assertThat(task.getRecurrenceType()).isEqualTo(TipoRecorrencia.MENSAL);
        assertThat(task.getExecutionDate()).isEqualTo(LocalDate.of(2026, 8, 1));
        assertThat(task.getId()).isNull();
        assertThat(task.getOccurrence()).isNull();
        assertThat(task.getTaskGroup()).isNull();
        assertThat(task.getStatus()).isEqualTo(StatusTask.PENDENTE);
    }

    @Test
    @DisplayName("toDTO: maps Task into a response without exposing the group entity")
    void toDTO_mapsTaskWithGroupResponse() {
        TaskGroup group = new TaskGroup(TipoRecorrencia.MENSAL, 12);
        group.setId(5L);
        Task task = new Task("Title", "Description", LocalDate.of(2026, 8, 1));
        task.setId(3L);
        task.setTaskGroup(group);
        task.setOccurrence(2);
        task.setStatus(StatusTask.EM_ANDAMENTO);

        TaskResponseDTO dto = taskMapper.toDTO(task);

        assertThat(dto.id()).isEqualTo(3L);
        assertThat(dto.title()).isEqualTo("Title");
        assertThat(dto.status()).isEqualTo(StatusTask.EM_ANDAMENTO);
        assertThat(dto.occurrence()).isEqualTo(2);
        TaskGroupResponseDTO groupDto = dto.taskGroup();
        assertThat(groupDto).isNotNull();
        assertThat(groupDto.id()).isEqualTo(5L);
        assertThat(groupDto.frequency()).isEqualTo(TipoRecorrencia.MENSAL);
        assertThat(groupDto.totalRecurrences()).isEqualTo(12);
    }

    @Test
    @DisplayName("updateEntityFromDto: only non-null fields are updated")
    void updateEntityFromDto_updatesOnlyNonNullFields() {
        Task task = new Task("Old title", "Old description", LocalDate.of(2026, 1, 1));
        task.setStatus(StatusTask.PENDENTE);
        TaskUpdateDTO dto = new TaskUpdateDTO("New title", null, StatusTask.CONCLUIDO, null);

        taskMapper.updateEntityFromDto(dto, task);

        assertThat(task.getTitle()).isEqualTo("New title");
        assertThat(task.getDescription()).isEqualTo("Old description");
        assertThat(task.getStatus()).isEqualTo(StatusTask.CONCLUIDO);
        assertThat(task.getExecutionDate()).isEqualTo(LocalDate.of(2026, 1, 1));
    }
}
