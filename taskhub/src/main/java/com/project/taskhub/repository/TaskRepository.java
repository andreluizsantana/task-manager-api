package com.project.taskhub.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.taskhub.entity.Task;
import com.project.taskhub.entity.enums.StatusTask;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(StatusTask status);

    Page<Task> findByStatus(StatusTask status, Pageable pageable);
}
