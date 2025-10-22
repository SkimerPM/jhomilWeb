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
}
