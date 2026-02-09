package com.example.HM.service;

import com.example.HM.dto.PatientResponseDto;
import com.example.HM.entity.Patient;
import com.example.HM.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final ModelMapper modelMapper;
    private final PatientRepository patientRepository;

    public List<PatientResponseDto> getAllPatients(Integer pageNumber, Integer pageSize) {
        Pageable pageable = (Pageable) PageRequest.of(pageNumber,pageSize);
        return patientRepository.findAll(pageable)
                .stream()
                .map(patient -> modelMapper.map(patient, PatientResponseDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public PatientResponseDto getPatientById(Long patientId) {
        Patient patient = patientRepository.findById(patientId).orElseThrow(() -> new EntityNotFoundException("Patient Not " +
                "Found with id: " + patientId));
        return modelMapper.map(patient, PatientResponseDto.class);
    }
}
