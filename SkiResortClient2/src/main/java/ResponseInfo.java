public class ResponseInfo {
  private int responseCode;
  private long startTime;
  private long endTime;
  private int failedAttempts;


  public ResponseInfo() {
    this.failedAttempts = 5;
  }

  public void setResponseCode(int responseCode) {
    this.responseCode = responseCode;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }

  public int getResponseCode() {
    return responseCode;
  }

  public int getFailedAttempts() {
    return failedAttempts;
  }

  public void decFailedAttempts(){
    failedAttempts--;
  }

  public long getStartTime() {
    return startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public long getLatency(){
    return this.endTime - this.startTime;
  }

}
