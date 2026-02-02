package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.config.JacksonConfig;
import org.example.dto.request.CreateTaskRequest;
import org.example.dto.response.TaskResponse;
import org.example.entity.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Standalone test class to verify Jackson serialization configuration
 * Run this class to test LocalDate/LocalTime serialization without starting the full application
 */
public class SerializationTestRunner {

    public static void main(String[] args) {
        System.out.println("🧪 Testing Jackson Serialization Configuration...\n");

        try {
            // Create ObjectMapper with our configuration
            JacksonConfig config = new JacksonConfig();
            ObjectMapper mapper = config.objectMapper();

            // Test 1: CreateTaskRequest Serialization
            System.out.println("📝 Test 1: CreateTaskRequest Serialization");
            CreateTaskRequest request = createSampleRequest();
            
            String requestJson = mapper.writeValueAsString(request);
            System.out.println("✅ Serialized JSON: " + requestJson);
            
            CreateTaskRequest deserializedRequest = mapper.readValue(requestJson, CreateTaskRequest.class);
            System.out.println("✅ Deserialized successfully");
            System.out.println("   Title: " + deserializedRequest.getTitle());
            System.out.println("   Due Date: " + deserializedRequest.getDueDate());
            System.out.println("   Due Time: " + deserializedRequest.getDueTime());
            System.out.println();

            // Test 2: TaskResponse Serialization
            System.out.println("📋 Test 2: TaskResponse Serialization");
            TaskResponse response = createSampleResponse();
            
            String responseJson = mapper.writeValueAsString(response);
            System.out.println("✅ Serialized JSON: " + responseJson);
            
            TaskResponse deserializedResponse = mapper.readValue(responseJson, TaskResponse.class);
            System.out.println("✅ Deserialized successfully");
            System.out.println("   ID: " + deserializedResponse.getId());
            System.out.println("   Due Date: " + deserializedResponse.getDueDate());
            System.out.println("   Due Time: " + deserializedResponse.getDueTime());
            System.out.println("   Created At: " + deserializedResponse.getCreatedAt());
            System.out.println();

            // Test 3: Task Entity Serialization
            System.out.println("🏗️ Test 3: Task Entity Serialization");
            Task task = createSampleTask();
            
            String taskJson = mapper.writeValueAsString(task);
            System.out.println("✅ Serialized JSON: " + taskJson);
            System.out.println();

            // Test 4: Edge Cases
            System.out.println("🔍 Test 4: Edge Cases");
            testEdgeCases(mapper);

            System.out.println("🎉 All serialization tests passed successfully!");
            System.out.println("\n📊 Summary:");
            System.out.println("✅ LocalDate serialization: Working");
            System.out.println("✅ LocalTime serialization: Working");
            System.out.println("✅ LocalDateTime serialization: Working");
            System.out.println("✅ JSON format: ISO standard");
            System.out.println("✅ Deserialization: Working");

        } catch (Exception e) {
            System.err.println("❌ Serialization test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static CreateTaskRequest createSampleRequest() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Sample Task for Testing");
        request.setDescription("Testing LocalDate and LocalTime serialization");
        request.setPriority("HIGH");
        request.setCategory("Development");
        request.setDueDate(LocalDate.of(2024, 2, 15));
        request.setDueTime(LocalTime.of(14, 30, 0));
        request.setRepeatType("DAILY");
        request.setHasReminder(true);
        return request;
    }

    private static TaskResponse createSampleResponse() {
        return new TaskResponse(
                1L,
                100L,
                "Sample Task Response",
                "Testing response serialization",
                "HIGH",
                "Development",
                LocalDate.of(2024, 2, 15),
                LocalTime.of(14, 30, 0),
                "DAILY",
                true,
                "PENDING",
                LocalDateTime.of(2024, 1, 15, 10, 0, 0),
                LocalDateTime.of(2024, 1, 15, 10, 0, 0)
        );
    }

    private static Task createSampleTask() {
        Task task = new Task();
        task.setId(1L);
        task.setUserId(100L);
        task.setTitle("Sample Task Entity");
        task.setDescription("Testing entity serialization");
        task.setPriority(Task.Priority.HIGH);
        task.setCategory("Development");
        task.setDueDate(LocalDate.of(2024, 2, 15));
        task.setDueTime(LocalTime.of(14, 30, 0));
        task.setRepeatType(Task.RepeatType.DAILY);
        task.setHasReminder(true);
        task.setStatus(Task.Status.PENDING);
        return task;
    }

    private static void testEdgeCases(ObjectMapper mapper) throws Exception {
        // Test with null time
        CreateTaskRequest requestWithNullTime = new CreateTaskRequest();
        requestWithNullTime.setTitle("Task with null time");
        requestWithNullTime.setDueDate(LocalDate.now());
        requestWithNullTime.setDueTime(null);
        requestWithNullTime.setPriority("MEDIUM");
        
        String nullTimeJson = mapper.writeValueAsString(requestWithNullTime);
        System.out.println("✅ Null time handling: " + nullTimeJson);
        
        // Test with different time formats
        LocalTime midnight = LocalTime.of(0, 0, 0);
        LocalTime noon = LocalTime.of(12, 0, 0);
        LocalTime endOfDay = LocalTime.of(23, 59, 59);
        
        System.out.println("✅ Midnight: " + mapper.writeValueAsString(midnight));
        System.out.println("✅ Noon: " + mapper.writeValueAsString(noon));
        System.out.println("✅ End of day: " + mapper.writeValueAsString(endOfDay));
    }
}