package spring.ganimede.security.dao;

import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ganimede.logger.AppLogger;
import spring.ganimede.logger.AppLoggerService;
import spring.ganimede.security.entity.Permission;
import spring.ganimede.security.entity.Role;
import spring.ganimede.security.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService
{
    private AppLogger logger = AppLoggerService.getLogger(UserServiceImpl.class.getName());

    @Autowired
    UserRepository userRepository;

    @Override
    public String getCommaSeparatedAuthorityList(String userName, String password) throws InvalidUserException, InvalidPasswordException
    {
        List<User> users = userRepository.findByName(userName);
        if(users == null || users.isEmpty())
            throw new InvalidUserException(userName);

        User user = users.stream().findFirst().get();
        if(!user.getPassword().equals(password))
            throw new InvalidPasswordException(password);

        List<String> permissioList = new ArrayList<>();
        Set<Role> roles = user.getRoles();
        roles.forEach(role -> {
            Set<Permission> permissions = role.getPermissions();
            permissions.forEach(permission -> {
                permissioList.add(permission.getName().name());
            });
        });

        String ret = "";
        for (String permission : permissioList)
        {
            ret += permission + ",";
        }

        return ret.substring(0, ret.lastIndexOf(","));
    }
}
