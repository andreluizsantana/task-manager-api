package com.project.taskhub.entity;

import java.io.Serializable;
import java.util.Objects;

import com.project.taskhub.entity.enums.StatusTask;
import com.project.taskhub.entity.enums.TipoRecorrencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tasks")
public class Task extends TaskBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @SequenceGenerator(name = "task_seq", sequenceName = "task_sequence", initialValue = 1, allocationSize = 1)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 120, message = "O título deve ter no máximo 120 caracteres")
    @Column(nullable = false, length = 120)
    private String titulo;

    @NotBlank(message = "A descrição é obrigatória")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTask status = StatusTask.PENDENTE;

    @Column(nullable = true, length = 50)
    @Enumerated(EnumType.STRING)
    private TipoRecorrencia tipoRecorrencia;

    // "índice" ou a posição atual para saber se esta é a tarefa nº 1... 2... 3...
    private Integer ocorrencia;

    @ManyToOne
    @JoinColumn(name = "task_group_id", nullable = true)
    private TaskGroup taskGroup;

    public Task(@NotBlank(message = "O título é obrigatório") @Size(max = 120, message = "O título deve ter no máximo 120 caracteres") String titulo,
	    @NotBlank(message = "A descrição é obrigatória") String descricao) {
	this.titulo = titulo;
	this.descricao = descricao;
    }

    public Task() {
    }

    public TipoRecorrencia getTipoRecorrencia() {
	return tipoRecorrencia;
    }

    public void setTipoRecorrencia(TipoRecorrencia tipoRecorrencia) {
	this.tipoRecorrencia = tipoRecorrencia;
    }

    public Integer getOcorrencia() {
	return ocorrencia;
    }

    public void setOcorrencia(Integer ocorrencia) {
	this.ocorrencia = ocorrencia;
    }

    public TaskGroup getTaskGroup() {
	return taskGroup;
    }

    public void setTaskGroup(TaskGroup taskGroup) {
	this.taskGroup = taskGroup;
    }

    public String getTitulo() {
	return titulo;
    }

    public void setTitulo(String titulo) {
	this.titulo = titulo;
    }

    public String getDescricao() {
	return descricao;
    }

    public void setDescricao(String descricao) {
	this.descricao = descricao;
    }

    public Long getId() {
	return id;
    }

    @Override
    public int hashCode() {
	return Objects.hash(id);
    }

    public StatusTask getStatus() {
	return status;
    }

    public void setStatus(StatusTask status) {
	this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Task other = (Task) obj;
	return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
	return "Task [id=" + id + ", titulo=" + titulo + ", descricao=" + descricao + "]";
    }

}
