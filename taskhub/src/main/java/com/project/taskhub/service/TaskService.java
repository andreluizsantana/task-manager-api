package com.project.taskhub.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.taskhub.dto.TaskGroupMapper;
import com.project.taskhub.dto.TaskMapper;
import com.project.taskhub.dto.TaskRequestDTO;
import com.project.taskhub.dto.TaskResponseDTO;
import com.project.taskhub.dto.TaskUpdateDTO;
import com.project.taskhub.entity.Task;
import com.project.taskhub.entity.TaskGroup;
import com.project.taskhub.entity.enums.TipoRecorrencia;
import com.project.taskhub.exceptions.TaskNotFoundException;
import com.project.taskhub.exceptions.TaskRecurrenceException;
import com.project.taskhub.repository.TaskGroupRepository;
import com.project.taskhub.repository.TaskRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor

public class TaskService {

    private final TaskMapper taskMapper;
    private final TaskGroupMapper taskGroupMapper;

    private final TaskRepository taskRepository;
    private final TaskGroupRepository taskGroupRepository;

    // salvar
    public TaskResponseDTO salvarTarefa(TaskRequestDTO taskrequestdto) {
	if (taskrequestdto.tipoRecorrencia() != null) {
	    throw new TaskRecurrenceException("Use /api/tasks/recurrent para tarefas recorrentes");
	}
	Task tarefa;
	tarefa = taskMapper.toEntity(taskrequestdto);
	tarefa = taskRepository.save(tarefa);
	return taskMapper.toDTO(tarefa);
    }

    // tarefa recorrente
    @Transactional
    public List<TaskResponseDTO> salvarTarefaRecorrente(TaskRequestDTO taskrequestdto) {
	TipoRecorrencia tipoRecorrente = taskrequestdto.tipoRecorrencia();
	Integer totalRecorrente = taskrequestdto.totalRecorrencia();
	Task tarefaRecorrente;
	List<Task> tarefasParaSalvar = new ArrayList<>();
	int i;
	if (tipoRecorrente == TipoRecorrencia.MENSAL && Objects.nonNull(totalRecorrente) && totalRecorrente > 0) {
	    TaskGroup taskgroup = new TaskGroup(tipoRecorrente, totalRecorrente);
	    taskgroup = taskGroupRepository.save(taskgroup);
	    for (i = 0; i < totalRecorrente; i++) {
		tarefaRecorrente = taskMapper.toEntity(taskrequestdto);
		tarefaRecorrente.setTaskGroup(taskgroup);
		tarefaRecorrente.setOcorrencia(i + 1);
		tarefasParaSalvar.add(tarefaRecorrente);
	    }
	} else {
	    throw new TaskRecurrenceException("Dados de recorrência inválidos.");
	}

	List<Task> paraSalvar = taskRepository.saveAll(tarefasParaSalvar);
	return paraSalvar.stream().map(taskMapper::toDTO).toList();
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
