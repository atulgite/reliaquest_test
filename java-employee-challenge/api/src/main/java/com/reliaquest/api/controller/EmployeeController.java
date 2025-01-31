package com.reliaquest.api.controller;

import com.reliaquest.api.exception.EmployeeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class EmployeeController implements IEmployeeController<Employee, EmployeeInput> {

    private final WebClient webClient;

    @Autowired
    public EmployeeController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8112/api/v1/employee").build();
    }

    @Override
    public ResponseEntity<Mono<List<Employee>>> getAllEmployees() {
        Mono<List<Employee>> employees = webClient.get()
                .uri("/")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Employee>>() {});
        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<Mono<List<Employee>>> getEmployeesByNameSearch(String searchString) {
        Mono<List<Employee>> employees = webClient.get()
                .uri("/search/{searchString}", searchString)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Employee>>() {});
        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<Mono<Employee>> getEmployeeById(String id) {
        Mono<Employee> employee = webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(new EmployeeNotFoundException("Employee not found with id: " + id)))
                .bodyToMono(Employee.class);
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<Mono<Integer>> getHighestSalaryOfEmployees() {
        Mono<Integer> highestSalary = webClient.get()
                .uri("/highestSalary")
                .retrieve()
                .bodyToMono(Integer.class);
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    public ResponseEntity<Mono<List<String>>> getTopTenHighestEarningEmployeeNames() {
        Mono<List<String>> employeeNames = webClient.get()
                .uri("/topTenHighestEarningEmployeeNames")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {});
        return ResponseEntity.ok(employeeNames);
    }

    @Override
    public ResponseEntity<Mono<Employee>> createEmployee(EmployeeInput employeeInput) {
        Mono<Employee> employee = webClient.post()
                .uri("/")
                .bodyValue(employeeInput)
                .retrieve()
                .bodyToMono(Employee.class);
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<Mono<String>> deleteEmployeeById(String id) {
        Mono<String> response = webClient.delete()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(String.class);
        return ResponseEntity.ok(response);
    }
}