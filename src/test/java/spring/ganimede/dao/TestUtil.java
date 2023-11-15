package spring.ganimede.dao;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import spring.ganimede.controller.CompanyDto;
import spring.ganimede.controller.EmployeeDto;
import spring.ganimede.entity.CompanyType;
import spring.ganimede.entity.EmployeeRole;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TestUtil<T>
{
    private Gson gson = new Gson();

    public static CompanyDto getCompany(String name, String code, String address, CompanyType type)
    {
        CompanyDto companyDto = new CompanyDto();
        companyDto.setName(name);
        companyDto.setCode(code);
        companyDto.setAddress(address);
        companyDto.setType(type);
        return companyDto;
    }

    public static EmployeeDto getEmployee(String name, String surname, Date birthday, String fiscalCode, EmployeeRole role, String companyCode)
    {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setName(name);
        employeeDto.setSurname(surname);
        employeeDto.setBirthday(birthday);
        employeeDto.setFiscalCode(fiscalCode);
        employeeDto.setRole(role);
        employeeDto.setCompanyCode(companyCode);
        return employeeDto;
    }

    public static Date getDate(final String dateInString)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        Date date = null;
        try
        {
            date = formatter.parse(dateInString);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date;
    }

    public List<T> convertResult(String result)
    {
        Type listType = new TypeToken<ArrayList<T>>()
        {}.getType();

        ArrayList<T> companyDtoList = gson.fromJson(result, listType);
        return companyDtoList;
    }
}
