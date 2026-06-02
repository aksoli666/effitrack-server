package com.effitrack.server.service;

import com.effitrack.server.handler.EquipmentMetricsHandler;
import com.effitrack.server.model.Equipment;
import com.effitrack.server.model.EquipmentLog;
import com.effitrack.server.model.EquipmentStatus;
import com.effitrack.server.model.User;
import com.effitrack.server.model.dto.EquipmentUpdateRequest;
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
    private static final String AI_NOT_NEEDED_MSG = "Обладнання працює справно. Аналіз не потрібен.";
    private static final String LOG_NOT_FOUND_MSG = "Лог інциденту не знайдено.";
    private static final String DEFAULT_SYSTEM_REASON = "Причина не вказана автоматикою.";
    private static final String DEFAULT_OPERATOR_COMMENT = "Оператор ще не залишив текстовий коментар.";
    private static final String PROMPT_CONTEXT_FORMAT = "Системний код/причина: %s. Додаткові деталі від оператора: %s.";
    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private EquipmentLogRepository logRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EquipmentMetricsHandler metricsHandler;
    @Autowired
    private AiAnalysisService aiAnalysisService;

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
        list.forEach(eq -> {
            metricsHandler.calculateDynamicFields(eq);
            enrichWithActiveLogData(eq);
        });
        return list;
    }

    public List<Equipment> getUserEquipmentWithStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(ERROR_PREFIX_OBJ_NOT_FOUND + userId));

        List<Equipment> userEquipment = user.getMonitoredEquipment();

        if (userEquipment == null) {
            return Collections.emptyList();
        }

        userEquipment.forEach(eq -> {
            metricsHandler.calculateDynamicFields(eq);
            enrichWithActiveLogData(eq);
        });
        return userEquipment;
    }

    public Optional<Equipment> findById(Long id) {
        Optional<Equipment> eq = equipmentRepository.findById(id);
        eq.ifPresent(e -> {
            metricsHandler.calculateDynamicFields(e);
            enrichWithActiveLogData(e);
        });
        return eq;
    }

    public Optional<Equipment> findByInventoryNum(String inv) {
        Optional<Equipment> eq = equipmentRepository.findByInventoryNumber(inv);
        eq.ifPresent(e -> {
            metricsHandler.calculateDynamicFields(e);
            enrichWithActiveLogData(e);
        });
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

    @Transactional
    public Optional<Equipment> updateEquipmentData(Long id, EquipmentUpdateRequest request) {
        return equipmentRepository.findById(id).map(equipment -> {
            logRepository.findFirstByEquipmentIdAndEndTimeIsNullOrderByStartTimeDesc(id)
                    .ifPresent(activeLog -> {
                        activeLog.setOperatorComment(request.getOperatorComment());
                        logRepository.save(activeLog);
                    });

            equipment.setOperatorComment(request.getOperatorComment());
            metricsHandler.calculateDynamicFields(equipment);
            return equipmentRepository.save(equipment);
        });
    }

    @Transactional
    public Optional<Equipment> generateAndSaveAiAnalysis(Long id) {
        return equipmentRepository.findById(id).map(equipment -> {
            if (equipment.getStatus() == EquipmentStatus.RUNNING) {
                equipment.setAiAnalysis(AI_NOT_NEEDED_MSG);
                return equipmentRepository.save(equipment);
            }

            EquipmentLog activeLog = logRepository
                    .findFirstByEquipmentIdAndEndTimeIsNullOrderByStartTimeDesc(id)
                    .orElse(null);

            if (activeLog == null) {
                equipment.setAiAnalysis(LOG_NOT_FOUND_MSG);
                return equipmentRepository.save(equipment);
            }

            String systemReason = activeLog.getReason() != null ? activeLog.getReason() : DEFAULT_SYSTEM_REASON;
            String operatorText = activeLog.getOperatorComment() != null ? activeLog.getOperatorComment() : DEFAULT_OPERATOR_COMMENT;

            String fullFaultContext = String.format(PROMPT_CONTEXT_FORMAT, systemReason, operatorText);

            String freshAnalysis = aiAnalysisService.generateFixAnalysis(equipment.getName(), fullFaultContext);

            activeLog.setAiAnalysis(freshAnalysis);
            logRepository.save(activeLog);

            equipment.setAiAnalysis(freshAnalysis);
            metricsHandler.calculateDynamicFields(equipment);
            return equipmentRepository.save(equipment);
        });
    }

    private void enrichWithActiveLogData(Equipment equipment) {
        if (equipment.getStatus() != EquipmentStatus.RUNNING) {
            logRepository.findFirstByEquipmentIdAndEndTimeIsNullOrderByStartTimeDesc(equipment.getId())
                    .ifPresent(activeLog -> {
                        equipment.setOperatorComment(activeLog.getOperatorComment());
                        equipment.setAiAnalysis(activeLog.getAiAnalysis());
                    });
        }
    }
}
