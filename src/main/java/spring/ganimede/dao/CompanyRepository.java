package spring.ganimede.dao;

import spring.ganimede.entity.Company;
import spring.ganimede.entity.CompanyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long>
{
    List<Company> findByName(String name);
    List<Company> findByCode(String name);
    List<Company> findByType(CompanyType type);
}
