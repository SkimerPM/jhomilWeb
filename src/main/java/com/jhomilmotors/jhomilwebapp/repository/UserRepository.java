package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.User;
import com.jhomilmotors.jhomilwebapp.enums.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByGoogleId(String googleId);
//
//    Page<User> findByUserNombre(String userName, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.rol.nombre = :roleName AND u.fechaRegistro BETWEEN :start AND :end")
    long countByRoleNameAndDateRange(
            @Param("roleName") RoleName roleName,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
