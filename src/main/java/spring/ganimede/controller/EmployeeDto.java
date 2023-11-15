package spring.ganimede.controller;

import spring.ganimede.entity.EmployeeRole;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EmployeeDto implements Serializable
{
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String surname;
    @NotNull
    private Date birthday;
    @NotEmpty
    @EqualsAndHashCode.Include
    private String fiscalCode;
    @NotNull
    private EmployeeRole role;
    private String companyName;
    @NotEmpty
    private String companyCode;
    private Date insertDate;
    private Date lastUpdate;

    public static boolean isValid(EmployeeDto employee)
    {
        if(employee == null)
            return false;

        if(employee.getName() == null || employee.getName().isEmpty())
            return false;

        if(employee.getSurname() == null || employee.getSurname().isEmpty())
            return false;

        if(employee.getBirthday() == null)
            return false;

        if(employee.getFiscalCode() == null || employee.getFiscalCode().isEmpty())
            return false;

        if(employee.getRole() == null)
            return false;

        if(employee.getCompanyCode() == null || employee.getCompanyCode().isEmpty())
            return false;

        return true;
    }
}
