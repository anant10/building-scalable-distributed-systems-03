package model;

public class LiftRide {
  private int time;
  private int liftID;

  public LiftRide(int time, int liftID) {
    this.time = time;
    this.liftID = liftID;
  }

  public int getTime() {
    return time;
  }

  public int getLiftID() {
    return liftID;
  }
}
