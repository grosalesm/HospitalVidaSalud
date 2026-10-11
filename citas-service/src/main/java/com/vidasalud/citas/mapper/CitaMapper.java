package com.vidasalud.citas.mapper;

import com.vidasalud.citas.dto.CitaRequestDTO;
import com.vidasalud.citas.dto.CitaResponseDTO;
import com.vidasalud.citas.entity.Cita;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CitaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    Cita toEntity(CitaRequestDTO requestDTO);

    CitaResponseDTO toResponseDTO(Cita cita);

    List<CitaResponseDTO> toResponseDTOList(List<Cita> citas);
}