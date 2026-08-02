package com.project.taskhub.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.project.taskhub.dto.response.TaskResponseDTO;
import com.project.taskhub.dto.update.TaskUpdateDTO;
import com.project.taskhub.entity.enums.StatusTask;
import com.project.taskhub.entity.enums.TipoRecorrencia;
import com.project.taskhub.security.TokenConfiguration;
import com.project.taskhub.service.TaskService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private TaskService taskService;

    @MockitoBean private TokenConfiguration tokenConfiguration;

    private final TaskResponseDTO response =
            new TaskResponseDTO(
                    1L,
                    "Title",
                    "Description",
                    StatusTask.PENDENTE,
                    TipoRecorrencia.UNICA,
                    null,
                    null,
                    null,
                    LocalDate.now(),
                    null);

    @Test
    @DisplayName("GET /api/tasks returns the paginated list")
    void listTasks_returnsPage() throws Exception {
        Page<TaskResponseDTO> page = new PageImpl<>(List.of(response));
        when(taskService.listTasks(org.mockito.ArgumentMatchers.any())).thenReturn(page);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Title"));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} returns the task")
    void getTaskById_returnsTask() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    @DisplayName("POST /api/tasks creates a task")
    void createTask_returnsCreated() throws Exception {
        when(taskService.saveTask(org.mockito.ArgumentMatchers.any())).thenReturn(response);

        mockMvc.perform(
                        post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"title\":\"Title\",\"description\":\"Description\",\"recurrenceType\":\"UNICA\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("POST /api/tasks with invalid body returns 400 with field errors")
    void createTask_withInvalidBody_returnsBadRequest() throws Exception {
        mockMvc.perform(
                        post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"title\":\"\",\"description\":\"Description\",\"recurrenceType\":\"UNICA\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").exists());
    }

    @Test
    @DisplayName("POST /api/tasks without recurrence type returns 400")
    void createTask_withoutRecurrenceType_returnsBadRequest() throws Exception {
        mockMvc.perform(
                        post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"title\":\"Title\",\"description\":\"Description\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/tasks/recurrent creates recurring tasks")
    void createRecurringTask_returnsCreated() throws Exception {
        when(taskService.saveRecurringTask(org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of(response));

        mockMvc.perform(
                        post("/api/tasks/recurrent")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"title\":\"Title\",\"description\":\"Description\",\"recurrenceType\":\"MENSAL\",\"totalRecurrences\":12}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} updates a task")
    void updateTask_returnsUpdated() throws Exception {
        when(taskService.updateTask(
                        org.mockito.ArgumentMatchers.eq(1L),
                        org.mockito.ArgumentMatchers.any(TaskUpdateDTO.class)))
                .thenReturn(response);

        mockMvc.perform(
                        put("/api/tasks/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"title\":\"New title\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} returns no content")
    void deleteTask_returnsNoContent() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1")).andExpect(status().isNoContent());
    }
}
