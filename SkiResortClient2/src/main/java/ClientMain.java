import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ClientMain {

  public static void main(String[] args) {
    CommandLineParser parser = new CommandLineParser();
    if (!parser.parseCommandLine(args)){
      return;
    }

    Integer numThreads = parser.getNumThreads();
    Integer numSkiers = parser.getNumSkiers();
    Integer numLifts = parser.getNumLifts();
    Integer numRuns = parser.getNumRuns();
    Integer port = parser.getPort();

    // Print out input info
    System.out.println("Number of threads: " + numThreads);
    System.out.println("Number of skiers: " + numSkiers);
    System.out.println("Number of lifts: " + numLifts);
    System.out.println("Number of runs: " + numRuns);
    System.out.println("port " + port);


    // phase 1
    int quarterOfNumThreads = (int)Math.round(numThreads/4.0);
    SharedStat sharedStat = new SharedStat();
    int totalThreads = (quarterOfNumThreads * 2) + numThreads;
    System.out.println("Total number of threads(N): " + totalThreads);

    CountDownLatch mainLatch = new CountDownLatch(totalThreads);
    int numPostRequestsEachThreadPhase1 = (int)Math.round((numRuns * 0.2) * (numSkiers/(quarterOfNumThreads + 0.0)));
    /*
    * NumWaitThreads are the amount of threads phase one should complete so that phase two can
    * start. Phase one will start quarterOfNumThreads and waits till NumWaitThreads are complete.
    *  As soon as NumWaitThreads are complete, the remaining threads will be running but once the
    *  function is run, we can start phase two */
    int numWaitThreadsPhase1 = (int)Math.ceil(quarterOfNumThreads / 10.0);
    Phase phase1 = new Phase(quarterOfNumThreads, numSkiers, 1, 90, numLifts, numRuns,
            numWaitThreadsPhase1, numPostRequestsEachThreadPhase1, mainLatch, sharedStat);
//    Phase phase1 = new Phase(quarterOfNumThreads, numSkiers, 1, 90, numLifts, numRuns,
//            1, numPostRequestsEachThreadPhase1, mainLatch, sharedStat);



    int numPostRequestsEachThreadPhase2 =
            (int)Math.round((numRuns * 0.6) * (numSkiers/(numThreads+0.0)));
    int numWaitThreadsPhase2 = (int)Math.ceil(numThreads / 10.0);
    Phase phase2 = new Phase(numThreads, numSkiers, 91, 360, numLifts, numRuns,
            numWaitThreadsPhase2, numPostRequestsEachThreadPhase2, mainLatch, sharedStat);


    int numPostRequestsEachThreadPhase3= (int)Math.round(numRuns * 0.1);
    Phase phase3 = new Phase(quarterOfNumThreads, numSkiers, 361, 420, numLifts, numRuns,
            0, numPostRequestsEachThreadPhase3, mainLatch, sharedStat);




    // run three phases
    long exptStartTime = System.currentTimeMillis();
    try {
      phase1.runMultipleThreads();
      phase2.runMultipleThreads();
      phase3.runMultipleThreads();
      mainLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    long exptEndTime = System.currentTimeMillis();
    long wallTime = (exptEndTime - exptStartTime)/1000;
    StatCalc statCalc = new StatCalc(sharedStat.getResponses(),
            sharedStat.getSucessfullPosts(), wallTime);


    System.out.println("sucessful posts: " + sharedStat.getSucessfullPosts());
    System.out.println("unsucessful posts: " + sharedStat.getFailedPosts());
    System.out.println("wall time: " + wallTime + "secs");
    System.out.println("throughput (lambda): " + statCalc.getThroughput());
    System.out.println("MEAN Response Time: " + statCalc.getMean() );
    System.out.println("MEDIAN Response Time: " + statCalc.getMedian() );
    System.out.println("99th percentile Response Time: " + statCalc.getNintyninthPercentile() );
    System.out.println("MAX Response Time: " + statCalc.getMaxResponse() );


  }

}
