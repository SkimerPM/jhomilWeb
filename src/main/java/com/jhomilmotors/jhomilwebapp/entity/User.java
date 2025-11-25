package com.jhomilmotors.jhomilwebapp.entity;

import com.jhomilmotors.jhomilwebapp.enums.RegistrationMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "core_usuario")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<RefreshToken> refreshTokens;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rol_id")
    private Role rol;


    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;


    @Column(name = "apellido", length = 100, nullable = false)
    private String apellido;

    @Email
    @NotBlank
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name ="direccion" , length = 50)
    private String direccion;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "telefono", length = 50)
    private String telefono;

    @Column(name = "documento", length = 20)
    private String documento;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_registro", length = 10, nullable = false)
    private RegistrationMethod metodoRegistro = RegistrationMethod.LOCAL;

    @Column(name = "google_id", length = 255)
    private String googleId;

    @Column(name = "foto_perfil", length = 512)
    private String fotoPerfil;

    @Column(name = "email_verificado", nullable = false)
    private boolean emailVerificado = false;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    public User(){}

    public User(Role rol, String nombre, String apellido, String email, String direccion, String passwordHash, String telefono, String documento, RegistrationMethod metodoRegistro, String googleId, String fotoPerfil, boolean activo, LocalDateTime fechaRegistro, LocalDateTime ultimoAcceso) {
        this.rol = rol;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.direccion = direccion;
        this.passwordHash = passwordHash;
        this.telefono = telefono;
        this.documento = documento;
        this.metodoRegistro = metodoRegistro;
        this.googleId = googleId;
        this.fotoPerfil = fotoPerfil;
        this.activo = activo;
        this.fechaRegistro = fechaRegistro;
        this.ultimoAcceso = ultimoAcceso;
    }

    public List<RefreshToken> getRefreshTokens() {
        return refreshTokens;
    }

    public void setRefreshTokens(List<RefreshToken> refreshTokens) {
        this.refreshTokens = refreshTokens;
    }

    public boolean isEmailVerificado() {
        return emailVerificado;
    }

    public void setEmailVerificado(boolean emailVerificado) {
        this.emailVerificado = emailVerificado;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role getRol() {
        return rol;
    }

    public void setRol(Role rol) {
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public RegistrationMethod getMetodoRegistro() {
        return metodoRegistro;
    }

    public void setMetodoRegistro(RegistrationMethod metodoRegistro) {
        this.metodoRegistro = metodoRegistro;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }
}