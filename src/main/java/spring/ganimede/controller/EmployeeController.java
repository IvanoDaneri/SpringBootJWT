package spring.ganimede.controller;

import spring.ganimede.dao.EmployeeNotValidException;
import spring.ganimede.entity.EmployeeRole;
import spring.ganimede.dao.EmployeeService;
import spring.ganimede.logger.AppLogger;
import spring.ganimede.logger.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

//@RequestMapping( value = Constants.APPLICATION_SERVICES )
@RestController
public class EmployeeController
{
    private AppLogger logger = AppLoggerService.getLogger(EmployeeController.class.getName());

    @Autowired
    EmployeeService employeeService;

    @GetMapping("/employees")
    public List<EmployeeDto> findAll()
    {
        return employeeService.findAll();
    }

    @GetMapping("/employees/{id}")
    public EmployeeDto findById(@PathVariable Long id)
    {
        return employeeService.findById(id);
    }

    @GetMapping("/employees/findByName/{name}")
    public List<EmployeeDto> findByName(@PathVariable String name)
    {
        return employeeService.findByName(name);
    }

    @GetMapping("/employees/findBySurname/{surname}")
    public List<EmployeeDto> findBySurname(@PathVariable String surname)
    {
        return employeeService.findBySurname(surname);
    }

    @GetMapping("/employees/findByNameSurname/{name}/{surname}")
    public List<EmployeeDto> findByNameSurname(@PathVariable String name, @PathVariable String surname)
    {
        return employeeService.findByNameSurname(name, surname);
    }

    @GetMapping("/employees/findByFiscalCode/{fiscalCode}")
    public EmployeeDto findByFiscalCode(@PathVariable String fiscalCode)
    {
        return employeeService.findByFiscalCode(fiscalCode);
    }

    @GetMapping("/employees/findByRole/{role}")
    public List<EmployeeDto> findByRole(@PathVariable EmployeeRole role)
    {
        return employeeService.findByRole(role);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value="/employees/addEmployee", method = RequestMethod.POST, consumes = "application/json")
    public EmployeeDto addEmployee(@Valid @RequestBody EmployeeDto employeeDto)
    {
        try {
            return employeeService.addEmployee(employeeDto);
        }
        catch (Exception e)
        {
            logger.error("Error in insert/update company: " + employeeDto.toString(), e);
            throw new EmployeeNotValidException(employeeDto, e.getMessage());
        }
    }
}
