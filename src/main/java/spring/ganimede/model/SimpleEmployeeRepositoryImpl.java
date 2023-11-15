package spring.ganimede.model;

import spring.ganimede.dao.EmployeeNotValidException;
import spring.ganimede.logger.AppLoggerService;
import spring.ganimede.logger.AppLogger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class SimpleEmployeeRepositoryImpl implements SimpleEmployeeRepository {
    private AppLogger logger = AppLoggerService.getLogger(SimpleEmployeeRepositoryImpl.class.getName());

    private Map<Long, SimpleEmployee> employeeMap = new HashMap<>();
    private Long maxId = 0L;

    @PostConstruct
    private void initDb() {
        logger.info("Before initDb");
        SimpleEmployee employee = new SimpleEmployee("Mario", "Rossi", SimpleEmployeeRole.OPERAIO);
        addEmployee(employee);
        employee = new SimpleEmployee("Gianni", "Verdi", SimpleEmployeeRole.OPERAIO);
        addEmployee(employee);
        employee = new SimpleEmployee("Sandro", "Bolchi", SimpleEmployeeRole.IMPIEGATO);
        addEmployee(employee);
        employee = new SimpleEmployee("Giorgio", "Neri", SimpleEmployeeRole.DIRIGENTE);
        addEmployee(employee);
        logger.info("After initDb");
    }

    @Override
    public List<SimpleEmployee> findAll() {
        return new ArrayList<>(employeeMap.values());
    }

    @Override
    public Optional<SimpleEmployee> findById(Long id) {
        return Optional.ofNullable(employeeMap.get(id));
    }

    @Override
    public SimpleEmployee addEmployee(SimpleEmployee newEmployee) throws EmployeeNotValidException
    {
        if (!SimpleEmployee.isValid(newEmployee))
            throw new EmployeeNotValidException();

        if (!employeeMap.containsValue(newEmployee))
        {
            logger.info("Add new employee: " + newEmployee.toString());
            newEmployee.setId(++maxId);
            employeeMap.put(newEmployee.getId(), newEmployee);
            return employeeMap.get(newEmployee.getId());
        } else
        {
            logger.info("Employee: " + newEmployee.toString() + " already present");
            return employeeMap.entrySet().stream()
                    .filter(employeeMap -> employeeMap.getValue().equals(newEmployee))
                    .findFirst()
                    .get()
                    .getValue();
        }
    }


}
