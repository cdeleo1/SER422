package edu.asupoly.heal.aqm.rest;

import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;

import edu.asupoly.heal.aqm.dmp.AQMDAOFactory;
import edu.asupoly.heal.aqm.dmp.IAQMDAO;

import java.util.logging.Logger;

@Path("/data")
public class DataResource {

    private static Logger log = Logger.getLogger(DataResource.class.getName());

    @SuppressWarnings("unchecked")
    @GET
    public Response getData(@QueryParam("num") int num,
            @QueryParam("start") String timeFrom,
            @QueryParam("end") String timeTo) throws Exception {
        String output = null;
        Date start = null, end = null;
        IAQMDAO dao = AQMDAOFactory.getDAO();
        JSONArray rd = new JSONArray();
        JSONArray rd2 = new JSONArray();

        String deviceid = null;
        //log.info("startFrom: "+timeFrom+ " endTo:" + timeTo);
        if (timeFrom != null) {
            start = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).parse(timeFrom);
            if (timeTo != null) {
                end = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).parse(timeTo);
            }
        }

        int tail = 50;
        if (num != 0) {
            tail = num;
        }
        log.info("num: " + num + ", tail: " + tail + ", start: " + timeFrom + " end:" + timeTo);
        rd = dao.findDylosReadingsByGroup(deviceid, tail, start, end, true);
        rd2 = dao.findSensordroneReadingsByGroup(deviceid, tail, start, end, true);
        rd.addAll(rd2);
        rd.addAll(rd);  // do we need? Not there when opened 5/27/20
        output = dao.getGeoJSONObject(rd);
        return Response.status(200).entity(output).build();
    }

    @GET
    @Path("/{sensorType}")
    public Response getDataBysensorType(@PathParam("sensorType") String sensorType,
            @QueryParam("num") int num,
            @QueryParam("start") String timeFrom,
            @QueryParam("end") String timeTo) throws Exception {
        String output = null;
        Date start = null, end = null;
        IAQMDAO dao = AQMDAOFactory.getDAO();
        JSONArray rd = new JSONArray();
        String deviceid = null;
        if (timeFrom != null) {
            start = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).parse(timeFrom);
            if (timeTo != null) {
                end = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).parse(timeTo);
            }
        }
        int tail = 50;
        if (num != 0) {
            tail = num;
        }
        log.info("num: " + num + ", tail: " + tail + ", start: " + timeFrom + " end:" + timeTo);
        if (sensorType.equals("Dylos")) {
            rd = dao.findDylosReadingsByGroup(deviceid, tail, start, end, true);
        } else if (sensorType.equals("Sensordrone")) {
            rd = dao.findSensordroneReadingsByGroup(deviceid, tail, start, end, true);
        }
        output = dao.getGeoJSONObject(rd);

        return Response.status(200).entity(output).build();
    }

    @GET
    @Path("/{sensorType}/{deviceId}")
    public Response getDataBydeviceId(@PathParam("sensorType") String sensorType,
            @PathParam("deviceId") String deviceId,
            @QueryParam("num") int num,
            @QueryParam("start") String timeFrom,
            @QueryParam("end") String timeTo) throws Exception {
        String output = null;
        Date start = null, end = null;
        IAQMDAO dao = AQMDAOFactory.getDAO();
        JSONArray rd = new JSONArray();
        if (timeFrom != null) {
            start = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).parse(timeFrom);
            if (timeTo != null) {
                end = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).parse(timeTo);
            }
        }
        int tail = 50;
        if (num != 0) {
            tail = num;
        }
        log.info("num: " + num + ", tail: " + tail + ", start: " + timeFrom + " end:" + timeTo);
        if (sensorType.equals("Dylos")) {
            rd = dao.findDylosReadingsByGroup(deviceId, tail, start, end, true);
        } else if (sensorType.equals("Sensordrone")) {
            rd = dao.findSensordroneReadingsByGroup(deviceId, tail, start, end, true);
        }
        output = dao.getGeoJSONObject(rd);

        return Response.status(200).entity(output).build();

    }

}
