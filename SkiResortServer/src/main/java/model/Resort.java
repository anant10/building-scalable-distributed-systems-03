package model;

public class Resort {
  private String resortName;
  private int resortID;

  public Resort(String resortName, int resortID) {
    this.resortName = resortName;
    this.resortID = resortID;
  }

  public String getResortName() {
    return resortName;
  }

  public int getResortID() {
    return resortID;
  }
}
