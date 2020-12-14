package com.github.CIriynos.Minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

/*
    @Author Tang_wenqi
    @Date 2020/12/11

    class Block is used to describe a single block in the Minesweeper Game
    it cannot control other blocks. And it doesn't know the presence of others.
 */

public class Block extends JComponent
{
    public Block(int x, int y, int orderX, int orderY, int size, GameSetting gameSetting)
            throws IOException, FontFormatException  //x, y means pixel point(left-up)
    {
        this.orderX = orderX;
        this.orderY = orderY;
        this.gameSetting = gameSetting;
        setBounds(x, y, size, size);
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

    public void setTarget(boolean b)
    {
        if(b) targeted = true;
        else  targeted = false;
        repaint();
    }

    public void cancelTarget()
    {
        if(targeted) targeted = false;
    }

    private void drawBase(Graphics2D g)
    {
        Rectangle2D rect = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
        g.setPaint(Color.cyan.darker());
        g.fill(rect);
    }

    private void drawTargetedBase(Graphics2D g)
    {
        Rectangle2D rect = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
        g.setPaint(Color.cyan.darker().brighter());
        g.fill(rect);
    }

    private void drawUncoveredBase(Graphics2D g)
    {
        Rectangle2D rect = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
        g.setPaint(Color.GRAY);
        g.setPaint(Color.LIGHT_GRAY);
        g.fill(rect);
    }

    private void drawTargetedUncoveredBase(Graphics2D g)
    {
        Rectangle2D rect = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
        g.setPaint(Color.LIGHT_GRAY.brighter());
        g.fill(rect);
    }

    private void drawNumber(Graphics2D g)
    {
        if(number == 0) return;
        g.drawImage(gameSetting.getNumberImage(number), 0, 0, getWidth(), getHeight(), null);
    }

    private void drawFlag(Graphics2D g)
    {
        if(!flaged) return;
        g.drawImage(gameSetting.getFlagImage(), 0, 0, getWidth(), getHeight(), null);
    }

    private void drawMine(Graphics2D g)
    {
        if(!mine) return;
        g.drawImage(gameSetting.getMineImage(), 0, 0, getWidth(), getHeight(), null);
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
    private GameSetting gameSetting;

    public static final int DEFAULT_SIZE = 20; //by pixel point
}