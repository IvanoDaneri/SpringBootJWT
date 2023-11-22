package spring.ganimede.security.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.ganimede.security.entity.Role;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long>
{
    List<Role> findByName(String name);

}
