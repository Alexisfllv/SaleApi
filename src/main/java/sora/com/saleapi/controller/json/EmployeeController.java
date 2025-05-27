package sora.com.saleapi.controller.json;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sora.com.saleapi.entity.json.Employee;
import sora.com.saleapi.service.EmployeeService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/importar")
    public ResponseEntity<List<Employee>> importEmployees(@RequestParam ("file") MultipartFile file)  throws IOException {
        List<Employee> employees = employeeService.getAllEmployees(file);
        return ResponseEntity.ok(employees);
    }
}
