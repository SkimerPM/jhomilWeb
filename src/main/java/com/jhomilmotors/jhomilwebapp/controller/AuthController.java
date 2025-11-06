package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.*;
import com.jhomilmotors.jhomilwebapp.entity.User;
import com.jhomilmotors.jhomilwebapp.entity.RefreshToken;
import com.jhomilmotors.jhomilwebapp.exception.ResourceNotFoundException;
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
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody LoginDTO loginDTO,
            HttpServletResponse response,
            @RequestHeader(value = "X-Client-Type", required = false) String clientType
    ) {
        User user = userService.validateLogin(loginDTO.getEmail(), loginDTO.getPassword());
        if (user == null) {
            throw new ResourceNotFoundException("Credenciales inválidas o usuario no encontrado");
        }
        // 2. Construir JWT (accessToken) usando googleId si existe
        String subject = (user.getGoogleId() != null && !user.getGoogleId().isBlank())
                ? user.getGoogleId()
                : user.getEmail();

        Map<String, Object> claims = Map.of("role", user.getRol().getNombre().name());
        String accessToken = jwtUtil.generateToken(claims, subject);

        // 3. Generar refreshToken
        RefreshToken refreshTokenObj = refreshTokenService.createRefreshToken(user);

        // 4. Armar la respuesta DTO
        AuthResponseDTO dto = new AuthResponseDTO(
                accessToken,
                user.getEmail(),
                user.getRol().getNombre().name()
        );

        // 5. Si el cliente es móvil, envía refresh en JSON.
        if ("mobile".equalsIgnoreCase(clientType)) {
            dto.setRefreshToken(refreshTokenObj.getToken());
            return ResponseEntity.ok(dto);
        } else {
            // 6. Si es web, envía refresh como cookie httpOnly
            Cookie refreshCookie = new Cookie("refreshToken", refreshTokenObj.getToken());
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/api/auth/refresh");
            refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 días
            response.addCookie(refreshCookie);
            return ResponseEntity.ok(dto);
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestHeader (value = "X-Client-Type", required = false) String clientType,
            @RequestBody(required = false) RefreshRequestDTO refreshRequestDTO
    ) {
        String refreshToken = null;

        // --- Detecta refreshToken según origen ---
        if ("mobile".equalsIgnoreCase(clientType)) {
            if (refreshRequestDTO != null && refreshRequestDTO.getRefreshToken() != null && !refreshRequestDTO.getRefreshToken().isBlank()) {
                refreshToken = refreshRequestDTO.getRefreshToken();
            } else {
                String bearer = request.getHeader("Authorization");
                if (bearer != null && bearer.startsWith("Bearer ")) {
                    refreshToken = bearer.substring(7);
                }
            }
        } else {
            // Para web, busca en cookies
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equals("refreshToken")) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }
        }

        // --- Validaciones de refreshToken ---
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token faltante"));
        }

        var refreshOpt = refreshTokenService.findByToken(refreshToken);
        if (refreshOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token no encontrado o inválido"));
        }
        RefreshToken refreshTokenObj = refreshOpt.get();

        if (refreshTokenObj.getExpires().isBefore(java.time.LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token vencido"));
        }
        if (refreshTokenObj.isRevoked()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token revocado"));
        }

        User user = refreshTokenObj.getUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario no encontrado"));
        }

        // --- Nueva generación del accessToken ---
        String subject = (user.getGoogleId() != null && !user.getGoogleId().isBlank())
                ? user.getGoogleId()
                : user.getEmail();
        Map<String, Object> claims = Map.of("role", user.getRol().getNombre().name());
        String newAccessToken = jwtUtil.generateToken(claims, subject);

        // --- Respuesta para móvil y web ---
        if ("mobile".equalsIgnoreCase(clientType)) {
            AuthResponseDTO dto = new AuthResponseDTO(
                    newAccessToken,
                    user.getEmail(),
                    user.getRol().getNombre().name(),
                    refreshTokenObj.getToken() // ← Incluye refreshToken en JSON para móvil
            );
            return ResponseEntity.ok(dto);
        } else {
            AuthResponseDTO dto = new AuthResponseDTO(
                    newAccessToken,
                    user.getEmail(),
                    user.getRol().getNombre().name()
            );
            // Opcional: setear cookie httpOnly para refreshToken aquí si es web
            return ResponseEntity.ok(dto);
        }
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
