package com.effitrack.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "equipment")
@Data
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "inventory_number", unique = true, nullable = false)
    private String inventoryNumber;

    @Column(name = "shop_number", nullable = false)
    private String shopNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentStatus status = EquipmentStatus.RUNNING;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "last_maintenance")
    private LocalDate lastMaintenance;

    @Column(name = "next_maintenance")
    private LocalDate nextMaintenance;

    @Column(name = "active_action")
    private String activeAction;

    @Column(name = "last_status_change")
    private LocalDateTime lastStatusChange;

    @Transient
    private int workTimeTodayMinutes;

    @Transient
    private int downtimeTodayMinutes;

    @Transient
    private int setupTodayMinutes;

    @Transient
    private int currentStatusDuration;

    @JsonIgnore
    @OneToMany(mappedBy = "equipment", fetch = FetchType.LAZY)
    private List<Task> tasks;
}
