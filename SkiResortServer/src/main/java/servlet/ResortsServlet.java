package servlet;

import com.google.gson.Gson;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Resort;
import model.ResortsList;
import model.ResponseMessage;
import model.SeasonsList;

@WebServlet(name = "servlet.ResortsServlet", urlPatterns = "/resorts/*")
public class ResortsServlet extends HttpServlet {
  private Gson gson  = new Gson();
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
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

    if (!isUrlValid(urlParts)) {
      ResponseMessage responseMessage = new ResponseMessage("Invalid Parameters");
      res.getWriter().write(gson.toJson(responseMessage));
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } else {
      res.setStatus(HttpServletResponse.SC_OK);
    }
  }

  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException,
          IOException {
    res.setContentType("application/json");
    res.setCharacterEncoding("UTF-8");
    String urlPath = req.getPathInfo();
//    System.out.println(urlPath);
    // check we have a URL!
    if (urlPath == null ) {
      res.setStatus(HttpServletResponse.SC_OK);
      // do any sophisticated processing with urlParts which contains all the url params
      Resort resort = new Resort("dummy", 1);
      ResortsList resortsList = new ResortsList();
      resortsList.add(resort);
      res.getWriter().write(gson.toJson(resortsList));
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
      // do any sophisticated processing with urlParts which contains all the url params
      SeasonsList seasonsList = new SeasonsList();
      seasonsList.add("s2334");
      res.getWriter().write(gson.toJson(seasonsList));
    }
  }

  private boolean isUrlValid(String[] urlPath) {
    // TODO: validate the request url path according to the API spec
    // urlPath  = "/1/seasons/2019/day/1/skier/123"
    // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]
     if (urlPath.length == 3) {
      try {
        Integer.parseInt(urlPath[1]);
        return (urlPath[2].equals("seasons"));
      } catch (Exception e) {
        return false;
      }
    }
     else return urlPath.length == 1;
  }
}
