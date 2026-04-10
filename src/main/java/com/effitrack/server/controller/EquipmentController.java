package com.effitrack.server.controller;

import com.effitrack.server.constant.StringConst;
import com.effitrack.server.model.Equipment;
import com.effitrack.server.model.dto.StatusRequest;
import com.effitrack.server.service.EquipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@Tag(name = StringConst.TAG_EQUIPMENT, description = StringConst.TAG_EQUIPMENT_DESC)
@RequestMapping(StringConst.BASE_URL_EQUIPMENT)
public class EquipmentController {
    @Autowired
    private EquipmentService service;

    @Operation(summary = StringConst.OP_EQ_ALL_SUM)
    @GetMapping
    public List<Equipment> getAll() {
        return service.getAllEquipment();
    }

    @Operation(summary = StringConst.OP_EQ_CREATE_SUM)
    @PostMapping
    public Equipment create(@RequestBody Equipment equipment) {
        return service.saveEquipment(equipment);
    }

    @Operation(summary = StringConst.OP_EQ_BATCH_CREATE_SUM)
    @PostMapping(StringConst.ENDPOINT_EQUIPMENT_BATCH)
    @PreAuthorize(StringConst.AUTHORITY_MASTER)
    public ResponseEntity<List<Equipment>> createBatch(@RequestBody List<Equipment> equipmentList) {
        return ResponseEntity.ok(service.saveAll(equipmentList));
    }

    @Operation(summary = StringConst.OP_EQ_SEARCH_SUM, description = StringConst.OP_EQ_SEARCH_DESC)
    @GetMapping(StringConst.ENDPOINT_SEARCH)
    public ResponseEntity<Equipment> getByInventoryNumber(@RequestParam(StringConst.PARAM_INVENTORY) String inv) {
        return service.findByInventoryNum(inv)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = StringConst.OP_EQ_ID_SUM, description = StringConst.OP_EQ_ID_DESC)
    @GetMapping(StringConst.ENDPOINT_BY_ID)
    public ResponseEntity<Equipment> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = StringConst.OP_EQ_STATUS_SUM, description = StringConst.OP_EQ_STATUS_DESC)
    @PostMapping(StringConst.ENDPOINT_STATUS_UPDATE)
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        service.changeStatus(id, request.getStatus(), request.getReason());
        return ResponseEntity.ok(StringConst.SUCCESS_STATUS_UPDATED);
    }
}
