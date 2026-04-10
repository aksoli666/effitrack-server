package com.effitrack.server.repository;

import com.effitrack.server.model.EquipmentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EquipmentLogRepository extends JpaRepository<EquipmentLog, Long> {
    List<EquipmentLog> findByEquipmentIdAndEndTimeGreaterThanEqual(Long equipmentId, LocalDateTime startOfDay);
    Optional<EquipmentLog> findFirstByEquipmentIdAndEndTimeIsNullOrderByStartTimeDesc(Long equipmentId);
}
