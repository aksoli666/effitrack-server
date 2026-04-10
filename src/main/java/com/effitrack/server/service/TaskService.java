package com.effitrack.server.service;

import com.effitrack.server.model.Equipment;
import com.effitrack.server.model.Task;
import com.effitrack.server.model.TaskStatus;
import com.effitrack.server.model.User;
import com.effitrack.server.model.dto.TaskUpdateRequest;
import com.effitrack.server.repository.EquipmentRepository;
import com.effitrack.server.repository.TaskRepository;
import com.effitrack.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.effitrack.server.constant.StringConst.ERROR_PREFIX_OBJ_NOT_FOUND;
import static com.effitrack.server.constant.StringConst.SHIFT;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Task> getTasksForUser(Long userId) {
        return taskRepository.findByAssigneeId(userId);
    }

    public Task createTask(Long userId, Task task) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(ERROR_PREFIX_OBJ_NOT_FOUND + userId));

        task.setAssignee(user);

        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.TODO);
        }

        return taskRepository.save(task);
    }

    public List<Task> createBatch(Long userId, List<Task> tasks) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(ERROR_PREFIX_OBJ_NOT_FOUND + userId));

        tasks.forEach(task -> {
            task.setAssignee(user);
            if (task.getStatus() == null) {
                task.setStatus(TaskStatus.TODO);
            }
        });

        return taskRepository.saveAll(tasks);
    }

    public Task startTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException(ERROR_PREFIX_OBJ_NOT_FOUND + taskId));

        task.setStatus(TaskStatus.IN_PROGRESS);
        taskRepository.save(task);

        if (task.getEquipment() != null) {
            Equipment eq = task.getEquipment();
            eq.setActiveAction(task.getTitle());
            equipmentRepository.save(eq);
        }

        return task;
    }

    public Task completeTask(Long taskId, int actualMinutes, String comment) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException(ERROR_PREFIX_OBJ_NOT_FOUND + taskId));

        task.setStatus(TaskStatus.DONE);
        task.setActualMinutes(actualMinutes);
        task.setOperatorComment(comment);
        task.setCompletedAt(LocalDateTime.now());

        if (task.getEquipment() != null) {
            Equipment eq = task.getEquipment();
            if (eq.getActiveAction() != null && eq.getActiveAction().contains(task.getTitle())) {
                eq.setActiveAction(SHIFT);
                equipmentRepository.save(eq);
            }
        }

        return taskRepository.save(task);
    }

    public Task updateTaskDetails(Long taskId, TaskUpdateRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException(ERROR_PREFIX_OBJ_NOT_FOUND + taskId));

        if (request.getPlannedDate() != null) {
            task.setPlannedDate(request.getPlannedDate());
        }
        if (request.getActualMinutes() != null) {
            task.setActualMinutes(request.getActualMinutes());
        }
        if (request.getOperatorComment() != null) {
            task.setOperatorComment(request.getOperatorComment());
        }

        return taskRepository.save(task);
    }
}
