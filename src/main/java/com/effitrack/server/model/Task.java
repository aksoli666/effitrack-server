package com.effitrack.server.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(name = "drawing_url")
    private String drawingUrl;

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    @JsonIgnoreProperties({"tasks", "hibernateLazyInitializer", "handler"})
    private Equipment equipment;

    @ManyToOne
    @JoinColumn(name = "assignee_id", nullable = false)
    @JsonIgnoreProperties("monitoredEquipment")
    private User assignee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.TODO;

    @Column(name = "planned_date", nullable = false)
    private LocalDateTime plannedDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "estimated_minutes", nullable = false)
    private int estimatedMinutes;

    @Column(name = "actual_minutes")
    private int actualMinutes;

    @Column(name = "operator_comment")
    private String operatorComment;
}
