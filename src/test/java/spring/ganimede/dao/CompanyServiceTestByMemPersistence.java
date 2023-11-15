package spring.ganimede.dao;

import spring.ganimede.SpringBootApp;
import spring.ganimede.controller.CompanyDto;
import spring.ganimede.entity.CompanyType;
import io.jsonwebtoken.lang.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootApp.class})
@TestPropertySource(locations = "classpath:test-mem-persistence.properties")
public class CompanyServiceTestByMemPersistence
{
    @Autowired
    CompanyService companyService;

    @Test
    public void insertCompany()
    {
        String companyCode = "0123456";
        CompanyDto companyDto = TestUtil.getCompany("Cybertech s.p.a.", companyCode, "Via Roma 34, Milano", CompanyType.HIGH_TECH);
        CompanyDto savedCompanyDto = companyService.addCompany(companyDto);
        Assert.notNull(savedCompanyDto);

        List<CompanyDto> companyDtoList = companyService.findAll();
        Assert.notNull(companyDtoList);
        Assert.notEmpty(companyDtoList);

        CompanyDto findCompanyDto = companyService.findByCode(companyCode);
        Assert.notNull(findCompanyDto);
    }

}
