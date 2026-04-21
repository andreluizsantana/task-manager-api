package com.project.taskhub.entity;

import java.io.Serializable;

import com.project.taskhub.entity.enums.TipoRecorrencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "taskgroup")
public class TaskGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @SequenceGenerator(name = "task_seq", sequenceName = "task_sequence", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private TipoRecorrencia frequencia;

    // número total de vezes que aquele grupo deve existir
    private Integer totalRecorrencia;

    public TaskGroup() {
    }

    public TaskGroup(TipoRecorrencia frequencia, Integer totalRecorrencia) {
	this.frequencia = frequencia;
	this.totalRecorrencia = totalRecorrencia;
    }

    public TipoRecorrencia getFrequencia() {
	return frequencia;
    }

    public void setFrequencia(TipoRecorrencia frequencia) {
	this.frequencia = frequencia;
    }

    public Integer getTotalRecorrencia() {
	return totalRecorrencia;
    }

    public void setTotalRecorrencia(Integer totalRecorrencia) {
	this.totalRecorrencia = totalRecorrencia;
    }

}
