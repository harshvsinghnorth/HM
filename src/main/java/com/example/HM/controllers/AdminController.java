package com.example.HM.controllers;

import com.example.HM.dto.DoctorResponseDto;
import com.example.HM.dto.OnboardDoctorRequestDto;
import com.example.HM.dto.PatientResponseDto;
import com.example.HM.service.DoctorService;
import com.example.HM.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final PatientService patientService;
    private final DoctorService doctorService;


    @GetMapping("/patients")
    public ResponseEntity<List<PatientResponseDto>> getAllPatients(
            @RequestParam(value ="page",defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "size",defaultValue = "10") Integer pageSize
    ){
        return ResponseEntity.ok(patientService.getAllPatients(pageNumber,pageSize));

    }

    @PostMapping("/OnBoardNewDoctor")
    public ResponseEntity<DoctorResponseDto> OnBoardNewDoctor(@RequestBody OnboardDoctorRequestDto onboardDoctorRequestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.OnBoardNewDoctor(onboardDoctorRequestDto));
    }
}
