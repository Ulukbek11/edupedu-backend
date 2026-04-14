package com.edupedu.app.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edupedu.app.request.CreateSubjectRequest;
import com.edupedu.app.request.SubjectDTO;
import com.edupedu.app.service.SubjectService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping("/admin/subjects")
    public ResponseEntity<List<SubjectDTO>> getAllSubjects() {
        return new ResponseEntity<>(subjectService.getAllSubjects(), HttpStatus.OK);
    }

    @GetMapping("/university/{universityId}/subjects")
    public ResponseEntity<List<SubjectDTO>> getSubjectsByUniversityId(@PathVariable Long universityId) {
        return new ResponseEntity<>(subjectService.getSubjectsByUniversityId(universityId), HttpStatus.OK);
    }

    @GetMapping("/admin/subjects/{id}")
    public ResponseEntity<SubjectDTO> getSubjectById(@PathVariable Long id) {
        return new ResponseEntity<>(subjectService.getSubjectById(id), HttpStatus.OK);
    }

    @PostMapping("/admin/subjects")
    public ResponseEntity<SubjectDTO> createSubject(@RequestBody @Valid CreateSubjectRequest request) {
        return new ResponseEntity<>(subjectService.createSubject(request), HttpStatus.CREATED);
    }

    @PutMapping("/admin/subjects/{id}")
    public ResponseEntity<SubjectDTO> updateSubject(@PathVariable Long id,
            @RequestBody @Valid CreateSubjectRequest request) {
        return new ResponseEntity<>(subjectService.updateSubject(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/admin/subjects/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
