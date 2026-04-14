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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edupedu.app.model.Faculty;
import com.edupedu.app.service.FacultyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    @GetMapping("/faculties")
    public ResponseEntity<List<Faculty>> getAllFaculties() {
        return new ResponseEntity<>(facultyService.getAllFaculties(), HttpStatus.OK);
    }

    @GetMapping("/faculties/university")
    public ResponseEntity<List<Faculty>> getFacultiesByUniversity(@RequestParam Long universityId) {
        return new ResponseEntity<>(facultyService.getFacultiesByUniversityId(universityId), HttpStatus.OK);
    }

    @GetMapping("/faculties/{id}")
    public ResponseEntity<Faculty> getFacultyById(@PathVariable Long id) {
        return new ResponseEntity<>(facultyService.getFacultyById(id), HttpStatus.OK);
    }

    @PostMapping("/admin/faculties")
    public ResponseEntity<Faculty> createFaculty(@RequestBody @Valid Faculty faculty) {
        return new ResponseEntity<>(facultyService.createFaculty(faculty), HttpStatus.CREATED);
    }

    @PutMapping("/admin/faculties/{id}")
    public ResponseEntity<Faculty> updateFaculty(@PathVariable Long id, @RequestBody @Valid Faculty faculty) {
        return new ResponseEntity<>(facultyService.updateFaculty(id, faculty), HttpStatus.OK);
    }

    @DeleteMapping("/admin/faculties/{id}")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
