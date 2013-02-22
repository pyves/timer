package ch.pyves;

import ch.pyves.timer.gui.CountDownTimerFrame;
import ch.pyves.timer.model.CountDownTimer;

public class CountDown {

    /**
     * @param args
     */
    public static void main (String[] args) {
        CountDownTimer timer = new CountDownTimer();
        CountDownTimerFrame frame = new CountDownTimerFrame(timer);
        frame.setVisible (true);
        timer.setFrame (frame);
        frame.refreshTime ();
    }

}
