package spring.ganimede.security.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"id", "lastUpdate", "permissions"})
public class Role
{
    @Id
    @Column(name = "role_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECURITY_SEQ")
    @SequenceGenerator(sequenceName = "SECURITY_SEQ", allocationSize = 1, name = "SECURITY_SEQ")
    private Long id;

    @Column(name = "role_name", nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private String name;

    @Column(name = "role_description", nullable = false)
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "insert_date", nullable = false)
    private Date insertDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_date", nullable = false)
    private Date lastUpdate;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "roles_permissions",
            joinColumns = { @JoinColumn(name = "role_id") },
            inverseJoinColumns = { @JoinColumn(name = "permission_id")}
    )
    Set<Permission> permissions = new HashSet<>();

}
