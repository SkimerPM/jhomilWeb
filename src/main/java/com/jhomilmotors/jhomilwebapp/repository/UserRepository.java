package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByGoogleId(String googleId);
//
//    Page<User> findByUserNombre(String userName, Pageable pageable);
}
