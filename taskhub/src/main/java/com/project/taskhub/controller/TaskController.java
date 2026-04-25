package com.project.taskhub.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.taskhub.dto.TaskRequestDTO;
import com.project.taskhub.dto.TaskResponseDTO;
import com.project.taskhub.dto.TaskUpdateDTO;
import com.project.taskhub.service.TaskService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskservice;

    public TaskController(TaskService taskservice) {
	this.taskservice = taskservice;
    }

    @GetMapping
    public Page<TaskResponseDTO> listarTarefas(Pageable pageable) {
	return taskservice.listarTarefas(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> buscaID(@PathVariable Long id) {
	TaskResponseDTO busca = taskservice.buscarID(id);
	return ResponseEntity.status(HttpStatus.OK).body(busca);
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> addTarefa(@Valid @RequestBody TaskRequestDTO dto) {
	TaskResponseDTO novaTarefa = taskservice.salvarTarefa(dto);
	return ResponseEntity.status(HttpStatus.CREATED).body(novaTarefa);
    }

    @PostMapping("/recurrent")
    public ResponseEntity<List<TaskResponseDTO>> salvarRecorrente(@RequestBody @Valid TaskRequestDTO dto) {
	List<TaskResponseDTO> resposta = taskservice.salvarTarefaRecorrente(dto);
	return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> atualizarTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO udto) {
	TaskResponseDTO atualiza = taskservice.atualizarDados(id, udto);
	return ResponseEntity.status(HttpStatus.OK).body(atualiza);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTask(@PathVariable Long id) {
	taskservice.deletarTarefa(id);
	return ResponseEntity.noContent().build();
    }

    @GetMapping("/test-scheduled")
    public ResponseEntity<String> testarScheduled() {
	taskservice.jobTaskVencida();
	return ResponseEntity.ok("Agendamento testado");
    }

}
