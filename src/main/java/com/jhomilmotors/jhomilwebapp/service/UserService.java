package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.AdminRegistrationDTO;
import com.jhomilmotors.jhomilwebapp.dto.UserRegistrationDTO;
import com.jhomilmotors.jhomilwebapp.entity.Role;
import com.jhomilmotors.jhomilwebapp.entity.User;
import com.jhomilmotors.jhomilwebapp.enums.RegistrationMethod;
import com.jhomilmotors.jhomilwebapp.enums.RoleName;
import com.jhomilmotors.jhomilwebapp.repository.RoleRepository;
import com.jhomilmotors.jhomilwebapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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
        user.setMetodoRegistro(RegistrationMethod.LOCAL);
        user.setActivo(true);
        user.setFechaRegistro(LocalDateTime.now());
        user.setUltimoAcceso(null);
        // Los demás campos quedan null para que el usuario los complete luego

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario con email '" + email + "' no encontrado"));
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
            newUser.setMetodoRegistro(RegistrationMethod.GOOGLE); // Lo marcamos como usuario de Google
            newUser.setGoogleId(googleId);
            newUser.setActivo(true);
            newUser.setFechaRegistro(LocalDateTime.now());
            // El passwordHash queda en null porque la autenticación es con Google

            userRepository.save(newUser);
        } else {
            // Si el usuario ya existe, actualizamos su fecha de último acceso
            User user = existUser.get();
            user.setUltimoAcceso(LocalDateTime.now());
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
        user.setActivo(true);
        user.setMetodoRegistro(RegistrationMethod.LOCAL);
        user.setFechaRegistro(LocalDateTime.now());

        return userRepository.save(user);
    }

}
