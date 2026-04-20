package com.edupedu.app.service;

import com.edupedu.app.exception.ResourceNotFoundException;
import com.edupedu.app.model.University;
import com.edupedu.app.model.User;
import com.edupedu.app.model.enums.Role;
import com.edupedu.app.repository.UniversityRepository;
import com.edupedu.app.repository.UserRepository;
import com.edupedu.app.request.UserCreateRequest;
import com.edupedu.app.request.UserUpdateRequest;
import com.edupedu.app.response.StudentResponse;
import com.edupedu.app.response.TeacherResponse;
import com.edupedu.app.response.UserResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final PasswordEncoder passwordEncoder;

    public Object getCurrentUser(User user) {
        if (user.getRole() == Role.ROLE_STUDENT) {
            return mapToStudentResponse(user);
        }
        if (user.getRole() == Role.ROLE_TEACHER) {
            return mapToTeacherResponse(user);
        }
        return mapToResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return mapToResponse(user);
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("User with email '" + request.email() + "' already exists");
        }

        User user = User.builder()
                .email(request.email())
                .phone(request.phone())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .role(request.role())
                .emailVerified(false)
                .enabled(true)
                .locked(false)
                .expired(false)
                .build();

        if (request.universityId() != null) {
            University university = universityRepository.findById(request.universityId())
                    .orElseThrow(() -> new ResourceNotFoundException("University", "id", request.universityId()));
            user.setUniversity(university);
        }

        user = userRepository.save(user);
        return mapToResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new IllegalArgumentException("User with email '" + request.email() + "' already exists");
            }
            user.setEmail(request.email());
        }

        if (request.phone() != null) {
            user.setPhone(request.phone());
        }
        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }
        if (request.role() != null) {
            user.setRole(request.role());
        }
        if (request.emailVerified() != null) {
            user.setEmailVerified(request.emailVerified());
        }
        if (request.enabled() != null) {
            user.setEnabled(request.enabled());
        }
        if (request.locked() != null) {
            user.setLocked(request.locked());
        }
        if (request.expired() != null) {
            user.setExpired(request.expired());
        }
        if (request.universityId() != null) {
            University university = universityRepository.findById(request.universityId())
                    .orElseThrow(() -> new ResourceNotFoundException("University", "id", request.universityId()));
            user.setUniversity(university);
        }

        user = userRepository.save(user);
        return mapToResponse(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getPhone(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getRole(),
                user.getUniversity() != null ? user.getUniversity().getId() : null,
                user.isEmailVerified(),
                user.isEnabled(),
                user.isLocked(),
                user.isExpired(),
                user.getCreatedAt(),
                user.getLastModifiedAt()
        );
    }

    private StudentResponse mapToStudentResponse(User user) {
        // return new StudentResponse(
        //     user.getId(),
        //     user.getEmail(),
        //     user.getFirstName(),
        //     user.getLastName(),
        //     user.getFullName(),
        //     user.getStudent().getId(),
        //         user.getStudent().getStudentNumber(),
        //         user.getStudent().getAccountNumber(),
        //         user.getStudent().getParentPhone(),
        //         user.getStudent().getStudentGroup() != null ? user.getStudent().getStudentGroup().getId() : null,
        //         user.getStudent().getStudentGroup().getName(),
        //         user.getUniversity() != null ? user.getUniversity().getId() : null,
        //         user.isEmailVerified(),
        //         user.isEnabled(),
        //         user.isLocked(),
        //         user.isExpired(),
        //         user.getCreatedAt(),
        //         user.getLastModifiedAt()
        // );

        return new StudentResponse(
        user.getId(),                                      // id
        user.getId(),                                      // user_id (using User ID for both or Student ID?)
        user.getEmail(),                                   // email
        user.getFirstName(),                               // first_name
        user.getLastName(),                                // last_name
        user.getFullName(),                                // full_name
        user.getStudent().getStudentNumber(),                        // student_number
        user.getStudent().getAccountNumber(),                        // account_number
        user.getStudent().getParentPhone(),                          // parent_phone
        user.getStudent().getStudentGroup() != null ? user.getStudent().getStudentGroup().getId() : null,   // student_group_id
        user.getStudent().getStudentGroup() != null ? user.getStudent().getStudentGroup().getName() : null, // student_group_name (FIXED NPE)
        user.getUniversity() != null ? user.getUniversity().getId() : null,             // university_id
        user.isEmailVerified(),                            // email_verified
        user.isEnabled(),                                  // enabled
        user.isLocked(),                                   // locked
        user.isExpired(),                                  // expired
        user.getCreatedAt(),                               // created_at
        user.getLastModifiedAt()                           // last_modified_at
    );
    }

    private TeacherResponse mapToTeacherResponse(User user) {
    var teacher = user.getTeacher(); // The Teacher entity linked to the User
    
    // Safety check for curated group (if the teacher is a curator)
    Long curatorId = null;
    String curatedGroupName = null;
    if (teacher.getCurator() != null) {
        curatorId = teacher.getCurator().getId();
        if (teacher.getCurator().getStudentGroup() != null) {
            curatedGroupName = teacher.getCurator().getStudentGroup().getName();
        }
    }

    // Mapping Lists of Subjects
    List<Long> subjectIds = List.of();
    List<String> subjectNames = List.of();
    if (teacher.getSubjects() != null) {
        subjectIds = teacher.getSubjects().stream().map(s -> s.getId()).toList();
        subjectNames = teacher.getSubjects().stream().map(s -> s.getName()).toList();
    }

    return new TeacherResponse(
        teacher.getId(),                                    // id (Teacher Entity ID)
        user.getId(),                                       // user_id
        user.getEmail(),                                    // email
        user.getFirstName(),                                // first_name
        user.getLastName(),                                 // last_name
        user.getFullName(),                                 // full_name
        teacher.getEmployeeNumber(),                        // employee_number
        subjectIds,                                         // subject_ids
        subjectNames,                                       // subject_names
        curatorId,                                          // curator_id
        curatedGroupName,                                   // curated_student_group_name
        user.getUniversity() != null ? user.getUniversity().getId() : null, // university_id
        user.isEmailVerified(),                             // email_verified
        user.isEnabled(),                                   // enabled
        user.isLocked(),                                    // locked
        user.isExpired(),                                   // expired
        user.getCreatedAt(),                                // created_at
        user.getLastModifiedAt()                            // last_modified_at
    );
}

    public List<UserResponse> getAllUsersFromUniversity(Long universityId) {
        return userRepository.findAllByUniversityId(universityId).stream().map(user -> mapToResponse(user)).toList();
    }


}
