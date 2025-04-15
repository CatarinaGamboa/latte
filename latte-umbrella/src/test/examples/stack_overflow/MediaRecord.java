package examples;
import java.util.TimerTask;

import specification.Borrowed;
import specification.Free;
import specification.Unique;

public class MediaRecord {

    public static void test( boolean isChecked) {
        MediaRecorder recorder = new MediaRecorder();

        recorder.setAudioSource();
        recorder.setOutputFormat();
        recorder.setAudioEncoder();
        recorder.setOutputFile();

        while (isChecked) { // While loop will make it start and stop multiple times
            recorder.start(); // here it were it throws
            //...
            recorder.stop();
        }

    }


    public static void test2( boolean isChecked) {
        MediaRecorder recorder = new MediaRecorder();

        recorder.setAudioSource();
        recorder.setVideoSource();
        recorder.setOutputFormat();
        recorder.setProfile(); // setProfile error
        // From stackoverflow - setProfile() tries to setOutputFormat but cannot because it is already explicitly set - which makes it redundant
        // From documentation - setProfile() should be done after setAudioSource and setVideoSource and after should be setOutputFile - however, it does not appear in the SM    
    }

}
// From website - https://developer.android.com/reference/android/media/MediaRecorder
//@StateSet(initial, initialized, dataSourceConfigured, prepared, recording, released, error)
 class MediaRecorder {

    public static final String AudioSource = null;

    // @StateRefinement(from="prepared", to="recording")
    public void start() {}

    // @StateRefinement(from="recording", to="initial")
    public void stop() {}

    // @StateRefinement(from="initial", to="initialized")
    public void setAudioSource() {}

    // @StateRefinement(from="initialized", to="dataSourceConfigured")
    public void setOutputFormat() {}

    // @StateRefinement(from="dataSourceConfigured")
    public void setAudioEncoder() {}

    // @StateRefinement(from="dataSourceConfigured")
    public void setOutputFile() {}

    // @StateRefinement(from="dataSourceConfigured")
    public void setVideoSource() {}

    // Not in the SM but seems to be this
    // @StateRefinement(from="initialized", to="dataSourceConfigured")
    public void setProfile() {}

}
