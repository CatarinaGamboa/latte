import java.util.TimerTask;

import specification.Borrowed;
import specification.Free;

public class SSSTimerTaskCannotReschedule {

    /*
    * Error cannot reschedule  a timer
    */
    public static void example1801324_simplified( @Borrowed Timer timer, String sessionKey, @Free TimerTask tt) {

        // Step 2) Cancel the timer
        timer.cancel();

        // Step 3 Schedule a new task for this timer -> ERROR Cannot reschedule a Timer
        timer.schedule(tt , 1000);
    }

}

/*
    * [If the timer is cancelled] any further attempt to schedule a task on the timer will result in an IllegalStateException
    * 
    * START --- schedule() ----> [SCHEDULED]--------> 
    *       ----------------------------------------> cancel() -----> [CANCELLED] (no further scheduling allowed)
    */
class Timer{
    //@StateRefinement(this, to="cancelled")
    public void cancel() {  }

    // @StateRefinement(this, from="start", to="scheduled")
    public void schedule(TimerTask task, /* delay > 0 */int delay) {}

}


// /*
//  * Error cannot reschedule  a timer
//  */
//  public static void example1801324( Map<String, Timer> timers, String sessionKey) {

//     // Step 1) Get the timer
//     Timer timer = timers.get(sessionKey);

//     // Step 2) Cancel the timer
//     timer.cancel();

//     // Step 3) Schedule a new task for this timer -> ERROR Cannot reschedule a Timer
    // timer.schedule(new TimerTask() {
    //     @Override
    //     public void run() {
    //         System.out.println("Timer task completed.");
    //     }
    // }, 1000);
// }



