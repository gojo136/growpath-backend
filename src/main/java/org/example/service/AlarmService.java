package org.example.service;

import org.example.entity.Alarm;
import org.example.repository.AlarmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AlarmService {

    @Autowired
    private AlarmRepository alarmRepository;

    public Alarm saveAlarm(Alarm alarm) {
        return alarmRepository.save(alarm);
    }

    public List<Alarm> getAlarmsByUserId(Long userId) {
        return alarmRepository.findByUserId(userId);
    }

    public void deleteAlarm(Long id) {
        alarmRepository.deleteById(id);
    }

    public Alarm updateAlarmStatus(Long id, Boolean enabled) {
        Alarm alarm = alarmRepository.findById(id).orElseThrow(() -> new RuntimeException("Alarm not found"));
        alarm.setEnabled(enabled);
        return alarmRepository.save(alarm);
    }
}
