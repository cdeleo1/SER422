package edu.asupoly.heal.aqm.rest;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import edu.asupoly.heal.aqm.dmp.*;
import edu.asupoly.heal.aqm.model.*;

import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class AQMDisplayServlet extends HttpServlet {

    private static Logger log = Logger.getLogger(AQMDisplayServlet.class.getName());

    @SuppressWarnings("unchecked")
    public final void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = null;
        IAQMDAO dao = AQMDAOFactory.getDAO();
        try {
            response.setContentType("text/plain");
            out = response.getWriter();

            JSONArray Dylosrd = dao.findDylosReadingsByGroup(null, 10, true);
            JSONArray Sensordronerd = dao.findSensordroneReadingsByGroup(null, 10, true);
            Dylosrd.addAll(Sensordronerd);

            String search_callback = getJSONPObject("search_callback", Dylosrd);
            out.print(search_callback);

        } catch (Throwable t) {
            log.log(Level.SEVERE, "Server pushed stacktrace on response: " + t);
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (Throwable t2) {
                log.log(Level.SEVERE, "Server pushed stacktrace on response: " + t2);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private String getJSONPObject(String callback, JSONArray rd) throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("type", "FeatureCollection");
        obj.put("features", rd);

        //return callback + "(" + obj.toString() + ");";
        return obj.toString(); //send GeoJson
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.info("In doPost of Display not allowed");
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "POST not supported by this servlet");
    }
}
