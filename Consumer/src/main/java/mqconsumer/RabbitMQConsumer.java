package mqconsumer;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import db.LiftRideDao;


public class RabbitMQConsumer {

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUsername("guest");
    factory.setPassword("guest");
    factory.setVirtualHost("/");
    factory.setHost("54.175.96.218");
    factory.setPort(5672);
    final Connection connection = factory.newConnection();
    LiftRideDao liftRideDao = new LiftRideDao();

    ExecutorService executor = Executors.newFixedThreadPool(100);//2 Threads
    for (int i = 0; i < 100; i++) { // call the (Processor(i).run) 2 times with 2 threads
      executor.submit(new Processor(connection, liftRideDao));
    }
  }
}
