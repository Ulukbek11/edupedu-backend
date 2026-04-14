package com.edupedu.app.repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.edupedu.app.model.Subject;
import com.edupedu.app.request.SubjectDTO;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    @Query("SELECT s FROM Subject s WHERE s.id IN :ids")
Set<Subject> findAllByIdIn(@Param("ids") List<Long> ids);

    @Query("SELECT s FROM Subject s JOIN s.teachers t WHERE t.user.university.id = :universityId")
    List<Subject> findByUniversityId(@Param("universityId") Long universityId);

}
