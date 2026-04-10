package com.effitrack.server.controller;

import com.effitrack.server.constant.StringConst;
import com.effitrack.server.model.Equipment;
import com.effitrack.server.model.User;
import com.effitrack.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@Tag(name = StringConst.TAG_USERS, description = StringConst.TAG_USERS_DESC)
@RequestMapping(StringConst.BASE_URL_USERS)
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = StringConst.OP_USER_MY_EQ_SUM, description = StringConst.OP_USER_MY_EQ_DESC)
    @GetMapping(StringConst.ENDPOINT_USER_EQUIPMENT)
    public List<Equipment> getMyEquipment(@PathVariable(StringConst.VAR_USER_ID) Long userId) {
        return userService.getUserEquipment(userId);
    }

    @Operation(summary = StringConst.OP_USER_ADD_EQ_SUM, description = StringConst.OP_USER_ADD_EQ_DESC)
    @PostMapping(StringConst.ENDPOINT_USER_EQUIPMENT)
    public ResponseEntity<String> addEquipment(
            @PathVariable(StringConst.VAR_USER_ID) Long userId,
            @RequestParam(StringConst.PARAM_INVENTORY) String inv) {

        boolean success = userService.addEquipmentToUser(userId, inv);

        if (success) {
            return ResponseEntity.ok(StringConst.SUCCESS_EQUIPMENT_ADDED);
        } else {
            return ResponseEntity.badRequest().body(StringConst.ERROR_NOT_FOUND);
        }
    }

    @Operation(summary = StringConst.OP_USER_PROFILE_SUM, description = StringConst.OP_USER_PROFILE_DESC)
    @GetMapping(StringConst.ENDPOINT_USER_PROFILE)
    public ResponseEntity<User> getUserProfile(@PathVariable Long userId) {
        return userService.findById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
