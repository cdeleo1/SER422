package edu.asupoly.heal.aqm.jms;

import java.io.*;
import javax.jms.*;
import javax.naming.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
        InitialContext jndi = new InitialContext(jndiProperties);
        TopicConnectionFactory conFactory
                = (TopicConnectionFactory)jndi.lookup("topicConnectionFactry");
        connection = conFactory.createTopicConnection();
        connection.setClientID(clientName);
        Topic chatTopic = (Topic)jndi.lookup(topicName);
        TopicSession subSession = connection.createTopicSession(false, 
                        Session.AUTO_ACKNOWLEDGE);
        TopicSubscriber subscriber = subSession.createDurableSubscriber(chatTopic,
                        "SimplePumpSubscriberModelWithListener");
        subscriber.setMessageListener(this);
        connection.start();
    }

    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage txtMessage = (TextMessage)message;
                System.out.println("Message received: " + txtMessage.getText());
                String jsonString = (String)txtMessage.getText();
                String returnValue = "PUSH_UNSET";
                if (jsonString != null) {
                    try {
                        IAQMDAO dao = AQMDAOFactory.getDAO();
                        Object obj = JSONValue.parse(jsonString);

                        if (obj instanceof JSONArray) {
                            System.out.println("obj instanceof JSONArray: true");
                            if (dao.importReadings(jsonString)) {
                                returnValue = "SERVER_DYLOS_IMPORT_SUCCESS";
                            } else {
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
            SimplePumpSubscriberModelWithListener subscriber = 
                    new SimplePumpSubscriberModelWithListener(args[0], args[1], 
                            args[2], args[3]);
            BufferedReader commandLine = 
                    new java.io.BufferedReader(new InputStreamReader(System.in));
            // closes the connection and exit the system when 'exit' enters in
            // the command line
            while (true) {
                String s = commandLine.readLine();
                if (s.equalsIgnoreCase("exit")) {
                    subscriber.connection.close();
                    System.exit(0);
                } else {
                    try {
                        IAQMDAO dao = AQMDAOFactory.getDAO();
                        dao.importReadings(s);
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
