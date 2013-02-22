package ch.pyves.timer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import ch.pyves.timer.model.CountDownTimer;

public class CountDownTimerFrame extends JFrame {

    private CountDownTimer timer = null;
    private Container contentPane;
    private JPanel menuPanel;
    private JPanel infoPanel;
    private JMenuBar menuBar;
    private Painter currentCenter;
    private JTextField startTime;
    private JTextField hours;
    private JTextField minutes;
    private JTextField seconds;
    private JTextField leadSeconds;
//    private JButton setStartTime;
    private JButton cns1815;
    private JButton cns2030;
    private JButton autoStart;
    private JButton setTime;
    private JButton start;
    private JButton stop;
    private JButton lateRun;
    private JLabel currentTime;
    public static final boolean _CNS=false;
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public CountDownTimerFrame (CountDownTimer countDown) throws HeadlessException {
        timer = countDown;
        
        setLocation(0,0);
        setSize (3200,3200);
        setMinimumSize (new Dimension (100, 200));
        setTitle ("CountDown Timer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        contentPane = getContentPane();       
        menuBar = new JMenuBar();
        setJMenuBar (menuBar);
        menuPanel = new JPanel ();
        if (_CNS){
            autoStart =new JButton("Auto Start");
            menuPanel.add (autoStart);
            autoStart.addActionListener (new ActionListener(){
                public void actionPerformed (ActionEvent e) {
                    currentCenter.setComputeSize (true);
                    startTime.setText ("auto");
                    hours.setText ("1");
                    minutes.setText ("50");
                    seconds.setText ("00");
                    leadSeconds.setText ("60");
                    if (!timer.isRunLate ()){
                        timer.setRunLate ();
                    }
                    setTime();
                }            
            });
            autoStart.setEnabled (timer.hasStamps ());
        }
        if(_CNS){
            cns1815 =new JButton("Start 18:15");
            menuPanel.add (cns1815);
            cns1815.addActionListener (new ActionListener(){

                public void actionPerformed (ActionEvent e) {
                    currentCenter.setComputeSize (true);
                    startTime.setText ("18:15:00");
                    hours.setText ("1");
                    minutes.setText ("50");
                    seconds.setText ("00");
                    leadSeconds.setText ("60");
                    if (!timer.isRunLate ()){
                        timer.setRunLate ();
                    }
                    setTime();
                }
                
            });
            cns2030 =new JButton("Start 20:30");
            menuPanel.add (cns2030);
            cns2030.addActionListener (new ActionListener(){

                public void actionPerformed (ActionEvent e) {
                    currentCenter.setComputeSize (true);
                    startTime.setText ("20:30:00");
                    hours.setText ("1");
                    minutes.setText ("50");
                    seconds.setText ("00");
                    leadSeconds.setText ("60");
                    if (!timer.isRunLate ()){
                        timer.setRunLate ();
                    }
                    setTime();
                }
                
            });
        }
        menuPanel.add (new JLabel("Start time"));
        startTime = new JTextField(timer.getStartTime (),8);
        menuPanel.add (startTime);
        menuPanel.add (new JLabel("Hours"));
        hours = new JTextField(Integer.toString (timer.getHours ()),2);
        menuPanel.add (hours);
        menuPanel.add (new JLabel("Minutes"));
        minutes = new JTextField(Integer.toString (timer.getMinutes ()),2);
        menuPanel.add (minutes);
        menuPanel.add (new JLabel("Seconds"));
        seconds = new JTextField(Integer.toString (timer.getSeconds ()),2);
        menuPanel.add (seconds);
        menuPanel.add (new JLabel("Lead seconds"));
        leadSeconds = new JTextField(Integer.toString (timer.getLeadSeconds ()),3);
        menuPanel.add (leadSeconds);
        setTime = new JButton("Set time");
        menuPanel.add (setTime);
        currentTime = new JLabel();
        currentTime.setText (timer.toString ());
        menuPanel.add (currentTime);
        setTime.addActionListener (new ActionListener(){
            public void actionPerformed (ActionEvent e) {
                setTime();
            }           
        });
        setTime.setEnabled (true);
        start = new JButton("Start");
        menuPanel.add (start);
        start.addActionListener (
                new ActionListener(){
                    public void actionPerformed (ActionEvent e) {
                        if (_CNS){
                        autoStart.setEnabled (false);
                        cns1815.setEnabled (false);
                        cns2030.setEnabled (false);
                        }
                        setTime.setEnabled (false);
                        start.setEnabled (false);
                        stop.setEnabled (true);
                        lateRun.setEnabled (false);
                        lateRun.setText ((!timer.isRunLate ())?"Stopping at zero":"Running late");
                        timer.startTimer ();
                        currentCenter.setComputeSize (false);
                        refreshTime ();
                    }
                });
        start.setEnabled (timer.hasRemainingTime ());
        stop = new JButton("Stop");
        menuPanel.add (stop);
        stop.addActionListener (
                new ActionListener(){
                    public void actionPerformed (ActionEvent e) {
                        if (_CNS){
                        autoStart.setEnabled (timer.hasStamps ());
                        cns1815.setEnabled (true);
                        cns2030.setEnabled (true);
                        }
                        setTime.setEnabled (true);
                        start.setEnabled (timer.hasRemainingTime ());
                        stop.setEnabled (false);
                        if(timer.isRunning ()||timer.isLeadRunning ()){
                            lateRun.setEnabled (true);
                            lateRun.setText ((timer.isRunLate ())?"Stop at zero":"Run late");
                        }
                        timer.stopTimer ();
                        currentTime.setText (timer.toString ());
                        currentCenter.setText (timer.toString ());
                        currentCenter.setComputeSize (true);
                        if (timer.hasRemainingTime () &&!timer.isLateRunning ()){
                            currentCenter.setBackgroundColor (Color.blue);
                            currentCenter.setColor (Color.white);
                        }
                        else {
                            currentCenter.setBackgroundColor (Color.red);
                            currentCenter.setColor (Color.white);
                        }
                        currentCenter.repaint ();
                    }
                });
        stop.setEnabled (false);
        lateRun = new JButton((timer.isRunLate ())?"Stop at zero":"Run late");
        menuPanel.add (lateRun);
        lateRun.addActionListener (
                new ActionListener(){
                    public void actionPerformed (ActionEvent e) {
                        timer.setRunLate ();
                        lateRun.setText ((timer.isRunLate ())?"Stop at zero":"Run late");
                     }
                });
        lateRun.setEnabled (true);
        contentPane.add(menuPanel, BorderLayout.NORTH);

        infoPanel = new JPanel ();
        contentPane.add(infoPanel, BorderLayout.SOUTH);
        
        currentCenter = new Painter(Color.white,(timer.hasRemainingTime ())?Color.blue:Color.red,timer.toString ());       
        contentPane.add(currentCenter, BorderLayout.CENTER);        
        
    }

    protected void setTime () {
        currentCenter.setComputeSize (true);
        try {
            timer.setHours (Integer.parseInt (hours.getText ()));
        }
        catch(NumberFormatException ex){
            timer.setHours (0);
        }
        try {
            timer.setMinutes (Integer.parseInt (minutes.getText ()));
        }
        catch(NumberFormatException ex){
            timer.setMinutes (0);
        }
        try {
            timer.setSeconds (Integer.parseInt (seconds.getText ()));
        }
        catch(NumberFormatException ex){
            timer.setSeconds (0);
        }
        try {
            timer.setLeadSeconds (Integer.parseInt (leadSeconds.getText ()));
        }
        catch(NumberFormatException ex){
            timer.setLeadSeconds (0);
        }
        timer.resetLateRunning ();
        timer.setStartTime (startTime.getText ());
        startTime.setText (timer.getStartTime ());
        currentCenter.setComputeSizeOnce (true);
        currentCenter.setText (timer.toString ());
        if (timer.hasStartTime ()){
            if (_CNS){
            autoStart.setEnabled (false);
            cns1815.setEnabled (false);
            cns2030.setEnabled (false);
            }
            setTime.setEnabled (false);
            start.setEnabled (false);
            stop.setEnabled (true);
            lateRun.setEnabled (false);
            lateRun.setText ((!timer.isRunLate ())?"Stopping at zero":"Running late");
            timer.startTimer ();
            currentCenter.setComputeSize (false);
            refreshTime ();                    
        }
        else {
            if (_CNS){
            autoStart.setEnabled (timer.hasStamps ());
            }
            lateRun.setEnabled (true);
            lateRun.setText ((timer.isRunLate ())?"Stop at zero":"Run late");
            start.setEnabled (timer.hasRemainingTime ());
            stop.setEnabled (false);
            refreshTime();
        }
       
    }

    public void refreshTime () {
        currentCenter.setText (timer.toString ());
        if (timer.isRunning ()&&!timer.isShowClock ()){
            currentCenter.setBackgroundColor (Color.green);
            currentCenter.setColor (Color.black);
            currentTime.setText (timer.toString ());
        }
        else {
            Color back = Color.pink;
            Color chars = Color.black;
            boolean computeSize = false;
            if (timer.isShowClock ()){
                back = Color.white;
                chars = Color.black;
                computeSize = true;
            }
            else if (timer.isLateRunning ()){
                back = Color.red;
                chars = Color.white;
                computeSize = true;
            }
            else if (timer.isLeadRunning ()){
                back = Color.blue;
                chars = Color.white;
                computeSize = true;
            } 
            else if (!timer.isRunning ()){
                back = Color.blue;
                chars = Color.lightGray;
                computeSize = true;               
            }
            currentCenter.setComputeSize (computeSize);
            currentCenter.setBackgroundColor (back);
            currentCenter.setColor (chars);
        }
        currentCenter.repaint ();
//        System.err.println(timer.toString ());
    }
    public void refreshDuration () {
        hours.setText (Integer.toString (timer.getHours ()));
        minutes.setText (Integer.toString (timer.getMinutes ()));
        seconds.setText (Integer.toString (timer.getSeconds ()));
    }
    public void endCount () {
        if (_CNS){
        autoStart.setEnabled (timer.hasStamps ());
        }
        currentCenter.setText (timer.toString ());
        currentCenter.setBackgroundColor (Color.red);
        currentCenter.setColor (Color.white);        
        currentCenter.repaint ();
        stop.setEnabled (false);
        if (_CNS){
        cns1815.setEnabled (true);
        cns2030.setEnabled (true);
        }
        setTime.setEnabled (true);
        currentTime.setText (timer.toString ());
//        System.err.println(timer.toString ());       
    }
    
    public void setCounterHasStarted(){
        if (_CNS){
        autoStart.setEnabled (false);
        cns1815.setEnabled (false);
        cns2030.setEnabled (false);
        }
        setTime.setEnabled (false);
        start.setEnabled (false);
        stop.setEnabled (true);
        lateRun.setEnabled (false);
        lateRun.setText ((!timer.isRunLate ())?"Stopping at zero":"Running late");
        timer.startTimer ();
        currentCenter.setComputeSizeOnce (true);
        refreshTime ();        
    }

    public void refreshStartTime(){
        startTime.setText (timer.getStartTime ());
    }
}
