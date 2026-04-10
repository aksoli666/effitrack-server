package com.effitrack.server.handler;

import com.effitrack.server.model.Equipment;
import com.effitrack.server.model.EquipmentStatus;
import com.effitrack.server.service.EquipmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.effitrack.server.constant.StringConst.ALERT_TOPIC;
import static com.effitrack.server.constant.StringConst.INPUT_CHANNEL;
import static com.effitrack.server.constant.StringConst.MSG_ERROR_PREFIX;
import static com.effitrack.server.constant.StringConst.MSG_NOT_FOUND_TEMPLATE;
import static com.effitrack.server.constant.StringConst.MSG_SUCCESS_TEMPLATE;

@Service
public class MqttHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private EquipmentService equipmentService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @ServiceActivator(inputChannel = INPUT_CHANNEL)
    public void handleMessage(Message<String> message) {
        String payload = message.getPayload();

        try {
            MqttPayload data = objectMapper.readValue(payload, MqttPayload.class);
            Optional<Equipment> equipmentOpt = equipmentService.findByInventoryNum(data.inventoryNumber);

            if (equipmentOpt.isPresent()) {
                Long id = equipmentOpt.get().getId();
                EquipmentStatus newStatus = EquipmentStatus.valueOf(data.status);
                equipmentService.changeStatus(id, newStatus, data.reason);

                String successMsg = MSG_SUCCESS_TEMPLATE + data.inventoryNumber;
                messagingTemplate.convertAndSend(ALERT_TOPIC, successMsg);

            } else {
                String errorMsg = MSG_NOT_FOUND_TEMPLATE + data.inventoryNumber;
                messagingTemplate.convertAndSend(ALERT_TOPIC, errorMsg);
            }

        } catch (Exception e) {
            System.err.println(MSG_ERROR_PREFIX + e.getMessage());
        }
    }

    @Data
    private static class MqttPayload {
        public String inventoryNumber;
        public String status;
        public String reason;
    }
}
