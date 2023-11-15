package net.javaguides.springboot.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.javaguides.springboot.dto.DepartmentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.javaguides.springboot.exception.ResourceNotFoundException;
import net.javaguides.springboot.model.Employee;
import net.javaguides.springboot.repository.EmployeeRepository;
import org.springframework.web.reactive.function.client.WebClient;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class EmployeeController {

	@Autowired
	private EmployeeRepository employeeRepository;
	private WebClient webClient;
	// get all employees
	@Autowired
	public EmployeeController(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.build();
	}
	@GetMapping("/employees")
	public List<Employee> getAllEmployees(){
		return employeeRepository.findAll();
	}		
	
	// create employee rest api
	@PostMapping("/employees")
	public Employee createEmployee(@RequestBody Employee employee) {
		return employeeRepository.save(employee);
	}
	
	// get employee by id rest api
//	@GetMapping("/employees/{id}")
//
//	public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
//		Employee employee = employeeRepository.findById(id)
//				.orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id :" + id));
////		String departmentCode = (employee.getDepartmentCode());
////System.out.println(departmentCode);
//		 //Use WebClient to retrieve department data based on the department code
//		DepartmentDto departmentDto = webClient.get()
//				.uri("http://localhost:8081/api/departments/"+employee.getDepartmentCode())
//				.retrieve()
//				.bodyToMono(DepartmentDto.class)
//				.block();
//
//		return ResponseEntity.ok(employee);
//
//	}
	@GetMapping("/employees/{id}")

	public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id :" + id));
		String departmentCode = (employee.getDepartmentCode());
		System.out.println(departmentCode);
		//Use WebClient to retrieve department data based on the department code
//		DepartmentDto departmentDto = webClient.get()
//				.uri("http://localhost:8080/api/departments", departmentCode)
//				.retrieve()
//				.bodyToMono(DepartmentDto.class)
//				.block();

		return ResponseEntity.ok(employee);


	}

	// update employee rest api
	
	@PutMapping("/employees/{id}")
	public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails){
		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id :" + id));
		
		employee.setFirstName(employeeDetails.getFirstName());
		employee.setLastName(employeeDetails.getLastName());
		employee.setEmailId(employeeDetails.getEmailId());
		
		Employee updatedEmployee = employeeRepository.save(employee);
		return ResponseEntity.ok(updatedEmployee);
	}
	
	// delete employee rest api
	@DeleteMapping("/employees/{id}")
	public ResponseEntity<Map<String, Boolean>> deleteEmployee(@PathVariable Long id){
		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id :" + id));
		
		employeeRepository.delete(employee);
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/employees/department/{id}")
	public ResponseEntity<Map<String, Object>> getEmployeeDepartmentById(@PathVariable Long id) {
		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not exist with id :" + id));

		// Retrieve department data based on the department code
		DepartmentDto departmentDto = webClient.get()
				.uri("http://localhost:8081/api/departments/{code}", employee.getDepartmentCode())
				.retrieve()
				.bodyToMono(DepartmentDto.class)
				.block();

		if (departmentDto == null) {
			throw new ResourceNotFoundException("Department not found for department code: " + employee.getDepartmentCode());
		}

		// Create a map to hold both employee and department data
		Map<String, Object> result = new HashMap<>();
		result.put("employee", employee);
		result.put("department", departmentDto);

		return ResponseEntity.ok(result);
	}


}
