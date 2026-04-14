package com.edupedu.app.controller;

import com.edupedu.app.service.UserService;
import com.edupedu.app.model.User;
import com.edupedu.app.request.UserCreateRequest;
import com.edupedu.app.request.UserUpdateRequest;
import com.edupedu.app.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/university")
    public ResponseEntity<List<UserResponse>> getAllUsersFromUniversity(@RequestParam Long universityId) {
        return new ResponseEntity<>(userService.getAllUsersFromUniversity(universityId), HttpStatus.OK);
    }
    

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return new ResponseEntity<>(userService.getUserByEmail(email), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request) {
        return new ResponseEntity<>(userService.createUser(request), HttpStatus.CREATED);
    }

    // @PutMapping("/{id}")
    // public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
    //                                                 @RequestBody @Valid UserUpdateRequest request) {
    //     return new ResponseEntity<>(userService.updateUser(id, request), HttpStatus.OK);
    // }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
