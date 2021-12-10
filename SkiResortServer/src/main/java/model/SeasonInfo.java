package model;

public class SeasonInfo {
  private String seasonID;
  private int totalVert;

  public SeasonInfo(String seasonID, int totalVert) {
    this.seasonID = seasonID;
    this.totalVert = totalVert;
  }

  public String getSeasonID() {
    return seasonID;
  }

  public int getTotalVert() {
    return totalVert;
  }
}
