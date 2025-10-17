package com.jhomilmotors.jhomilwebapp.service;

import com.jhomilmotors.jhomilwebapp.dto.CreateRoleDTO;
import com.jhomilmotors.jhomilwebapp.entity.Role;
import com.jhomilmotors.jhomilwebapp.enums.RoleName;
import com.jhomilmotors.jhomilwebapp.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class RoleService {
    private final RoleRepository roleRepository;
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    public Role createRole(CreateRoleDTO dto) {
        Role role = new Role(RoleName.valueOf(dto.getRoleName()), dto.getDescription());
        return roleRepository.save(role);
    }

    public Optional<Role> findByNombre(RoleName roleName) {
        return roleRepository.findByNombre(roleName);
    }

}
