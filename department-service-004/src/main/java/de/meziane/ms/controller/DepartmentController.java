package de.meziane.ms.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.meziane.ms.domain.Department;
import de.meziane.ms.domain.Employee;
import de.meziane.ms.repository.DepartmentRepository;

@RestController
public class DepartmentController {
	
	@Autowired
	DepartmentRepository departmentRepository;
			
	@GetMapping("/departments")
	public List<Department> findAll() {
		List<Department> depts = departmentRepository.findAll();
		return depts;
	}
	
	
	@GetMapping("/departments/{id}")
	public Department findById(@PathVariable Long id) {
		Department dept = departmentRepository.getOne(id); 
		return dept;
	}
	
	@GetMapping("/departments/with-employees/{id}")
	public Department findByIdWithEmployees(@PathVariable Long id) {
		
		Department dept = departmentRepository.getOne(id);
		RestTemplate restTemplate = new RestTemplate();
		String EmployeeResourceUrl   = "http://localhost:8082/{id}/employees";
		 
		ResponseEntity<List<Employee>> response = restTemplate.exchange(EmployeeResourceUrl, 
				HttpMethod.GET, null, new ParameterizedTypeReference<List<Employee>>() {}, id);
		List<Employee> employees = response.getBody();	
		
		dept.setEmployees(employees);
		
		
		return dept;
	}

}
