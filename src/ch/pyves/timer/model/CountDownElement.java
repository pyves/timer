/**
 * 
 */
package ch.pyves.timer.model;

import java.io.Serializable;

/**
 * @author pyberger
 *
 */
public class CountDownElement implements Serializable, Comparable<CountDownElement> {
    
    public enum OverlapType {
        NO_COMPUTE,NO_OVERLAP, COUNTDOWN_LEAD, LATE_LEAD, COUNTDOWN_COUNTDOWN, LATE_COUNTDOWN, LATE_LATE;
    }
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    boolean withStart;
    long startTime;
    int leadDuration;
    int countDuration;
    int lateDuration; 
    boolean runLate;
    /**
     * 
     */
    public CountDownElement () {
        // TODO Auto-generated constructor stub
    }

    public CountDownElement (long startTime, int leadDuration,
            int countDuration, int lateDuration) {
        this.startTime = startTime;
        this.withStart = (this.startTime > 0);
        this.leadDuration = leadDuration;
        this.countDuration = countDuration;
        this.lateDuration = lateDuration;
        this.runLate = (this.lateDuration > 0);
    }

    public CountDownElement (int leadDuration, int countDuration) {
        this.startTime = 0;   
        this.withStart = false;
        this.leadDuration = leadDuration;
        this.countDuration = countDuration;
        this.lateDuration = 0;
        this.runLate = false;        
    }

    public CountDownElement (long startTime, int leadDuration,
            int countDuration, boolean runLate) {
        this.startTime = startTime;
        this.withStart = (this.startTime > 0);
        this.leadDuration = leadDuration;
        this.countDuration = countDuration;
        this.runLate = runLate;
        if (runLate){
            this.lateDuration=Integer.MAX_VALUE;
        }
        else {
            this.lateDuration=0;
        }
    }

    public int compareTo (CountDownElement cde) {
        if (withStart && cde.withStart){
            if (startTime==cde.startTime){
                return Integer.valueOf (countDuration).compareTo (Integer.valueOf (cde.countDuration));
            }
            else {
                return Long.valueOf (startTime).compareTo (Long.valueOf (cde.startTime));
            }
        }
        else if (withStart){
            return -1;
        }
        else if (cde.withStart){
            return 1;
        }
        else {
            return Integer.valueOf (countDuration).compareTo (Integer.valueOf (cde.countDuration));
        }
    }

    public boolean isWithStart () {
        return withStart;
    }

    public long getStartTime () {
        return startTime;
    }

    public int getLeadDuration () {
        return leadDuration;
    }

    public int getCountDuration () {
        return countDuration;
    }

    public int getLateDuration () {
        return lateDuration;
    }

    public boolean isRunLate () {
        return runLate;
    }

    public void setStartTime (long startTime) {
        this.startTime = startTime;
    }

    public void setLeadDuration (int leadDuration) {
        this.leadDuration = leadDuration;
    }

    public void setCountDuration (int countDuration) {
        this.countDuration = countDuration;
    }

    public void setLateDuration (int lateDuration) {
        this.lateDuration = lateDuration;
    }

    public OverlapType computeOverlap (CountDownElement cde){
        long now = System.currentTimeMillis ();
        long endCountTime1 = ((withStart)?startTime:now)+countDuration * 1000;
        long endCountTime2 = ((cde.withStart)?cde.startTime:now)+cde.countDuration * 1000;
        long endFullTime1 = endCountTime1+lateDuration*1000;
        long endFullTime2 = endCountTime2+cde.lateDuration*1000;
        long startLeadTime1 = ((withStart)?startTime:now)-leadDuration * 1000;
        long startLeadTime2 = ((cde.withStart)?cde.startTime:now)-cde.leadDuration * 1000;
        if (endFullTime1 < startLeadTime2 || endFullTime2 < startLeadTime1){
            return OverlapType.NO_OVERLAP;
        }
        return OverlapType.NO_COMPUTE;
    }
}
