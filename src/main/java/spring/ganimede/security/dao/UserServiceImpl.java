package spring.ganimede.security.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ganimede.logger.AppLogger;
import spring.ganimede.logger.AppLoggerService;
import spring.ganimede.security.entity.Permission;
import spring.ganimede.security.entity.Role;
import spring.ganimede.security.entity.User;

import java.time.Instant;
import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService
{
    private final AppLogger logger = AppLoggerService.getLogger(UserServiceImpl.class.getName());

    @Autowired
    UserRepository userRepository;

    @Override
    public String getCommaSeparatedPermissionList(String userName, String password) throws InvalidUserException, InvalidPasswordException
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

    @Override
    public Map<String, Object> getPermissions(String userName, String password) throws InvalidUserException, InvalidPasswordException
    {
        List<User> users = userRepository.findByName(userName);
        if(users == null || users.isEmpty())
            throw new InvalidUserException(userName);

        User user = users.stream().findFirst().get();
        if(user.isUserBlocked())
            throw new BlockedUserException(userName);

        if(!user.getPassword().equals(password))
            throw new InvalidPasswordException(user.getName());

        // Convert the legacy Date object to a modern Instant
        Instant expirationInstant = user.getPasswordExpiration().toInstant();
        if(expirationInstant.isBefore(Instant.now()))
            throw new ExpiredPasswordException(user.getName());

        List<String> permissioList = new ArrayList<>();
        Set<Role> roles = user.getRoles();
        roles.forEach(role -> {
            Set<Permission> permissions = role.getPermissions();
            permissions.forEach(permission -> {
                permissioList.add(permission.getName().name());
            });
        });

        final Map<String, Object> claims = new HashMap<>();
        claims.put(PERMISSIONS, permissioList);
        return claims;
    }

}
