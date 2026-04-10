package com.effitrack.server.repository;

import com.effitrack.server.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    Optional<Equipment> findByInventoryNumber(String inventoryNumber);
}
