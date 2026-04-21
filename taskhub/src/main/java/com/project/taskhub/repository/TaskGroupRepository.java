package com.project.taskhub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.taskhub.entity.TaskGroup;

@Repository
public interface TaskGroupRepository extends JpaRepository<TaskGroup, Long> {

}
