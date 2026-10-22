package com.project.taskhub.repository;

import com.project.taskhub.entity.Task;
import com.project.taskhub.entity.enums.StatusTask;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @EntityGraph(attributePaths = {"taskGroup"})
    Page<Task> findAllByOwnerId(Long ownerId, Pageable pageable);

    @EntityGraph(attributePaths = {"taskGroup"})
    Optional<Task> findByIdAndOwnerId(Long id, Long ownerId);

    List<Task> findByStatus(StatusTask status);

    List<Task> findByStatusAndExecutionDateBefore(StatusTask status, LocalDate executionDate);

    @Modifying
    @Query(
            "update Task t set t.status = :newStatus, t.updatedAt = CURRENT_TIMESTAMP where t.status = :oldStatus and t.executionDate < :today")
    int markOverdue(
            @Param("oldStatus") StatusTask oldStatus,
            @Param("newStatus") StatusTask newStatus,
            @Param("today") LocalDate today);
}
