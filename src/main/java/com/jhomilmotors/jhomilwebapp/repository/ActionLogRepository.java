package com.jhomilmotors.jhomilwebapp.repository;

import com.jhomilmotors.jhomilwebapp.entity.ActionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActionLogRepository extends JpaRepository<ActionLog, Long> {
    List<ActionLog> findByUsuarioId(Long usuarioId);

    Page<ActionLog> findByUsuarioId(Long usuarioId, Pageable pageable);

    @Query("SELECT al FROM ActionLog al WHERE al.fecha BETWEEN :inicio AND :fin ORDER BY al.fecha DESC")
    List<ActionLog> findLogsBetweenDates(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    List<ActionLog> findByAccionContainingIgnoreCase(String accion);
}