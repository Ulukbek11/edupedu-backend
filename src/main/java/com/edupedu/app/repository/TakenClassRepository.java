package com.edupedu.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edupedu.app.model.TakenClass;

@Repository
public interface TakenClassRepository extends JpaRepository<TakenClass, Long> {
    
}
