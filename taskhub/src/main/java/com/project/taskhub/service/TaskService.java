package com.project.taskhub.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.taskhub.dto.TaskMapper;
import com.project.taskhub.dto.TaskRequestDTO;
import com.project.taskhub.dto.TaskResponseDTO;
import com.project.taskhub.dto.TaskUpdateDTO;
import com.project.taskhub.entity.Task;
import com.project.taskhub.exceptions.TaskNotFoundException;
import com.project.taskhub.repository.TaskRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor

public class TaskService {

    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;

    // salvar
    public TaskResponseDTO salvarTarefa(TaskRequestDTO taskrequestdto) {
	Task tarefa = taskMapper.toEntity(taskrequestdto);
	tarefa = taskRepository.save(tarefa);
	return taskMapper.toDTO(tarefa);
    }

    // atualizar status
    public TaskResponseDTO atualizarDados(Long id, TaskUpdateDTO taskupdatedto) {
	Task atualiza = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
	taskMapper.updateEntityFromDto(taskupdatedto, atualiza);
	Task tarefaAtualizada = taskRepository.save(atualiza);
	return taskMapper.toDTO(tarefaAtualizada);
    }

    // listar
    public Page<TaskResponseDTO> listarTarefas(Pageable pageable) {
	Page<Task> tarefas = taskRepository.findAll(pageable);
	return tarefas.map(a -> taskMapper.toDTO(a));
    }

    // bucar por ID
    public TaskResponseDTO buscarID(Long id) {
	Task localizaID = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
	return taskMapper.toDTO(localizaID);
    }

    // deletar
    public void deletarTarefa(Long id) {
	Task localizaId = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
	taskRepository.delete(localizaId);

    }
}
