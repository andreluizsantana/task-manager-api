package com.project.taskhub.repository;

import com.project.taskhub.entity.Task;
import com.project.taskhub.entity.enums.StatusTask;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @EntityGraph(attributePaths = {"taskGroup"})
    @Override
    Page<Task> findAll(Pageable pageable);

    List<Task> findByStatus(StatusTask status);

    List<Task> findByStatusAndExecutionDateBefore(StatusTask status, LocalDate executionDate);
}
