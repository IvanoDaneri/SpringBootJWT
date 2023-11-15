package spring.ganimede.controller;

import spring.ganimede.dao.EmployeeNotFoundException;
import spring.ganimede.model.SimpleEmployeeRepository;
import spring.ganimede.model.SimpleEmployee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SimpleEmployeeController
{
    @Autowired
    SimpleEmployeeRepository repository;

    @GetMapping("/simpleEmployees")
    public List<SimpleEmployee> findAll()
    {
        return repository.findAll();
    }

    @GetMapping("/simpleEmployees/{id}")
    public SimpleEmployee findById(@PathVariable Long id)
    {
        return repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @RequestMapping(value="/simpleEmployees/addSimpleEmployee", method = RequestMethod.POST, consumes = "application/json")
    public SimpleEmployee addEmployee(@RequestBody SimpleEmployee employee)
    {
        return repository.addEmployee(employee);
    }
}
