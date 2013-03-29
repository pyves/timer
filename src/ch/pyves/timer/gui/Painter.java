package ch.pyves.timer.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;

import javax.swing.JPanel;

class Painter extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int fontSize = 600;
    private boolean computeSize = true;
    private boolean computeSizeOnce=false;
    
    public void setComputeSizeOnce (boolean computeSizeOnce) {
        this.computeSizeOnce = computeSizeOnce;
    }

    public void setComputeSize (boolean computeSize) {
        this.computeSize = computeSize;
    }

    Color textColor;
    Color backgroundColor;
    public Color getBackgroundColor () {
        return backgroundColor;
    }

    public void setBackgroundColor (Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    String text;

    public Painter(Color c, Color back, String s) {
        this.text = s;
        this.textColor = c;
        this.backgroundColor = back;
    }
   
    public Painter(Color c, String s) {
      this.text = s;
      this.textColor = c;
    }
    protected void setColor(Color c) {
      this.textColor = c;
    }
    protected Color getColor() {
      return this.textColor;
    }
    protected void setText(String s) {
       this.text = s;
    }
    protected String getText() {
       return this.text;
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    public void paintComponent(Graphics g) {
      Graphics2D g2;
      g2 = (Graphics2D) g;

      g2.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
                  RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(
      RenderingHints.KEY_RENDERING,
                  RenderingHints.VALUE_RENDER_QUALITY);
      //Make background 
      g2.setColor(backgroundColor);
      g2.fillRect(0, 0, getSize(
      ).width -1, getSize().height -1);

      //Set font rendering context and font 
      FontRenderContext frc =
       g2.getFontRenderContext();
      Font f = new Font(Font.SANS_SERIF, Font.BOLD, fontSize);

      //Create styled text from font and string
      TextLayout tl = new TextLayout(text, f, frc);
      if (computeSize||computeSizeOnce){
          int df = 0;
          if (fontSize>10){
              if (tl.getAdvance ()>getWidth ()||(tl.getDescent()+tl.getAscent ())>getHeight ()){
                  df=-1;
              }
              else if (tl.getAdvance ()<getWidth ()-4&&(tl.getDescent()+tl.getAscent ())<getHeight ()-4){
                  df=1;
              }
          }
          //System.out.println("fontSize="+fontSize+" df="+df);
          while (df != 0 && fontSize>10){
              fontSize+=df;
              f = new Font(Font.SANS_SERIF, Font.BOLD, fontSize);
              tl = new TextLayout(text, f, frc);
              if (df==-1&&!(tl.getAdvance ()>getWidth ()||(tl.getDescent()+tl.getAscent ())>getHeight ())){
                  df=0;
              }
              else if (df==1 && !(tl.getAdvance ()<getWidth ()-4&&(tl.getDescent()+tl.getAscent ())<getHeight ()-4)){
                  df=0;
              }
              //System.out.println("--> fontSize="+fontSize+" df="+df);
          }
          if (computeSizeOnce){
              computeSizeOnce=false;
          }
      }
//      float fontHeight = tl.getDescent()+tl.getAscent ();
      float verticalPosition = getHeight ()/2+tl.getAscent()/2;
//      while (tl.getAdvance ()>getWidth () && fontSize>100){
//          fontSize--;
//          f = new Font(Font.SANS_SERIF, Font.BOLD, fontSize);
//          tl = new TextLayout(text, f, frc);
//          System.err.println("fontSize="+fontSize+" advance="+tl.getAdvance ()+" width="+getWidth ());
//      }
//      System.err.println(tl.getAdvance ());
      //Get the size of the drawing area 
//      Dimension theSize = getSize();
//      System.err.println(this.getWidth ());
      //Set the 2D graphics context color for drawing the text
      g2.setColor(textColor);

      //Draw the text into the drawing area
      tl.draw(g2, 5, verticalPosition);

//      Rectangle2D bounds = tl.getBounds();
//      bounds.setRect(bounds.getX()+5,
//                     bounds.getY()+verticalPosition,
//                     bounds.getWidth(),
//                     bounds.getHeight());
//      g2.draw(bounds);
      //Put a blue box around the styled text
      //unless the text string is "Ready"
//      if(this.text != "Ready") {
//        g2.drawRect(0, 0, getSize(
//        ).width -1, getSize().height -1);
//      }
    }

    public boolean getComputeSize () {
        
        return computeSize;
    }
  }