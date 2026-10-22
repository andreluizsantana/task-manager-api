package com.project.taskhub.service;

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
import com.project.taskhub.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Log4j2
public class TaskService {

    private static final int FIRST_OCCURRENCE = 1;
    private static final int MAX_RECURRENCES = 36;
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("America/Sao_Paulo");

    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;
    private final TaskGroupRepository taskGroupRepository;
    private final UserRepository userRepository;

    public TaskService(
            TaskMapper taskMapper,
            TaskRepository taskRepository,
            TaskGroupRepository taskGroupRepository,
            UserRepository userRepository) {
        this.taskMapper = taskMapper;
        this.taskRepository = taskRepository;
        this.taskGroupRepository = taskGroupRepository;
        this.userRepository = userRepository;
    }

    private void validateRecurrence(TipoRecorrencia type, Integer totalRecurrences) {
        if (type != TipoRecorrencia.MENSAL || totalRecurrences == null || totalRecurrences <= 0) {
            throw new TaskRecurrenceException("Dados de recorrência inválidos.");
        }
        if (totalRecurrences > MAX_RECURRENCES) {
            throw new TaskRecurrenceException(
                    "Total de recorrência não pode exceder " + MAX_RECURRENCES + " tarefas.");
        }
    }

    private TaskGroup createTaskGroup(TipoRecorrencia type, Integer totalRecurrences) {
        TaskGroup group = new TaskGroup(type, totalRecurrences);
        return taskGroupRepository.save(group);
    }

    private List<Task> generateRecurringTasks(
            TaskRequestDTO dto, TaskGroup group, Integer totalRecurrences, Long ownerId) {
        var owner = userRepository.getReferenceById(ownerId);
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < totalRecurrences; i++) {
            Task task = taskMapper.toEntity(dto);
            task.setTaskGroup(group);
            task.setOwner(owner);
            task.setOccurrence(i + FIRST_OCCURRENCE);
            task.setExecutionDate(dto.executionDate().plusMonths(i));
            tasks.add(task);
        }
        return tasks;
    }

    public TaskResponseDTO saveTask(TaskRequestDTO dto, Long ownerId) {
        if (dto.recurrenceType() != TipoRecorrencia.UNICA) {
            throw new TaskRecurrenceException(
                    "Use '/api/tasks/recurrent' para tarefas recorrentes.");
        }
        Task task = taskMapper.toEntity(dto);
        task.setOwner(userRepository.getReferenceById(ownerId));
        task = taskRepository.save(task);
        return taskMapper.toDTO(task);
    }

    @Transactional
    public List<TaskResponseDTO> saveRecurringTask(TaskRequestDTO dto, Long ownerId) {
        validateRecurrence(dto.recurrenceType(), dto.totalRecurrences());
        if (dto.executionDate() == null) {
            throw new TaskRecurrenceException(
                    "A data inicial é obrigatória para tarefas recorrentes.");
        }
        TaskGroup group = createTaskGroup(dto.recurrenceType(), dto.totalRecurrences());
        List<Task> tasks = generateRecurringTasks(dto, group, dto.totalRecurrences(), ownerId);
        List<Task> saved = taskRepository.saveAll(tasks);

        return saved.stream().map(taskMapper::toDTO).toList();
    }

    public TaskResponseDTO updateTask(Long id, TaskUpdateDTO dto, Long ownerId) {
        Task task = findOwnedTask(id, ownerId);
        taskMapper.updateEntityFromDto(dto, task);
        Task updated = taskRepository.save(task);
        return taskMapper.toDTO(updated);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> listTasks(Pageable pageable, Long ownerId) {
        Page<Task> tasks = taskRepository.findAllByOwnerId(ownerId, pageable);
        return tasks.map(taskMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(Long id, Long ownerId) {
        Task task = findOwnedTask(id, ownerId);
        return taskMapper.toDTO(task);
    }

    public void deleteTask(Long id, Long ownerId) {
        Task task = findOwnedTask(id, ownerId);
        taskRepository.delete(task);
    }

    private Task findOwnedTask(Long id, Long ownerId) {
        return taskRepository
                .findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Transactional
    @Scheduled(cron = "1 0 0 * * *", zone = "America/Sao_Paulo")
    public void markOverdueTasks() {
        log.info("Starting markOverdueTasks at {}", LocalDateTime.now(BUSINESS_ZONE));
        LocalDate today = LocalDate.now(BUSINESS_ZONE);
        int updated =
                taskRepository.markOverdue(StatusTask.PENDENTE, StatusTask.NAO_EXECUTADA, today);
        if (updated == 0) {
            log.info("No overdue tasks found.");
            return;
        }
        log.info("Updated {} overdue tasks", updated);
        log.info("Finished markOverdueTasks at {}", LocalDateTime.now(BUSINESS_ZONE));
    }
}
