package model;

import java.util.ArrayList;
import java.util.List;

public class SkierVertical {
  private List<SeasonInfo> resorts;

  public SkierVertical() {
    resorts = new ArrayList<>();
  }

  public void add(SeasonInfo info){
    resorts.add(info);
  }

  public List<SeasonInfo> getResorts() {
    return resorts;
  }
}
