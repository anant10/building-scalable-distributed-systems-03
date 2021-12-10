import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StatCalc {
  private List<Long> responseTimes;
  private double mean;
  private double median;
  private int throughput;
  private long maxResponse;
  private long nintyninthPercentile;

  public StatCalc(List<String> responseTimes, int successfulPosts, long wallTime) {
    this.responseTimes = new ArrayList<>();
    getResponseTimes(responseTimes);
    this.mean = calculateMean();
    this.median = calculateMedian();
    this.throughput =  (int)(successfulPosts / wallTime);
    this.maxResponse = this.responseTimes.get(responseTimes.size() - 1);
    this.nintyninthPercentile =
            this.responseTimes.get((int) Math.ceil(responseTimes.size() * 0.99) - 1);
  }

  private void getResponseTimes(List<String> results) {
    for (String result : results) {
      String[] resultSplit = result.split(",");
      Long responseTime = Long.parseLong(resultSplit[2]);
      responseTimes.add(responseTime);
    }
  }

  private double calculateMean() {
    double sum = 0;
    for (Long response : this.responseTimes) {
      sum += response;
    }
    return sum / responseTimes.size();
  }

  private double calculateMedian() {
    Collections.sort(responseTimes);
    double median;
    int numElements = responseTimes.size();
    if (numElements % 2 == 0) {
      long sumOfMiddleElements =
              responseTimes.get(numElements / 2) + responseTimes.get(numElements / 2 - 1);
      median = ((double) sumOfMiddleElements) / 2;
    } else {
      median = (double) responseTimes.get(numElements / 2);
    }
    return median;
  }

  public double getMean() {
    return mean;
  }

  public double getMedian() {
    return median;
  }

  public int getThroughput() {
    return throughput;
  }

  public long getMaxResponse() {
    return maxResponse;
  }

  public long getNintyninthPercentile() {
    return nintyninthPercentile;
  }
}