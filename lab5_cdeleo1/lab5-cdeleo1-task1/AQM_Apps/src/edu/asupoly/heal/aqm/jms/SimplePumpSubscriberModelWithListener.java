package edu.asupoly.heal.aqm.jms;

import java.io.*;
import javax.jms.*;
import javax.naming.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import java.util.Random;

import edu.asupoly.heal.aqm.dmp.IAQMDAO;
import edu.asupoly.heal.aqm.dmp.AQMDAOFactory;
import edu.asupoly.heal.aqm.dmp.AQMDAOJDBCImpl;
import edu.asupoly.heal.aqm.model.DylosReading;
import edu.asupoly.heal.aqm.model.SensordroneReading;
import edu.asupoly.heal.aqm.model.ServerPushEvent;

public class SimplePumpSubscriberModelWithListener implements 
        javax.jms.MessageListener {
    
    private TopicSession pubSession;
    private TopicConnection connection;
    
    private Properties jndiProperties =  new Properties();

    /* Establish JMS publisher and subscriber */
    public SimplePumpSubscriberModelWithListener(String topicName, 
            String clientName, String username, String password) 
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
                = (TopicConnectionFactory)jndi.lookup("topicConnectionFactry");
        // Create a JMS connection
        connection = conFactory.createTopicConnection();
        connection.setClientID(clientName);
        // Look up a JMS topic - see jndi.properties in the classes directory
        Topic chatTopic = (Topic)jndi.lookup(topicName);
        //connection.setClientID("cdeleo1"); // this is normally done by configuration not programmatically
        TopicSession subSession = connection.createTopicSession(false, 
                        Session.AUTO_ACKNOWLEDGE);
        TopicSubscriber subscriber = subSession.createDurableSubscriber(chatTopic,
                        "SimplePumpSubscriberModelWithListener");
        subscriber.setMessageListener(this);  // so we will use onMessage
        // Start the JMS connection; allows messages to be delivered
        connection.start();
    }

    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage txtMessage = (TextMessage)message;
                System.out.println("Message received: " + txtMessage.getText());
                Boolean appReturnValue;
                String jsonString = (String)txtMessage.getText();
                System.out.println("jsonString value: " + jsonString);
                String returnValue = "PUSH_UNSET";
                if (jsonString != null) {
                    try {
                        IAQMDAO dao = AQMDAOFactory.getDAO();
                        Object obj = JSONValue.parse(jsonString);

                        if (obj instanceof JSONArray) {
                            System.out.println("obj instanceof JSONArray: true");
                            if (dao.importReadings(jsonString)) {
                                System.out.println("dao.importReadings(jsonString): SERVER_DYLOS_IMPORT_SUCCESS");
                                returnValue = "SERVER_DYLOS_IMPORT_SUCCESS";
                            } else {
                                System.out.println("dao.importReadings(jsonString): SERVER_SENSOR_IMPORT_FAILED");
                                returnValue = "SERVER_SENSOR_IMPORT_FAILED";
                            }
                        }
                    } catch (Exception daoException) {
                        daoException.printStackTrace();
                    }
                    
                } else {
                    returnValue = "SERVER_BAD_OBJECT_TYPE";
                }
                
                System.out.println("returnValue value: " + returnValue);
                
                /*if(txtString != null) {
                    try {
                        IAQMDAO dao = AQMDAOFactory.getDAO();
                        appReturnValue = dao.importReadings(txtString);
                        System.out.println("appReturnValue = " + appReturnValue);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                } */             
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
            if (args.length != 4) {
                System.out.println("Please Provide the topic name, unique "
                        + "client id, username, password!");
            }
            
            SimplePumpSubscriberModelWithListener demo = 
                    new SimplePumpSubscriberModelWithListener(args[0], args[1], 
                            args[2], args[3]);
            BufferedReader commandLine = 
                    new java.io.BufferedReader(new InputStreamReader(System.in));
            // closes the connection and exit the system when 'exit' enters in
            // the command line
            while (true) {
                String s = commandLine.readLine();
                if (s.equalsIgnoreCase("exit")) {
                    demo.connection.close();
                    System.exit(0);
                } else {
                    Boolean appReturnValue = false;
                    try {
                        IAQMDAO dao = AQMDAOFactory.getDAO();
                        appReturnValue = dao.importReadings(s);
                        System.out.println("appReturnValue = " + appReturnValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}