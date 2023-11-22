package spring.ganimede.security.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"id", "password", "passwordExpiration", "userBlocked", "lastUpdate", "roles"})
public class User
{
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECURITY_SEQ")
    @SequenceGenerator(sequenceName = "SECURITY_SEQ", allocationSize = 1, name = "SECURITY_SEQ")
    private Long id;

    @Column(name = "user_name", nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private String name;

    @Column(name = "nick_name", nullable = false)
    private String nickName;

    @Column(name = "password", nullable = false)
    private String password;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "password_expiration", nullable = false)
    private Date passwordExpiration;

    @ColumnDefault("'N'")
    @Column(name = "user_blocked", nullable = false)
    @org.hibernate.annotations.Type(type = "yes_no")
    private boolean userBlocked = false;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "insert_date", nullable = false)
    private Date insertDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_date", nullable = false)
    private Date lastUpdate;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "users_roles",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "role_id")}
    )
    Set<Role> roles = new HashSet<>();


    public String getPassword()
    {
        try
        {
            return EncryptorProvider.getInstance().unmarshal(password);
        }
        catch (Exception e)
        {
            return password;
        }
    }

    public void setPassword(String password)
    {
        try
        {
            this.password = EncryptorProvider.getInstance().marshal(password);
        }
        catch (Exception e)
        {
            this.password = password;
        }
    }

    @PrePersist
    protected void onCreate()
    {
        this.insertDate = new Date();
        this.lastUpdate = insertDate;
    }

    @PreUpdate
    protected void onUpdate()
    {
        this.lastUpdate = new Date();
    }

}
