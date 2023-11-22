package spring.ganimede.security.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "permissions")
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"id", "lastUpdate"})
public class Permission
{
    @Id
    @Column(name = "permission_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECURITY_SEQ")
    @SequenceGenerator(sequenceName = "SECURITY_SEQ", allocationSize = 1, name = "SECURITY_SEQ")
    private Long id;

    @Column(name = "permission_name", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    @EqualsAndHashCode.Include
    private PermissionEnum name;

    @Column(name = "permission_description", nullable = false)
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "insert_date", nullable = false)
    private Date insertDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_date", nullable = false)
    private Date lastUpdate;

}
