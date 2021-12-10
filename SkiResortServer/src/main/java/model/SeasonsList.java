package model;

import java.util.ArrayList;
import java.util.List;

public class SeasonsList {
  private List<String> seasons;

  public SeasonsList() {
    seasons = new ArrayList<>();
  }

  public void add(String season){
    this.seasons.add(season);
  }

  public List<String> getSeasons() {
    return seasons;
  }
}
