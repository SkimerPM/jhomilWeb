package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.*;
import com.jhomilmotors.jhomilwebapp.entity.User;
import com.jhomilmotors.jhomilwebapp.entity.RefreshToken;
import com.jhomilmotors.jhomilwebapp.service.UserService;
import com.jhomilmotors.jhomilwebapp.service.RefreshTokenService;
import com.jhomilmotors.jhomilwebapp.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        User user = userService.validateLogin(loginDTO.getEmail(), loginDTO.getPassword());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Use googleId si existe, sino email
        String subject = (user.getGoogleId() != null && !user.getGoogleId().isBlank())
                ? user.getGoogleId()
                : user.getEmail();

        Map<String, Object> claims = Map.of("role", user.getRol().getNombre().name());
        String accessToken = jwtUtil.generateToken(claims, subject);

        RefreshToken refreshTokenObj = refreshTokenService.createRefreshToken(user);

        Cookie refreshCookie = new Cookie("refreshToken", refreshTokenObj.getToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/api/auth/refresh");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 días
        response.addCookie(refreshCookie);

        AuthResponseDTO dto = new AuthResponseDTO(
                accessToken,
                user.getEmail(),
                user.getRol().getNombre().name()
        );
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var refreshOpt = refreshTokenService.findByToken(refreshToken);
        if (refreshOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        RefreshToken refreshTokenObj = refreshOpt.get();

        if (refreshTokenObj.getExpires().isBefore(java.time.LocalDateTime.now()) || refreshTokenObj.isRevoked()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = refreshTokenObj.getUser();

        // Igual: usa googleId si existe
        String subject = (user.getGoogleId() != null && !user.getGoogleId().isBlank())
                ? user.getGoogleId()
                : user.getEmail();

        Map<String, Object> claims = Map.of("role", user.getRol().getNombre().name());
        String newAccessToken = jwtUtil.generateToken(claims, subject);

        AuthResponseDTO dto = new AuthResponseDTO(
                newAccessToken,
                user.getEmail(),
                user.getRol().getNombre().name()
        );
        return ResponseEntity.ok(dto);
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        if (refreshToken != null) {
            var refreshOpt = refreshTokenService.findByToken(refreshToken);
            refreshOpt.ifPresent(refreshTokenService::revokeRefreshToken);
        }
        // Borra la cookie del navegador
        Cookie expiredCookie = new Cookie("refreshToken", null);
        expiredCookie.setHttpOnly(true);
        expiredCookie.setPath("/api/auth/refresh");
        expiredCookie.setMaxAge(0); // elimina
        response.addCookie(expiredCookie);

        return ResponseEntity.ok(Map.of("message", "Logout exitoso"));
    }
}
