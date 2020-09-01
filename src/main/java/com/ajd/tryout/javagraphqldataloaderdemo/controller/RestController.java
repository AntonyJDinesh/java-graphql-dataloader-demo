package com.ajd.tryout.javagraphqldataloaderdemo.controller;

import com.ajd.tryout.javagraphqldataloaderdemo.model.entity.Employee;
import com.ajd.tryout.javagraphqldataloaderdemo.model.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/rest")
public class RestController {

    @Autowired
    private EmployeeService employeeService;

    @RequestMapping(path="/employee")
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }
}
