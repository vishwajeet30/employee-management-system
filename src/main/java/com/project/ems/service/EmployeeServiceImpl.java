package com.project.ems.service;

import com.project.ems.dto.EmployeeRequest;
import com.project.ems.dto.EmployeeResponse;
import com.project.ems.entity.Employee;
import com.project.ems.exception.DuplicateResourceException;
import com.project.ems.exception.ResourceNotFoundException;
import com.project.ems.mapper.EmployeeMapper;
import com.project.ems.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request){
        if(employeeRepository.existsByEmail(request.email())){
            throw new DuplicateResourceException("Email already exists");
        }
        if(employeeRepository.existsByEmployeeCode(request.employeeCode())){
            throw new DuplicateResourceException("Employee code already exists");
        }
        Employee employee = EmployeeMapper.toEntity(request);
        Employee savedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.toResponse(savedEmployee);
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID :" + id));
    }
}
