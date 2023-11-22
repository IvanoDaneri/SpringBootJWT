package spring.ganimede.old_model;

import java.util.Date;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"id", "role", "date"})
@ToString(exclude = {"id", "date"})
public class SimpleEmployee
{
    private Long id;
    private String name;
    private String surname;
    private SimpleEmployeeRole role;
    private Date date;


    public SimpleEmployee(String name, String surname, SimpleEmployeeRole role)
    {
        this.name = name;
        this.surname = surname;
        this.role = role;
    }

    public static boolean isValid(SimpleEmployee employee)
    {
        if(employee == null)
            return false;

        if(employee.getSurname() == null || employee.getSurname().isEmpty())
            return false;

        if(employee.getName() == null || employee.getName().isEmpty())
            return false;

        if(employee.getRole() == null)
            return false;

        return true;
    }
}
