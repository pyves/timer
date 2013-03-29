package ch.pyves.timer.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import ch.pyves.timer.gui.CountDownTimerFrame;

public class CountDownTimer implements ActionListener {
    private Preferences countDownPrefs = Preferences.userNodeForPackage (CountDownTimer.class);
    private int hours = countDownPrefs.getInt ("hours", 1);
    private int minutes = countDownPrefs.getInt ("minutes",50);
    private int seconds = countDownPrefs.getInt ("seconds",0);
    private int tenth = 9;
    private long counterStartTime = 0;
    private long millisElapsed = 0;
    private long startTime = 0;
    private boolean showClock = false;
    private CountDownTimerFrame frame = null;
    private Timer timer;
    private int leadSeconds = countDownPrefs.getInt ("leadSeconds",60);
    private boolean leadRunning = false;
    private boolean runLate =false;
    private boolean isLateRunning =false;
    private LinkedHashMap<Long, Integer> stamps = null;
    private boolean autoStart = false;
    
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("kk:mm:ss");
    
    
    public int getLeadSeconds () {
        return leadSeconds;
    }

    public void setLeadSeconds (int leadSeconds) {
        this.leadSeconds = leadSeconds;
        countDownPrefs.putInt ("leadSeconds", leadSeconds);
    }

    public int getHours () {
        return hours;
    }

    public int getMinutes () {
        return minutes;
    }

    public int getSeconds () {
        return seconds;
    }

    public CountDownTimerFrame getFrame () {
        return frame;
    }

    public void setHours (int hours) {
        this.hours = hours;
        countDownPrefs.putInt ("hours", hours);
    }

    public void setMinutes (int minutes) {
        int aMinutes = minutes;
        while (aMinutes > 59){
            hours+=aMinutes/60;
            countDownPrefs.putInt ("hours", hours);
            aMinutes-=(aMinutes/60)*60;
        }
        this.minutes = aMinutes;
        countDownPrefs.putInt ("minutes", aMinutes);
    }
    

    public void setSeconds (int seconds) {
        int aSeconds = seconds;
        if (aSeconds > 3599){
            this.hours+=aSeconds/3600;
            countDownPrefs.putInt ("hours", hours);
            aSeconds -= (aSeconds/3600)*3600;
        }
        if (aSeconds > 59){
            this.minutes+=aSeconds/60;
            countDownPrefs.putInt ("minutes", minutes);
            if (this.minutes>59){
                this.hours+=(this.minutes/60);
                countDownPrefs.putInt ("hours", hours);
                this.minutes-=(this.minutes/60)*60;
                countDownPrefs.putInt ("minutes", minutes);
            }
            aSeconds-=(aSeconds/60)*60;
        }
        this.seconds = aSeconds;
        countDownPrefs.putInt ("seconds", aSeconds);
    }

    public void setStartTime(int startHour, int startMinute, int startSecond){
        Calendar cal = Calendar.getInstance ();
        int year = cal.get (Calendar.YEAR);
        int month = cal.get (Calendar.MONTH);
        int day = cal.get (Calendar.DAY_OF_MONTH);
        cal.clear ();
        cal.set (year, month, day, startHour, startMinute, startSecond);
        setStartTime(cal.getTimeInMillis ());
    }
    public void setStartTime(long timestamp){
        startTime = timestamp;
        startTime -= leadSeconds * 1000; 
        showClock = true;
        counterStartTime = 0;
        millisElapsed = 0;
        isLateRunning=false;
        int delta = (int) (startTime - System.currentTimeMillis ());
        if (delta<0){
            if (-delta<=leadSeconds*1000){
                leadSeconds-=(-delta/1000);
                if (leadSeconds>0){
                    leadRunning=true;
                }
                else {
                    counterStartTime=System.currentTimeMillis ();
                    millisElapsed = 0;                    
                }
            }
            else {
                delta-=leadSeconds*1000;
                startTime+=leadSeconds*1000;
                leadSeconds=0;
                int runningTime = (hours*3600+minutes*60+seconds)*1000;
                if (-delta<runningTime){
                    int deltaS = (-delta/1000);
                    int deltaM = 0;
                    int deltaH = 0;
                    if (deltaS>60){
                        deltaM = deltaS/60;
                        deltaS = deltaS%60;
                    }
                    if (deltaM>60){
                        deltaH=deltaM/60;
                        deltaM=deltaM%60;
                    }
                    if (deltaS>0){
                        if (seconds>=deltaS){
                            seconds=seconds-deltaS;
                        }
                        else {
                            deltaM++;
                            seconds=seconds+60-deltaS;
                        }
                    }
                    if (deltaM>0){
                        if (minutes>=deltaM){
                            minutes=minutes-deltaM;
                        }
                        else {
                            deltaH++;
                            minutes=minutes+60-deltaM;
                        }
                    }
                    if (deltaH>0){
                        hours=hours-deltaH;
                    }
                }
                else {
                    if (runLate){
                        isLateRunning=true;
                        int deltaS = (-delta/1000);
                        int deltaM = 0;
                        int deltaH = 0;
                        if (deltaS>60){
                            deltaM = deltaS/60;
                            deltaS = deltaS%60;
                        }
                        if (deltaM>60){
                            deltaH=deltaM/60;
                            deltaM=deltaM%60;
                        }
                        seconds=deltaS;
                        minutes=deltaM;
                        hours=deltaH;
                    }
                    else {
                        hours=0;
                        minutes=0;
                        seconds=0;
                        frame.refreshTime();
                        frame.endCount();
                        return;
                    }
                }
                counterStartTime=System.currentTimeMillis ();
                millisElapsed = 0;                    
            }
            showClock=false;
            frame.setCounterHasStarted ();
        }
        else {
            startTimer();
        }
    }

