package spring.ganimede.datasource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

//@Configuration
//@EnableJpaRepositories(basePackages = "esa.galileo")
//@EnableTransactionManagement
public class H2JpaConfig
{
    @Autowired
    private Environment environment;

//    @Bean
//    public DataSource dataSource() {
//            DriverManagerDataSource dataSource = new DriverManagerDataSource();
//            dataSource.setDriverClassName(environment.getProperty("spring.datasource.driver-class-name"));
//            dataSource.setUrl(environment.getProperty("spring.datasource.url"));
//            dataSource.setUsername("spring.datasource.username");
//            dataSource.setPassword("spring.datasource.username");
//            return dataSource;
//    }

}
