package com.github.CIriynos.Minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Block extends JComponent
{
    public Block(int x, int y, int size) throws IOException, FontFormatException  //x, y means pixel point(left-up)
    {
        orderX = x;
        orderY = y;
        setBounds(x, y, size, size);

        //load Texture and Fonts
        File file = new File("C:\\Windows\\Fonts\\comic.ttf");
        FileInputStream in = new FileInputStream(file);
        font = Font.createFont(Font.TRUETYPE_FONT, in);
        font = font.deriveFont(Font.BOLD, (float)getWidth() / 3.0f * 2.0f);
    }
    public Block(int x, int y) throws IOException, FontFormatException {
        this(x, y, DEFAULT_SIZE);
    }

    public void setMine(boolean foo) {
        if (initOrder != 0) return;
        mine = foo;
    }

    public void setNumber(int cnt) //cnt -> mines in 3*3
    {
        if(isMine()) return;
        number = cnt;
        initialized = true;
        repaint();
    }

    public boolean leftClick()
    {
        if(!initialized || flaged) return true; //nothing was done
        if(isMine()) return false; //false -> Game over
        if(uncovered) return true;
        uncovered = true;
        return true;
    }

    public void rightClick()
    {
        //means set flag or cancel flag
        //Note: this method could be used without initialization
        if(uncovered) return; //cannot operate
        if(isFlaged()) flaged = false;
        else flaged = true;
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

    public void setGameState(int state)
    {
        /*  gameState = 0 ==> haven't start
            gameState = 1 ==> in game
            gameState = 2 ==> failure
            gameState = 3 ==> success
            gameState = 4 ==> (reserved)
        */
        gameState = state;
    }

    private void drawBase(Graphics2D g)
    {
        Rectangle2D rect = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
        g.setPaint(Color.GRAY);
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
        g.setPaint(Color.DARK_GRAY);
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
        Ellipse2D circle = new Ellipse2D.Double(0, 0, getWidth() / 2, getHeight() / 2);
        g.setPaint(Color.white);
        g.fill(circle);
    }

    private void drawMine(Graphics2D g)
    {
        if(!mine) return;
        Ellipse2D circle = new Ellipse2D.Double(0, 0, getWidth() / 2, getHeight() / 2);
        g.setPaint(Color.RED);
        g.fill(circle);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        try {
            Graphics2D g2 = (Graphics2D) g;
            if(gameState == 0){
                if(targeted) drawTargetedBase(g2);
                else drawBase(g2);
            } else {
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
                if(gameState == 3) drawMine(g2);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean isMine(){return mine;}
    public boolean isFlaged(){return flaged;}
    public boolean isInitialized(){return initialized;}
    public Integer getNumber(){return number;}

    private Integer orderX;
    private Integer orderY;
    private Integer number = 0;
    private boolean mine = false;
    private boolean flaged = false;
    private boolean uncovered = false;
    private boolean initialized = false;
    private boolean targeted = false;
    private int initOrder = 0;
    private int gameState = 0;
    private Font font;

    private static final int DEFAULT_SIZE = 20; //by pixel point

    //used for debug
    public static void main(String args[])
    {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try{
                    JFrame frame = new JFrame("Test for Block");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setLayout(null);
                    frame.setSize(500, 300);
                    Block block = new Block(30, 30);
                    frame.add(block);
                    frame.setVisible(true);

                    block.setMine(true);
                    block.setGameState(1);
                    block.setNumber(1);
                    block.rightClick();
                    block.leftClick();
                    block.setGameState(3);

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
