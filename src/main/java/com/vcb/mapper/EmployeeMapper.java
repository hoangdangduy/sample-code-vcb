package com.vcb.mapper;

import com.vcb.entity.Employee;
import com.vcb.model.dto.EmployeeRequest;
import com.vcb.model.dto.EmployeeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EmployeeMapper {

    EmployeeResponse toResponse(Employee employee);

    Employee toEntity(EmployeeRequest request);
}

