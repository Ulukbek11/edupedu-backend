package com.edupedu.app.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edupedu.app.exception.ResourceNotFoundException;
import com.edupedu.app.model.StudentGroup;

import com.edupedu.app.repository.StudentGroupRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentGroupService {

    private final StudentGroupRepository studentGroupRepository;

    @Transactional(readOnly = true)
    public List<StudentGroup> getAllStudentGroups() {
        return studentGroupRepository.findAll();
    }

    @Transactional(readOnly = true)
    public StudentGroup getStudentGroupById(Long id) {
        return studentGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StudentGroup", "id", id));
    }

    @Transactional
    public StudentGroup createStudentGroup(StudentGroup studentGroup) {
        return studentGroupRepository.save(studentGroup);
    }

    @Transactional
    public StudentGroup updateStudentGroup(Long id, StudentGroup updatedStudentGroup) {
        StudentGroup existingStudentGroup = studentGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StudentGroup", "id", id));

        existingStudentGroup.setName(updatedStudentGroup.getName());
        existingStudentGroup.setYear(updatedStudentGroup.getYear());
        existingStudentGroup.setFaculty(updatedStudentGroup.getFaculty());
        existingStudentGroup.setUniversity(updatedStudentGroup.getUniversity());

        return studentGroupRepository.save(existingStudentGroup);
    }

    @Transactional
    public void deleteStudentGroup(Long id) {
        StudentGroup studentGroup = studentGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StudentGroup", "id", id));
        studentGroupRepository.delete(studentGroup);
    }
}
