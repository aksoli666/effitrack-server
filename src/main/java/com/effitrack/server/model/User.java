package com.effitrack.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "table_number", unique = true, nullable = false)
    private String tableNumber;

    @Column(name = "profession")
    private String profession;

    @Column(name = "pin_code", nullable = false)
    private String pinCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.OPERATOR;

    @Column(name = "shop_number")
    private String shopNumber;

    @Column(name = "email")
    private String email;

    @ManyToOne
    @JoinColumn(name = "master_id")
    @JsonIgnore
    private User master;

    @ManyToMany
    @JoinTable(
            name = "user_equipment",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "equipment_id")
    )
    private List<Equipment> monitoredEquipment;
}
