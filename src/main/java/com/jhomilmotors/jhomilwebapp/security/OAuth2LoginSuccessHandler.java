package com.jhomilmotors.jhomilwebapp.security;

import com.jhomilmotors.jhomilwebapp.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public OAuth2LoginSuccessHandler(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String googleId = oauthUser.getAttribute("sub");

        // Actualiza o crea el usuario Google en la BD
        userService.processOAuthPostLogin(email, name, googleId);

        // Busca el usuario completo
        com.jhomilmotors.jhomilwebapp.entity.User user = userService.findByEmail(email);

        // Genera el JWT con googleId como subject si existe
        String subject = (user.getGoogleId() != null && !user.getGoogleId().isBlank())
                ? user.getGoogleId()
                : user.getEmail();

        Map<String, Object> claims = Map.of("role", user.getRol().getNombre().name());
        String accessToken = jwtUtil.generateToken(claims, subject);

        // Redirige a la ruta especial del frontend con el accessToken en query param
        response.sendRedirect("http://localhost:5173/oauth2-callback?token=" + accessToken);
    }
}