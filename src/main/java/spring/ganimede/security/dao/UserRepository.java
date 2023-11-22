package spring.ganimede.security.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.ganimede.security.entity.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>
{
    List<User> findByName(String name);

}
