package com.example.HM.controllers;

import com.example.HM.dto.AppointmentResponseDto;
import com.example.HM.dto.CreateAppointmentRequestDto;
import com.example.HM.dto.PatientResponseDto;
import com.example.HM.service.AppointmentService;
import com.example.HM.service.PatientService;
import com.example.HM.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;

    @PostMapping("/appointments")
    public ResponseEntity<AppointmentResponseDto> createNewAppointment(@RequestBody CreateAppointmentRequestDto createAppointmentRequestDto) {
        // A patient's profile shares its id with the logged-in user (@MapsId)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentService.createNewAppointment(createAppointmentRequestDto, currentUser().getId()));
    }

    @GetMapping("/profile")
    public ResponseEntity<PatientResponseDto> getPatientProfile() {
        return ResponseEntity.ok(patientService.getPatientById(currentUser().getId()));
    }

    private User currentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
