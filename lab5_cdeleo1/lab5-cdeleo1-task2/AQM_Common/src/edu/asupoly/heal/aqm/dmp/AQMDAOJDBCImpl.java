package edu.asupoly.heal.aqm.dmp;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import edu.asupoly.heal.aqm.model.SensordroneReading;
import edu.asupoly.heal.aqm.model.DylosReading;
import edu.asupoly.heal.aqm.model.ServerPushEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AQMDAOJDBCImpl implements IAQMDAO {
	private static Logger log = Logger.getLogger(AQMDAOJDBCImpl.class.getName());
	private static final long MS_ONE_YEAR_FROM_NOW = 1000L * 60L * 60 * 24L * 365L;

	protected String __jdbcURL;
	protected Properties __jdbcProperties;

	public AQMDAOJDBCImpl() {
	}

	@Override
	public void init(Properties p) throws Exception {
		__jdbcProperties = new Properties();
		String jdbcDriver = p.getProperty("jdbc.driver");
		String jdbcURL = p.getProperty("jdbc.url");
		// do we need user and password?
		// these would be in the properties if defined (embedded derby does not need)
		__jdbcProperties.setProperty("user", p.getProperty("jdbc.user"));
		__jdbcProperties.setProperty("password", p.getProperty("jdbc.passwd"));

		if (jdbcDriver == null || jdbcURL == null) {
			throw new Exception("JDBC not configured");
		}

		// load the driver, test the URL
		try {
			// read in all the JDBC properties and SQL queries we need
			Enumeration<?> keys = p.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				if (key.startsWith("sql")) {
					__jdbcProperties.setProperty(key, p.getProperty(key));
				}
			}

			Class.forName(jdbcDriver);
			__jdbcURL = jdbcURL;
			__jdbcProperties.setProperty("jdbc.driver", jdbcDriver);
			__jdbcProperties.setProperty("jdbc.url", jdbcURL);

			// test the connection
			if (!_testConnection(jdbcURL,
					p.getProperty("sql.checkConnectionQuery"))) {
				log.info("Testing Connection Failed "
						+ p.getProperty("sql.checkConnectionQuery"));
				throw new Exception("Unable to connect to database");
			} else {
				log.info("Testing DAO Connection -- OK");
			}
		} catch (Throwable t) {
			log.log(Level.SEVERE, "DAO Exception: " + t);
			throw new Exception(t);
		}
	}

	protected Connection _getConnection() throws SQLException {
		return DriverManager.getConnection(__jdbcURL, __jdbcProperties);
	}

	protected boolean _testConnection(String url, String query) {
		Connection c = null;
		Statement s = null;
		try {
			c = _getConnection();
			s = c.createStatement();
			return s.execute(query);
		} catch (Throwable t) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + t);
			t.printStackTrace();
			return false;
		} finally {
			try {
				if (s != null)
					s.close();
				if (c != null)
					c.close();
			} catch (SQLException se) {
				log.log(Level.SEVERE, "Server pushed stacktrace on response: " + se);
			}
		}
	}

	public boolean importReadings(String toImport) throws Exception {
		Connection c = null;
		PreparedStatement psCommon = null;
		PreparedStatement psDylos  = null;
		PreparedStatement psSensordrone  = null;
		boolean previousAC = true;
		boolean rval = true;

		JSONArray jsonary = null;
		JSONObject jsonobj = new JSONObject();
		try {
			Object obj = JSONValue.parse(toImport);
			if (obj instanceof JSONArray) {	
				jsonary = (JSONArray) obj;
				if (jsonary.isEmpty())
					return false;
			} else {
				return false;  // we did not get a JSON Array like we expected
			}
			// OK we have a JSON Array, process each element.

			// Set up the Db connection and statements
			c = _getConnection();
			previousAC = c.getAutoCommit();
			c.setAutoCommit(false);
			psCommon = c.prepareStatement(__jdbcProperties.getProperty("sql.importCommonReadings"));
			psDylos = c.prepareStatement(__jdbcProperties.getProperty("sql.importDylosReadings"));
			psSensordrone = c.prepareStatement(__jdbcProperties.getProperty("sql.importSensordroneReadings"));
			for (int i = 0; i < jsonary.size(); i++) {
				jsonobj = (JSONObject) jsonary.get(i);

				String dateTime = (String) jsonobj.get("dateTime");
				Date d = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(dateTime);
				Timestamp t = new java.sql.Timestamp(d.getTime()); //, AQMDAOFactory.AQM_CALENDAR);

				String type = (String)jsonobj.get("type");
				importCommonReading(jsonobj, psCommon, t);
				if (type != null && type.equals("sensordrone")) {
					log.info("data type = "+type);
					importSensordroneReading(jsonobj, psSensordrone, t);
				} else if (type != null && type.equals("dylos") ) {
					log.info("data type = "+type);
					importDylosReading(jsonobj, psDylos, t);
				}
			}
			c.commit();
			return rval;
		} catch (Throwable t) {
			log.log(Level.SEVERE, "Error pushing AQM event to database: " + t);
			throw new Exception(t);
		} finally {
			try {
				if (psCommon != null)
					psCommon.close();
				if (psDylos != null)
					psDylos.close();
				if (psSensordrone != null)
					psSensordrone.close();
				if (c != null) {
					c.rollback();
					c.setAutoCommit(previousAC);
					c.close();
				}
			} catch (SQLException se2) {
				log.log(Level.SEVERE, "Error cleaning up JDBC resources: " + se2);
			}
		}
	}

	private void importCommonReading(JSONObject jsonobj, PreparedStatement ps1, Timestamp d) throws Exception{
		try {
			System.out.println(jsonobj.toString());
			ps1.setString(1, (String) jsonobj.get("deviceId"));
			ps1.setTimestamp(2, d);
			ps1.setDouble(3, (Double) jsonobj.get("geoLatitude"));
			ps1.setDouble(4, (Double) jsonobj.get("geoLongitude"));
			ps1.setString(5, (String) jsonobj.get("geoMethod"));

			ps1.executeUpdate();
			ps1.clearParameters();
		} catch (Throwable t) {
			t.printStackTrace();
			throw new Exception("Could not set PS for AQM Common attributes");
		}
	}
	// received Dylos json string sample:
	// [{"deviceId":"aqm1","userId":"patient1","dateTime":"Sat Mar 08 22:24:10 MST 2014",
	// "smallParticle":76,"largeParticle":16,
	// "geoLatitude":33.3099177,"geoLongitude":-111.6726974,"geoMethod":"manual"},{...},...]
	private void importDylosReading(JSONObject jsonobj, PreparedStatement ps2, Timestamp d) throws Exception {
		try {
			ps2.setString(1, (String) jsonobj.get("deviceId"));
			ps2.setTimestamp(2, d);
			ps2.setInt(3, ((Long) jsonobj.get("smallParticle")).intValue());
			ps2.setInt(4, ((Long) jsonobj.get("largeParticle")).intValue());
			ps2.setString(5, (String) jsonobj.get("userId"));

			ps2.executeUpdate();
			ps2.clearParameters();
		} catch (SQLException se) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + se);
			throw new Exception(se);
		} catch (Throwable t) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + t);
			throw new Exception(t);
		}
	}

	// received Sensordrone json string sample
	// {"deviceId":"SensorDroneB8:FF:FE:B9:D9:A0","dateTime":"20140313_195444",
	// "co2DeviceID":"UNKNOWN","coData":-2,"co2Data":-1,
	// "presureData":96128,"tempData":27,"humidityData":42,
	// "geoLatitude":33.2830173,"geoLongitude":-111.7627723,"geoMethod":"Network"}
	public void importSensordroneReading(JSONObject jsonobj, PreparedStatement ps2, Timestamp d) throws Exception {
		try {
			ps2.setString(1, (String) jsonobj.get("deviceId"));
			ps2.setTimestamp(2, d);
			ps2.setInt(3, ((Long) jsonobj.get("pressureData")).intValue());
			ps2.setInt(4, ((Long) jsonobj.get("tempData")).intValue());
			ps2.setInt(5, ((Long) jsonobj.get("coData")).intValue());
			ps2.setInt(6, ((Long) jsonobj.get("humidityData")).intValue());
			ps2.setString(7, (String) jsonobj.get("co2DeviceID"));
			ps2.setInt(8, ((Long) jsonobj.get("co2Data")).intValue());

			ps2.executeUpdate();
			ps2.clearParameters();
		} catch (SQLException se) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + se);
			throw new Exception(se);
		} catch (Throwable t) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + t);
			throw new Exception(t);
		} 
	}

	public boolean addPushEvent(ServerPushEvent s) throws Exception {
		if (s == null) return false;

		Connection c = null;
		PreparedStatement ps = null;

		try {
			c = _getConnection();
			ps = c.prepareStatement(__jdbcProperties.getProperty("sql.addServerPushEvent"));

			String dateTime = s.getEventTime();
			Date d = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US)
					.parse(dateTime);
			ps.setTimestamp(1, new java.sql.Timestamp(d.getTime()), AQMDAOFactory.AQM_CALENDAR);

			ps.setInt(2,  s.getResponseCode());
			ps.setInt(3, s.getDeviceType());
			ps.setString(4, s.getMessage());
			ps.executeUpdate();
			ps.clearParameters();
		} catch (SQLException se) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + se);
		} catch (Throwable t) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + t);
		} finally {
			try {
				if (ps != null) ps.close();
				if (c != null) c.close();
			} catch (SQLException se2) {
			    se2.printStackTrace();
				log.log(Level.SEVERE, "Server pushed stacktrace on response: " + se2);
			}
		}
		return true;

	}


	// [{"deviceId":"aqm1","userId":"patient1","dateTime":"Sat Mar 08 22:24:10 MST 2014",
	// "smallParticle":76,"largeParticle":16,
	// "geoLatitude":33.3099177,"geoLongitude":-111.6726974,"geoMethod":"manual"},{...},...]
	@SuppressWarnings("unchecked")
	public JSONArray findDylosReadingsTest() throws Exception {
		int count = 10;
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		JSONArray rd = new JSONArray();
		try {
			c = _getConnection();
			//			Statement statement = c.createStatement();
			//			statement.setMaxRows(10);
			//			rs = statement
			//					.executeQuery("select * from particle_reading order by dateTime desc");

			ps = c.prepareStatement(__jdbcProperties.getProperty("sql.findDylosReadingsTest"));
			rs = ps.executeQuery();
			while (rs.next() && count > 0) {
				Timestamp t = rs.getTimestamp("dateTime", AQMDAOFactory.AQM_CALENDAR);
				Date d = new Date(t.getTime());

				String deviceId = rs.getString("deviceId");
				String dateTime = d.toString();
				double geoLatitude = rs.getDouble("latitude");
				double geoLongitude = rs.getDouble("longitude");
				String geoMethod = rs.getString("method");
				int smallParticle = rs.getInt("smallParticle");
				int largeParticle = rs.getInt("largeParticle");
				String userId = rs.getString("userId");

				DylosReading prd = new DylosReading(deviceId, userId, dateTime,
						smallParticle, largeParticle, geoLatitude,
						geoLongitude, geoMethod);

				rd.add(prd);
				count--;
			}
		} catch (SQLException se) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + se);
			throw new Exception(se);
		} catch (Throwable t) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + t);
			throw new Exception(t);
		} finally {
			if (c != null) c.close();
			if (ps != null) ps.close();
			if (rs != null) rs.close();
		}

		return rd;
	}

	// [{"deviceId":"SensorDroneB8:FF:FE:B9:D9:A0","dateTime":"20140313_195444",
	// "co2DeviceID":"UNKNOWN","coData":-2,"co2Data":-1,
	// "presureData":96128,"tempData":27,"humidityData":42,
	// "geoLatitude":33.2830173,"geoLongitude":-111.7627723,"geoMethod":"Network"},{...},...]
	@SuppressWarnings("unchecked")
	public JSONArray findSensordroneReadingsTest() throws Exception {
		int count = 10;
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		JSONArray rd = new JSONArray();
		try {
			c = _getConnection();
			ps = c.prepareStatement(__jdbcProperties.getProperty("sql.findSensordroneReadingsTest"));
			rs = ps.executeQuery();
			while (rs.next() && count > 0) {
				Timestamp t = rs.getTimestamp("dateTime", AQMDAOFactory.AQM_CALENDAR);
				Date d = new Date(t.getTime());

				String deviceId = rs.getString("deviceId");
				String dateTime = d.toString();
				double geoLatitude = rs.getDouble("latitude");
				double geoLongitude = rs.getDouble("longitude");
				String geoMethod = rs.getString("method");
				int presureData = rs.getInt("pressureData");
				int tempData = rs.getInt("tempData");
				int coData = rs.getInt("coData");
				int humidityData = rs.getInt("humidityData");
				String co2DeviceID = rs.getString("co2sensorid");
				int co2Data = rs.getInt("co2Data");

				SensordroneReading ssr = new SensordroneReading(deviceId,
						dateTime, co2DeviceID, coData, co2Data, presureData,
						tempData, humidityData, geoLatitude, geoLongitude,
						geoMethod);

				rd.add(ssr);
				count--;
			}
		} catch (SQLException se) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + se);
			throw new Exception(se);
		} catch (Throwable t) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + t);
			throw new Exception(t);
		} finally {
			if (c != null) c.close();
			if (ps != null) ps.close();
			if (rs != null) rs.close();
		}

		return rd;
	}

	@SuppressWarnings("unchecked")
	public JSONArray findCommonReadingsTest() throws Exception {
		int count = 10;
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		JSONArray rd = new JSONArray();
		try {
			c = _getConnection();
			ps = c.prepareStatement(__jdbcProperties.getProperty("sql.findCommonReadingsTest"));
			rs = ps.executeQuery();
			while (rs.next() && count > 0) {
				JSONObject obj = new JSONObject();
				Timestamp t = rs.getTimestamp("dateTime", AQMDAOFactory.AQM_CALENDAR);
				Date d = new Date(t.getTime());

				String deviceId = rs.getString("deviceId");
				String dateTime = d.toString();
				double geoLatitude = rs.getDouble("latitude");
				double geoLongitude = rs.getDouble("longitude");
				String geoMethod = rs.getString("method");

				obj.put("deviceId", deviceId);
				obj.put("dateTime", dateTime);
				obj.put("geoLatitude", geoLatitude);
				obj.put("geoLongitude", geoLongitude);
				obj.put("geoMethod", geoMethod);

				rd.add(obj);
				count--;
			}
		} catch (SQLException se) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + se);
			throw new Exception(se);
		} catch (Throwable t) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + t);
			throw new Exception(t);
		} finally {
			if (c != null) c.close();
			if (ps != null) ps.close();
			if (rs != null) rs.close();
		}

		return rd;
	}

	public void findDeviceIdinDylos(PrintWriter out) throws Exception {
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		JSONArray rd = new JSONArray();
		List<String> deviceids = new ArrayList<String>();

		try {
			c = _getConnection();
			ps = c.prepareStatement(__jdbcProperties.getProperty("sql.findDeviceIdinDylos"));
			rs = ps.executeQuery();
			while (rs.next()) {
				deviceids.add(rs.getString("deviceid"));
			}

			log.info(" have "+ deviceids.size() + " deviceids in Dylos." );
			out.println("  contains "+deviceids.size() +" devices now");
			for (int i = 0; i < deviceids.size(); i++) {
				out.println("\n"+ (i+1) +". deviceid=" + deviceids.get(i)+ "\n");
				rd = (JSONArray)findDylosReadingsByGroup(deviceids.get(i), Integer.MAX_VALUE);
				StringWriter json = new StringWriter();
				rd.writeJSONString(json);
				out.print(json.toString() + "\n");
			}

		} catch (SQLException se) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + se);
			throw new Exception(se);
		} catch (Throwable t) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + t);
			throw new Exception(t);
		} finally {
			if (c != null) c.close();
			if (ps != null) ps.close();
			if (rs != null) rs.close();
		}
	}

	public void findDeviceIdinSensordrone(PrintWriter out) throws Exception {
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		JSONArray rd = new JSONArray();
		List<String> deviceids = new ArrayList<String>();

		try {
			c = _getConnection();
			ps = c.prepareStatement(__jdbcProperties.getProperty("sql.findDeviceIdinSensordrone"));
			rs = ps.executeQuery();
			while (rs.next()) {
				deviceids.add(rs.getString("deviceid"));
			}

			log.info(" have "+ deviceids.size() + " deviceids in Sensordrone." );
			out.println("  contains "+deviceids.size() +" devices now");
			for (int i = 0; i < deviceids.size(); i++) {
				out.println("\n"+ (i+1) +". deviceid=" + deviceids.get(i)+ "\n");
				rd = (JSONArray)findSensordroneReadingsByGroup(deviceids.get(i), Integer.MAX_VALUE);
				StringWriter json = new StringWriter();
				rd.writeJSONString(json);
				out.print(json.toString() + "\n");
			}

		} catch (SQLException se) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + se);
			throw new Exception(se);
		} catch (Throwable t) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + t);
			throw new Exception(t);
		} finally {
			if (c != null) c.close();
			if (ps != null) ps.close();
			if (rs != null) rs.close();
		}
	}

	public JSONArray findDylosReadingsByGroup(String deviceid, int tail, boolean isGeoJson) throws Exception {
		return findDylosReadingsByGroup(deviceid, tail, null,null,true);
	}

	public JSONArray findSensordroneReadingsByGroup(String deviceid, int tail, boolean isGeoJson) throws Exception {
		return findSensordroneReadingsByGroup(deviceid, tail, null, null, true);
	}

	public JSONArray findDylosReadingsByGroup(String deviceid, int tail) throws Exception {
		return findDylosReadingsByGroup(deviceid, tail, null,null,false);
	}

	public JSONArray findSensordroneReadingsByGroup(String deviceid, int tail) throws Exception {
		return findSensordroneReadingsByGroup(deviceid, tail, null, null, false);
	}

	@Override
	public ServerPushEvent getLastServerPush() throws Exception {
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ServerPushEvent rval = null;
		boolean includeErrors = true;
		int minCode = 0;

		try {
			c = _getConnection();
			ps = c.prepareStatement(__jdbcProperties.getProperty("sql.getServerPushEvents"));

			if (includeErrors) {
				minCode = -9999;
			}
			ps.setInt(1, minCode);
			rs = ps.executeQuery();
			if (rs.next()) {
				Timestamp t = rs.getTimestamp("eventtime", AQMDAOFactory.AQM_CALENDAR);
				Date d = new Date(t.getTime());

				rval = new ServerPushEvent(d.toString(), rs.getInt("responsecode"), rs.getInt("devicetype"),
						rs.getString("message"));
			}
			return rval;
		} catch (SQLException se) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + se);
			throw new Exception(se);
		} catch (Throwable t) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + t);
			throw new Exception(t);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (c != null)
					c.close();
			} catch (SQLException se2) {
				log.log(Level.SEVERE, "Server pushed stacktrace on response: " + se2);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONArray findDylosReadingsForUserBetween(String userId, Date start, Date end) throws Exception {
		if (start == null || end == null) return null;
		//

		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		JSONArray rval = new JSONArray();
		try {

			c = _getConnection();
			ps = c.prepareStatement(__jdbcProperties.getProperty("sql.findDylosReadingsForUserBetween"));

			if (userId == null) {                
				ps.setString(1,  "%");
			} else {                
				ps.setString(1, userId);
			}
			// start is not null
			ps.setTimestamp(2, new java.sql.Timestamp(start.getTime()), AQMDAOFactory.AQM_CALENDAR);
			// end is not null
			ps.setTimestamp(3, new java.sql.Timestamp(end.getTime()), AQMDAOFactory.AQM_CALENDAR);

			rs = ps.executeQuery();
			while (rs.next()) {
				Timestamp t = rs.getTimestamp("datetime", AQMDAOFactory.AQM_CALENDAR);
				Date d = new Date(t.getTime());

				DylosReading dr = new DylosReading(rs.getString("deviceid"), rs.getString("userid"), d.toString(), rs.getInt("smallparticle"), rs.getInt("largeparticle"), rs.getDouble("latitude"), rs.getDouble("longitude"), rs.getString("method"));
				rval.add(dr);
			}

		} catch (SQLException se) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + se);
			throw new Exception(se);
		} catch (Throwable t) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + t);
			throw new Exception(t);
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (c != null) c.close();
			} catch (SQLException se2) {
				log.log(Level.SEVERE, "Server pushed stacktrace on response: " + se2);
			}
		}
		return rval;
	}

	@Override
	public String findDeviceIdinDylos() throws Exception {
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> deviceids = new ArrayList<String>();
		String rd = null;
		try {
			c = _getConnection();
			ps = c.prepareStatement(__jdbcProperties.getProperty("sql.findDeviceIdinDylos"));
			rs = ps.executeQuery();
			while (rs.next()) {
				deviceids.add(rs.getString("deviceid"));
			}

			log.info(" have "+ deviceids.size() + " deviceids in Dylos." );
			rd = JSONValue.toJSONString(deviceids);

		} catch (SQLException se) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + se);
			throw new Exception(se);
		} catch (Throwable t) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + t);
			throw new Exception(t);
		} finally {
			if (c != null) c.close();
			if (ps != null) ps.close();
			if (rs != null) rs.close();
		}
		return rd;
	}

	@Override
	public String findDeviceIdinSensordrone() throws Exception {
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String rd = null;
		List<String> deviceids = new ArrayList<String>();

		try {
			c = _getConnection();
			ps = c.prepareStatement(__jdbcProperties.getProperty("sql.findDeviceIdinSensordrone"));
			rs = ps.executeQuery();
			while (rs.next()) {
				deviceids.add(rs.getString("deviceid"));
			}

			log.info(" have "+ deviceids.size() + " deviceids in Sensordrone." );
			rd = JSONValue.toJSONString(deviceids);

		} catch (SQLException se) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + se);
			throw new Exception(se);
		} catch (Throwable t) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + t);
			throw new Exception(t);
		} finally {
			if (c != null) c.close();
			if (ps != null) ps.close();
			if (rs != null) rs.close();
		}
		return rd;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getGeoJSONObject(JSONArray rd) throws Exception {
		JSONObject obj = new JSONObject();
		obj.put("type", "FeatureCollection");
		obj.put("features", rd);

		return obj.toString(); //send GeoJson
	}

	@Override
	public JSONArray findDylosReadingsByGroup(String deviceid, int tail,
			Date start, Date end, boolean isGeoJson) throws Exception {
		//if (tail == Integer.MAX_VALUE) tail = 10;
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		JSONArray rd = new JSONArray();
		JSONArray geoArray = new JSONArray();
		try {
			c = _getConnection();
			ps = c.prepareStatement(__jdbcProperties.getProperty("sql.findDylosReadingsByGroup"));
			if (deviceid == null) 
				ps.setString(1,  "%");
			else ps.setString(1, deviceid);
			if(start != null) {
				ps.setTimestamp(2, new java.sql.Timestamp(start.getTime()), AQMDAOFactory.AQM_CALENDAR);
				if(end != null) {
					ps.setTimestamp(3, new java.sql.Timestamp(end.getTime()), AQMDAOFactory.AQM_CALENDAR);
					log.info("query dylos: ("+ deviceid + ", "+ tail + ", "+start.toString()+ ", " + end.toString());
				} else {
					ps.setTimestamp(3, new java.sql.Timestamp(start.getTime()+MS_ONE_YEAR_FROM_NOW), AQMDAOFactory.AQM_CALENDAR);
					log.info("query dylos: ("+ deviceid + ", "+ tail + ", "+start.toString()+ ", " + "+1 year");
				}
			} else {
				Calendar cal = AQMDAOFactory.AQM_CALENDAR;
				cal.setTime(new Date());
				cal.add(Calendar.MONTH, -3);
				start = cal.getTime();
				ps.setTimestamp(2, new java.sql.Timestamp(start.getTime()), AQMDAOFactory.AQM_CALENDAR);
				ps.setTimestamp(3, new java.sql.Timestamp(start.getTime()+MS_ONE_YEAR_FROM_NOW), AQMDAOFactory.AQM_CALENDAR);
				log.info("query dylos: ("+ deviceid + ", "+ tail + ", "+start.toString()+ ", " + "+1 year");
			}

			rs = ps.executeQuery();
			while (rs.next() && tail > 0) {
				Timestamp t = rs.getTimestamp("dateTime", AQMDAOFactory.AQM_CALENDAR);
				Date d = new Date(t.getTime());

				String deviceId = rs.getString("deviceId");
				String dateTime = d.toString();
				double geoLatitude = rs.getDouble("latitude");
				double geoLongitude = rs.getDouble("longitude");
				String geoMethod = rs.getString("method");
				int smallParticle = rs.getInt("smallParticle");
				int largeParticle = rs.getInt("largeParticle");
				String userId = rs.getString("userId");

				DylosReading prd = new DylosReading(deviceId, userId, dateTime,
						smallParticle, largeParticle, geoLatitude,
						geoLongitude, geoMethod);
				rd.add(prd);

				JSONObject geojson = prd.getGeoJSONFeature();
				geoArray.add(geojson);

				tail--;
			}
		} catch (SQLException se) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + se);
			throw new Exception(se);
		} catch (Throwable t) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + t);
			throw new Exception(t);
		} finally {
			if (c != null) c.close();
			if (ps != null) ps.close();
			if (rs != null) rs.close();
		}

		if (!isGeoJson) return rd;
		else return geoArray;
	}

	@Override
	public JSONArray findSensordroneReadingsByGroup(String deviceid, int tail,
			Date start, Date end, boolean isGeoJson) throws Exception {
		//if (tail == Integer.MAX_VALUE) tail = 10;
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		JSONArray rd = new JSONArray();
		JSONArray geoArray = new JSONArray();
		try {
			c = _getConnection();
			ps = c.prepareStatement(__jdbcProperties.getProperty("sql.findSensordroneReadingsByGroup"));
			ps.setString(1, deviceid);
			if (deviceid == null) 
				ps.setString(1,  "%");
			else ps.setString(1, deviceid);
			if(start != null) {
				ps.setTimestamp(2, new java.sql.Timestamp(start.getTime()), AQMDAOFactory.AQM_CALENDAR);
				if(end != null) {
					ps.setTimestamp(3, new java.sql.Timestamp(end.getTime()), AQMDAOFactory.AQM_CALENDAR);
					log.info("query sensordrone: ("+ deviceid + ", "+ tail + ", "+start.toString()+ ", " + end.toString());
				} else {
					ps.setTimestamp(3, new java.sql.Timestamp(start.getTime()+MS_ONE_YEAR_FROM_NOW), AQMDAOFactory.AQM_CALENDAR);
					log.info("query sensordrone: ("+ deviceid + ", "+ tail + ", "+start.toString()+ ", " + "+1 year");
				}
			} else {
				Calendar cal = AQMDAOFactory.AQM_CALENDAR;
				cal.setTime(new Date());
				cal.add(Calendar.MONTH, -3);
				start = cal.getTime();
				ps.setTimestamp(2, new java.sql.Timestamp(start.getTime()), AQMDAOFactory.AQM_CALENDAR);
				ps.setTimestamp(3, new java.sql.Timestamp(start.getTime()+MS_ONE_YEAR_FROM_NOW), AQMDAOFactory.AQM_CALENDAR);
				log.info("query sensordrone: ("+ deviceid + ", "+ tail + ", "+start.toString()+ ", " + "+1 year");
			}

			rs = ps.executeQuery();
			while (rs.next() && tail > 0) {
				Timestamp t = rs.getTimestamp("dateTime", AQMDAOFactory.AQM_CALENDAR);
				Date d = new Date(t.getTime());

				String deviceId = rs.getString("deviceId");
				String dateTime = d.toString();
				double geoLatitude = rs.getDouble("latitude");
				double geoLongitude = rs.getDouble("longitude");
				String geoMethod = rs.getString("method");
				int presureData = rs.getInt("pressureData");
				int tempData = rs.getInt("tempData");
				int coData = rs.getInt("coData");
				int humidityData = rs.getInt("humidityData");
				String co2DeviceID = rs.getString("co2sensorid");
				int co2Data = rs.getInt("co2Data");

				SensordroneReading ssr = new SensordroneReading(deviceId,
						dateTime, co2DeviceID, coData, co2Data, presureData,
						tempData, humidityData, geoLatitude, geoLongitude,
						geoMethod);

				rd.add(ssr);

				JSONObject geojson = ssr.getGeoJSONFeature();
				geoArray.add(geojson);

				tail--;
			}
		} catch (SQLException se) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + se);
			throw new Exception(se);
		} catch (Throwable t) {
			log.log(Level.SEVERE, "Server pushed stacktrace on response: " + t);
			throw new Exception(t);
		} finally {
			if (c != null) c.close();
			if (ps != null) ps.close();
			if (rs != null) rs.close();
		}

		if (!isGeoJson) return rd;
		else return geoArray;
	}
}
