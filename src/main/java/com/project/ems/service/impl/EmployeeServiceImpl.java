package com.project.ems.service.impl;

import com.project.ems.dto.EmployeeRequest;
import com.project.ems.dto.EmployeeResponse;
import com.project.ems.entity.Employee;
import com.project.ems.exception.DuplicateResourceException;
import com.project.ems.exception.ResourceNotFoundException;
import com.project.ems.mapper.EmployeeMapper;
import com.project.ems.repository.EmployeeRepository;
import com.project.ems.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request){
        if(employeeRepository.existsByEmail(request.email())){
            throw new DuplicateResourceException("Email already exists: " + request.email());
        }
        if(employeeRepository.existsByEmployeeCode(request.employeeCode())){
            throw new DuplicateResourceException("Employee code already exists: " + request.employeeCode());
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

    @Override
    public EmployeeResponse getEmployeeByEmployeeCode(String employeeCode){
        Employee employee = employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not Found with code : " + employeeCode));
        return EmployeeMapper.toResponse(employee);
    }

    @Override
    public Page<EmployeeResponse> getAllEmployees(
            int page,
            int size,
            String sortBy,
            String direction){
        Sort sort = direction.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return employeeRepository.findAll(pageable).map(EmployeeMapper::toResponse);
    }

    @Override
    public Page<EmployeeResponse> searchEmployees(
            String keyword,
            int page,
            int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("employeeCode").descending());
        return employeeRepository.searchEmployees(keyword, pageable)
                .map(EmployeeMapper::toResponse);
    }

    @Override
    public void deleteEmployee(Long id){
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id : " + id));
        employeeRepository.delete(employee);
    }

}
