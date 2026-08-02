package com.project.taskhub.entity;

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
import java.io.Serializable;

@Entity
@Table(name = "task_group")
public class TaskGroup extends TaskBase implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @SequenceGenerator(
            name = "task_group_seq",
            sequenceName = "task_group_sequence",
            initialValue = 1,
            allocationSize = 50)
    private Long id;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private TipoRecorrencia frequency;

    // total number of times this group should exist
    @Column(name = "total_recorrencia")
    private Integer totalRecurrences;

    public TaskGroup() {}

    public TaskGroup(TipoRecorrencia frequency, Integer totalRecurrences) {
        this.frequency = frequency;
        this.totalRecurrences = totalRecurrences;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoRecorrencia getFrequency() {
        return frequency;
    }

    public void setFrequency(TipoRecorrencia frequency) {
        this.frequency = frequency;
    }

    public Integer getTotalRecurrences() {
        return totalRecurrences;
    }

    public void setTotalRecurrences(Integer totalRecurrences) {
        this.totalRecurrences = totalRecurrences;
    }
}
