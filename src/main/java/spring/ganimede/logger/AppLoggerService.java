package spring.ganimede.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppLoggerService implements AppLogger
{
    private final Logger logger;

    public static synchronized AppLogger getLogger(final String className )
        {
        return new AppLoggerService( className );
        }

    protected AppLoggerService(final String className )
        {
        this.logger = LogManager.getLogger(className);
        }

    public void info(final String message)
    {
        this.logger.info(message);
    }

    public void info(final String message, final Throwable t)
    {
        this.logger.info(message, t);
    }

    public void warn(final String message)
    {
        this.logger.warn(message);
    }

    public void warn(final String message, final Throwable t)
    {
        this.logger.warn(message, t);
    }

    public void error(final String message)
    {
        this.logger.error(message);
    }

    public void error(final String message, final Throwable t)
    {
        this.logger.error(message, t);
    }
}
