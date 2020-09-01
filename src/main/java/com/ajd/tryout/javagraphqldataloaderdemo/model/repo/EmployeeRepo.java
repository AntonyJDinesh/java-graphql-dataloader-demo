package com.ajd.tryout.javagraphqldataloaderdemo.model.repo;

import com.ajd.tryout.javagraphqldataloaderdemo.model.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepo extends JpaRepository<Employee, Long> {
}
