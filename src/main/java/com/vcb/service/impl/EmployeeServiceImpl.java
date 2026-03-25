package com.vcb.service.impl;

import com.vcb.exception.EmployeeNotFound;
import com.vcb.exception.EmployeeSaveException;
import com.vcb.exception.ErrorCode;
import com.vcb.mapper.EmployeeMapper;
import com.vcb.model.dto.EmployeeRequest;
import com.vcb.model.dto.EmployeeResponse;
import com.vcb.repository.EmployeeRepository;
import com.vcb.service.EmployeeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream().map(employeeMapper::toResponse).toList();
    }

    @Override
    public EmployeeResponse getEmployeeByUsername(String code) {
        return employeeMapper.toResponse(employeeRepository.findEmployeeByUsername(code)
                .orElseThrow(() -> new EmployeeNotFound(code)));
    }

    @Override
    public void addEmployee(EmployeeRequest dto) {
        try {
            employeeRepository.save(employeeMapper.toEntity(dto));
        } catch (DataIntegrityViolationException e) {
            log.error("Constraint violation when saving employee: {}", e.getMessage());
            throw new EmployeeSaveException(ErrorCode.EMPLOYEE_ALREADY_EXISTS);
        } catch (Exception e) {
            log.error("Unexpected error when saving employee: {}", e.getMessage());
            throw new EmployeeSaveException(ErrorCode.EMPLOYEE_SAVE_FAILED);
        }
    }

    @Override
    @Transactional
    public void deleteEmployeeBy(String username) {
        if (!employeeRepository.existsByUsername(username)) {
            throw new EmployeeNotFound(username);
        }
        try {
            employeeRepository.deleteByUsername(username);
        } catch (Exception e) {
            log.error("Unexpected error when deleting employee {}: {}", username, e.getMessage());
            throw new EmployeeSaveException(ErrorCode.EMPLOYEE_DELETE_FAILED);
        }
    }
}

