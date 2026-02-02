package org.example.controller;

import org.example.entity.Alarm;
import org.example.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alarms")
public class AlarmController {

    @Autowired
    private AlarmService alarmService;

    @PostMapping
    public ResponseEntity<Alarm> saveAlarm(@RequestBody Alarm alarm) {
        return ResponseEntity.ok(alarmService.saveAlarm(alarm));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Alarm>> getAlarmsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(alarmService.getAlarmsByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlarm(@PathVariable Long id) {
        alarmService.deleteAlarm(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Alarm> updateAlarmStatus(@PathVariable Long id, @RequestParam Boolean enabled) {
        return ResponseEntity.ok(alarmService.updateAlarmStatus(id, enabled));
    }
}
