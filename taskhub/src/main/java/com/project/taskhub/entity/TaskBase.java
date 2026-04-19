package com.project.taskhub.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class TaskBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    @LastModifiedDate
    private LocalDateTime atualizadoEm;

    public TaskBase() {
    }

    public LocalDateTime getCriadoEm() {
	return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
	return atualizadoEm;
    }

}
