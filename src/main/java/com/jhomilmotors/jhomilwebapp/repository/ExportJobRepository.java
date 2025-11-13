package com.jhomilmotors.jhomilwebapp.repository;
import com.jhomilmotors.jhomilwebapp.entity.ExportJob;
import com.jhomilmotors.jhomilwebapp.enums.ExportJobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExportJobRepository extends JpaRepository<ExportJob, Long> {
    List<ExportJob> findByUsuarioId(Long usuarioId);
    List<ExportJob> findByTipo(ExportJobType tipo);
    List<ExportJob> findByStatus(String status);
    Page<ExportJob> findByUsuarioId(Long usuarioId, Pageable pageable);
    List<ExportJob> findByFechaCreacionAfter(LocalDateTime fecha);
}