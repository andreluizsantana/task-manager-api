package com.project.taskhub.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

import lombok.extern.log4j.Log4j2;

@Service
@Transactional
@Log4j2
public class TaskService {

    private final TaskMapper taskMapper;
    private final NotificationService notificationService;
    private final TaskRepository taskRepository;
    private final TaskGroupRepository taskGroupRepository;

    public TaskService(TaskMapper taskMapper, NotificationService notificationService, TaskRepository taskRepository, TaskGroupRepository taskGroupRepository) {
	this.taskMapper = taskMapper;
	this.notificationService = notificationService;
	this.taskRepository = taskRepository;
	this.taskGroupRepository = taskGroupRepository;
    }

    private static final int FIRST_OCCURRENCE = 1;

    // Valida se é uma task recorrete

    private void validarRecorrencia(TipoRecorrencia tipo, Integer totalRe) {
	int qtdeMaximaRec = 36;
	if (tipo != TipoRecorrencia.MENSAL || Objects.isNull(totalRe) || totalRe <= 0) {
	    throw new TaskRecurrenceException("Dados de recorrência inválidos.");
	}
	if (totalRe > qtdeMaximaRec) {
	    throw new TaskRecurrenceException("Total de recorrência não pode exceder " + qtdeMaximaRec + " tarefas.");
	}
    }

    // Cria o grupo

    private TaskGroup criarTaskGroup(TipoRecorrencia tipo, Integer totalRe) {
	TaskGroup group = new TaskGroup(tipo, totalRe);
	return taskGroupRepository.save(group);
    }

    // Gera as recorrencias

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

    // Salvar

    public TaskResponseDTO salvarTarefa(TaskRequestDTO taskrequestdto) {
	if (taskrequestdto.tipoRecorrencia() != TipoRecorrencia.UNICA) {
	    throw new TaskRecurrenceException("Use '/api/tasks/recurrent' para tarefas recorrentes.");
	}
	Task tarefa;
	tarefa = taskMapper.toEntity(taskrequestdto);
	tarefa = taskRepository.save(tarefa);
	return taskMapper.toDTO(tarefa);
    }

    // Tarefa recorrente

    @Transactional
    public List<TaskResponseDTO> salvarTarefaRecorrente(TaskRequestDTO taskrequestdto) {
	validarRecorrencia(taskrequestdto.tipoRecorrencia(), taskrequestdto.totalRecorrencia());
	TaskGroup group = criarTaskGroup(taskrequestdto.tipoRecorrencia(), taskrequestdto.totalRecorrencia());
	List<Task> tarefas = gerarTarefasRecorrentes(taskrequestdto, group, taskrequestdto.totalRecorrencia());
	List<Task> salvas = taskRepository.saveAll(tarefas);

	return salvas.stream().map(taskMapper::toDTO).toList();
    }

    // Atualizar status

    public TaskResponseDTO atualizarDados(Long id, TaskUpdateDTO taskupdatedto) {
	Task atualiza = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
	taskMapper.updateEntityFromDto(taskupdatedto, atualiza);
	Task tarefaAtualizada = taskRepository.save(atualiza);
	return taskMapper.toDTO(tarefaAtualizada);
    }

    // Listar

    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> listarTarefas(Pageable pageable) {
	Page<Task> tarefas = taskRepository.findAll(pageable);
	return tarefas.map(taskMapper::toDTO);
    }

    // Bucar por ID

    @Transactional(readOnly = true)
    public TaskResponseDTO buscarID(Long id) {
	Task localizaID = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
	return taskMapper.toDTO(localizaID);
    }

    // Deletar

    public void deletarTarefa(Long id) {
	Task localizaId = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
	taskRepository.delete(localizaId);
    }

    @Transactional
    @Scheduled(cron = "1 0 0 * * *", zone = "America/Sao_Paulo")
    public void jobTaskVencida() {
	log.info("Início do jobTaskVencida em {}", LocalDateTime.now());
	LocalDate hoje = LocalDate.now();
	List<Task> tarefasVencidas = taskRepository.findByStatusAndDataExecucaoBefore(StatusTask.PENDENTE, hoje);
	if (tarefasVencidas.isEmpty()) {
	    log.info("Nenhuma tarefa vencida encontrada.");
	    return;
	}
	tarefasVencidas.forEach(t -> t.setStatus(StatusTask.NAO_EXECUTADA));
	log.info("Atualizadas {} tarefas vencidas", tarefasVencidas.size());
	log.info("Fim do jobTaskVencida em {}", LocalDateTime.now());
    }
}