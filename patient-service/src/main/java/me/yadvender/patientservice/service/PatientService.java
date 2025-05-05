package me.yadvender.patientservice.service;

import me.yadvender.patientservice.dto.PatientRequestDTO;
import me.yadvender.patientservice.dto.PatientResponseDTO;
import me.yadvender.patientservice.exception.EmailAlreadyExistsException;
import me.yadvender.patientservice.exception.PatientNotFoundException;
import me.yadvender.patientservice.mapper.PatientMapper;
import me.yadvender.patientservice.model.Patient;
import me.yadvender.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository){
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getPatient(){
        return patientRepository.findAll().stream().map(PatientMapper::toDTO).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException("A patient with this request already exists" + patientRequestDTO.getEmail());
        }
        Patient patient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));
        return PatientMapper.toDTO(patient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO){
        Patient patient = patientRepository.findById(id).orElseThrow(()->
                new PatientNotFoundException("Patient not found with id:" + id));
        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)){
            throw new EmailAlreadyExistsException("A patient with this request already exists" + patientRequestDTO.getEmail());
        }
        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        Patient updatedPatient = patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatient);
    }

    @DeleteMapping
    public void deletePatient(UUID id){
        patientRepository.deleteById(id);
    }
}
