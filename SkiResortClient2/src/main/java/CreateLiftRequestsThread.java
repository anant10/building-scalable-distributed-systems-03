import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;

public class CreateLiftRequestsThread implements Runnable {
  private static final String BASE_PATH = "http://alb-01-834638797.us-east-1.elb.amazonaws.com/SkiResortServer/";
  private static final int RESORT_ID = 1;
  private static final String SEASON_ID = "1234";
  private static final String DAY_ID = "12";
  private int skierIdStart;
  private int skierIdEnd;
  private int startTime;
  private int endTime;
  private int numLifts;
  private CountDownLatch phaseLatch;
  private CountDownLatch mainLatch;
  private int numPostRequestForEachThread;
  private SharedStat sharedStat;

  public CreateLiftRequestsThread(int skierIdStart, int skierIdEnd, int startTime,
                                  int endTime, int numLifts, int numPostRequestForEachThread,
                                  CountDownLatch phaseLatch, CountDownLatch mainLatch,
                                  SharedStat sharedStat) {
    this.skierIdStart = skierIdStart;
    this.skierIdEnd = skierIdEnd;
    this.startTime = startTime;
    this.endTime = endTime;
    this.numLifts = numLifts;
    this.numPostRequestForEachThread = numPostRequestForEachThread;
    this.phaseLatch = phaseLatch;
    this.mainLatch = mainLatch;
    this.sharedStat = sharedStat;
  }

  @Override
  public void run() {
    int successfulPosts = 0;
    int failedPosts = 0;
    List<String> responseDetails = new ArrayList<>();
    SkiersApi skiersApi = new SkiersApi();
    ApiClient apiClient = skiersApi.getApiClient();
    apiClient.setBasePath(BASE_PATH);
    for(int i=0; i<numPostRequestForEachThread; i++){
//      System.out.println(i);
      LiftRide liftRide = new LiftRide();
      liftRide.setLiftID(ThreadLocalRandom.current().nextInt(this.numLifts)+1);
      liftRide.setTime(ThreadLocalRandom.current().nextInt(this.endTime-this.startTime)+this.startTime);
      int skierId = ThreadLocalRandom.current().nextInt(
              this.skierIdEnd-this.skierIdStart)+this.skierIdStart;
      ResponseInfo responseInfo = new ResponseInfo();
      long startTime = System.currentTimeMillis();
      responseInfo.setStartTime(startTime);
      sendSingleRequest(skiersApi, liftRide, skierId, responseInfo);
      while(responseInfo.getResponseCode()!=200 && responseInfo.getFailedAttempts()>0){
        sendSingleRequest(skiersApi, liftRide, skierId, responseInfo);
      }
      if(responseInfo.getResponseCode()==200){
        successfulPosts++;
      }
      else{
        failedPosts++;
      }
      long latency = responseInfo.getLatency();
      String eachResponse =
              responseInfo.getStartTime() + ",POST," + latency + "," + responseInfo.getResponseCode() + "\n";
      responseDetails.add(eachResponse);
    }

    phaseLatch.countDown();
    mainLatch.countDown();
    sharedStat.incSuccessfulPosts(successfulPosts);
    sharedStat.incFailedPosts(failedPosts);
    sharedStat.addAllResponseTimes(responseDetails);

  }

  private void sendSingleRequest(SkiersApi skiersApi, LiftRide liftRide, int skierId,
                                         ResponseInfo responseInfo){
    try {
      skiersApi.writeNewLiftRide(liftRide, this.RESORT_ID, this.SEASON_ID, this.DAY_ID, skierId);

      responseInfo.setEndTime(System.currentTimeMillis());
      responseInfo.setResponseCode(200);
    } catch (ApiException e) {
      responseInfo.setEndTime(System.currentTimeMillis());
      responseInfo.decFailedAttempts();
      responseInfo.setResponseCode(500);
      e.printStackTrace();
    }
  }
}
