import java.util.concurrent.CountDownLatch;

public class Phase {
  private int numThreads;
  private int numSkiers;
  private int startTime;
  private int endTime;
  private int numLifts;
  private int numRuns;
  private int numWaitThreads;
  private int numPostRequests;
  private CountDownLatch mainLatch;
  private SharedStat sharedStat;

  public Phase(int numThreads, int numSkiers, int startTime, int endTime, int numLifts,
               int numRuns, int numWaitThreads, int numPostRequests, CountDownLatch mainLatch,
               SharedStat sharedStat) {
    this.numThreads = numThreads;
    this.numSkiers = numSkiers;
    this.startTime = startTime;
    this.endTime = endTime;
    this.numLifts = numLifts;
    this.numRuns = numRuns;
    this.numWaitThreads = numWaitThreads;
    this.numPostRequests = numPostRequests;
    this.mainLatch = mainLatch;
    this.sharedStat = sharedStat;
  }

  public void runMultipleThreads() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(this.numWaitThreads);
    int numSkiersPerThread = numSkiers/numThreads;
    for(int i=0; i<this.numThreads; i++){
      int skierIdStart = 1 + (i * numSkiersPerThread);
      int skierIdEnd = (i+1) * (numSkiersPerThread);
      Runnable createLiftRequestsThread = new CreateLiftRequestsThread(skierIdStart, skierIdEnd,
              startTime, endTime, numLifts, numPostRequests, latch, mainLatch, sharedStat);
      Thread thread = new Thread(createLiftRequestsThread);
      thread.start();
    }
    latch.await();
  }

}
