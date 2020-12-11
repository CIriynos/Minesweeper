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
        font = font.deriveFont((float)getWidth() / 3.0f * 2.0f);
    }
    public Block(int x, int y) throws IOException, FontFormatException {
        this(x, y, DEFAULT_SIZE);
    }

    public void setMine(boolean foo)
    {
        if(initOrder != 0) return;
        mine = foo;
    }

    public void setBesides(Block left_up, Block up, Block right_up,
                              Block left, Block right, Block down_left,
                              Block down, Block down_right)
    {
        besides[1] = left_up; besides[2] = up;   besides[3] = right_up;
        besides[4] = left;   besides[5] = right;besides[6] = down_left;
        besides[7] = down;   besides[8] = down_right;
    }

    public void init()
    {
        if(isMine()) return;
        int cnt = 0;
        for(int i = 1; i <= 8; i++){
            if(besides[i] == null) continue; //border
            if(besides[i].isMine()) cnt++;
        }
        number = cnt;
        initialized = true;
    }

    public boolean leftClick()
    {
        if(!initialized || flaged) return true; //nothing was done
        if(isMine()) return false; //false -> Game over
        if(uncovered) return true;
        uncovered = true;

        //then start DFS
        if(number == 0){
            for(Block block: besides){
                if(block == null) continue; //border
                block.leftClick();
            }
        }
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

    public boolean doubleClick() //Click within left & right
    {
        if (!initialized || !uncovered || flaged) return false;
        boolean result = true;
        for (Block block: besides){
            if(block == null) continue; //border
            result = block.leftClick();
        }
        return result;
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
        g.setPaint(Color.CYAN);
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
        Ellipse2D circle = new Ellipse2D.Double(0, 0, getWidth() / 2, getHeight() / 2);
        g.setPaint(Color.white);
        g.fill(circle);
    }

    private void drawMine(Graphics g)
    {
        if(!mine) return;
        /////////////////////
    }

    @Override
    public void paintComponent(Graphics g)
    {
        try {
            Graphics2D g2 = (Graphics2D) g;
            if(gameState == 0){
                if(targeted) drawTargetedBase(g2);
                else drawBase(g2);
            }
            else if(gameState == 1 || gameState == 2)
            {
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
    private Block[] besides;
    private Font font;

    private static final int DEFAULT_SIZE = 20; //by pixel point

    public static void main(String args[])
    {
        try {
            JFrame frame = new JFrame("Test for Block");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Block[][] blocks = new Block[3][3];
            blocks[0][0] = new Block(0, 0, 50);
            blocks[1][0] = new Block(70, 0, 50);
            blocks[2][0] = new Block(70 * 2, 0, 50);
            blocks[1][0] = new Block(0, 70, 50);
            blocks[1][1] = new Block(70, 70, 50);
            blocks[1][2] = new Block(70 * 2, 70, 50);
            blocks[2][0] = new Block(0, 70 * 2, 50);
            blocks[2][1] = new Block(70, 70 * 2, 50);
            blocks[2][2] = new Block(70 * 2, 70 * 2, 50);

            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    blocks[i][j].setMine(false);
                }
            }
            }catch(Exception e){
            e.printStackTrace();
        }
    }
}
