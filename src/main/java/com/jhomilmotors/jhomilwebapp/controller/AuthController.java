package com.jhomilmotors.jhomilwebapp.controller;

import com.jhomilmotors.jhomilwebapp.dto.*;
import com.jhomilmotors.jhomilwebapp.entity.User;
import com.jhomilmotors.jhomilwebapp.entity.EmailVerificationToken;
import com.jhomilmotors.jhomilwebapp.entity.RefreshToken;
import com.jhomilmotors.jhomilwebapp.enums.RegistrationMethod;
import com.jhomilmotors.jhomilwebapp.exception.ResourceNotFoundException;
import com.jhomilmotors.jhomilwebapp.repository.EmailVerificationTokenRepository;
import com.jhomilmotors.jhomilwebapp.repository.UserRepository;
import com.jhomilmotors.jhomilwebapp.service.UserService;
import com.jhomilmotors.jhomilwebapp.service.RefreshTokenService;
import com.jhomilmotors.jhomilwebapp.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import com.jhomilmotors.jhomilwebapp.service.EmailService;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;


    public AuthController(
            UserService userService,
            JwtUtil jwtUtil,
            RefreshTokenService refreshTokenService,
            EmailVerificationTokenRepository emailVerificationTokenRepository,
            UserRepository userRepository,
            EmailService emailService
    ) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginDTO loginDTO,
            HttpServletResponse response,
            @RequestHeader(value = "X-Client-Type", required = false) String clientType
    ) {
        User user;
        try {
            user = userService.validateLogin(loginDTO.getEmail(), loginDTO.getPassword());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
        if (user == null) {
            throw new ResourceNotFoundException("Credenciales inválidas o usuario no encontrado");
        }
        String subject = (user.getGoogleId() != null && !user.getGoogleId().isBlank())
                ? user.getGoogleId() : user.getEmail();
        Map<String, Object> claims = Map.of("role", user.getRol().getNombre().name());
        String accessToken = jwtUtil.generateToken(claims, subject);

        RefreshToken refreshTokenObj = refreshTokenService.createRefreshToken(user);

        AuthResponseDTO dto = new AuthResponseDTO(
                accessToken,
                user.getEmail(),
                user.getRol().getNombre().name()
        );

        if ("mobile".equalsIgnoreCase(clientType)) {
            dto.setRefreshToken(refreshTokenObj.getToken());
            return ResponseEntity.ok(dto);
        } else {
            Cookie refreshCookie = new Cookie("refreshToken", refreshTokenObj.getToken());
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/api/auth/refresh");
            refreshCookie.setMaxAge(7 * 24 * 60 * 60);
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
        if ("mobile".equalsIgnoreCase(clientType)) {
            if (refreshRequestDTO != null && refreshRequestDTO.getRefreshToken() != null &&
                    !refreshRequestDTO.getRefreshToken().isBlank()) {
                refreshToken = refreshRequestDTO.getRefreshToken();
            } else {
                String bearer = request.getHeader("Authorization");
                if (bearer != null && bearer.startsWith("Bearer ")) {
                    refreshToken = bearer.substring(7);
                }
            }
        } else {
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equals("refreshToken")) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }
        }
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

        if (refreshTokenObj.getExpires().isBefore(LocalDateTime.now())) {
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
        String subject = (user.getGoogleId() != null && !user.getGoogleId().isBlank())
                ? user.getGoogleId()
                : user.getEmail();
        Map<String, Object> claims = Map.of("role", user.getRol().getNombre().name());
        String newAccessToken = jwtUtil.generateToken(claims, subject);

        if ("mobile".equalsIgnoreCase(clientType)) {
            AuthResponseDTO dto = new AuthResponseDTO(
                    newAccessToken,
                    user.getEmail(),
                    user.getRol().getNombre().name(),
                    refreshTokenObj.getToken()
            );
            return ResponseEntity.ok(dto);
        } else {
            AuthResponseDTO dto = new AuthResponseDTO(
                    newAccessToken,
                    user.getEmail(),
                    user.getRol().getNombre().name()
            );
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
        Cookie expiredCookie = new Cookie("refreshToken", null);
        expiredCookie.setHttpOnly(true);
        expiredCookie.setPath("/api/auth/refresh");
        expiredCookie.setMaxAge(0);
        response.addCookie(expiredCookie);

        return ResponseEntity.ok(Map.of("message", "Logout exitoso"));
    }

    // === NUEVO ENDPOINT DE VERIFICACIÓN DE EMAIL ===
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        Optional<EmailVerificationToken> tokenOpt = emailVerificationTokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            // Puedes redirigir a error si lo prefieres
            return ResponseEntity.status(302)
                    .location(URI.create("http://localhost:5173/login?verified=false&reason=notfound"))
                    .build();
        }
        EmailVerificationToken evt = tokenOpt.get();
        if (evt.isUsed() || evt.getExpires().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(302)
                    .location(URI.create("http://localhost:5173/login?verified=false&reason=expired"))
                    .build();
        }

        User user = evt.getUser();
        if (!user.isEmailVerificado()) {
            user.setEmailVerificado(true);
            userRepository.save(user);
        }
        evt.setUsed(true);
        evt.setUsedAt(LocalDateTime.now());
        emailVerificationTokenRepository.save(evt);

        return ResponseEntity.status(302)
                .location(URI.create("http://localhost:5173/login?verified=true"))
                .build();
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            // Puedes devolver 200 igual, para evitar usuarios enumerando emails
            return ResponseEntity.ok(Map.of("message", "Si el correo está registrado, se ha reenviado el email de verificación."));
        }

        User user = userOpt.get();
        if (user.isEmailVerificado()) {
            return ResponseEntity.ok(Map.of("message", "Tu correo ya está verificado. Ya puedes iniciar sesión."));
        }

        // Opcional: elimina tokens viejos no usados
        emailVerificationTokenRepository.deleteAll(
                emailVerificationTokenRepository.findAll().stream()
                        .filter(evt -> evt.getUser().getId().equals(user.getId()) && !evt.isUsed())
                        .toList()
        );

        // Genera y manda un nuevo token
        String tokenValue = java.util.UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusHours(24);

        EmailVerificationToken token = new EmailVerificationToken(tokenValue, user, now, expiresAt);
        emailVerificationTokenRepository.save(token);

        String verifyUrl = "http://localhost:8080/api/auth/verify-email?token=" + tokenValue;
        emailService.sendVerificationEmail(user.getEmail(), verifyUrl);

        return ResponseEntity.ok(Map.of("message", "Se ha reenviado el enlace de verificación. Revisa tu correo."));
    }

}
