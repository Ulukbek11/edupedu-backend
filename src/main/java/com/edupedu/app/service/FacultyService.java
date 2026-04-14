package com.edupedu.app.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edupedu.app.exception.ResourceNotFoundException;
import com.edupedu.app.model.Faculty;
import com.edupedu.app.model.University;
import com.edupedu.app.repository.FacultyRepository;
import com.edupedu.app.repository.UniversityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final UniversityRepository universityRepository;

    @Transactional(readOnly = true)
    public List<Faculty> getAllFaculties() {
        return facultyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Faculty getFacultyById(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", id));
    }

    @Transactional(readOnly = true)
    public List<Faculty> getFacultiesByUniversityId(Long universityId) {
        return facultyRepository.findAllByUniversityId(universityId);
    }

    @Transactional
    public Faculty createFaculty(Faculty faculty) {
        Long universityId = getUniversityId(faculty);
        University university = universityRepository.findById(universityId)
                .orElseThrow(() -> new ResourceNotFoundException("University", "id", universityId));

        Faculty newFaculty = Faculty.builder()
                .name(faculty.getName())
                .code(faculty.getCode())
                .monthlyFee(faculty.getMonthlyFee())
                .university(university)
                .build();

        return facultyRepository.save(newFaculty);
    }

    @Transactional
    public Faculty updateFaculty(Long id, Faculty updatedFaculty) {
        Faculty existingFaculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", id));

        if (updatedFaculty.getName() != null) {
            existingFaculty.setName(updatedFaculty.getName());
        }
        if (updatedFaculty.getCode() != null) {
            existingFaculty.setCode(updatedFaculty.getCode());
        }
        if (updatedFaculty.getMonthlyFee() != null) {
            existingFaculty.setMonthlyFee(updatedFaculty.getMonthlyFee());
        }
        if (updatedFaculty.getUniversity() != null && updatedFaculty.getUniversity().getId() != null) {
            Long universityId = updatedFaculty.getUniversity().getId();
            University university = universityRepository.findById(universityId)
                    .orElseThrow(() -> new ResourceNotFoundException("University", "id", universityId));
            existingFaculty.setUniversity(university);
        }

        return facultyRepository.save(existingFaculty);
    }

    @Transactional
    public void deleteFaculty(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", id));
        facultyRepository.delete(faculty);
    }

    private Long getUniversityId(Faculty faculty) {
        if (faculty.getUniversity() == null || faculty.getUniversity().getId() == null) {
            throw new IllegalArgumentException("Faculty university id is required");
        }
        return faculty.getUniversity().getId();
    }
}
