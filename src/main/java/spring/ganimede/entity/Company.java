package spring.ganimede.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "companies")
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"id", "lastUpdate", "employees"})
public class Company
{
    @Id
    @Column(name = "company_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMP_SEQ")
    @SequenceGenerator(sequenceName = "COMPANY_SEQ", allocationSize = 1, name = "COMP_SEQ")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "company_code", nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private String code;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "company_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CompanyType type;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "insert_date", nullable = false)
    private Date insertDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_date", nullable = false)
    private Date lastUpdate;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,
            mappedBy = "company", orphanRemoval = true)
    private List<Employee> employees;

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
