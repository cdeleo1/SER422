package edu.asupoly.heal.aqm.jms;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;
import java.util.Properties;
import javax.jms.*;
import javax.naming.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.util.Timer; 
import java.util.TimerTask;

import edu.asupoly.heal.aqm.dmp.IAQMDAO;
import edu.asupoly.heal.aqm.dmp.AQMDAOFactory;
import edu.asupoly.heal.aqm.dmp.AQMDAOJDBCImpl;
import edu.asupoly.heal.aqm.model.DylosReading;
import edu.asupoly.heal.aqm.model.SensordroneReading;
import edu.asupoly.heal.aqm.model.ServerPushEvent;

public class SimplePumpPublisherModel {
    private static Logger log = 
            Logger.getLogger(SimplePumpPublisherModel.class.getName());
    private TopicSession pubSession;
    private TopicPublisher publisher;
    private TopicConnection connection;
    private static final String topicConnectionFactory = "topicConnectionFactry";
    private static final String topicName = "Chat1";
    private static final String username = "SimplePumpPublisher";
    private static final String password = "SimplePumpPublisher";
    private static final String jmsURL = "tcp://localhost:61616";
    // MUTUALLY EXCLUSIVE ARGUMENTS (CANNOT SPECIFY BOTH -foreach and -periodic)
    private static final String USAGE = 
            "java edu.asupoly.heal.aqm.jms.SimplePumpPublisherModel " +
            "-foreach int\n\n" +
            "OR\n\n" +
            "java edu.asupoly.heal.aqm.jms.SimplePumpPublisherModel " +
            "-periodic int\n\n";
    // number of pushes per sensor readings
    private static final int DEFAULT_FOREACH = 3;
    // push updates every # of seconds
    private static final int DEFAULT_PERIODIC = 60;
    private Properties jndiProperties = new Properties();
    private String sensorJsonString = "";
    private static final long MAX_TIMER = 60000;
    public static SimplePumpPublisherModel obj;

    /* Establish JMS publisher */
    public SimplePumpPublisherModel() throws Exception {
        final File file = new File("../properties/jndi.properties");
        try {
            jndiProperties.load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InitialContext jndi = new InitialContext(jndiProperties);
        TopicConnectionFactory conFactory
                = (TopicConnectionFactory) jndi.lookup(topicConnectionFactory);
        connection = conFactory.createTopicConnection(username, password);
        ActiveMQConnectionFactory connectionFactory
                = new ActiveMQConnectionFactory(jmsURL);
        connectionFactory.createConnection();
        pubSession = connection.createTopicSession(false,
                TopicSession.AUTO_ACKNOWLEDGE);
        Topic chatTopic = (Topic) jndi.lookup(topicName);
        publisher = pubSession.createPublisher(chatTopic);
        connection.start();
        publisher.setDeliveryMode(DeliveryMode.PERSISTENT);
    }

    private boolean goPublish(String s) {
        boolean rval = true;
        try {
            // Create and send message using topic publisher
            TextMessage message = pubSession.createTextMessage();
            message.setText(s);
            publisher.publish(message);
        } catch (Throwable thw1) {
            thw1.printStackTrace();
            rval = false;
        }
        return rval;
    }
    
    @SuppressWarnings("unchecked")
    private void setSensorData() throws Exception {
        IAQMDAO dao = AQMDAOFactory.getDAO();
        String sensorDataString = "";
        try {
            JSONArray Dylosrd = dao.findDylosReadingsByGroup(null, 50, true);
            JSONArray Sensordronerd = 
                    dao.findSensordroneReadingsByGroup(null, 50, true);
            Dylosrd.addAll(Sensordronerd);
            sensorDataString = getJSONPObject(Dylosrd);
        } catch (Exception e) {
            log.log(Level.SEVERE, "setSensorData() pushed stacktrace: " + e);
        }
        sensorJsonString = sensorDataString;
    }
    
    private String getSensorData() {
        return sensorJsonString;
    }
    
    @SuppressWarnings("unchecked")
    private String getJSONPObject(JSONArray rd) throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("type", "FeatureCollection");
        obj.put("features", rd);
        return obj.toString(); //send GeoJson
    }

    public static void main(String[] args) {
        // uncomment this line for verbose logging to the screen
        //BasicConfigurator.configure();

        // CHECK COMMAND LINE ARGUMENTS/FLAGS
        // GET JSON STRING OF ALL SENSOR DATA IN DATABASE
        // SETUP TIMER/LOOP TO CHECK DATABASE
        // DETERMINE AIR QUALITY OF EACH TILE ON MAP
        // DETERMINE WHICH TILES NEED TO BE NOTIFIED
        // PUBLISH LAT/LONG TILES TO NOTIFY SPECIFIC SUBSCRIBERS

        // CHECK COMMAND LINE ARGUMENTS/FLAGS
        int i = 0;
        String arg;
        boolean foreachFlag = false;
        boolean periodicFlag = false;
        while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];
            if (args.length == 2 && arg.equals("-foreach")) {
                System.out.println("foreach mode on\n\n");
                foreachFlag = true;
            } else if (args.length == 2 && arg.equals("-periodic")) {
                System.out.println("periodic mode on\n\n");
                periodicFlag = true;
            } else {
                System.out.println("Please specify either -foreach OR -periodic"
                                + ", not both.\n\n");
                System.err.println(USAGE);
            }
        }
        
        Timer t = new Timer();
        long milliseconds = 0;
        try {
            SimplePumpPublisherModel pub = new SimplePumpPublisherModel();
            BufferedReader commandLine = 
                    new java.io.BufferedReader(new InputStreamReader(System.in));
            
            if (args.length == 2) {
                // SET JSON STRING OF ALL SENSOR DATA IN DATABASE
                pub.setSensorData();
                String jsonString = pub.getSensorData();
                System.out.println("sensorJsonString result: " + jsonString);
                // SETUP TIMER/LOOP TO CHECK DATABASE
                if (foreachFlag == true) {
                    // FOREACH LOGIC
                } else if (periodicFlag == true) {
                    // PERIODIC LOGIC
                    milliseconds = Long.parseLong(args[1]) * 1000;
                    t.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            System.out.println("\n\nTimer run() loop\n\n");
                            try {
                                pub.setSensorData();
                                String timeJsonString = pub.getSensorData();
                                System.out.println("timeJsonString result: \n\n" 
                                        + timeJsonString);
                            } catch (Exception e) {
                                System.out.println("Exception thrown: " + e);
                            }
                        }
                    }, milliseconds, MAX_TIMER);
                    System.out.println("\n\nTimer End\n\n");
                }
            }
            
            // CLOSES CONNECTION IF 'exit' IS ENTERED ON COMMAND LINE
            while (true) {
                String s = commandLine.readLine();
                if (s.equalsIgnoreCase("exit")) {
                    pub.connection.close();
                    t.cancel();
                    System.out.println("\n\nTimer cancelled");
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
