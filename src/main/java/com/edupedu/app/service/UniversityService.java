package com.edupedu.app.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edupedu.app.exception.ResourceNotFoundException;
import com.edupedu.app.model.University;
import com.edupedu.app.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UniversityService {

    private final UniversityRepository universityRepository;

    @Transactional(readOnly = true)
    public List<University> getAllUniversities() {
        return universityRepository.findAll();
    }

    @Transactional(readOnly = true)
    public University getUniversityById(Long id) {
        return universityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("University", "id", id));
    }

    @Transactional
    public University createUniversity(University university) {
        return universityRepository.save(university);
    }

    @Transactional
    public University updateUniversity(Long id, University updatedUniversity) {
        University existingUniversity = universityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("University", "id", id));

        if (updatedUniversity.getName() != null) {
            existingUniversity.setName(updatedUniversity.getName());
        }
        if (updatedUniversity.getCreditsPerSemester() != null) {
            existingUniversity.setCreditsPerSemester(updatedUniversity.getCreditsPerSemester());
        }

        return universityRepository.save(existingUniversity);
    }

    @Transactional
    public void deleteUniversity(Long id) {
        University university = universityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("University", "id", id));
        universityRepository.delete(university);
    }
}
