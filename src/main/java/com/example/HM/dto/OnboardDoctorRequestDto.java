package com.example.HM.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnboardDoctorRequestDto {
    private Long userId;
    private String name;
    private String specialization;
}
