package com.vidasalud.pacientes.mapper;

import com.vidasalud.pacientes.dto.PacienteRequestDTO;
import com.vidasalud.pacientes.dto.PacienteResponseDTO;
import com.vidasalud.pacientes.entity.Paciente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PacienteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "estado", ignore = true)
    Paciente toEntity(PacienteRequestDTO requestDTO);

    PacienteResponseDTO toResponseDTO(Paciente paciente);

    List<PacienteResponseDTO> toResponseDTOList(List<Paciente> pacientes);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "estado", ignore = true)
    void updateEntityFromDTO(PacienteRequestDTO requestDTO, @MappingTarget Paciente paciente);
}