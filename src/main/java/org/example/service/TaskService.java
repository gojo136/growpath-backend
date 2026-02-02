package org.example.service;

import org.example.dto.request.CreateTaskRequest;
import org.example.dto.request.UpdateTaskRequest;
import org.example.dto.response.TaskResponse;
import org.example.entity.Task;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.TaskRepository;
import org.example.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public TaskResponse createTask(CreateTaskRequest request) {
        Long userId = getCurrentUserId();

        Task task = new Task();
        task.setUserId(userId);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        // Robust enum parsing
        try {
            task.setPriority(
                    request.getPriority() != null ? Task.Priority.valueOf(request.getPriority().toUpperCase().trim())
                            : Task.Priority.MEDIUM);
        } catch (Exception e) {
            task.setPriority(Task.Priority.MEDIUM);
        }

        task.setCategory(request.getCategory());
        task.setDueDate(request.getDueDate());
        task.setDueTime(request.getDueTime());

        try {
            task.setRepeatType(request.getRepeatType() != null
                    ? Task.RepeatType.valueOf(request.getRepeatType().toUpperCase().trim())
                    : Task.RepeatType.NONE);
        } catch (Exception e) {
            task.setRepeatType(Task.RepeatType.NONE);
        }

        task.setHasReminder(request.getHasReminder() != null ? request.getHasReminder() : false);
        task.setStatus(Task.Status.PENDING);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        Task savedTask = taskRepository.save(task);
        return convertToResponse(savedTask);
    }

    public List<TaskResponse> getAllTasks() {
        Long userId = getCurrentUserId();
        List<Task> tasks = taskRepository.findByUserIdOrderByDueDateAscDueTimeAsc(userId);
        return tasks.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByDate(java.time.LocalDate date) {
        Long userId = getCurrentUserId();
        List<Task> tasks = taskRepository.findByUserIdOrderByDueDateAscDueTimeAsc(userId);

        // Filter tasks by the specified date
        List<Task> filteredTasks = tasks.stream()
                .filter(task -> task.getDueDate() != null && task.getDueDate().equals(date))
                .collect(Collectors.toList());

        return filteredTasks.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public TaskResponse getTaskById(Long id) {
        Long userId = getCurrentUserId();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (!task.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        return convertToResponse(task);
    }

    public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
        Long userId = getCurrentUserId();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (!task.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        if (request.getTitle() != null)
            task.setTitle(request.getTitle());
        if (request.getDescription() != null)
            task.setDescription(request.getDescription());

        if (request.getPriority() != null) {
            try {
                task.setPriority(Task.Priority.valueOf(request.getPriority().toUpperCase().trim()));
            } catch (Exception e) {
                // Keep existing priority if invalid
            }
        }

        if (request.getCategory() != null)
            task.setCategory(request.getCategory());
        if (request.getDueDate() != null)
            task.setDueDate(request.getDueDate());
        if (request.getDueTime() != null)
            task.setDueTime(request.getDueTime());

        if (request.getRepeatType() != null) {
            try {
                task.setRepeatType(Task.RepeatType.valueOf(request.getRepeatType().toUpperCase().trim()));
            } catch (Exception e) {
                // Keep existing repeat type if invalid
            }
        }

        if (request.getHasReminder() != null)
            task.setHasReminder(request.getHasReminder());

        if (request.getStatus() != null) {
            try {
                task.setStatus(Task.Status.valueOf(request.getStatus().toUpperCase().trim()));
            } catch (Exception e) {
                // Keep existing status if invalid
            }
        }

        task.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskRepository.save(task);
        return convertToResponse(updatedTask);
    }

    public void deleteTask(Long id) {
        Long userId = getCurrentUserId();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (!task.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        taskRepository.delete(task);
    }

    public TaskResponse toggleTaskCompletion(Long id) {
        Long userId = getCurrentUserId();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        if (!task.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        // Toggle status
        task.setStatus(task.getStatus() == Task.Status.COMPLETED ? Task.Status.PENDING : Task.Status.COMPLETED);
        task.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskRepository.save(task);
        return convertToResponse(updatedTask);
    }

    private TaskResponse convertToResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getUserId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority().name(),
                task.getCategory(),
                task.getDueDate(),
                task.getDueTime(),
                task.getRepeatType().name(),
                task.getHasReminder(),
                task.getStatus().name(),
                task.getCreatedAt(),
                task.getUpdatedAt());
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName() != null) {
            try {
                // The subject in JWT token is the user ID
                return Long.parseLong(authentication.getName());
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid user ID in token");
            }
        }
        throw new RuntimeException("User not authenticated");
    }
}