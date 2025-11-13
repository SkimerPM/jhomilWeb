package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.ImportJob;
import com.jhomilmotors.jhomilwebapp.enums.ImportJobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ImportJobRepository extends JpaRepository<ImportJob, Long> {
    List<ImportJob> findByUsuarioId(Long usuarioId);
    List<ImportJob> findByTipo(ImportJobType tipo);
    List<ImportJob> findByStatus(String status);
    Page<ImportJob> findByUsuarioId(Long usuarioId, Pageable pageable);
    List<ImportJob> findByFechaInicioAfter(LocalDateTime fecha);
}