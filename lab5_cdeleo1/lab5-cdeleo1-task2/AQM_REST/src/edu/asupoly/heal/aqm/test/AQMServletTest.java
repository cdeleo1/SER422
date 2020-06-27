package edu.asupoly.heal.aqm.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import edu.asupoly.heal.aqm.dmp.AQMDAOFactory;
import edu.asupoly.heal.aqm.dmp.IAQMDAO;

import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class AQMServletTest extends HttpServlet {

    private static Logger log = Logger.getLogger(AQMServletTest.class.getName());

    public final void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JSONArray prtrdArray = new JSONArray();
        JSONArray senrdArray = new JSONArray();
        JSONArray commonArray = new JSONArray();
        IAQMDAO dao = AQMDAOFactory.getDAO();
        PrintWriter out = response.getWriter();
        try {
            senrdArray = dao.findSensordroneReadingsTest();
            prtrdArray = dao.findDylosReadingsTest();
            commonArray = dao.findCommonReadingsTest();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception thrown! " + e);
        }

        //response.setContentType("application/json");
        response.setContentType("text/plain");
        StringWriter json3 = new StringWriter();
        out.println("aqmdata common:");
        commonArray.writeJSONString(json3);
        out.print(json3.toString());

        StringWriter json = new StringWriter();
        out.println("\n\nSensordrone Readings:");
        senrdArray.writeJSONString(json);
        out.print(json.toString());

        StringWriter json2 = new StringWriter();
        out.println("\n\n\nPartical Readings:");
        prtrdArray.writeJSONString(json2);
        out.print(json2.toString());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletInputStream sis = null;
        String jsonString = "Not the real string here!";
        String returnValue = "PUSH_UNSET";

        try {
            sis = request.getInputStream();
            if (sis != null) {
                StringWriter writer = new StringWriter();
                IOUtils.copy(sis, writer);
                jsonString = writer.toString();
                log.info(jsonString);

                if (jsonString != null) {
                    IAQMDAO dao = AQMDAOFactory.getDAO();
                    Object obj = JSONValue.parse(jsonString);

                    if (obj instanceof JSONArray) {
                        if (dao.importReadings(jsonString)) {
                            returnValue = "SERVER_DYLOS_IMPORT_SUCCESS";
                        } else {
                            returnValue = "SERVER_SENSOR_IMPORT_FAILED";
                        }
                    }
                } else {
                    returnValue = "SERVER_BAD_OBJECT_TYPE";
                }
            } else {
                returnValue = "SERVER_STREAM_ERROR";
            }
        } catch (StreamCorruptedException sce) {
            log.log(Level.SEVERE, "Exception thrown! " + sce);
            returnValue = "SERVER_STREAM_CORRUPTED_EXCEPTION";
        } catch (IOException ie) {
            log.log(Level.SEVERE, "Exception thrown! " + ie);
            returnValue = "SERVER_IO_EXCEPTION";
        } catch (SecurityException se) {
            log.log(Level.SEVERE, "Exception thrown! " + se);
            returnValue = "SERVER_SECURITY_EXCEPTION";
        } catch (NullPointerException npe) {
            log.log(Level.SEVERE, "Exception thrown! " + npe);
            returnValue = "SERVER_NULL_POINTER_EXCEPTION";
        } catch (Throwable t) {
            log.log(Level.SEVERE, "Exception thrown! " + t);
            returnValue = "SERVER_UNKNOWN_ERROR";
        }

        PrintWriter pw = null;
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/plain");
            pw = response.getWriter();
            pw.println(returnValue);
        } catch (Throwable t3) {
            log.log(Level.SEVERE, "Exception thrown! " + t3);
        } finally {
            try {
                if (pw != null) {
                    pw.close();
                }
                if (sis != null) {
                    sis.close();
                }
            } catch (Throwable t2) {
                log.log(Level.SEVERE, "Exception thrown! " + t2);
            }
        }
    }
}
