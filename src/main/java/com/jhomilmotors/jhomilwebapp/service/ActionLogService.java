package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.entity.ActionLog;
import com.jhomilmotors.jhomilwebapp.repository.ActionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActionLogService {
    private final ActionLogRepository actionLogRepository;

    public List<ActionLog> getAllLogs() {
        return actionLogRepository.findAll();
    }

    public List<ActionLog> getLogsByUserId(Long userId) {
        return actionLogRepository.findByUsuarioId(userId);
    }

    public Page<ActionLog> getLogsByUserIdPaged(Long userId, Pageable pageable) {
        return actionLogRepository.findByUsuarioId(userId, pageable);
    }

    public List<ActionLog> getLogsBetweenDates(LocalDateTime inicio, LocalDateTime fin) {
        return actionLogRepository.findLogsBetweenDates(inicio, fin);
    }

    public List<ActionLog> searchLogsByAccion(String accion) {
        return actionLogRepository.findByAccionContainingIgnoreCase(accion);
    }
}
