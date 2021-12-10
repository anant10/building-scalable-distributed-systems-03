package model;

public class LiftRideDetail {
private int liftId;
private int day;
private int time;
private int skierId;
private int resortId;
private int season;

  public LiftRideDetail(int liftId, int time, int resortId, int season, int day, int skierId) {
    this.liftId = liftId;
    this.day = day;
    this.time = time;
    this.skierId = skierId;
    this.resortId = resortId;
    this.season = season;
  }

  public LiftRideDetail(LiftRide liftRide, int resortId, int season, int day, int skierId) {
    this.liftId = liftRide.getLiftID();
    this.time = liftRide.getTime();
    this.day = day;
    this.skierId = skierId;
    this.resortId = resortId;
    this.season = season;
  }

  public int getLiftId() {
    return liftId;
  }

  public int getDay() {
    return day;
  }

  public int getTime() {
    return time;
  }

  public int getSkierId() {
    return skierId;
  }

  public int getResortId() {
    return resortId;
  }

  public int getSeason() {
    return season;
  }
}
