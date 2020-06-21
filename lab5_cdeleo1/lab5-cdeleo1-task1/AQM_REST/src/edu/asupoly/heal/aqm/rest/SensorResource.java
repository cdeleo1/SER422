package edu.asupoly.heal.aqm.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONValue;

import edu.asupoly.heal.aqm.dmp.AQMDAOFactory;
import edu.asupoly.heal.aqm.dmp.IAQMDAO;

@Path("/sensors")
public class SensorResource {

    private static Logger log = Logger.getLogger(SensorResource.class.getName());

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getSensors() {
        String sensorType = null;
        List<String> sensors = new ArrayList<String>();
        sensors.add("Dylos");
        sensors.add("Sensordrone");
        sensorType = JSONValue.toJSONString(sensors);

        log.info("GET on SensorResource getSensors");
        return sensorType;
    }

    @GET
    @Path("/{sensorType}")
    public Response getDeviceId(@PathParam("sensorType") String sensorType) throws Exception {
        IAQMDAO dao = AQMDAOFactory.getDAO();
        String deviceid = null;

        if (sensorType.equals("Dylos")) {
            deviceid = dao.findDeviceIdinDylos();
        } else if (sensorType.equals("Sensordrone")) {
            deviceid = dao.findDeviceIdinSensordrone();
        }
        log.info("GET on SensorResource getDeviceId");

        return Response.status(200).entity(deviceid).build();
    }
}
