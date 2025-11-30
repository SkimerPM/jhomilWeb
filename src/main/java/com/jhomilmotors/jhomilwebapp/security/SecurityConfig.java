package com.jhomilmotors.jhomilwebapp.security;

import com.jhomilmotors.jhomilwebapp.repository.UserRepository;
import com.jhomilmotors.jhomilwebapp.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    public SecurityConfig(JwtUtil jwtUtil, OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.jwtUtil = jwtUtil;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    // Bean para inyectar el handler sin conflictos circulares
    @Bean
    public OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler(UserService userService, JwtUtil jwtUtil) {
        return new OAuth2LoginSuccessHandler(userService, jwtUtil);
    }

    // Bean para el filtro JWT
    @Bean
    public JwtAuthFilter jwtAuthFilter(UserRepository userRepository) {
        return new JwtAuthFilter(jwtUtil, userRepository);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserRepository userRepository) throws Exception {
        http
                // 1. Desactivar CSRF (no necesario con JWT)
                .csrf(csrf -> csrf.disable())

                // 2. CONFIGURACIÓN CORS CORREGIDA (Usa el Bean de abajo)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 3. MANEJO DE EXCEPCIONES (CRÍTICO PARA TU ERROR)
                // Evita que la API redirija al login de Google si falla el token. Devuelve 401.
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )

                .authorizeHttpRequests(auth -> auth
                        // Auth pública
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/google").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers("/api/auth/resend-verification").permitAll()
                        .requestMatchers("/api/auth/verify-email").permitAll()
                        .requestMatchers("/login/oauth2/**").permitAll()

                        // Manejo de imágenes (Upload requiere auth, borrar también)
                        .requestMatchers(HttpMethod.POST, "/api/upload").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/delete-image/**").authenticated()

                        // Carrito (público o híbrido según tu lógica)
                        .requestMatchers("/api/v1/cart/**").permitAll()

                        // Promociones (GET público, gestión ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/promotions/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/promotions/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/promotions/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/promotions/**").hasAuthority("ROLE_ADMIN")

                        // PromotionProduct (GET público, gestión ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/promotion-products/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/promotion-products/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/promotion-products/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/promotion-products/**").hasAuthority("ROLE_ADMIN")

                        // Compras y proveedores (SOLO ADMIN)
                        .requestMatchers("/api/purchases/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/suppliers/**").hasAuthority("ROLE_ADMIN")

                        // Catálogo y Productos (Lectura pública)
                        .requestMatchers(HttpMethod.GET, "/api/v1/catalog/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/buscar**").permitAll()

                        // Rutas Específicas de Admin en Catálogo
                        .requestMatchers("/api/v1/catalog/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/catalog/create-full-product").permitAll() // Ojo: ¿Esto debe ser público? Lo dejé como estaba.
                        .requestMatchers("/api/admin/product-attributes").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/admin/variant-attributes").hasAuthority("ROLE_ADMIN")

                        // Dashboard Admin
                        .requestMatchers("/admin/dashboard/**").hasAuthority("ROLE_ADMIN")

                        // Gestión de Usuarios (Admin) y Perfil Propio
                        .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/users/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").authenticated() // Usuario puede editarse a sí mismo (validar en controller)
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/{id}").hasAuthority("ROLE_ADMIN")

                        // Cualquier otra cosa requiere autenticación
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> {
                    oauth2.successHandler(oAuth2LoginSuccessHandler);
                });

        // Añadir el filtro JWT antes del filtro de autenticación de Spring
        http.addFilterBefore(jwtAuthFilter(userRepository), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 4. BEAN DE CONFIGURACIÓN CORS EXPLICITA
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Lista de orígenes permitidos (Localhost y Producción)
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",                   // Frontend Local
                "https://jhomilwebfrontend.onrender.com"   // Frontend Producción
        ));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Cabeceras permitidas
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));

        // Exponer cabeceras al frontend (útil para ver errores o tokens en headers)
        configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));

        // Permitir credenciales (cookies/tokens)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}