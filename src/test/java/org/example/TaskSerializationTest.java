package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.JacksonConfig;
import org.example.dto.request.CreateTaskRequest;
import org.example.dto.response.TaskResponse;
import org.example.entity.Task;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TaskSerializationTest {

    @Test
    public void testTaskSerialization() throws Exception {
        // Create Jackson ObjectMapper with our configuration
        JacksonConfig config = new JacksonConfig();
        ObjectMapper mapper = config.objectMapper();

        // Test CreateTaskRequest serialization
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Test Task");
        request.setDescription("Test Description");
        request.setPriority("HIGH");
        request.setCategory("Work");
        request.setDueDate(LocalDate.now().plusDays(1));
        request.setDueTime(LocalTime.of(14, 30));
        request.setRepeatType("DAILY");
        request.setHasReminder(true);

        // Serialize to JSON
        String json = mapper.writeValueAsString(request);
        System.out.println("CreateTaskRequest JSON: " + json);
        
        // Deserialize back
        CreateTaskRequest deserializedRequest = mapper.readValue(json, CreateTaskRequest.class);
        assertEquals(request.getTitle(), deserializedRequest.getTitle());
        assertEquals(request.getDueDate(), deserializedRequest.getDueDate());
        assertEquals(request.getDueTime(), deserializedRequest.getDueTime());

        // Test TaskResponse serialization
        TaskResponse response = new TaskResponse(
                1L,
                100L,
                "Test Task",
                "Test Description",
                "HIGH",
                "Work",
                LocalDate.now().plusDays(1),
                LocalTime.of(14, 30),
                "DAILY",
                true,
                "PENDING",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Serialize TaskResponse
        String responseJson = mapper.writeValueAsString(response);
        System.out.println("TaskResponse JSON: " + responseJson);
        
        // Deserialize back
        TaskResponse deserializedResponse = mapper.readValue(responseJson, TaskResponse.class);
        assertEquals(response.getTitle(), deserializedResponse.getTitle());
        assertEquals(response.getDueDate(), deserializedResponse.getDueDate());
        assertEquals(response.getDueTime(), deserializedResponse.getDueTime());

        // Test Task entity serialization
        Task task = new Task();
        task.setId(1L);
        task.setUserId(100L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setPriority(Task.Priority.HIGH);
        task.setCategory("Work");
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setDueTime(LocalTime.of(14, 30));
        task.setRepeatType(Task.RepeatType.DAILY);
        task.setHasReminder(true);
        task.setStatus(Task.Status.PENDING);

        // Serialize Task entity
        String taskJson = mapper.writeValueAsString(task);
        System.out.println("Task Entity JSON: " + taskJson);

        System.out.println("✅ All serialization tests passed!");
    }
}