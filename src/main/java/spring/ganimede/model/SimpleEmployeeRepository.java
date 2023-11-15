package spring.ganimede.model;

import spring.ganimede.dao.EmployeeNotValidException;

import java.util.List;
import java.util.Optional;

public interface SimpleEmployeeRepository
{
    List<SimpleEmployee> findAll();

    Optional<SimpleEmployee> findById(Long id);

    SimpleEmployee addEmployee(SimpleEmployee newEmployee) throws EmployeeNotValidException;
}
