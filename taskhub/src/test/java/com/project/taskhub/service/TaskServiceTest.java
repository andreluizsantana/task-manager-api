package com.project.taskhub.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.taskhub.dto.mapper.TaskMapper;
import com.project.taskhub.dto.request.TaskRequestDTO;
import com.project.taskhub.dto.response.TaskResponseDTO;
import com.project.taskhub.dto.update.TaskUpdateDTO;
import com.project.taskhub.entity.Task;
import com.project.taskhub.entity.TaskGroup;
import com.project.taskhub.entity.enums.StatusTask;
import com.project.taskhub.entity.enums.TipoRecorrencia;
import com.project.taskhub.exceptions.TaskNotFoundException;
import com.project.taskhub.exceptions.TaskRecurrenceException;
import com.project.taskhub.repository.TaskGroupRepository;
import com.project.taskhub.repository.TaskRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock private TaskMapper taskMapper;
    @Mock private TaskRepository taskRepository;
    @Mock private TaskGroupRepository taskGroupRepository;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskMapper, taskRepository, taskGroupRepository);
    }

    private TaskRequestDTO singleTaskRequest() {
        return new TaskRequestDTO(
                "Title", "Description", TipoRecorrencia.UNICA, null, LocalDate.now());
    }

    private TaskRequestDTO monthlyTaskRequest(int totalRecurrences) {
        return new TaskRequestDTO(
                "Title", "Description", TipoRecorrencia.MENSAL, totalRecurrences, LocalDate.now());
    }

    @Test
    @DisplayName("saveTask: unique task is saved and returned")
    void saveTask_whenUnique_savesAndReturnsDto() {
        TaskRequestDTO dto = singleTaskRequest();
        Task task = new Task("Title", "Description", dto.executionDate());
        Task saved = new Task("Title", "Description", dto.executionDate());
        TaskResponseDTO response =
                new TaskResponseDTO(
                        1L,
                        "Title",
                        "Description",
                        StatusTask.PENDENTE,
                        TipoRecorrencia.UNICA,
                        null,
                        null,
                        null,
                        dto.executionDate(),
                        null);

        when(taskMapper.toEntity(dto)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(saved);
        when(taskMapper.toDTO(saved)).thenReturn(response);

        TaskResponseDTO result = taskService.saveTask(dto);

        assertThat(result).isEqualTo(response);
        verify(taskRepository).save(task);
    }

    @Test
    @DisplayName("saveTask: recurrent type is rejected")
    void saveTask_whenRecurrentType_throws() {
        TaskRequestDTO dto = monthlyTaskRequest(2);

        assertThatThrownBy(() -> taskService.saveTask(dto))
                .isInstanceOf(TaskRecurrenceException.class)
                .hasMessageContaining("/api/tasks/recurrent");
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveRecurringTask: valid recurrence creates group and saves all tasks")
    void saveRecurringTask_whenValid_createsGroupAndSavesAll() {
        TaskRequestDTO dto = monthlyTaskRequest(2);
        TaskGroup group = new TaskGroup(TipoRecorrencia.MENSAL, 2);
        Task task = new Task("Title", "Description", LocalDate.now());
        Task saved = new Task("Title", "Description", LocalDate.now());
        saved.setId(10L);
        TaskResponseDTO response =
                new TaskResponseDTO(
                        10L,
                        "Title",
                        "Description",
                        StatusTask.PENDENTE,
                        TipoRecorrencia.MENSAL,
                        null,
                        null,
                        1,
                        null,
                        null);

        when(taskGroupRepository.save(any(TaskGroup.class))).thenReturn(group);
        when(taskMapper.toEntity(dto)).thenReturn(task);
        when(taskRepository.saveAll(anyList())).thenReturn(List.of(saved, saved));
        when(taskMapper.toDTO(any(Task.class))).thenReturn(response);

        List<TaskResponseDTO> result = taskService.saveRecurringTask(dto);

        assertThat(result).hasSize(2);
        verify(taskGroupRepository).save(any(TaskGroup.class));
        verify(taskRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("saveRecurringTask: non-monthly type is rejected")
    void saveRecurringTask_whenNonMonthlyType_throws() {
        TaskRequestDTO dto =
                new TaskRequestDTO(
                        "Title", "Description", TipoRecorrencia.UNICA, 2, LocalDate.now());

        assertThatThrownBy(() -> taskService.saveRecurringTask(dto))
                .isInstanceOf(TaskRecurrenceException.class);
    }

    @Test
    @DisplayName("saveRecurringTask: null totalRecurrences is rejected")
    void saveRecurringTask_whenNullTotalRecurrences_throws() {
        TaskRequestDTO dto =
                new TaskRequestDTO(
                        "Title", "Description", TipoRecorrencia.MENSAL, null, LocalDate.now());

        assertThatThrownBy(() -> taskService.saveRecurringTask(dto))
                .isInstanceOf(TaskRecurrenceException.class);
    }

    @Test
    @DisplayName("saveRecurringTask: zero totalRecurrences is rejected")
    void saveRecurringTask_whenZeroTotalRecurrences_throws() {
        TaskRequestDTO dto = monthlyTaskRequest(0);

        assertThatThrownBy(() -> taskService.saveRecurringTask(dto))
                .isInstanceOf(TaskRecurrenceException.class);
    }

    @Test
    @DisplayName("saveRecurringTask: totalRecurrences above the limit is rejected")
    void saveRecurringTask_whenTotalRecurrencesExceedsLimit_throws() {
        TaskRequestDTO dto = monthlyTaskRequest(37);

        assertThatThrownBy(() -> taskService.saveRecurringTask(dto))
                .isInstanceOf(TaskRecurrenceException.class);
    }

    @Test
    @DisplayName("updateTask: task not found throws")
    void updateTask_whenNotFound_throws() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(
                        () ->
                                taskService.updateTask(
                                        1L, new TaskUpdateDTO("New", null, null, null)))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    @DisplayName("updateTask: existing task is updated and returned")
    void updateTask_whenFound_updatesAndReturnsDto() {
        Task task = new Task("Old", "Old", LocalDate.now());
        TaskUpdateDTO dto = new TaskUpdateDTO("New", null, StatusTask.CONCLUIDO, null);
        TaskResponseDTO response =
                new TaskResponseDTO(
                        1L,
                        "New",
                        "Old",
                        StatusTask.CONCLUIDO,
                        TipoRecorrencia.UNICA,
                        null,
                        null,
                        null,
                        null,
                        null);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDTO(task)).thenReturn(response);

        TaskResponseDTO result = taskService.updateTask(1L, dto);

        assertThat(result).isEqualTo(response);
        verify(taskMapper).updateEntityFromDto(dto, task);
    }

    @Test
    @DisplayName("getTaskById: task not found throws")
    void getTaskById_whenNotFound_throws() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(1L))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    @DisplayName("getTaskById: existing task is returned as DTO")
    void getTaskById_whenFound_returnsDto() {
        Task task = new Task("Title", "Description", LocalDate.now());
        TaskResponseDTO response =
                new TaskResponseDTO(
                        1L,
                        "Title",
                        "Description",
                        StatusTask.PENDENTE,
                        TipoRecorrencia.UNICA,
                        null,
                        null,
                        null,
                        null,
                        null);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toDTO(task)).thenReturn(response);

        TaskResponseDTO result = taskService.getTaskById(1L);

        assertThat(result).isEqualTo(response);
    }

    @Test
    @DisplayName("deleteTask: task not found throws")
    void deleteTask_whenNotFound_throws() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(1L))
                .isInstanceOf(TaskNotFoundException.class);
        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    @DisplayName("deleteTask: existing task is deleted")
    void deleteTask_whenFound_deletes() {
        Task task = new Task("Title", "Description", LocalDate.now());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteTask(1L);

        verify(taskRepository).delete(task);
    }

    @Test
    @DisplayName("listTasks: returns mapped page")
    void listTasks_returnsMappedPage() {
        Task task = new Task("Title", "Description", LocalDate.now());
        Page<Task> tasks = new PageImpl<>(List.of(task));
        Pageable pageable = Pageable.ofSize(10);
        TaskResponseDTO response =
                new TaskResponseDTO(
                        1L,
                        "Title",
                        "Description",
                        StatusTask.PENDENTE,
                        TipoRecorrencia.UNICA,
                        null,
                        null,
                        null,
                        null,
                        null);

        when(taskRepository.findAll(pageable)).thenReturn(tasks);
        when(taskMapper.toDTO(task)).thenReturn(response);

        Page<TaskResponseDTO> result = taskService.listTasks(pageable);

        assertThat(result.getContent()).hasSize(1).containsExactly(response);
    }

    @Test
    @DisplayName("markOverdueTasks: pending overdue tasks are marked as not executed")
    void markOverdueTasks_whenOverdue_updatesStatus() {
        Task overdue = new Task("Title", "Description", LocalDate.now().minusDays(1));

        when(taskRepository.findByStatusAndExecutionDateBefore(
                        eq(StatusTask.PENDENTE), any(LocalDate.class)))
                .thenReturn(List.of(overdue));

        taskService.markOverdueTasks();

        assertThat(overdue.getStatus()).isEqualTo(StatusTask.NAO_EXECUTADA);
    }
}
