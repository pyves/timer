package ch.pyves;

import ch.pyves.timer.gui.CountDownTimerFrame;
import ch.pyves.timer.model.CountDownTimer;

public class CountDown {

    /**
     * @param args
     */
    public static void main (String[] args) {
        CountDownTimer timer = new CountDownTimer();
        if (args != null && args.length>0){
            if (args.length==2){
                timer.setWidth (Integer.parseInt (args[0]));
                timer.setHeight (Integer.parseInt (args[1]));
            }
            else if (args.length==4){
                timer.setxPos (Integer.parseInt (args[0]));
                timer.setyPos (Integer.parseInt (args[1]));
                timer.setWidth (Integer.parseInt (args[2]));
                timer.setHeight (Integer.parseInt (args[3]));
            }
        }
        CountDownTimerFrame frame = new CountDownTimerFrame(timer);
        frame.setVisible (true);
        timer.setFrame (frame);
        frame.refreshTime ();
    }

}
