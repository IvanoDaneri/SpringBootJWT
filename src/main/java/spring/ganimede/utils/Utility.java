package spring.ganimede.utils;

import spring.ganimede.logger.AppLogger;
import spring.ganimede.logger.AppLoggerService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility
{
    private static AppLogger logger = AppLoggerService.getLogger(Utility.class.getName());

    public static Date getDateWithoutTime(Date date)
    {
        try{
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.parse(dateFormat.format(date));
        }
        catch (ParseException e)
        {
            logger.error("Date string parse error", e);
            return date;
        }
    }

}
