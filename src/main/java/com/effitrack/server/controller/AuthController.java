package com.effitrack.server.controller;

import com.effitrack.server.constant.StringConst;
import com.effitrack.server.model.User;
import com.effitrack.server.model.dto.LoginRequest;
import com.effitrack.server.security.JwtCore;
import com.effitrack.server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = StringConst.TAG_AUTH, description = StringConst.TAG_AUTH_DESC)
@RequestMapping(StringConst.BASE_URL_AUTH)
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtCore jwtCore;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Operation(summary = StringConst.OP_REGISTER_SUM, description = StringConst.OP_REGISTER_DESC)
    @PreAuthorize(StringConst.AUTHORITY_MASTER)
    @PostMapping(StringConst.ENDPOINT_REGISTER)
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userService.existsByTableNumber(user.getTableNumber())) {
            return ResponseEntity.badRequest().body(StringConst.ERROR_TABLE_TAKEN);
        }
        userService.createUser(user);
        return ResponseEntity.ok(StringConst.SUCCESS_REGISTER);
    }

    @Operation(summary = StringConst.OP_REGISTER_BATCH_SUM)
    @PostMapping(StringConst.ENDPOINT_REGISTER_BATCH)
    @PreAuthorize(StringConst.AUTHORITY_MASTER)
    public ResponseEntity<?> registerBatch(@RequestBody List<User> users) {
        for (User user : users) {
            if (!userService.existsByTableNumber(user.getTableNumber())) {
                userService.createUser(user);
            }
        }
        return ResponseEntity.ok(StringConst.SUCCESS_BATCH_REGISTER);
    }

    @Operation(summary = StringConst.OP_LOGIN_SUM, description = StringConst.OP_LOGIN_DESC)
    @PostMapping(StringConst.ENDPOINT_LOGIN)
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getTableNumber(), loginRequest.getPinCode())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);
        return ResponseEntity.ok(jwt);
    }
}
