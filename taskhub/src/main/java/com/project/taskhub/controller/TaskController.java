package com.project.taskhub.controller;

import com.project.taskhub.dto.request.TaskRequestDTO;
import com.project.taskhub.dto.response.TaskResponseDTO;
import com.project.taskhub.dto.update.TaskUpdateDTO;
import com.project.taskhub.security.JWTUserData;
import com.project.taskhub.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Lists all tasks with pagination.
     *
     * @param pageable pagination with a default limit of 100 records per page (configurable in
     *     application.properties)
     * @return a paged list of all records as TaskResponseDTO
     */
    @GetMapping
    public Page<TaskResponseDTO> listTasks(
            @PageableDefault(size = 100, sort = "id", direction = Sort.Direction.DESC)
                    Pageable pageable,
            @AuthenticationPrincipal JWTUserData user) {
        return taskService.listTasks(pageable, user.userId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @PathVariable Long id, @AuthenticationPrincipal JWTUserData user) {
        TaskResponseDTO task = taskService.getTaskById(id, user.userId());
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @Valid @RequestBody TaskRequestDTO dto, @AuthenticationPrincipal JWTUserData user) {
        TaskResponseDTO created = taskService.saveTask(dto, user.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/recurrent")
    public ResponseEntity<List<TaskResponseDTO>> createRecurringTask(
            @Valid @RequestBody TaskRequestDTO dto, @AuthenticationPrincipal JWTUserData user) {
        List<TaskResponseDTO> response = taskService.saveRecurringTask(dto, user.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateDTO dto,
            @AuthenticationPrincipal JWTUserData user) {
        TaskResponseDTO updated = taskService.updateTask(id, dto, user.userId());
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id, @AuthenticationPrincipal JWTUserData user) {
        taskService.deleteTask(id, user.userId());
        return ResponseEntity.noContent().build();
    }
}
