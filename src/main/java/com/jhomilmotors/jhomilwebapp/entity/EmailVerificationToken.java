package com.jhomilmotors.jhomilwebapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "core_email_verification_token")
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mapea al campo token de Django (CharField max_length=128, unique=True)
    @Column(name = "token", length = 128, nullable = false, unique = true)
    private String token;

    // FK a core_usuario
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "expires", nullable = false)
    private LocalDateTime expires;

    @Column(name = "used", nullable = false)
    private boolean used = false;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    public EmailVerificationToken() {}

    // Constructor de conveniencia que usaremos luego en el service
    public EmailVerificationToken(String token, User user, LocalDateTime created, LocalDateTime expires) {
        this.token = token;
        this.user = user;
        this.created = created;
        this.expires = expires;
        this.used = false;
    }

    // getters y setters...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getExpires() {
        return expires;
    }

    public void setExpires(LocalDateTime expires) {
        this.expires = expires;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }
}
