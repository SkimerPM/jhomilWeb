package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.AppContent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AppContentRepository extends JpaRepository<AppContent, Long> {
    Optional<AppContent> findByCodigo(String codigo);
}