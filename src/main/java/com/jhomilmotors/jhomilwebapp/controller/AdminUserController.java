package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.AdminRegistrationDTO;
import com.jhomilmotors.jhomilwebapp.entity.User;
import com.jhomilmotors.jhomilwebapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint protegido por Spring Security usando @PreAuthorize
    @PostMapping("/register-admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> registerAdmin(@Valid @RequestBody AdminRegistrationDTO dto) {
        User newAdmin = userService.registerAdmin(dto);
        return ResponseEntity.ok(newAdmin);
    }
}
