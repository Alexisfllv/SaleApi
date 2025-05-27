package sora.com.saleapi.service;

import org.springframework.web.multipart.MultipartFile;
import sora.com.saleapi.entity.json.Employee;

import java.io.IOException;
import java.util.List;

public interface EmployeeService {

    List<Employee> getAllEmployees(MultipartFile file) throws IOException;
}
