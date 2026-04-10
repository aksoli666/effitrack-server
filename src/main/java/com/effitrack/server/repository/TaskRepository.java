package com.effitrack.server.repository;

import com.effitrack.server.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssigneeId(Long userId);

    List<Task> findByAssigneeIdAndPlannedDateBetween(Long assigneeId, java.time.LocalDateTime start, java.time.LocalDateTime end);
}
