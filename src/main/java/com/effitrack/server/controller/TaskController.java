package com.effitrack.server.controller;

import com.effitrack.server.constant.StringConst;
import com.effitrack.server.model.Task;
import com.effitrack.server.model.dto.TaskCompleteRequest;
import com.effitrack.server.model.dto.TaskUpdateRequest;
import com.effitrack.server.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@Tag(name = StringConst.TAG_TASKS, description = StringConst.TAG_TASKS_DESC)
@RequestMapping(StringConst.BASE_URL_TASKS)
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Operation(summary = StringConst.OP_TASK_LIST_SUM, description = StringConst.OP_TASK_LIST_DESC)
    @GetMapping(StringConst.ENDPOINT_USER_TASKS)
    public ResponseEntity<List<Task>> getUserTasks(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getTasksForUser(userId));
    }

    @Operation(summary = StringConst.OP_TASK_CREATE_SUM)
    @PostMapping(StringConst.ENDPOINT_USER_TASKS)
    public ResponseEntity<Task> createTask(@PathVariable Long userId, @RequestBody Task task) {
        return ResponseEntity.ok(taskService.createTask(userId, task));
    }

    @Operation(summary = StringConst.OP_TASK_BATCH_CREATE_SUM)
    @PostMapping(StringConst.ENDPOINT_TASK_BATCH)
    public ResponseEntity<List<Task>> createBatch(@PathVariable Long userId, @RequestBody List<Task> tasks) {
        return ResponseEntity.ok(taskService.createBatch(userId, tasks));
    }

    @Operation(summary = StringConst.OP_TASK_START_SUM, description = StringConst.OP_TASK_START_DESC)
    @PostMapping(StringConst.ENDPOINT_START_TASK)
    public ResponseEntity<Task> startTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskService.startTask(taskId));
    }

    @Operation(summary = StringConst.OP_TASK_COMPLETE_SUM, description = StringConst.OP_TASK_COMPLETE_DESC)
    @PostMapping(StringConst.ENDPOINT_COMPLETE_TASK)
    public ResponseEntity<Task> completeTask(
            @PathVariable Long taskId,
            @RequestBody TaskCompleteRequest request
    ) {
        return ResponseEntity.ok(
                taskService.completeTask(taskId, request.getActualMinutes(), request.getOperatorComment())
        );
    }

    @Operation(summary = StringConst.OP_TASK_UPDATE_SUM)
    @PutMapping(StringConst.ENDPOINT_TASK_UPDATE)
    public ResponseEntity<Task> updateTask(
            @PathVariable Long taskId,
            @RequestBody TaskUpdateRequest request
    ) {
        return ResponseEntity.ok(taskService.updateTaskDetails(taskId, request));
    }
}
