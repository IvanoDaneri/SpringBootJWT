package spring.ganimede.controller;

import spring.ganimede.dao.CompanyNotValidException;
import spring.ganimede.dao.CompanyService;
import spring.ganimede.logger.AppLogger;
import spring.ganimede.logger.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

//@RequestMapping( value = Constants.APPLICATION_SERVICES)
@RestController
public class CompanyController
{
    private AppLogger logger = AppLoggerService.getLogger(CompanyController.class.getName());

    @Autowired
    CompanyService companyService;

    @GetMapping("/companies")
    public List<CompanyDto> findAll()
    {
        return companyService.findAll();
    }

    @GetMapping("/companies/{id}")
    public CompanyDto findById(@PathVariable Long id)
    {
        return companyService.findById(id);
    }

    @GetMapping("/companies/findByName/{name}")
    public List<CompanyDto> findByName(@PathVariable String name)
    {
        return companyService.findByName(name);
    }

    @GetMapping("/companies/findByCode/{code}")
    public CompanyDto findByCode(@PathVariable String code)
    {
        return companyService.findByCode(code);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value="/companies/addCompany", method = RequestMethod.POST, consumes = "application/json")
    public CompanyDto addCompany(@Valid @RequestBody CompanyDto companyDto)
    {
        try {
            return companyService.addCompany(companyDto);
        }
        catch (Exception e)
        {
            logger.error("Error in insert/update company: " + companyDto.toString(), e);
            throw new CompanyNotValidException(companyDto, e.getMessage());
        }
    }
}
