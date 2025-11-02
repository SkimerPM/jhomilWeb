package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.AdminRegistrationDTO;
import com.jhomilmotors.jhomilwebapp.dto.AdminUserFormUpdate;
import com.jhomilmotors.jhomilwebapp.dto.AdminUserListResponseDTO;
import com.jhomilmotors.jhomilwebapp.entity.User;
import com.jhomilmotors.jhomilwebapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint protegido por Spring Security usando @PreAuthorize
    @PostMapping("/register-admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<User> registerAdmin(@Valid @RequestBody AdminRegistrationDTO dto) {
        User newAdmin = userService.registerAdmin(dto);
        return ResponseEntity.ok(newAdmin);
    }

    //metodo para panel adminsitrativo -> LISTAR USUARIOS
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<AdminUserListResponseDTO> listarUsuarios(Pageable pageable) {
        return userService.listAll(pageable);
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> actualizarUsuario(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserFormUpdate dto) {
        userService.updateUserFromAdmin(id, dto);
        return ResponseEntity.ok().build();
    }
}
