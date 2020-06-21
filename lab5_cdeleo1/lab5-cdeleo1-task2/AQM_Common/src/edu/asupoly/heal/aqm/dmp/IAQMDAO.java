package edu.asupoly.heal.aqm.dmp;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Properties;

import org.json.simple.JSONArray;

import edu.asupoly.heal.aqm.model.ServerPushEvent;

/**
 * @author kevinagary This interface defines how we will work with persistent
 *         storage
 */
public interface IAQMDAO {
	public abstract void init(Properties p) throws Exception;

	public boolean importReadings(String toImport) throws Exception;

	public JSONArray findDylosReadingsTest() throws Exception;
	public JSONArray findSensordroneReadingsTest() throws Exception;
	public JSONArray findCommonReadingsTest() throws Exception;
	
	public void findDeviceIdinDylos(PrintWriter out) throws Exception;
	public void findDeviceIdinSensordrone(PrintWriter out) throws Exception;
	public String findDeviceIdinDylos() throws Exception;
	public String findDeviceIdinSensordrone() throws Exception;
	
	public JSONArray findDylosReadingsByGroup(String deviceid, int tail) throws Exception;
	public JSONArray findSensordroneReadingsByGroup(String deviceid, int tail) throws Exception;
	public JSONArray findDylosReadingsByGroup(String deviceid, int tail, boolean isGeoJson) throws Exception;
	public JSONArray findSensordroneReadingsByGroup(String deviceid, int tail, boolean isGeoJson) throws Exception;

	public boolean addPushEvent(ServerPushEvent s) throws Exception;
	public ServerPushEvent getLastServerPush() throws Exception;
	public JSONArray findDylosReadingsForUserBetween(String userId, Date start, Date end) throws Exception;
	public String getGeoJSONObject(JSONArray rd) throws Exception;
	
	public JSONArray findDylosReadingsByGroup(String deviceid, int tail, Date start, Date end ,boolean isGeoJson) throws Exception;
	public JSONArray findSensordroneReadingsByGroup(String deviceid, int tail, Date start, Date end ,boolean isGeoJson) throws Exception;
}
