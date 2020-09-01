package com.ajd.tryout.javagraphqldataloaderdemo.model.repo;

import com.ajd.tryout.javagraphqldataloaderdemo.model.entity.Dept;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeptRepo extends JpaRepository<Dept, Long> {
}
