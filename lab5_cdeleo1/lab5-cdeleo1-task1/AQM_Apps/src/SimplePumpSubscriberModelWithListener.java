import javax.jms.*;
import javax.naming.*;
import org.apache.log4j.BasicConfigurator;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SimplePumpSubscriberModelWithListener implements 
        javax.jms.MessageListener {

    private TopicSession pubSession;
    private TopicConnection connection;

    /* Establish JMS publisher and subscriber */
    public SimplePumpSubscriberModelWithListener(String topicName, 
            String clientName, String username, String password) 
            throws Exception {

        // Obtain a JNDI connection
        InitialContext jndi = new InitialContext();

        // Look up a JMS connection factory
        TopicConnectionFactory conFactory
                = (TopicConnectionFactory)jndi.lookup("connectionFactory");

        // Create a JMS connection
        connection = conFactory.createTopicConnection();
        // Look up a JMS topic - see jndi.properties in the classes directory
        Topic chatTopic = (Topic)jndi.lookup("Chat1");
        //connection.setClientID(); // this is normally done by configuration not programmatically

        
        TopicSession subSession = connection.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
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
            if (args.length != 4) {
                System.out.println(
                        "Please Provide the topic name,unique client id, "
                                + "username, password!");
            }

            SimplePumpSubscriberModelWithListener demo = 
                    new SimplePumpSubscriberModelWithListener(
                            args[0], args[1], args[2], args[3]);
            
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