    public void setStartTime(String sTime){
        if (sTime.equals ("hh:mm:ss")||sTime.trim ().equals ("")){
            startTime=0;
            return;
        }
        else if (sTime.equals ("auto")){
            setStartNextStamp ();
            return;
        }
        int startHour = 0;
        int startMinute = 0;
        int startSecond = 0;
        Pattern p = Pattern.compile ("(\\d+)[:\\./\\-](\\d+)[:\\./\\-](\\d+)");
        Matcher m = p.matcher (sTime.trim());
        if (m.matches ()){
            startHour = Integer.parseInt (m.group (1));
            startMinute = Integer.parseInt (m.group (2));
            startSecond = Integer.parseInt (m.group (3));
        }
        else {
            p = Pattern.compile ("(\\d+)[:\\./\\-](\\d+)");
            m = p.matcher (sTime.trim());
            if (m.matches ()){           
                startHour = Integer.parseInt (m.group (1));
                startMinute = Integer.parseInt (m.group (2));  
            }
            else {
                return;
            }
        }
        setStartTime (startHour, startMinute, startSecond);
    }
public boolean isAutoStart(){
    return autoStart;
}
    public void setFrame (CountDownTimerFrame frame) {
        this.frame = frame;
    }
    /** Default constructor */
    public CountDownTimer () {
        stamps = new LinkedHashMap<Long,Integer> ();
        long now = System.currentTimeMillis ();
        if (CountDownTimerFrame._CNS){
        try {
            File f = new File(System.getProperty ("user.dir"),"ts.txt");
            BufferedReader in = new BufferedReader (new FileReader (f));
            String line;
            while ((line=in.readLine ())!=null){
                line = line.trim ();
                if (!line.isEmpty ()&&!line.startsWith ("#")){
                    String[] nums = line.split ("\t");
                    long l = 0;
                    int i = 6600;
                    if (nums.length>0){
                        l = (Long.parseLong (nums[0])*1000);
                    }
                    if (nums.length>1){
                        i = (Integer.parseInt (nums[1]));
                    }
                    if (l<now){
                        stamps.clear ();
                    }
                    stamps.put (Long.valueOf (l),Integer.valueOf (i));
                }
            }
            in.close ();
        }
        catch (Exception e){
            stamps=null;
            JOptionPane.showMessageDialog (frame, "Erreur "+e.getMessage ());
        }
        }
//        stamps=new Vector<Long> ();
//        stamps.add (Long.valueOf (1295897400000L));
//        stamps.add (Long.valueOf (now+120000));       
        timer = new Timer(100,this);
    }
    
    public boolean hasStamps(){
        return stamps!=null&&!stamps.isEmpty ();
    }
    
    public void setStartNextStamp(){
        autoStart=true;
         hours = countDownPrefs.getInt ("hours", 1);
         minutes = countDownPrefs.getInt ("minutes",50);
         seconds = countDownPrefs.getInt ("seconds",0);
         leadSeconds = countDownPrefs.getInt ("leadSeconds",60);
         long nextStamp = -1;
         Long lNextStamp = Long.valueOf (nextStamp);
         for (Long next : stamps.keySet ()){
             if (next.longValue ()<counterStartTime){
                 continue;
             }
             lNextStamp = next;
             nextStamp = next.longValue ();
             break;
         }
        if (nextStamp==-1){
            stopTimer();
            frame.endCount ();
        }
        else {
            hours = 0;
            minutes = 0;
            setSeconds (stamps.get (lNextStamp).intValue ());
            setStartTime(nextStamp);
            frame.refreshStartTime ();
            frame.refreshDuration ();
        }
    }
    
