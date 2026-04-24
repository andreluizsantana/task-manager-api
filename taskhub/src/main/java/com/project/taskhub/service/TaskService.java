package com.project.taskhub.service;

import java.time.LocalDate;
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

    private static final int FIRST_OCCURRENCE = 1;

    // valida se é uma task recorrete
    private void validarRecorrencia(TipoRecorrencia tipo, Integer totalRe) {
	if (tipo != TipoRecorrencia.MENSAL || Objects.isNull(totalRe) || totalRe <= 0) {
	    throw new TaskRecurrenceException("Dados de recorrência inválidos.");
	}
    }

    // cria o grupo
    private TaskGroup criarTaskGroup(TipoRecorrencia tipo, Integer totalRe) {
	TaskGroup group = new TaskGroup(tipo, totalRe);
	return taskGroupRepository.save(group);
    }

    // gera as recorrencias
    private List<Task> gerarTarefasRecorrentes(TaskRequestDTO taskrequestdto, TaskGroup group, Integer totalRe) {
	List<Task> tarefas = new ArrayList<>();
	for (int i = 0; i < totalRe; i++) {
	    Task tarefa = taskMapper.toEntity(taskrequestdto);
	    tarefa.setTaskGroup(group);
	    tarefa.setOcorrencia(i + FIRST_OCCURRENCE);
	    tarefa.setDataExecucao(LocalDate.now().plusMonths(i));
	    tarefas.add(tarefa);
	}
	return tarefas;
    }

    // salvar
    public TaskResponseDTO salvarTarefa(TaskRequestDTO taskrequestdto) {
	if (taskrequestdto.tipoRecorrencia() != null) {
	    throw new TaskRecurrenceException("Use '/api/tasks/recurrent' para tarefas recorrentes.");
	}
	Task tarefa;
	tarefa = taskMapper.toEntity(taskrequestdto);
	tarefa = taskRepository.save(tarefa);
	return taskMapper.toDTO(tarefa);
    }

    // tarefa recorrente
    @Transactional
    public List<TaskResponseDTO> salvarTarefaRecorrente(TaskRequestDTO taskrequestdto) {
	validarRecorrencia(taskrequestdto.tipoRecorrencia(), taskrequestdto.totalRecorrencia());
	TaskGroup group = criarTaskGroup(taskrequestdto.tipoRecorrencia(), taskrequestdto.totalRecorrencia());
	List<Task> tarefas = gerarTarefasRecorrentes(taskrequestdto, group, taskrequestdto.totalRecorrencia());
	List<Task> salvas = taskRepository.saveAll(tarefas);

	return salvas.stream().map(taskMapper::toDTO).toList();
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