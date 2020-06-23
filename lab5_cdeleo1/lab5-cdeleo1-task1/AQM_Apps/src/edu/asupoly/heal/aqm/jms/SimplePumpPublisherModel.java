package edu.asupoly.heal.aqm.jms;

import javax.jms.*;
import javax.naming.*;
import org.apache.log4j.BasicConfigurator;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.io.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Properties;

import java.io.IOException;

import edu.asupoly.heal.aqm.dmp.IAQMDAO;
import edu.asupoly.heal.aqm.dmp.AQMDAOFactory;
import edu.asupoly.heal.aqm.dmp.AQMDAOJDBCImpl;
import edu.asupoly.heal.aqm.model.DylosReading;
import edu.asupoly.heal.aqm.model.SensordroneReading;
import edu.asupoly.heal.aqm.model.ServerPushEvent;

public class SimplePumpPublisherModel implements javax.jms.MessageListener {

    private TopicSession pubSession;
    private TopicPublisher publisher;
    private TopicConnection connection;

    private static final String USAGE = "java edu.asupoly.heal.aqm.clients.SimplePump -url <URL> -device dylos|sensordrone -lat1 float -lat2 float -long1 float -long2 float -rate int -num int";

    private static final String URL = "-url";
    private static final String RATE = "-rate";
    private static final String NUM = "-num";
    private static final String LAT1 = "-lat1";
    private static final String LONG1 = "-long1";
    private static final String LAT2 = "-lat2";
    private static final String LONG2 = "-long2";
    private static final String DEVICE = "-device";

    private static final String DYLOS_TEMPLATE_JSON = "{ \"type\":\"dylos\",\"deviceId\":\"DEVICE\",\"userId\":\"simple_pump\"," + " \"dateTime\":\"DATE_TIME\",\"smallParticle\":READING1,\"largeParticle\":READING2," + " \"geoLatitude\":GEO_LAT,\"geoLongitude\":GEO_LONG,\"geoMethod\":\"random\" }";
    private static final String SENSORDRONE_TEMPLATE_JSON = "{ \"coData\":READING1,\"dateTime\":\"DATE_TIME\",\"geoLongitude\":GEO_LONG," + " \"co2Data\":READING2,\"co2DeviceID\":\"UNKNOWN\",\"geoMethod\":\"random\",\"type\":\"sensordrone\"," + " \"pressureData\":-1,\"tempData\":-1,\"geoLatitude\":GEO_LAT, \"deviceId\":\"DEVICE\",\"humidityData\":-1 }";

    private static final int DEFAULT_RATE = 10; // number of pushes per minute
    private static final int DEFAULT_NUM = 20; // total number of pushes
    private static final float DEFAULT_LAT1 = 33.42f;  // these default coords draw a bounding box from tempe campus to poly campus
    private static final float DEFAULT_LONG1 = -111.963f;
    private static final float DEFAULT_LAT2 = 33.28f;
    private static final float DEFAULT_LONG2 = -111.6f;
    private static final String DEFAULT_URL = "http://localhost:8080/aqm/aqmimport";
    private static final String DEFAULT_DEVICE_ID = "UNKNOWN";
    private static final int DYLOS_UPPER_BOUND_SMALL = 1500;
    private static final int SENSORDRONE_UPPER_BOUND_SMALL = 50;
    private static final int DYLOS_UPPER_BOUND_LARGE = 1500;
    private static final int SENSORDRONE_UPPER_BOUND_LARGE = 2000;

    private Properties jndiProperties = new Properties();