    public boolean hasRemainingTime () {
        return hours > 0 || minutes > 0 || seconds > 0;
    }

    public void startTimer(){
        if (showClock&&startTime>System.currentTimeMillis ()){
            //timer.setDelay (100);
        }
        else if (leadSeconds>0){
            leadRunning=true;
        }
        else {
            counterStartTime=System.currentTimeMillis ();
            millisElapsed = 0;
        }
        timer.start ();
    }
    public boolean isLeadRunning () {
        return leadRunning;
    }

    public void stopTimer(){
        if (autoStart){
 //           stamps.add (0, Long.valueOf (startTime));
            autoStart=false;
        }
        showClock=false;
        timer.stop ();
    }
    public boolean isRunning() {
        return !leadRunning && timer.isRunning ()&& !isLateRunning;
    }
    public boolean isLateRunning () {
        return isLateRunning;
    }
    public void resetLateRunning(){
        isLateRunning=false;
    }

    public boolean isRunLate () {
        return runLate;
    }

    public void setRunLate () {
        runLate = !runLate;
    }
    public boolean isShowClock () {
        return showClock;
    }
    public String getStartTime(){
        return (startTime>0)?timeFormat.format (new Date(startTime+leadSeconds*1000)):"hh:mm:ss";
    }
    public boolean hasStartTime(){
        return startTime>0;
    }
    public void actionPerformed (ActionEvent e) {
        if (showClock){
            long now = System.currentTimeMillis ();
            if (now < startTime){
                frame.refreshTime ();
                return;
            }
            else {
                showClock = false;
//                if (leadSeconds>0){
                    leadRunning=true;
//                }
                //timer.setDelay (100);
                frame.setCounterHasStarted ();
                counterStartTime = now;
                millisElapsed = 0;
                frame.refreshTime ();
                if (leadSeconds>0){
                    return;
                }
            }
        }
        if (tenth>0){
            tenth--;
            return;
        }
        else {
            millisElapsed += 1000;
            if (counterStartTime>0){
                long now = System.currentTimeMillis ();
                long compute = counterStartTime+millisElapsed;
                long drift = compute-now;
//                System.out.println("["+now+" || "+compute+"] ("+toString ()+") Drift="+drift);
                if (drift>100){
                    //running early --> + 1 tenth for next run
//                    System.out.println("Running early: drift="+drift);
                    tenth=10;
                }
                else if (drift<-100){
                    //running late --> - 1 tenth for next run
//                    System.out.println("Running late: drift="+drift);
                    tenth=8;
                }
                else {
                    tenth=9;
                }
            }
            else {
                tenth=9;
            }
        }
        if (leadSeconds>0){
            leadSeconds--;
            frame.refreshTime();
            return;
        }
        else if (leadRunning){
            frame.refreshTime ();
            leadRunning=false;
            counterStartTime=System.currentTimeMillis ();
            millisElapsed = 0;
        }
        if (!hasRemainingTime ()&& !isLateRunning){
            if (runLate){
                isLateRunning = true;
            }
            else {
                stopTimer ();
                frame.refreshTime();
                frame.endCount();
                return;
            }
        }
        if (isLateRunning){
            if (seconds < 59){
                seconds++;
            }
            else {
                seconds = 0;
                if (minutes < 59){
                    minutes++;
                }
                else {
                    minutes = 0;
                    hours++;
                }
            }
            if (autoStart&&(minutes>10||hours>0)){
                setStartNextStamp ();
            }
        }
        else {
            if (seconds > 0){
                seconds--;
            }
            else {
                seconds = 59;
                if (minutes > 0){
                    minutes--;
                }
                else {
                    minutes = 59;
                    hours--;
                }
            }
        }
        if (!hasRemainingTime ()&& !isLateRunning){
            if (runLate){
                isLateRunning = true;
            }
            else {
                stopTimer ();
                frame.refreshTime();
                frame.endCount();
                return;
            }
        }
        frame.refreshTime();
    }
    
    @Override
    public String toString() {
        return (showClock)?timeFormat.format (new Date()):hours+":"+((minutes<10)?"0":"")+minutes+":"+((seconds<10)?"0":"")+seconds+((leadSeconds>0)?"+"+((leadSeconds<100)?"0":"")+((leadSeconds<10)?"0":"")+leadSeconds:"");
    }
}
