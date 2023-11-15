package spring.ganimede.controller;

import spring.ganimede.entity.CompanyType;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class CompanyDto implements Serializable
{
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    @EqualsAndHashCode.Include
    private String code;
    private String address;
    @NotNull
    private CompanyType type;
    private Date insertDate;
    private Date lastUpdate;

    public static boolean isValid(CompanyDto company)
    {
        if(company == null)
            return false;

        if(company.getName() == null || company.getName().isEmpty())
            return false;

        if(company.getCode() == null || company.getCode().isEmpty())
            return false;

        if(company.getAddress() == null || company.getAddress().isEmpty())
            return false;

        if(company.getType() == null)
            return false;

        return true;
    }

}
