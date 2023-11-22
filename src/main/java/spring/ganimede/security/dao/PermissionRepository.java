package spring.ganimede.security.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.ganimede.security.entity.Permission;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long>
{
    List<Permission> findByName(String name);

}
