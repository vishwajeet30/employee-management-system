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
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id : " + id));
        if(!employee.getEmployeeCode().equals(request.employeeCode()) && employeeRepository.existsByEmployeeCode(request.employeeCode())){
            throw new DuplicateResourceException("Employee code already exists : " + request.employeeCode());
        }
        if(!employee.getEmail().equals(request.email()) &&  employeeRepository.existsByEmail(request.email())){
            throw new DuplicateResourceException("Email already exists : " + request.email());
        }

        employee.setEmployeeCode(request.employeeCode());
        employee.setFirstName(request.firstName());
        employee.setLastName(request.lastName());
        employee.setEmail(request.email());
        employee.setPhone(request.phone());
        employee.setDepartment(request.department());
        employee.setDesignation(request.designation());
        employee.setSalary(request.salary());
        employee.setJoiningDate(request.joiningDate());

        Employee updatedEmployee = employeeRepository.save(employee);

        return EmployeeMapper.toResponse(updatedEmployee);
    }

    @Override
    public EmployeeResponse getEmployeeById(Long id){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: "+id));
        return EmployeeMapper.toResponse(employee);
    }

}
