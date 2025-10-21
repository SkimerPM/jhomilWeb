package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.*;
import com.jhomilmotors.jhomilwebapp.entity.User;
import com.jhomilmotors.jhomilwebapp.entity.RefreshToken;
import com.jhomilmotors.jhomilwebapp.service.UserService;
import com.jhomilmotors.jhomilwebapp.service.RefreshTokenService;
import com.jhomilmotors.jhomilwebapp.security.JwtUtil;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthController(UserService userService, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        User user = userService.validateLogin(loginDTO.getEmail(), loginDTO.getPassword());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> claims = Map.of("role", user.getRol().getNombre().name());
        String accessToken = jwtUtil.generateToken(claims, user.getEmail());

        RefreshToken refreshTokenObj = refreshTokenService.createRefreshToken(user);

        AuthResponseDTO response = new AuthResponseDTO(
                accessToken,
                refreshTokenObj.getToken(),
                user.getEmail(),
                user.getRol().getNombre().name()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody RefreshRequestDTO request) {
        var refreshOpt = refreshTokenService.findByToken(request.getRefreshToken());
        if (refreshOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        RefreshToken refreshToken = refreshOpt.get();

        if (refreshToken.getExpires().isBefore(java.time.LocalDateTime.now()) || refreshToken.isRevoked()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = refreshToken.getUser();
        Map<String, Object> claims = Map.of("role", user.getRol().getNombre().name());
        String newAccessToken = jwtUtil.generateToken(claims, user.getEmail());

        AuthResponseDTO response = new AuthResponseDTO(
                newAccessToken,
                refreshToken.getToken(),
                user.getEmail(),
                user.getRol().getNombre().name()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequestDTO request) {
        var refreshOpt = refreshTokenService.findByToken(request.getRefreshToken());
        refreshOpt.ifPresent(refreshTokenService::revokeRefreshToken);
        return ResponseEntity.ok(Map.of("message", "Logout exitoso"));
    }
}