    /* Establish JMS publisher */
    public SimplePumpPublisherModel(String topicName, String username,
            String password) throws Exception {

        final File file = new File("../properties/jndi.properties");
        try {
            jndiProperties.load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Obtain a JNDI connection - see jndi.properties
        InitialContext jndi = new InitialContext(jndiProperties);
        // Look up a JMS connection factory
        TopicConnectionFactory conFactory
                = (TopicConnectionFactory) jndi.lookup("topicConnectionFactry");
        // Create a JMS connection
        connection = conFactory.createTopicConnection(username, password);
        String jmsURL = "tcp://localhost:61616";
        // Create a ConnectionFactory
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(jmsURL);
        connectionFactory.createConnection();
        // Create JMS session objects for publisher
        pubSession = connection.createTopicSession(false,
                TopicSession.AUTO_ACKNOWLEDGE);
        //TopicSession subSession = 
        //        connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        // Look up a JMS topic
        Topic chatTopic = (Topic) jndi.lookup(topicName);

        // Create a JMS publisher
        publisher = pubSession.createPublisher(chatTopic);
        //TopicSubscriber subscriber = subSession.createSubscriber(chatTopic);
        // Set a JMS message listener
        //subscriber.setMessageListener(this);
        // Start the JMS connection; allows messages to be delivered
        connection.start();
        // Create and send message using topic publisher
        //TextMessage message = pubSession.createTextMessage();
        //message.setText(username + ": Howdy Friends!");
        //publisher.publish(message);
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

    /*
     * A client can register a message listener with a consumer. A message
     * listener is similar to an event listener. Whenever a message arrives at
     * the destination, the JMS provider delivers the message by calling the
     * listener's onMessage method, which acts on the contents of the message.
     */
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            System.out.println(text);
        } catch (JMSException jmse) {
            jmse.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // uncomment this line for verbose logging to the screen
        //BasicConfigurator.configure();

        float lat1 = DEFAULT_LAT1;
        float lat2 = DEFAULT_LAT2;
        float long1 = DEFAULT_LONG1;
        float long2 = DEFAULT_LONG2;
        int rate = DEFAULT_RATE;
        int num = DEFAULT_NUM;
        String device = DEFAULT_DEVICE_ID;
        String url = DEFAULT_URL;
        int sensorReading1 = 0, sensorReading2 = 0;
        float latitude = DEFAULT_LAT1, longitude = DEFAULT_LONG1;   // the actual coordinates for the sensor readings.

        try {
            if (args.length != 3) {
                System.out
                        .println("Please Provide the topic name,username,password!");
            }
            /*
            for (int i = 0; i < args.length; i++) {
                // if I wasn't lazy I would've made a String enum or something
                System.out.println("Processing arg " + args[i] + " value " + 
                        args[i + 1]);
                if (args[i].equals(URL)) {
                    url = args[++i];
                } else if (args[i].equals(LAT1)) {
                    lat1 = Integer.parseInt(args[++i]);
                } else if (args[i].equals(LAT2)) {
                    lat2 = Integer.parseInt(args[++i]);
                } else if (args[i].equals(LONG1)) {
                    long1 = Integer.parseInt(args[++i]);
                } else if (args[i].equals(LONG2)) {
                    long2 = Integer.parseInt(args[++i]);
                } else if (args[i].equals(RATE)) {
                    rate = Integer.parseInt(args[++i]);
                } else if (args[i].equals(NUM)) {
                    num = Integer.parseInt(args[++i]);
                } else if (args[i].equals(DEVICE)) {
                    device = args[++i];
                } else {
                    System.out.println(USAGE); // we got some unexpected flag or value
                    System.exit(-1);
                }
            }
            // let's arrange lat/long in ascending order to make our random num gen code easier
            if (lat1 > lat2) {
                float dummy = lat2;
                lat2 = lat1;
                lat1 = dummy;
            }
            if (long1 > long2) {
                float dummy = long2;
                long2 = long1;
                long1 = dummy;
            }
            // geoLocation within the lat/long bounding box
            Random randomGen = new Random(System.currentTimeMillis());
            String json = null;

            SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z YYYY");

            for (int i = 0; i < num; i++) {
                String nextDate = dateFormatter.format(
                        new Date(System.currentTimeMillis()));
                // need to know which sensor
                if (device.equals(DEFAULT_DEVICE_ID)) {
                    device = randomGen.nextBoolean() ? "dylos" : "sensordrone";
                }
                // set device-specific values for readings
                if (device.equalsIgnoreCase("dylos")) {
                    json = DYLOS_TEMPLATE_JSON;
                    sensorReading1 = randomGen.nextInt(DYLOS_UPPER_BOUND_SMALL);
                    sensorReading2 = randomGen.nextInt(DYLOS_UPPER_BOUND_LARGE);
                } else if (device.equalsIgnoreCase("sensordrone")) {
                    json = SENSORDRONE_TEMPLATE_JSON;
                    sensorReading1 = randomGen.nextInt(SENSORDRONE_UPPER_BOUND_SMALL);
                    sensorReading2 = randomGen.nextInt(SENSORDRONE_UPPER_BOUND_LARGE);
                }

                // need to gen lat long within our box.
                latitude = randomGen.nextFloat() * (lat2 - lat1) + lat1;
                longitude = randomGen.nextFloat() * (long2 - long1) + long1;

                // set the fields, same sub in both
                json = json.replace("READING1", "" + sensorReading1);
                json = json.replace("READING2", "" + sensorReading2);
                json = json.replace("GEO_LAT", "" + latitude);
                json = json.replace("GEO_LONG", "" + longitude);
                json = json.replace("DATE_TIME", nextDate);
                json = json.replace("DEVICE", device);
             */
            SimplePumpPublisherModel demo
                    = new SimplePumpPublisherModel(args[0], args[1], args[2]);

            //demo.goPublish(json);
            BufferedReader commandLine
                    = new java.io.BufferedReader(new InputStreamReader(System.in));

            // closes the connection and exit the system when 'exit' entered in the command line
            while (true) {
                String s = commandLine.readLine();
                if (s.equalsIgnoreCase("exit")) {
                    demo.connection.close();
                    System.exit(0);
                }
                if (demo.goPublish(s)) {
                    System.out.println("Published " + s);
                } else {
                    System.out.println("Unable to publish " + s);
                }
            }
            //} 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
