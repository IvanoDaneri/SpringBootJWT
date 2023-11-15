package spring.ganimede.entity;

import javax.persistence.*;
import java.util.Date;

import lombok.*;

@Entity
@Table(name = "employees")
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"id", "lastUpdate"})
public class Employee
{
    @Id
    @Column(name = "employee_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMP_SEQ")
    @SequenceGenerator(sequenceName = "EMPLOYEE_SEQ", allocationSize = 1, name = "EMP_SEQ")
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "surname", nullable = false)
    private String surname;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "birthday_date", nullable = false)
    private Date birthday;
    @Column(name = "fiscal_code", nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private String fiscalCode;
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private EmployeeRole role;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "insert_date", nullable = false)
    private Date insertDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_date", nullable = false)
    private Date lastUpdate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "company_id")
    private Company company;

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
