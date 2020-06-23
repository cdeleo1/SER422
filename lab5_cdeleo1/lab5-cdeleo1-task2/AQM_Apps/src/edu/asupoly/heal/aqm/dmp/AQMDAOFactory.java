package edu.asupoly.heal.aqm.dmp;

import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Properties;
import java.util.TimeZone;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.asupoly.heal.aqm.dmp.IAQMDAO;
import edu.asupoly.heal.aqm.dmp.AQMDAOJDBCImpl;
import edu.asupoly.heal.aqm.model.DylosReading;
import edu.asupoly.heal.aqm.model.SensordroneReading;
import edu.asupoly.heal.aqm.model.ServerPushEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public final class AQMDAOFactory {
    private static Logger log = Logger.getLogger(AQMDAOFactory.class.getName());
    public static final Calendar AQM_CALENDAR = 
            Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    private static IAQMDAO __dao = null;
    private static Properties __daoProperties = null;

    private AQMDAOFactory() {
        // We do not want this factory instantiated
    }

    public static IAQMDAO getDAO() {
        if (__dao != null) {
            return __dao;
        }
        __daoProperties = new Properties();        
        final File file = new File("../properties/dao.properties");
        try {
            __daoProperties.load(new FileInputStream(file));
            String daoClassName = __daoProperties.getProperty("daoClassName");
            Class<?> daoClass = Class.forName(daoClassName);
            __dao = (IAQMDAO) daoClass.newInstance();
            __dao.init(__daoProperties);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable t1) {
            log.log(Level.SEVERE, "DAO Exception: " + t1);
            t1.printStackTrace();
        }
        return __dao;
    }
}
