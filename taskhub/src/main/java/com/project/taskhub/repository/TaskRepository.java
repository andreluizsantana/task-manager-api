package com.project.taskhub.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.taskhub.entity.Task;
import com.project.taskhub.entity.enums.StatusTask;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @EntityGraph(attributePaths = { "taskGroup" })
    @Query("SELECT t FROM Task t")
    Page<Task> findAllOptimized(Pageable pageable);

    @EntityGraph(attributePaths = { "taskGroup" })
    @Override
    Page<Task> findAll(Pageable pageable);

    List<Task> findByStatus(StatusTask status);

    List<Task> findByStatusAndDataExecucaoBefore(StatusTask status, LocalDate dataExecucao);
}
