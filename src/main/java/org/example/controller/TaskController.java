package org.example.controller;

import org.example.dto.response.ApiResponse;
import org.example.dto.request.CreateTaskRequest;
import org.example.dto.response.TaskResponse;
import org.example.dto.request.UpdateTaskRequest;
import org.example.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(@RequestBody CreateTaskRequest request) {
        TaskResponse task = taskService.createTask(request);
        return ResponseEntity.ok(ApiResponse.success(task));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getAllTasks(
            @RequestParam(value = "date", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date) {
        List<TaskResponse> tasks;
        if (date != null) {
            tasks = taskService.getTasksByDate(date);
        } else {
            tasks = taskService.getAllTasks();
        }
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(@PathVariable("id") Long id) {
        TaskResponse task = taskService.getTaskById(id);
        return ResponseEntity.ok(ApiResponse.success(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(@PathVariable("id") Long id,
            @RequestBody UpdateTaskRequest request) {
        TaskResponse task = taskService.updateTask(id, request);
        return ResponseEntity.ok(ApiResponse.success(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable("id") Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<TaskResponse>> toggleTaskCompletion(@PathVariable("id") Long id) {
        TaskResponse task = taskService.toggleTaskCompletion(id);
        return ResponseEntity.ok(ApiResponse.success(task));
    }
}