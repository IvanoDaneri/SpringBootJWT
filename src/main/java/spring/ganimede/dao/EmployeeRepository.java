package spring.ganimede.dao;

import spring.ganimede.entity.Employee;
import spring.ganimede.entity.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.stream.Stream;

public interface EmployeeRepository extends JpaRepository<Employee, Long>
{
    List<Employee> findByName(String name);
    List<Employee> findBySurname(String surname);
    List<Employee> findByFiscalCode(String fiscalCode);
    List<Employee> findByRole(EmployeeRole role);
    @Query("select e from Employee e where e.name = :name and e.surname = :surname")
    Stream<Employee> findByNameAndSurnameReturnStream(@Param("name") String name, @Param("surname") String surname);
}
