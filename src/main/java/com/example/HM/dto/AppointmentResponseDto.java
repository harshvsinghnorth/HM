package com.example.HM.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponseDto {
    private Long Id;
    private LocalDate appointmentTime;
    private String reason;
    private DoctorResponseDto doctor;
}
