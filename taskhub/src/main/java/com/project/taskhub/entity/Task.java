package com.project.taskhub.entity;

import com.project.taskhub.entity.enums.StatusTask;
import com.project.taskhub.entity.enums.TipoRecorrencia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "tasks")
public class Task extends TaskBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_seq")
    @SequenceGenerator(
            name = "task_seq",
            sequenceName = "task_sequence",
            initialValue = 1,
            allocationSize = 1)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 120, message = "O título deve ter no máximo 120 caracteres")
    @Column(nullable = false, length = 120)
    private String title;

    @NotBlank(message = "A descrição é obrigatória")
    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTask status = StatusTask.PENDENTE;

    @Column(name = "tipo_recorrencia", nullable = true, length = 50)
    @Enumerated(EnumType.STRING)
    private TipoRecorrencia recurrenceType;

    @Column(name = "ocorrencia")
    private Integer occurrence;

    @Column(name = "data_execucao")
    private LocalDate executionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_group_id", nullable = true)
    private TaskGroup taskGroup;

    public Task(
            @NotBlank(message = "O título é obrigatório")
                    @Size(max = 120, message = "O título deve ter no máximo 120 caracteres")
                    String title,
            @NotBlank(message = "A descrição é obrigatória") String description,
            LocalDate executionDate) {
        this.title = title;
        this.description = description;
        this.executionDate = executionDate;
    }

    public Task() {}

    public TipoRecorrencia getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(TipoRecorrencia recurrenceType) {
        this.recurrenceType = recurrenceType;
    }

    public Integer getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(Integer occurrence) {
        this.occurrence = occurrence;
    }

    public TaskGroup getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(TaskGroup taskGroup) {
        this.taskGroup = taskGroup;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDate executionDate) {
        this.executionDate = executionDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Task other = (Task) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "Task [id=" + id + ", title=" + title + ", description=" + description + "]";
    }
}
