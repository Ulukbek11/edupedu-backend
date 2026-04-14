package com.edupedu.app.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edupedu.app.exception.ResourceNotFoundException;
import com.edupedu.app.model.Subject;
import com.edupedu.app.repository.SubjectRepository;
import com.edupedu.app.request.CreateSubjectRequest;
import com.edupedu.app.request.SubjectDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    @Transactional(readOnly = true)
    public List<SubjectDTO> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(this::mapToSubjectDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public SubjectDTO getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", id));
        return mapToSubjectDTO(subject);
    }

    @Transactional
    public SubjectDTO createSubject(CreateSubjectRequest request) {
        Subject subject = Subject.builder()
                .name(request.name())
                .description(request.description())
                .credits(request.credits() != null ? request.credits() : 2)
                .build();
        subject = subjectRepository.save(subject);
        return mapToSubjectDTO(subject);
    }

    @Transactional
    public SubjectDTO updateSubject(Long id, CreateSubjectRequest request) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", id));

        subject.setName(request.name());
        subject.setDescription(request.description());
        if (request.credits() != null) {
            subject.setCredits(request.credits());
        }

        subject = subjectRepository.save(subject);
        return mapToSubjectDTO(subject);
    }

    @Transactional
    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subject", "id", id);
        }
        subjectRepository.deleteById(id);
    }

    private SubjectDTO mapToSubjectDTO(Subject subject) {
        return new SubjectDTO(subject.getId(), subject.getName(), subject.getDescription(), subject.getCredits());
    }

    public List<SubjectDTO> getSubjectsByUniversityId(Long universityId) {
        return subjectRepository.findByUniversityId(universityId).stream()
                .map(this::mapToSubjectDTO)
                .toList();
    }
}
