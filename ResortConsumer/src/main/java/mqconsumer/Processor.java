package mqconsumer;

import com.google.gson.Gson;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import db.LiftRideDao;
import model.LiftRideDetail;

public class Processor implements Runnable {

  private static Gson gson  = new Gson();
  private final static String QUEUE_NAME = "ResortQueue";
  private Connection connection;
  private LiftRideDao liftRideDao;
  private boolean autoAck = true;

  public Processor(Connection conn, LiftRideDao liftRideDao) {
    this.liftRideDao = liftRideDao;
    this.connection = conn;
  }

  public void run() {
    try {
      final Channel channel = connection.createChannel();
      channel.queueDeclare(QUEUE_NAME, true, false, false, null);
      // max one message per receiver
      channel.basicQos(1);
      System.out.println(" [*] Thread waiting for messages. To exit press CTRL+C");

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        if(!autoAck){
          channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
          System.out.println( "Callback thread ID = " + Thread.currentThread().getId() + " Received '" + message + "'");
        }

        LiftRideDetail liftRideDetail = gson.fromJson(message, LiftRideDetail.class);
        this.liftRideDao.createLiftRide(liftRideDetail);
//        System.out.println( "Callback thread ID = " + Thread.currentThread().getId() + " Received '" + message + "'");
      };
      // process messages
      channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> { });
    } catch (IOException ex) {
      Logger.getLogger(Processor.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}