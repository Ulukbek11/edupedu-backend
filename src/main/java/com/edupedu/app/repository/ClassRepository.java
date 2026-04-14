package com.edupedu.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edupedu.app.model.Class;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {

}
