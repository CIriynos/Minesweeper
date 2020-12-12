package com.github.CIriynos.Minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/*
    @Author Tang_wenqi
    @Date 2020/12/11

    class Block is used to describe a single block in the Minesweeper Game
    it cannot control other blocks. And it doesn't know the presence of others.
 */

public class Block extends JComponent
{
    public Block(int x, int y, int orderX, int orderY, int size) throws IOException, FontFormatException  //x, y means pixel point(left-up)
    {
        this.orderX = orderX;
        this.orderY = orderY;
        setBounds(x, y, size, size);

        //load Texture and Fonts
        File file = new File("C:\\Windows\\Fonts\\comic.ttf");
        FileInputStream in = new FileInputStream(file);
        font = Font.createFont(Font.TRUETYPE_FONT, in);
        font = font.deriveFont(Font.BOLD, (float)getWidth() / 3.0f * 2.0f);
    }

    public void setMine(boolean foo) {
        mine = foo;
    }

    public void setNumber(int cnt) //cnt -> mines in 3*3
    {
        initialized = true;
        if(isMine()) return;
        number = cnt;
        repaint();
    }

    public boolean leftClick()
    {
        if(!initialized || flaged) return true; //nothing was done
        if(isMine()) return false; //false -> Game over
        if(uncovered) return true;
        uncovered = true;
        repaint();
        return true;
    }

    public int rightClick()
    {
        //means set flag or cancel flag
        if(uncovered) return 0; //cannot operate
        if(flaged) {
            flaged = false; repaint();
            return 1; //cancel
        }
        else{
            flaged = true; repaint();
            return 2;  //succeed
        }
    }

    public void setTarget()
    {
        //if uninitialized, this could be used as the same.
        if(uncovered) return;
        targeted = true;
    }

    public void cancelTarget()
    {
        if(targeted) targeted = false;
    }

    private void drawBase(Graphics2D g)
    {
        Rectangle2D rect = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
        g.setPaint(Color.DARK_GRAY);
        g.fill(rect);
    }

    private void drawTargetedBase(Graphics2D g)
    {
        //waiting for update...
        drawBase(g);
    }

    private void drawUncoveredBase(Graphics2D g)
    {
        Rectangle2D rect = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
        g.setPaint(Color.GRAY);
        g.fill(rect);
    }

    private void drawTargetedUncoveredBase(Graphics2D g)
    {
        //waiting for update...
        drawUncoveredBase(g);
    }

    private void drawNumber(Graphics2D g)
    {
        if(number == 0) return;
        FontRenderContext context = g.getFontRenderContext();
        Rectangle2D bounds = font.getStringBounds(Integer.toString(number), context);
        double x = (getWidth() - bounds.getWidth()) / 2;
        double y = (getHeight() - bounds.getHeight()) / 2 - bounds.getY();
        switch(number){
            case 1: g.setColor(Color.BLUE); break;
            case 2: g.setColor(Color.GREEN); break;
            case 3: g.setColor(Color.RED); break;
            case 4: g.setColor(Color.CYAN); break;
            case 5: g.setPaint(Color.PINK); break;
            default: g.setColor(Color.BLACK); break;
        }
        g.drawString(Integer.toString(number), (int)x, (int)y);
    }

    private void drawFlag(Graphics2D g)
    {
        if(!flaged) return;
        Ellipse2D circle = new Ellipse2D.Double(0, 0, (double)getWidth() / 2, (double)getHeight() / 2);
        g.setPaint(Color.white);
        g.fill(circle);
    }

    private void drawMine(Graphics2D g)
    {
        if(!mine) return;
        Ellipse2D circle = new Ellipse2D.Double(0, 0, (double)getWidth() / 2, (double)getHeight() / 2);
        g.setPaint(Color.RED);
        g.fill(circle);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        try {
            Graphics2D g2 = (Graphics2D) g;
            //draw base
            if(uncovered){
                if(targeted) drawTargetedUncoveredBase(g2);
                else drawUncoveredBase(g2);
            }
            else {
                if (targeted) drawTargetedBase(g2);
                else drawBase(g2);
            }
            //draw number, flag, mine, etc.
            if(uncovered) drawNumber(g2);
            else if(flaged) drawFlag(g2);
            if(mineDisplay) drawMine(g2);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean isMine(){return mine;}
    public boolean isFlaged(){return flaged;}
    public boolean isInitialized(){return initialized;}
    public boolean isUncovered(){return uncovered;}
    public Integer getNumber(){return number;}
    public void setMineDisplay(boolean p){mineDisplay = p; repaint();}
    public boolean getMineDisplay(){return mineDisplay;}

    public Integer orderX;
    public Integer orderY;
    private Integer number = 0;
    private boolean mine = false;
    private boolean flaged = false;
    private boolean uncovered = false;
    private boolean initialized = false;
    private boolean targeted = false;
    private boolean mineDisplay = false;
    private Font font;

    public static final int DEFAULT_SIZE = 20; //by pixel point

    //used for debug
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

            }
        });
    }
}