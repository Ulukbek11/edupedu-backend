package com.edupedu.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edupedu.app.model.StudentGroup;
import com.edupedu.app.model.University;

@Repository
public interface StudentGroupRepository extends JpaRepository<StudentGroup, Long> {

    StudentGroup findByUniversity(University university);
}
