package edu.asupoly.heal.aqm.jms;

import javax.jms.*;
import javax.naming.*;
import org.apache.log4j.BasicConfigurator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import edu.asupoly.heal.aqm.dmp.AQMDAOFactory;
import edu.asupoly.heal.aqm.dmp.IAQMDAO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class SimplePumpSubscriberModelWithListener implements 
        javax.jms.MessageListener {
    
    private TopicSession pubSession;
    private TopicConnection connection;
    
    private Properties jndiProperties =  new Properties();

    /* Establish JMS publisher and subscriber */
    public SimplePumpSubscriberModelWithListener(String sensorJson) 
            throws Exception {
        
        final File file = new File("../properties/jndi.properties");
        try {
            jndiProperties.load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Obtain a JNDI connection
        InitialContext jndi = new InitialContext(jndiProperties);
        // Look up a JMS connection factory
        TopicConnectionFactory conFactory
                = (TopicConnectionFactory)jndi.lookup("connectionFactory");
        // Create a JMS connection
        connection = conFactory.createTopicConnection();
        // Look up a JMS topic - see jndi.properties in the classes directory
        Topic chatTopic = (Topic)jndi.lookup("Chat1");
        connection.setClientID("cdeleo1"); // this is normally done by configuration not programmatically
        TopicSession subSession = connection.createTopicSession(false, 
                        TopicSession.AUTO_ACKNOWLEDGE);
        TopicSubscriber subscriber = subSession.createDurableSubscriber(chatTopic,
                        "SimplePumpSubscriberModelWithListener");
        subscriber.setMessageListener(this);  // so we will use onMessage
        // Start the JMS connection; allows messages to be delivered
        connection.start();
    }

    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage txtMessage = (TextMessage) message;
                System.out.println("Message received: " + txtMessage.getText());
            } else {
                System.out.println("Invalid message received.");
            }
        } catch (JMSException e1) {
            e1.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // uncomment this line for verbose logging to the screen
        // BasicConfigurator.configure();

        try {
            if (args.length != 1) {
                System.out.println("Please Provide the sensor data in one of " +
                        "the following formats: \n" +
                        "Dylos Format:\n" +
                        "{\"type\":\"dylos\",\"deviceId\":\"aqm1\",\"userId\":"+
                        "\"user1\", \"dateTime\":\"Fri May 29\n" +
                        "13:27:09 MST 2020\",\"smallParticle\":93," +
                        "\"largeParticle\":26,\n\"geoLatitude\":33.3099177," +
                        "\"geoLongitude\":-111.6726974,\"geoMethod\":" + 
                        "\"manual\"\n}\n\n" +
                        "Sensordrone Format:\n" +
                        "{ \"coData\":3,\"dateTime\":\"20200529_124956\"," +
                        "\"geoLongitude\":-111.6823322,\n\"co2Data\":5," +
                        "\"co2DeviceID\":\"UNKNOWN\",\"geoMethod\":" +
                        "\"Network\",\"type\":\"sensordrone\",\n" +
                        "\"presureData\":96464,\"tempData\":25," +
                        "\"geoLatitude\":33.2993061,\n\"deviceId\":" +
                        "\"SensorDroneB8:FF:FE:B9:C3:FA\",\"humidityData\":" +
                        "32\n}\n\n");
            }
            
            String json = args[0];
            
            SimplePumpSubscriberModelWithListener demo = 
                    new SimplePumpSubscriberModelWithListener(json);
            BufferedReader commandLine = 
                    new java.io.BufferedReader(new InputStreamReader(System.in));
            // closes the connection and exit the system when 'exit' enters in
            // the command line
            while (true) {
                String s = commandLine.readLine();
                if (s.equalsIgnoreCase("exit")) {
                    demo.connection.close();
                    System.exit(0);
                }
                IAQMDAO dao = AQMDAOFactory.getDAO();
                dao.importReadings(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
