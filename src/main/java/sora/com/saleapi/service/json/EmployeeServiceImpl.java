package sora.com.saleapi.service.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sora.com.saleapi.entity.json.Employee;
import sora.com.saleapi.repo.json.EmployeeRepository;
import sora.com.saleapi.service.EmployeeService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public List<Employee> getAllEmployees(MultipartFile file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());

        List<Employee> employees = Arrays.asList(
                mapper.readValue(file.getInputStream(), Employee[].class)
        );
        return employeeRepository.saveAll(employees);
    }
}
