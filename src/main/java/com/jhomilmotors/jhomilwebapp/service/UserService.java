package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.*;
import com.jhomilmotors.jhomilwebapp.entity.EmailVerificationToken;
import com.jhomilmotors.jhomilwebapp.entity.Role;
import com.jhomilmotors.jhomilwebapp.entity.User;
import com.jhomilmotors.jhomilwebapp.enums.RegistrationMethod;
import com.jhomilmotors.jhomilwebapp.enums.RoleName;
import com.jhomilmotors.jhomilwebapp.repository.EmailVerificationTokenRepository;
import com.jhomilmotors.jhomilwebapp.repository.RoleRepository;
import com.jhomilmotors.jhomilwebapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import java.util.Collections;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;
    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            EmailVerificationTokenRepository emailVerificationTokenRepository,
            EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.emailService = emailService;
    }

    public User registerUser(UserRegistrationDTO dto) {
        // 1. Validar email único
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // 2. Buscar y asignar el rol CUSTOMER por defecto
        Role role = roleRepository.findByNombre(RoleName.CUSTOMER)
                .orElseThrow(() -> new IllegalArgumentException("Rol CUSTOMER no encontrado"));

        // 3. Encriptar contraseña
        String passwordHash = passwordEncoder.encode(dto.getPassword());

        User user = new User();
        user.setRol(role);
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordHash);
        user.setNombre(dto.getNombre());
        user.setApellido(dto.getApellido());
        user.setMetodoRegistro(RegistrationMethod.LOCAL);

        // Nuevo: email no verificado al registrarse por método LOCAL
        user.setEmailVerificado(false);

        // Puedes mantener activo=true y luego, en el login, combinar ambos flags
        user.setActivo(true);

        user.setFechaRegistro(LocalDateTime.now());
        user.setUltimoAcceso(null);
        // Los demás campos quedan null para que el usuario los complete luego

        User savedUser = userRepository.save(user);

        // Nuevo: generar token de verificación y guardarlo
        createEmailVerificationToken(savedUser);

        // Más adelante, desde aquí llamaremos a un EmailService para enviar el correo
        return savedUser;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con email '" + email + "' no encontrado"));
    }

    public User findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con googleId '" + googleId + "' no encontrado"));
    }

    public void updateLastAccess(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setUltimoAcceso(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public User validateLogin(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Solo usuarios locales: exige email verificado
            if (user.getMetodoRegistro() == RegistrationMethod.LOCAL && !user.isEmailVerificado()) {
                throw new IllegalStateException("Debes verificar tu correo electrónico antes de iniciar sesión.");
            }

            if (passwordEncoder.matches(password, user.getPasswordHash())) {
                return user;
            }
        }
        return null;
    }

    public void processOAuthPostLogin(String email, String name, String googleId) {
        Optional<User> existUser = userRepository.findByEmail(email);

        if (existUser.isEmpty()) {
            // Si el usuario no existe, lo creamos
            User newUser = new User();

            // Buscamos y asignamos el rol de cliente
            Role customerRole = roleRepository.findByNombre(RoleName.CUSTOMER)
                    .orElseThrow(() -> new EntityNotFoundException("Rol CUSTOMER no encontrado"));

            newUser.setEmail(email);
            newUser.setNombre(name);
            newUser.setRol(customerRole);
            newUser.setMetodoRegistro(RegistrationMethod.GOOGLE); // Usuario de Google
            newUser.setGoogleId(googleId);

            // Para Google asumimos email verificado por el proveedor
            newUser.setEmailVerificado(true);

            newUser.setActivo(true);
            newUser.setFechaRegistro(LocalDateTime.now());
            // El passwordHash queda en null porque la autenticación es con Google

            userRepository.save(newUser);
        } else {
            // Si el usuario existe, actualiza googleId y método de registro si vienen del login Google
            User user = existUser.get();
            user.setUltimoAcceso(LocalDateTime.now());
            if (user.getGoogleId() == null || !user.getGoogleId().equals(googleId)) {
                user.setGoogleId(googleId);
                user.setMetodoRegistro(RegistrationMethod.GOOGLE);
                // Si pasa a Google, también podemos marcar el email como verificado
                user.setEmailVerificado(true);
            }
            userRepository.save(user);
        }
    }

    public User registerAdmin(AdminRegistrationDTO dto) {
        // Verifica que el rol enviado en el DTO sea "ADMIN"
        if (!"ADMIN".equals(dto.getRole())) {
            throw new IllegalArgumentException("Solo se permite crear usuarios con rol ADMIN desde este endpoint.");
        }

        // Verifica si ya existe email
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email ya registrado.");
        }

        // Busca el rol ADMIN
        Role adminRole = roleRepository.findByNombre(RoleName.ADMIN)
                .orElseThrow(() -> new IllegalArgumentException("Rol ADMIN no existe"));

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setNombre(dto.getFirstName());
        user.setApellido(dto.getLastName());
        user.setRol(adminRole);
        user.setMetodoRegistro(RegistrationMethod.LOCAL);

        // Admin creado por backoffice: lo marcamos como verificado
        user.setEmailVerificado(true);
        user.setActivo(true);

        user.setFechaRegistro(LocalDateTime.now());

        return userRepository.save(user);
    }

    public Page<AdminUserListResponseDTO> listAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(u -> new AdminUserListResponseDTO(
                        u.getId(),
                        u.getRol().getNombre().name(),
                        u.getNombre(),
                        u.getApellido(),
                        u.getEmail(),
                        u.getTelefono(),
                        u.getDocumento(),
                        u.isActivo(),
                        u.getFechaRegistro(),
                        u.getUltimoAcceso()
                ));
    }

    // Usuario por id:
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    public CustomerProfileDTO actualizar(Long id, UpdateProfileCustomerDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        user.setNombre(request.getName());
        user.setApellido(request.getLastname());
        user.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            user.setTelefono(request.getPhoneNumber());
        }
        if (request.getAddress() != null && !request.getAddress().trim().isEmpty()) {
            user.setDireccion(request.getAddress());
        }
        User updatedUser = userRepository.save(user);
        return mapUserToCustomerProfileDTO(updatedUser);
    }

    public User actualizarEstado(Long id, boolean activo) {
        User user = userRepository.findById(id).orElseThrow();
        user.setActivo(activo);
        return userRepository.save(user);
    }

    @Transactional
    public void updateUserFromAdmin(Long id, AdminUserFormUpdate dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        // Actualiza SOLO los campos permitidos
        user.setNombre(dto.getNombre());
        user.setApellido(dto.getApellido());
        user.setEmail(dto.getEmail());
        user.setTelefono(dto.getTelefono());
        user.setActivo(dto.isActivo());
        user.setDocumento(dto.getDocumento());

        // No se actualizan: passwordHash, metodoRegistro, rol, googleId, fotoPerfil, fechaRegistro, ultimoAcceso

        userRepository.save(user);
    }

    private CustomerProfileDTO mapUserToCustomerProfileDTO(User user) {
        return new CustomerProfileDTO(
                user.getId(),
                user.getNombre(),
                user.getApellido(),
                user.getEmail(),
                user.getTelefono(),
                user.getDireccion()
        );
    }

    public User getUserFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Usuario no autenticado");
        }
        String identifier = authentication.getName();
        User user;
        if (identifier != null && identifier.contains("@")) {
            user = findByEmail(identifier);
        } else {
            user = findByGoogleId(identifier);
        }
        return user; // Devuelve el objeto User completo
    }

    public Long getUserIdFromAuthentication(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        return user.getId();
    }

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    /**
     * Valida el token de Android con Google y devuelve el Usuario (existente o nuevo).
     */
    public User loginWithGoogleMobile(String idTokenString) {
        try {
            // 1. Configurar Verificador
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            // 2. Verificar Token
            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                throw new IllegalArgumentException("Token de Google inválido o expirado");
            }

            // 3. Extraer datos
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String googleId = payload.getSubject();
            String name = (String) payload.get("name");
            String givenName = (String) payload.get("given_name");
            String familyName = (String) payload.get("family_name");

            // 4. Lógica de Buscar o Crear (Reutilizamos lógica parecida a processOAuthPostLogin pero devolviendo el User)
            Optional<User> existUser = userRepository.findByEmail(email);

            User user;
            if (existUser.isEmpty()) {
                // --- CREAR NUEVO ---
                user = new User();
                Role customerRole = roleRepository.findByNombre(RoleName.CUSTOMER)
                        .orElseThrow(() -> new EntityNotFoundException("Rol CUSTOMER no encontrado"));

                user.setEmail(email);
                // Usamos el nombre separado si existe, si no, el completo
                user.setNombre(givenName != null ? givenName : name);
                user.setApellido(familyName != null ? familyName : "");
                user.setRol(customerRole);
                user.setMetodoRegistro(RegistrationMethod.GOOGLE);
                user.setGoogleId(googleId);
                user.setEmailVerificado(true); // Google ya verificó
                user.setActivo(true);
                user.setFechaRegistro(LocalDateTime.now());
                user.setPasswordHash(""); // Sin pass

                user = userRepository.save(user);
            } else {
                // --- ACTUALIZAR EXISTENTE ---
                user = existUser.get();
                // Vinculamos cuenta si no lo estaba
                if (user.getGoogleId() == null || !user.getGoogleId().equals(googleId)) {
                    user.setGoogleId(googleId);
                    user.setMetodoRegistro(RegistrationMethod.GOOGLE);
                    user.setEmailVerificado(true);
                    userRepository.save(user);
                }
                // Actualizar último acceso
                updateLastAccess(user.getId());
            }

            return user;

        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Error verificando token de Google: " + e.getMessage());
        }
    }

    // ============================
    // LÓGICA DE TOKEN DE VERIFICACIÓN
    // ============================

    private void createEmailVerificationToken(User user) {
        String tokenValue = java.util.UUID.randomUUID().toString();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusHours(24);

        EmailVerificationToken token = new EmailVerificationToken(
                tokenValue, user, now, expiresAt
        );
        emailVerificationTokenRepository.save(token);

        // Construir URL de verificación (ajusta tu dominio según entorno)
        String baseUrl = "http://localhost:8080"; // o ajústalo dinámico para producción
        String verifyUrl = baseUrl + "/api/auth/verify-email?token=" + tokenValue;

        // Llama al servicio de email
        emailService.sendVerificationEmail(user.getEmail(), verifyUrl);
    }
}
