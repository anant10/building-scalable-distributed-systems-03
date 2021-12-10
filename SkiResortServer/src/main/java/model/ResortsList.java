package model;

import java.util.ArrayList;
import java.util.List;

public class ResortsList {
  private List<Resort> resorts;

  public ResortsList() {
    this.resorts = new ArrayList<>();
  }

  public void add(Resort resort){
    this.resorts.add(resort);
  }

  public List<Resort> getResorts() {
    return resorts;
  }
}
