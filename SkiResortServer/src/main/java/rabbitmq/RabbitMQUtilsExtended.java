package rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;


public class RabbitMQUtilsExtended {
  private static Connection connection;
  private final static String SKIER_QUEUE_NAME = "SkierQueue";
  private final static String RESORT_QUEUE_NAME = "ResortQueue";
  private static final String DOMAIN = "54.175.96.218";
  private static final Integer PORT = 5672;
  private static final String USERNAME = "guest";
  private static final String PASSWORD = "guest";
  private static final String VHOST = "/";
  private static BlockingQueue<Channel> skierQueue;
  private static BlockingQueue<Channel> resortQueue;

  public RabbitMQUtilsExtended(int numberOfChannels){
    skierQueue = new ArrayBlockingQueue<Channel>(numberOfChannels);
    resortQueue = new ArrayBlockingQueue<Channel>(numberOfChannels);
    try{
      ConnectionFactory factory = new ConnectionFactory();

      factory.setHost(DOMAIN);
      factory.setPort(PORT);
      factory.setVirtualHost(VHOST);
      factory.setUsername(USERNAME);
      factory.setPassword(PASSWORD);

      connection = factory.newConnection();
      for(int i=0; i<numberOfChannels; i++){
        Channel channel = connection.createChannel();
        channel.queueDeclare(SKIER_QUEUE_NAME, true, false, false, null);
        skierQueue.put(connection.createChannel());

        Channel channel2 = connection.createChannel();
        channel2.queueDeclare(RESORT_QUEUE_NAME, true, false, false, null);
        resortQueue.put(connection.createChannel());
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (TimeoutException e) {
      e.printStackTrace();
    }
  }

  public void publish(String message) {
    try {
      Channel channel1 = skierQueue.take();
      channel1.basicPublish("", SKIER_QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
      skierQueue.put(channel1);

      Channel channel2 = resortQueue.take();
      channel2.basicPublish("", RESORT_QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
      resortQueue.put(channel2);
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void releaseConnection() {
    if (connection != null) {
      try {
        connection.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void releaseResources() {
    try {
      while(!skierQueue.isEmpty()){
        Channel channel = skierQueue.take();
        channel.close();
      }
      if (connection != null)  connection.close();
    } catch (IOException | TimeoutException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}