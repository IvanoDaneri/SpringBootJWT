package spring.ganimede;

import spring.ganimede.logger.AppLoggerService;
import spring.ganimede.logger.AppLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import javax.annotation.PostConstruct;


@SpringBootApplication
public class SpringBootApp extends SpringBootServletInitializer
{
	private AppLogger logger = AppLoggerService.getLogger( this.getClass().getName() );

	public static void main(String[] args)
	{
		SpringApplication.run( SpringBootApp.class, args );
	}	/* main */


	@Override
	protected SpringApplicationBuilder configure( SpringApplicationBuilder application )
	{
		return application.sources( SpringBootApp.class );
	}	/* configure */


	@PostConstruct
	private void init()
	{
		logger.info("Test, I'm in init method!");
	}

}
