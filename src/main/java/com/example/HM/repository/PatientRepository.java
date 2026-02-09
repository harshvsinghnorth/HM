package com.example.HM.repository;

import com.example.HM.dto.PatientResponseDto;
import com.example.HM.entity.Patient;
import com.example.HM.service.PatientService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {

}