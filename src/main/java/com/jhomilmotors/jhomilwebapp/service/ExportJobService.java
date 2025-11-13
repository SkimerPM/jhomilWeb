package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.entity.ExportJob;
import com.jhomilmotors.jhomilwebapp.enums.ExportJobType;
import com.jhomilmotors.jhomilwebapp.repository.ExportJobRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportJobService {
    private final ExportJobRepository exportJobRepository;

    public List<ExportJob> getAll() {
        return exportJobRepository.findAll();
    }

    public List<ExportJob> getByUsuarioId(Long usuarioId) {
        return exportJobRepository.findByUsuarioId(usuarioId);
    }

    public Page<ExportJob> getByUsuarioIdPaged(Long usuarioId, Pageable pageable) {
        return exportJobRepository.findByUsuarioId(usuarioId, pageable);
    }

    public List<ExportJob> getByTipo(ExportJobType tipo) {
        return exportJobRepository.findByTipo(tipo);
    }

    public List<ExportJob> getByStatus(String status) {
        return exportJobRepository.findByStatus(status);
    }

    public List<ExportJob> getByFechaCreacionAfter(LocalDateTime fecha) {
        return exportJobRepository.findByFechaCreacionAfter(fecha);
    }
}
