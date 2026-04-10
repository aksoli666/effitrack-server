package com.effitrack.server.service;

import com.effitrack.server.handler.EquipmentMetricsHandler;
import com.effitrack.server.model.Equipment;
import com.effitrack.server.model.EquipmentLog;
import com.effitrack.server.model.EquipmentStatus;
import com.effitrack.server.model.User;
import com.effitrack.server.repository.EquipmentLogRepository;
import com.effitrack.server.repository.EquipmentRepository;
import com.effitrack.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.effitrack.server.constant.StringConst.ERROR_PREFIX_OBJ_NOT_FOUND;

@Service
public class EquipmentService {
    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private EquipmentLogRepository logRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EquipmentMetricsHandler metricsHandler;

    public Equipment saveEquipment(Equipment equipment) {
        if (equipment.getLastStatusChange() == null) {
            equipment.setLastStatusChange(LocalDateTime.now());
        }
        return equipmentRepository.save(equipment);
    }

    public List<Equipment> saveAll(List<Equipment> equipmentList) {
        LocalDateTime now = LocalDateTime.now();
        equipmentList.forEach(eq -> {
            if (eq.getLastStatusChange() == null) {
                eq.setLastStatusChange(now);
            }
        });
        return equipmentRepository.saveAll(equipmentList);
    }

    public List<Equipment> getAllEquipment() {
        List<Equipment> list = equipmentRepository.findAll();
        list.forEach(metricsHandler::calculateDynamicFields);
        return list;
    }

    public List<Equipment> getUserEquipmentWithStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(ERROR_PREFIX_OBJ_NOT_FOUND + userId));

        List<Equipment> userEquipment = user.getMonitoredEquipment();

        if (userEquipment == null) {
            return Collections.emptyList();
        }

        userEquipment.forEach(metricsHandler::calculateDynamicFields);
        return userEquipment;
    }

    public Optional<Equipment> findById(Long id) {
        Optional<Equipment> eq = equipmentRepository.findById(id);
        eq.ifPresent(metricsHandler::calculateDynamicFields);
        return eq;
    }

    public Optional<Equipment> findByInventoryNum(String inv) {
        Optional<Equipment> eq = equipmentRepository.findByInventoryNumber(inv);
        eq.ifPresent(metricsHandler::calculateDynamicFields);
        return eq;
    }

    @Transactional
    public void changeStatus(Long equipmentId, EquipmentStatus newStatus, String reason) {
        Optional<Equipment> equipmentOpt = equipmentRepository.findById(equipmentId);

        if (equipmentOpt.isPresent()) {
            Equipment equipment = equipmentOpt.get();
            LocalDateTime now = LocalDateTime.now();

            logRepository.findFirstByEquipmentIdAndEndTimeIsNullOrderByStartTimeDesc(equipmentId)
                    .ifPresent(currentLog -> {
                        currentLog.setEndTime(now);
                        logRepository.save(currentLog);
                    });

            EquipmentLog newLog = new EquipmentLog();
            newLog.setEquipment(equipment);
            newLog.setStatus(newStatus);
            newLog.setReason(reason);
            newLog.setStartTime(now);
            logRepository.save(newLog);

            equipment.setStatus(newStatus);
            equipment.setActiveAction(reason);
            equipment.setLastStatusChange(now);

            equipmentRepository.save(equipment);
        }
    }
}
