package servlet;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;

import model.LiftRide;
import model.LiftRideDetail;
import model.ResponseMessage;
import model.SeasonInfo;
import model.SkierVertical;
import rabbitmq.RabbitMQUtils;
import rabbitmq.RabbitMQUtilsExtended;
import rabbitmq.RabbitMQUtilsWithFanout;

@WebServlet(name = "servlet.SkierServlet", urlPatterns = "/skiers/*")
public class SkierServlet extends javax.servlet.http.HttpServlet {
  private static Gson gson  = new Gson();
  private static final int DAY_ID_MIN = 1;
  private static final int DAY_ID_MAX = 366;
  private static final String SEASONS_PARAMETER = "seasons";
  private static final String DAYS_PARAMETER = "days";
  private static final String SKIERS_PARAMETER = "skiers";
  private static final String VERTICAL_PARAMETER = "vertical";
//  private static final RabbitMQUtils rmqUtils = new RabbitMQUtils(40);
  private static final RabbitMQUtilsExtended rmqUtils = new RabbitMQUtilsExtended(40);



  protected void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse res) throws javax.servlet.ServletException, IOException {
    res.setContentType("application/json");
    res.setCharacterEncoding("UTF-8");

    String urlPath = req.getPathInfo();

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      ResponseMessage responseMessage = new ResponseMessage("Invalid Parameters");
      res.getWriter().write(gson.toJson(responseMessage));
      return;
    }

    String[] urlParts = urlPath.split("/");
    // and now validate url path and return the response status code
    // (and maybe also some value if input is valid)

//    for(String str: urlParts){
//      System.out.print(str + "  ");
//    }
//    System.out.println();

    if (!isUrlValid(urlParts)) {
      ResponseMessage responseMessage = new ResponseMessage("Invalid Parameters");
      res.getWriter().write(gson.toJson(responseMessage));
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } else {
      StringBuilder buffer = new StringBuilder();
      BufferedReader reader = req.getReader();
      String line;
      while ((line = reader.readLine()) != null) {
        buffer.append(line);
        buffer.append(System.lineSeparator());
      }
      String data = buffer.toString();
//      System.out.println(data);
      LiftRide liftRide = gson.fromJson(data, LiftRide.class);

      LiftRideDetail liftRideDetail = new LiftRideDetail(liftRide,
              Integer.parseInt(urlParts[1]),
              Integer.parseInt(urlParts[3]),
              Integer.parseInt(urlParts[5]),
              Integer.parseInt(urlParts[7]));
//      System.out.println(urlParts[1] + " " + urlParts[3] +
//              " " +urlParts[5]+ " " + urlParts[7]);
////      System.out.println(Integer.parseInt(urlParts[0]) + " " + Integer.parseInt(urlParts[2]) +
////                      " " +Integer.parseInt(urlParts[4])+ " " + Integer.parseInt(urlParts[6]));
//      LiftRideDetail liftRideDetail = new LiftRideDetail(1,2,3,4,5,6);
//      System.out.println("time:"+liftRide.getTime() + ",liftId:" + liftRide.getLiftID());


      String message = gson.toJson(liftRideDetail);
      rmqUtils.publish(message);



//      LiftRide liftRide = new LiftRide(12,1234);
//      LiftRide liftRide = new LiftRide(12,1234);
//      res.getWriter().write(gson.toJson(liftRide));
      res.setStatus(HttpServletResponse.SC_OK);
    }
  }

  public static void main(String[] args) throws InterruptedException {



    /*a connection is tcp socket to a rmq server. Each time the RabbitMQ client library opens a
     connection, it creates a dedicated thread for reading data sent to it from the server over
     that socket. This thread is not related to the ExecutorService object passed to method newConnection.
    */
    Runnable runnable = new Runnable() {
      @Override
      public void run() {

        // channel per thread

        for (int i = 51; i <= 80; i++) {
          LiftRideDetail liftRideDetail = new LiftRideDetail(i,2,3,4,5,6);
          String message = gson.toJson(liftRideDetail).toString();
          rmqUtils.publish(message);
        }
        System.out.println(" [All Messages  Sent '");
      }

    };
    // start threads and wait for completion
    Thread t1 = new  Thread (runnable);
//    Thread t2 = new  Thread (runnable);
    t1.start();
//    t2.start();
    t1.join();
//    t2.join();
    // close connection
    rmqUtils.releaseConnection();
  }

  protected void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse res) throws javax.servlet.ServletException, IOException {
    res.setContentType("application/json");
    res.setCharacterEncoding("UTF-8");
    String urlPath = req.getPathInfo();

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      ResponseMessage responseMessage = new ResponseMessage("Invalid Parameters");
      res.getWriter().write(gson.toJson(responseMessage));
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    String[] urlParts = urlPath.split("/");
    // and now validate url path and return the response status code
    // (and maybe also some value if input is valid)

    if (!isUrlValid(urlParts)) {
      ResponseMessage responseMessage = new ResponseMessage("Invalid Parameters");
      res.getWriter().write(gson.toJson(responseMessage));
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } else {
      res.setStatus(HttpServletResponse.SC_OK);
      if(urlParts.length==3){
        SkierVertical skierVertical = new SkierVertical();
        skierVertical.add(new SeasonInfo("season", 0));
        res.getWriter().write(gson.toJson(skierVertical));
      }
      else{
        res.getWriter().write(gson.toJson(34507));
      }
    }
  }

  private boolean isUrlValid(String[] urlPath) {
    // TODO: validate the request url path according to the API spec
    // urlPath  = "/1/seasons/2019/day/1/skier/123"
    // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
    if (urlPath.length == 8) {
      try {
        for (int i = 1; i < urlPath.length; i += 2) {
          Integer.parseInt(urlPath[i]);
        }
        return (urlPath[3].length() == 4
                && Integer.valueOf(urlPath[5]) >= DAY_ID_MIN
                && Integer.valueOf(urlPath[5]) <= DAY_ID_MAX
                && urlPath[2].equals(SEASONS_PARAMETER)
                && urlPath[4].equals(DAYS_PARAMETER)
                && urlPath[6].equals(SKIERS_PARAMETER));
      } catch (Exception e) {
        return false;
      }
    } else if (urlPath.length == 3) {
      try {
        Integer.parseInt(urlPath[1]);
        return (urlPath[2].equals(VERTICAL_PARAMETER));
      } catch (Exception e) {
        return false;
      }
    }
    return false;
  }
}
