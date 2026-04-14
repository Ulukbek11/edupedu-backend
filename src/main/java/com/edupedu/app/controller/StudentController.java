package com.edupedu.app.controller;

import com.edupedu.app.service.StudentService;
import com.edupedu.app.exception.ResourceNotFoundException;
import com.edupedu.app.model.Student;
import com.edupedu.app.model.StudentGroup;
import com.edupedu.app.model.User;
import com.edupedu.app.request.BulkAssignRequest;
import com.edupedu.app.request.StudentCreateRequest;
import com.edupedu.app.request.StudentDTO;
import com.edupedu.app.request.StudentUpdateRequest;
import com.edupedu.app.response.StudentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        return new ResponseEntity<>(studentService.getAllStudents(), HttpStatus.OK);
    }

    @GetMapping("/university")
    public ResponseEntity<List<Student>> getAllStudentsFromUniversity(@RequestParam Long universityId) {
        return new ResponseEntity<>(studentService.getAllStudentsFromUniversity(universityId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        return new ResponseEntity<>(studentService.getStudentById(id), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<StudentResponse> getStudentByUserId(@PathVariable Long userId) {
        return new ResponseEntity<>(studentService.getStudentByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<StudentResponse>> getStudentsByGroupId(@PathVariable Long groupId) {
        return new ResponseEntity<>(studentService.getStudentsByGroupId(groupId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@RequestBody @Valid StudentCreateRequest request) {
        return new ResponseEntity<>(studentService.createStudent(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long id,
                                                          @RequestBody @Valid StudentUpdateRequest request) {
        return new ResponseEntity<>(studentService.updateStudent(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<StudentResponse>> getUnassignedStudents(@RequestParam Long universityId) {
        return new ResponseEntity<>(studentService.getUnassignedStudents(universityId), HttpStatus.OK);
    }

    @PutMapping("/assign/{id}")
    public String putMethodName(@PathVariable String id, @RequestBody String entity) {
        //TODO: process PUT request
        
        return entity;
    }

    @PutMapping("/{studentId}/group")
    @Transactional
    public ResponseEntity<StudentDTO> updateStudentClass(
                    @PathVariable Long studentId,
                    @RequestBody Long studentGroupId) {
            return ResponseEntity.ok(studentService.updateStudentGroup(studentId, studentGroupId));
    }

    @PutMapping("/bulk-assign")
        @Transactional
        public ResponseEntity<List<StudentDTO>> bulkAssignStudentsToClass(@RequestBody BulkAssignRequest request) {
                return ResponseEntity.ok(studentService.bulkAssignStudentsToClass(request));
        }



    
}
