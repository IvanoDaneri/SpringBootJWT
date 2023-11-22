package spring.ganimede.security.dao;

import io.jsonwebtoken.lang.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import spring.ganimede.SpringBootApp;
import spring.ganimede.dao.TestUtil;
import spring.ganimede.security.entity.Permission;
import spring.ganimede.security.entity.Role;
import spring.ganimede.security.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Set;

// This test inserts users and roles in TEST_SPRING_DB
// To insert users and roles in SPRING_DB (production db) use <insert_user_in_test_db.sql> (script generated exporting data from TEST_SPRING_DB tables)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootApp.class})
@TestPropertySource(locations = "classpath:test.oracle.persistence.properties")
@Transactional
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServiceTest
{
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserService userService;

    @Test
    @Rollback(false)
    public void test1_insertUsers()
    {
        User operator = createUser("guest", "myGuest", "myGuestPassword",
                TestUtil.getDate("01-Feb-2024"), "operator");
        Assert.notNull(operator);
        Assert.notNull(userRepository.save(operator));

        User supervisor = createUser("mySup", "mySupervisor", "mySupPassword",
                TestUtil.getDate("01-Feb-2024"), "supervisor");
        Assert.notNull(supervisor);
        Assert.notNull(userRepository.save(supervisor));

        User admin = createUser("myAdmin", "myAdministrator", "myAdminPassword",
                TestUtil.getDate("01-Feb-2024"), "admin");
        Assert.notNull(admin);
        Assert.notNull(userRepository.save(admin));
    }

    @Test
    public void test2_readUserPermissions()
    {
        printUserPermission("guest");
        printUserPermission("mySup");
        printUserPermission("myAdmin");
    }

    @Test
    public void test3_checkCommaSeparatedAuthorityList()
    {
        Assert.notNull(userService.getCommaSeparatedAuthorityList("guest", "myGuestPassword"));
        Assert.notNull(userService.getCommaSeparatedAuthorityList("mySup", "mySupPassword"));
        Assert.notNull(userService.getCommaSeparatedAuthorityList("myAdmin", "myAdminPassword"));
    }

    private void printUserPermission(String userName)
    {
        System.out.println("User permissions of user: " + userName);

        List<User> users = userRepository.findByName(userName);
        if(users != null && !users.isEmpty())
        {
            User user = users.stream().findFirst().get();
            Set<Role> roles = user.getRoles();
            roles.forEach(role -> {
                Set<Permission> permissions = role.getPermissions();
                permissions.forEach(permission -> {
                    System.out.println(permission);
                });
            });
        }
    }

    private User createUser(String userName, String nickName, String password, Date passwordExpiration, String roleName)
    {
        List<User> users = userRepository.findByName(userName);

        // Update user
        if(users != null && !users.isEmpty())
        {
            User user = users.stream().findFirst().get();
            user.setNickName(nickName);
            user.setPassword(password);
            user.setPasswordExpiration(passwordExpiration);

            List<Role> roles = roleRepository.findByName(roleName);
            if (roles != null && !roles.isEmpty()) {
                user.getRoles().add(roles.stream().findFirst().get());
            }

            return user;
        }
        // Insert new user
        else
        {
            User newUser = new User();
            newUser.setName(userName);
            newUser.setNickName(nickName);
            newUser.setPassword(password);
            newUser.setPasswordExpiration(passwordExpiration);

            List<Role> roles = roleRepository.findByName(roleName);
            if (roles != null && !roles.isEmpty()) {
                newUser.getRoles().add(roles.stream().findFirst().get());
            }

            return newUser;
        }
    }
}
