package edu.asupoly.heal.aqm.jms;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Properties;
import javax.jms.*;
import javax.naming.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

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

    private static final String USAGE = 
            "java .:../lib/*:classes <ChatTopic> <username> <password>";
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

        InitialContext jndi = new InitialContext(jndiProperties);
        TopicConnectionFactory conFactory
                = (TopicConnectionFactory) jndi.lookup("topicConnectionFactry");
        connection = conFactory.createTopicConnection(username, password);
        String jmsURL = "tcp://localhost:61616";
        ActiveMQConnectionFactory connectionFactory = 
                new ActiveMQConnectionFactory(jmsURL);
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
        try {
            if (args.length != 3) {
                System.out.println("Please Provide the topic name, username, " +
                        "password!");
            }
            SimplePumpPublisherModel pub
                    = new SimplePumpPublisherModel(args[0], args[1], args[2]);
            BufferedReader commandLine
                    = new java.io.BufferedReader(new InputStreamReader(System.in));
            // closes the connection and exit the system when 'exit' entered 
            // in the command line
            while (true) {
                String s = commandLine.readLine();
                if (s.equalsIgnoreCase("exit")) {
                    pub.connection.close();
                    System.exit(0);
                }
                if (pub.goPublish(s)) {
                    System.out.println("Published " + s);
                } else {
                    System.out.println("Unable to publish " + s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
