package com.effitrack.server.handler;

import com.effitrack.server.model.Equipment;
import com.effitrack.server.model.EquipmentLog;
import com.effitrack.server.repository.EquipmentLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class EquipmentMetricsHandler {
    private static final double SECONDS_IN_MINUTE = 60.0;

    @Autowired
    private EquipmentLogRepository logRepository;

    public void calculateDynamicFields(Equipment equipment) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        if (equipment.getLastStatusChange() != null) {
            long seconds = Duration.between(equipment.getLastStatusChange(), now).getSeconds();
            equipment.setCurrentStatusDuration((int) Math.round(seconds / SECONDS_IN_MINUTE));
        } else {
            equipment.setCurrentStatusDuration(0);
        }

        List<EquipmentLog> todayLogs = logRepository.findByEquipmentIdAndEndTimeGreaterThanEqual(equipment.getId(), startOfDay);

        long totalWorkSeconds = 0;
        long totalDowntimeSeconds = 0;
        long totalSetupSeconds = 0;

        for (EquipmentLog log : todayLogs) {
            if (log.getEndTime() != null) {
                LocalDateTime effectiveLogStart = log.getStartTime().isBefore(startOfDay)
                        ? startOfDay
                        : log.getStartTime();

                if (log.getEndTime().isBefore(effectiveLogStart)) continue;

                long duration = Duration.between(effectiveLogStart, log.getEndTime()).getSeconds();

                switch (log.getStatus()) {
                    case RUNNING -> totalWorkSeconds += duration;
                    case DOWNTIME -> totalDowntimeSeconds += duration;
                    case SETUP -> totalSetupSeconds += duration;
                }
            }
        }

        if (equipment.getLastStatusChange() != null) {
            LocalDateTime effectiveStart = equipment.getLastStatusChange().isBefore(startOfDay)
                    ? startOfDay
                    : equipment.getLastStatusChange();

            long currentDurationSeconds = Duration.between(effectiveStart, now).getSeconds();

            switch (equipment.getStatus()) {
                case RUNNING -> totalWorkSeconds += currentDurationSeconds;
                case DOWNTIME -> totalDowntimeSeconds += currentDurationSeconds;
                case SETUP -> totalSetupSeconds += currentDurationSeconds;
            }
        }

        equipment.setWorkTimeTodayMinutes((int) Math.round(totalWorkSeconds / SECONDS_IN_MINUTE));
        equipment.setDowntimeTodayMinutes((int) Math.round(totalDowntimeSeconds / SECONDS_IN_MINUTE));
        equipment.setSetupTodayMinutes((int) Math.round(totalSetupSeconds / SECONDS_IN_MINUTE));
    }
}
