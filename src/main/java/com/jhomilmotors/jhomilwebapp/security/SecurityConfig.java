package com.jhomilmotors.jhomilwebapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static org.springframework.security.config.Customizer.withDefaults;
import com.jhomilmotors.jhomilwebapp.security.OAuth2LoginSuccessHandler;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    public SecurityConfig(JwtUtil jwtUtil, OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.jwtUtil = jwtUtil;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                .authorizeHttpRequests(auth -> auth

                                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()

                                // Permitir GET a usuarios por email (opcional si quieres que sea público)
//                        .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()

                                //para productos metod get
                                .requestMatchers(HttpMethod.GET, "/api/v1/catalog/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll()

                                .requestMatchers(HttpMethod.GET, "/api/v1/buscar**").permitAll()

                                .requestMatchers("/login/oauth2/**").permitAll()



                                .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
                                // Proteger rutas de admin
                                .requestMatchers("/admin/dashboard/**").hasAuthority("ROLE_ADMIN")

                                //rutas admin pero de gestion de usuarios:
                                .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.PATCH, "/api/users/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(HttpMethod.GET, "/api/users/**").hasAuthority("ROLE_ADMIN")



                                // Todo lo demás requiere autenticación
                                .anyRequest().authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
                                // Proteger rutas de admin
                                .requestMatchers("/admin/dashboard/**").hasAuthority("ROLE_ADMIN")


                                // Todo lo demás requiere autenticación
                                .anyRequest().authenticated()

                )
                .oauth2Login(oauth2 -> {
                    oauth2.successHandler(oAuth2LoginSuccessHandler);
                });
        http.addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
