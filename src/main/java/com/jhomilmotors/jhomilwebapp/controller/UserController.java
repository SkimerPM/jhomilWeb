package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.UserProfileDTO;
import com.jhomilmotors.jhomilwebapp.dto.UserRegistrationDTO;
import com.jhomilmotors.jhomilwebapp.entity.User;
import com.jhomilmotors.jhomilwebapp.repository.UserRepository;
import com.jhomilmotors.jhomilwebapp.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();  // Aquí tendrás el email correctamente
        User user = userService.findByEmail(email);
        UserProfileDTO dto = new UserProfileDTO(
                user.getNombre(), user.getEmail(), user.getRol().getNombre().name()
        );
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<User> registerUser(@RequestBody @Valid UserRegistrationDTO request) {
        User creado = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public List<UserProfileDTO> listarUsuarios() {
        // Puedes usar directamente el entity User, pero ideal es mapearlo a un DTO
        return userService.listAll()
                .stream()
                .map(u -> new UserProfileDTO(
                        u.getNombre(),
                        u.getEmail(),
                        u.getRol().getNombre().name() // Ajusta según tu modelo
                ))
                .toList();
    }


    //usuarios por id:
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDTO> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        UserProfileDTO dto = new UserProfileDTO(
                user.getNombre(), user.getEmail(), user.getRol().getNombre().name()
        );
        return ResponseEntity.ok(dto);
    }

    //update usuario
    @PutMapping("/{id}")
    public ResponseEntity<User> actualizarUsuario(@PathVariable Long id, @RequestBody UserProfileDTO datos) {
        User actualizado = userService.actualizar(id, datos);
        return ResponseEntity.ok(actualizado);
    }

    //activar o desactivar usuario:
    @PatchMapping("/{id}/estado")
    public ResponseEntity<User> actualizarEstado(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        boolean activo = body.getOrDefault("activo", true);
        User actualizado = userService.actualizarEstado(id, activo);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


}
