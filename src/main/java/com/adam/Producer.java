package com.adam;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.io.IOUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import java.io.File;
import java.io.FileReader;
import java.lang.invoke.MethodHandles;

public class Producer {

    private final static Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {
        try {
            Parameters parameters = new Parameters(args);

            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(parameters.url);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Topic destination = session.createTopic(parameters.topic);

            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            String messageBody = IOUtils.toString(new FileReader(parameters.message));

            TextMessage message = session.createTextMessage(messageBody);
            message.setStringProperty("Channel", parameters.channel);
            message.setJMSExpiration(parameters.expiration);

            LOG.info("Sent message: {}", message);
            producer.send(message);

            session.close();
            connection.close();
        } catch (Exception e) {
            LOG.error("Producing interrupted", e);
        }
    }

    private static class Parameters {

        @Option(name = "-url", required = false)
        public String url = "tcp://localhost:61616";

        @Option(name = "-topic", required = true)
        public String topic;

        @Option(name = "-channel", required = true)
        public String channel;

        @Option(name = "-message", required = true)
        public File message;

        @Option(name = "-expiration", required = false)
        public int expiration = 86400000;

        public Parameters(String[] args) throws CmdLineException {
            parse(args);
        }

        public void parse(String[] args) throws CmdLineException {
            CmdLineParser parser = new CmdLineParser(this);
            parser.parseArgument(args);
        }
    }

}
