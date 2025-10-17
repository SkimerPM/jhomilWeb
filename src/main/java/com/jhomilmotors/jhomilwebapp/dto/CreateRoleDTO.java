package com.jhomilmotors.jhomilwebapp.dto;
import com.jhomilmotors.jhomilwebapp.enums.RoleName;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleDTO {
    @NotBlank
    private String roleName;
    private String description;
}
