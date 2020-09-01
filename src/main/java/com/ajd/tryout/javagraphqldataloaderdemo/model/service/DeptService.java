package com.ajd.tryout.javagraphqldataloaderdemo.model.service;

import com.ajd.tryout.javagraphqldataloaderdemo.model.entity.Dept;
import com.ajd.tryout.javagraphqldataloaderdemo.model.repo.DeptRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeptService {
    @Autowired
    private DeptRepo deptRepo;

    public Dept getDeptById(Long id) {
        return deptRepo.findById(id).orElse(null);
    }

    public List<Dept> getDeptsById(List<Long> ids) {
        System.out.println("getDeptsById: size - " + ids.size());
        return deptRepo.findAllById(ids);
    }
}
