package db;

import org.apache.commons.dbcp2.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.LiftRideDetail;

public class LiftRideDao {
  private static BasicDataSource dataSource;

  public LiftRideDao() {
    dataSource = DBCPDataSource.getDataSource();
  }

  public void createLiftRide(LiftRideDetail newLiftRide) {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    String insertQueryStatement = "INSERT INTO LiftRides (skierId, resortId, season, day, time, liftId) " +
            "VALUES (?,?,?,?,?,?)";
    try {
      conn = dataSource.getConnection();
      preparedStatement = conn.prepareStatement(insertQueryStatement);
      preparedStatement.setInt(1, newLiftRide.getSkierId());
      preparedStatement.setInt(2, newLiftRide.getResortId());
      preparedStatement.setInt(3, newLiftRide.getSeason());
      preparedStatement.setInt(4, newLiftRide.getDay());
      preparedStatement.setInt(5, newLiftRide.getTime());
      preparedStatement.setInt(6, newLiftRide.getLiftId());

      // execute insert SQL statement
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (conn != null) {
          conn.close();
        }
        if (preparedStatement != null) {
          preparedStatement.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    LiftRideDao liftRideDao = new LiftRideDao();
    liftRideDao.createLiftRide(new LiftRideDetail(10, 2, 3, 5, 500, 20));
  }
}