package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.entity.RefreshToken;
import com.jhomilmotors.jhomilwebapp.entity.User;
import com.jhomilmotors.jhomilwebapp.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setCreated(LocalDateTime.now());
        refreshToken.setExpires(LocalDateTime.now().plusDays(7)); // Vigencia 7 días
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    //El token sigue guardado en la base, pero un usuario ya no puede usarlo para obtener nuevos access tokens.
    //Caso de uso: El usuario solicita “logout” en un dispositivo o sesión específica.
    public void revokeRefreshToken(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
    //Elimina todos los refresh tokens asociados a un usuario directamente de la base de datos.
    //Útil para “logout global” (por ejemplo, al cambiar la contraseña, forzar logout total, cierre de cuenta, etc).
    //Caso de uso: El usuario cambia la contraseña, entonces forzas el cierre de sesión en todos los dispositivos, borrando todos los refresh tokens de ese usuario.
    public void deleteAllByUser(Long userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }
}
