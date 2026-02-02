package org.example.repository;

import org.example.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByUserIdOrderByDueDateAscDueTimeAsc(Long userId);
    
    List<Task> findByUserIdAndStatusOrderByDueDateAscDueTimeAsc(Long userId, Task.Status status);
    
    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.dueDate = :date ORDER BY t.dueTime ASC")
    List<Task> findByUserIdAndDueDate(@Param("userId") Long userId, @Param("date") LocalDate date);
    
    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.dueDate BETWEEN :startDate AND :endDate ORDER BY t.dueDate ASC, t.dueTime ASC")
    List<Task> findByUserIdAndDueDateBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    List<Task> findByUserIdAndCategoryOrderByDueDateAscDueTimeAsc(Long userId, String category);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId AND t.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Task.Status status);
}