package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.CustomerProfileDTO;
import com.jhomilmotors.jhomilwebapp.dto.UpdateProfileCustomerDTO;
import com.jhomilmotors.jhomilwebapp.dto.UserProfileDTO;
import com.jhomilmotors.jhomilwebapp.dto.UserRegistrationDTO;
import com.jhomilmotors.jhomilwebapp.entity.User;
import com.jhomilmotors.jhomilwebapp.repository.UserRepository;
import com.jhomilmotors.jhomilwebapp.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<CustomerProfileDTO> getCurrentUser(Authentication authentication) {
        User user = userService.getUserFromAuthentication(authentication);
        CustomerProfileDTO dto = new CustomerProfileDTO(
                user.getId(),
                user.getNombre(),
                user.getApellido(),
                user.getEmail(),
                user.getTelefono(),
                user.getDireccion()
        );
        return ResponseEntity.ok(dto);
    }


    @PostMapping
    public ResponseEntity<User> registerUser(@RequestBody @Valid UserRegistrationDTO request) {
        User creado = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    //usuarios por id:
    //creo que este metodo no se usa, pero lo dejo  por si despues sale error en algo ya lo habilitamos.
//    @GetMapping("/{id}")
//    public ResponseEntity<UserProfileDTO> getUserById(@PathVariable Long id) {
//        User user = userService.findById(id);
//        UserProfileDTO dto = new UserProfileDTO(
//                user.getNombre(), user.getEmail(), user.getRol().getNombre().name()
//        );
//        return ResponseEntity.ok(dto);
//    }

    //update usuario
    @PutMapping("/me")
    public ResponseEntity<CustomerProfileDTO> actualizarPerfilPropio(
            @RequestBody @Valid UpdateProfileCustomerDTO request,
            Authentication authentication) { // RECIBIMOS EL OBJETO Authentication
        // Obtenemos el ID del usuario de forma segura
        Long userId = userService.getUserIdFromAuthentication(authentication);
        // Llamamos al mismo servicio de antes, pero con el ID seguro
        CustomerProfileDTO responseDTO = userService.actualizar(userId, request);
        return ResponseEntity.ok(responseDTO);
    }

    //activar o desactivar usuario:
    @PatchMapping("/{id}/estado")
    public ResponseEntity<User> actualizarEstado(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        boolean activo = body.getOrDefault("activo", true);
        User actualizado = userService.actualizarEstado(id, activo);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    //creo que este metodo deberia estar en adminUserControler...
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
