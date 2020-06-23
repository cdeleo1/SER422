/**
 *
 */
package edu.asupoly.heal.aqm.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import edu.asupoly.heal.aqm.dmp.*;
import edu.asupoly.heal.aqm.model.*;

import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class AQMImportServlet extends HttpServlet {

    private static Logger log = Logger.getLogger(AQMImportServlet.class.getName());

    public final void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = null;
        IAQMDAO dao = AQMDAOFactory.getDAO();
        try {
            response.setContentType("text/plain");
            out = response.getWriter();
            Map<String, String[]> requestParams = request.getParameterMap();
            Set<Entry<String, String[]>> set = requestParams.entrySet();
            Iterator<Entry<String, String[]>> it = set.iterator();
            if (!it.hasNext()) {
                out.println("Dylos Reading: ");
                dao.findDeviceIdinDylos(out);
                out.println("\n\nSensordrone Reading:");
                dao.findDeviceIdinSensordrone(out);
            } else {
                String deviceid = null;
                int tail = Integer.MAX_VALUE;
                String type = null;

                while (it.hasNext()) {
                    Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) it.next();
                    String paramName = entry.getKey();
                    String[] paramValues = entry.getValue();

                    if (paramValues.length == 1) {
                        String paramValue = paramValues[0];
                        log.info("paramName = " + paramName + ", paramValue = " + paramValue);
                        if (paramName.equals("Dylos") && !paramValue.isEmpty()) {
                            type = "Dylos";
                            tail = Integer.parseInt(paramValue);
                        }
                        if (paramName.equals("Sensordrone") && !paramValue.isEmpty()) {
                            type = "Sensordrone";
                            tail = Integer.parseInt(paramValue);
                        }
                        if (paramName.equals("deviceid") && !paramValue.isEmpty()) {
                            deviceid = paramValue;
                        }
                    }
                }
                if (type != null) {
                    out.println(type + " Readings:\n ");
                    JSONArray rd = new JSONArray();
                    if (type.equals("Dylos")) {
                        rd = dao.findDylosReadingsByGroup(deviceid, tail);
                    } else if (type.equals("Sensordrone")) {
                        rd = dao.findSensordroneReadingsByGroup(deviceid, tail);
                    }
                    printString(rd, out);
                } else if (type == null && deviceid != null) {
                    out.println("request sample:\n ?Dylos=100&deviceid=aqm0\n ?Sensordrone=20&deviceid=SensorDrone00:00:00:00:00:00"
                            + "\n ?Dylos=50");
                }
            }

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

    private void printString(JSONArray rd, PrintWriter out) throws Exception {
        StringWriter json = new StringWriter();
        rd.writeJSONString(json);
        //out.println(json.toString() + "\n");
        for (int i = 0; i < rd.size(); i++) {
            rd = (JSONArray) JSONValue.parse(json.toString());
            JSONObject jval = (JSONObject) rd.get(i);
            out.println((i + 1) + ". " + jval.toJSONString() + "\n");
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletInputStream sis = null;
        int appReturnValue = ServerPushEvent.PUSH_UNSET;
        String jsonString = "";
        //lastImportTime = new Date();

        try {
            sis = request.getInputStream();
            if (sis != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(sis));
                if (br != null) {
                    String line = br.readLine();
                    while (line != null) {
                        jsonString = jsonString + line; // get received JSON data from request
                        line = br.readLine();
                    }
                }
                log.info("Received data: " + jsonString);
                if (jsonString != null) {
                    IAQMDAO dao = AQMDAOFactory.getDAO();
                    appReturnValue = (dao.importReadings(jsonString)) ? ServerPushEvent.SERVER_PUSH_OK : ServerPushEvent.SERVER_IMPORT_FAILED;
                }
                if (br != null) {
                    br.close();
                }
            } else {
                appReturnValue = ServerPushEvent.SERVER_STREAM_ERROR;
            }
        } catch (StreamCorruptedException sce) {
            log.log(Level.SEVERE, "Server pushed stacktrace on response: " + sce);
            appReturnValue = ServerPushEvent.SERVER_STREAM_CORRUPTED_EXCEPTION;
        } catch (IOException ie) {
            log.log(Level.SEVERE, "Server pushed stacktrace on response: " + ie);
            appReturnValue = ServerPushEvent.SERVER_IO_EXCEPTION;
        } catch (SecurityException se) {
            log.log(Level.SEVERE, "Server pushed stacktrace on response: " + se);
            appReturnValue = ServerPushEvent.SERVER_SECURITY_EXCEPTION;
        } catch (NullPointerException npe) {
            log.log(Level.SEVERE, "Server pushed stacktrace on response: " + npe);
            appReturnValue = ServerPushEvent.SERVER_NULL_POINTER_EXCEPTION;
        } catch (Throwable t) {
            log.log(Level.SEVERE, "Server pushed stacktrace on response: " + t);
            appReturnValue = ServerPushEvent.SERVER_UNKNOWN_ERROR;
        }

        PrintWriter pw = null;
        try {
            log.info("Server returning value: " + appReturnValue);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/plain");
            pw = response.getWriter();
            pw.println("" + appReturnValue);
        } catch (Throwable t3) {
            log.log(Level.SEVERE, "Server pushed stacktrace on response: " + t3);
        } finally {
            try {
                if (pw != null) {
                    pw.close();
                }
                if (sis != null) {
                    sis.close();
                }
            } catch (Throwable t2) {
                log.log(Level.SEVERE, "Server pushed stacktrace on response: " + t2);
            }
        }
    }

    /*	private void __recordResult(IAQMDAO dao, Date d, int rval, int type, String label) {
        String msg = "";
        if (rval >= 0) {
            msg = "Pushed " + rval + " " + label + " to the server";            
        } else {
            msg = "Unable to push " + label + " to the server";
        }
        log.info(msg);

        try {
            dao.addPushEvent(new ServerPushEvent(d.toString(), rval, type, msg));
        } catch (Throwable ts) {
        	ts.printStackTrace();
        	log.info("Unable to record " + label + " push event");
        }
    }*/
}
