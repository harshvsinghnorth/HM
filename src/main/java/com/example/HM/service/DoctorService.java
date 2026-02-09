package com.example.HM.service;

import com.example.HM.dto.DoctorResponseDto;
import com.example.HM.dto.OnboardDoctorRequestDto;
import com.example.HM.entity.Doctor;
import com.example.HM.entity.User;
import com.example.HM.entity.type.RoleType;
import com.example.HM.repository.DoctorRepository;
import com.example.HM.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;
    @Transactional
    public DoctorResponseDto OnBoardNewDoctor(OnboardDoctorRequestDto onboardDoctorRequestDto){
        User user = userRepository.findById(onboardDoctorRequestDto.getUserId()).orElseThrow();
        if(doctorRepository.existsById(onboardDoctorRequestDto.getUserId())){
            throw new IllegalArgumentException("Already a doctor");
        }

        Doctor doctor = Doctor.builder()
                .name(onboardDoctorRequestDto.getName())
                .specialization(onboardDoctorRequestDto.getSpecialization())
                .user(user)
                .build();

        user.getRoles().add(RoleType.DOCTOR);
        return modelMapper.map(doctorRepository.save(doctor),DoctorResponseDto.class);

    }


    public List<DoctorResponseDto> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(doctor -> modelMapper.map(doctor, DoctorResponseDto.class))
                .collect(Collectors.toList());
    }
}
