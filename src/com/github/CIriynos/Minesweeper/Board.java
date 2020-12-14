package com.github.CIriynos.Minesweeper;

import javax.swing.*;
import com.github.CIriynos.Minesweeper.Block;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;
import java.util.Random;

/*
    @Author Tang_Wenqi
    @Date 2020/12/11

    CLASS Board is a JPanel that can exhibit all of the blocks in the game.
    it can also control these blocks, record the positions of them, and provide some
    operations like "Click within the left & right mouse".
 */

public class Board extends JPanel
{
    public Board(int column, int line, int mineNum, GameSetting gameSetting, SceneInGame father)
    {

        this.column = column;
        this.line = line;
        this.mineNum = mineNum;
        this.father = father;
        this.gameSetting = gameSetting;
        flagCount = 0;
        if(column * line < mineNum + 1)
            this.mineNum = column * line - 1;

        //Add mouseListener
        addMouseListener(new MouseHandler());
        addMouseMotionListener(new MouseMotionHandler());
        //set board color
        backgroundColor = Color.LIGHT_GRAY.brighter();
        setBackground(backgroundColor);
        //set layout
        setLayout(null);
        //set bounds
        setSize(DEFAULT_LEFT_INTERVAL * 2 + (column - 1) * DEFAULT_BLOCK_INTERVAL + column * Block.DEFAULT_SIZE,
                    DEFAULT_UP_INTERVAL * 2 + (line - 1) * DEFAULT_BLOCK_INTERVAL + line * Block.DEFAULT_SIZE);
        buildBlocks();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), getHeight());
    }

    private void buildBlocks(){
        try{
            //new blocks
            for(int y = 0; y < line; y++){
                for(int x = 0; x < column; x++){
                    int positionX = leftInterval + x * (Block.DEFAULT_SIZE + blockInterval);
                    int positionY = upInterval + y * (Block.DEFAULT_SIZE + blockInterval);
                    map[x][y] = new Block(positionX, positionY, x, y, Block.DEFAULT_SIZE, gameSetting);
                    blockRect[x][y] = new Rectangle2D.Double(positionX, positionY, Block.DEFAULT_SIZE, Block.DEFAULT_SIZE);
                    add(map[x][y]);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private Block find(int x, int y)
    {
        //There we could use Date Struction or Algorithm
        //(but waiting for update..)
        for(int i = 0; i < column; i++){
            for(int j = 0; j < line; j++){
                if(blockRect[i][j].contains(x, y)){
                    return map[i][j];
                }
            }
        }
        return null;
    }

    public void initMine(Block firstBlock)
    {
        Random r = new Random();
        for(int i = 1; i <= mineNum; i++){
            int x = r.nextInt(column);
            int y = r.nextInt(line);
            if(x == firstBlock.orderX && y == firstBlock.orderY){ i --; continue; }
            if(map[x][y].isMine()) { i--; continue; }
            map[x][y].setMine(true);
        }
        for(int i = 0; i < column; i++){
            for(int j = 0; j < line; j++){
                if(!map[i][j].isMine()){
                    map[i][j].setMine(false);
                }
            }
        }
        //set numbers for blocks
        //There is O(n^2) algo, waiting for optimization...
        int cnt = 0;
        for(int x = 0; x < column; x++) {
            for (int y = 0; y < line; y++) {
                if (map[x][y].isMine()) {
                    map[x][y].setNumber(0);
                    continue;
                }
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (x + i < 0 || y + j < 0 || x + i >= column || y + j >= line) continue;
                        if (i == 0 && j == 0) continue;
                        if (map[x + i][y + j].isMine()) cnt++;
                    }
                }
                map[x][y].setNumber(cnt);
                cnt = 0; //for next use
            }
        }
    }

    public boolean leftClick(int x, int y)
    {
        Block target = find(x, y);
        if(target == null){
            return true;
        }
        if(!firstClick){
            firstClick = true;
            //System.out.println("First Run");
            initMine(target);
            father.updateState(GameState.ONGOING);
        }
        System.out.println("Left Click.");
        return dfs(target, target.orderX, target.orderY);
    }

    public void rightClick(int x, int y)
    {
        Block target = find(x, y);
        if(target == null) return;
        int result = target.rightClick();
        System.out.println("Right Click.");
        if(result == 1) flagCount --;
        else if(result == 2) flagCount ++;
    }

    public void setMineVisible()
    {
        for(int i = 0; i < column; i++){
            for(int j = 0; j < line; j++){
                map[i][j].setMineDisplay(true);
            }
        }
    }

    public boolean doubleClick(int x, int y)
    {
        Block target = find(x, y);
        if(target == null) return true;
        if(!target.isInitialized() || target.isFlaged() || !target.isUncovered())
            return true;
        //judge if it is ok
        int flag_cnt = 0;
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                if(target.orderX + i < 0 || target.orderY + j < 0
                        || target.orderX + i >= column || target.orderY + j >= line) continue; //out-of-bound
                if(map[target.orderX + i][target.orderY + j].isFlaged()) flag_cnt ++;
            }
        }
        if(flag_cnt != target.getNumber()) return true;
        //operation
        boolean result = false;
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                if(target.orderX + i < 0 || target.orderY + j < 0
                        || target.orderX + i >= column || target.orderY + j >= line) continue; //out-of-bound
                result = dfs(map[target.orderX + i][target.orderY + j], target.orderX + i, target.orderY + j);
            }
        }
        System.out.println("Double Click.");
        return result;
    }

    public int dfsTimes = 0; // for debug
    private boolean dfs(Block block, int x, int y)
    {
        //System.out.print(x + " " + y + ": ");
        if(!block.isInitialized() || block.isFlaged() || block.isUncovered()) {
            //System.out.println(" Do not work.");
            return true;
        }
        if(block.getNumber() != 0){
            //System.out.println(" Reach the end.");
            return block.leftClick();
        }
        if(block.isMine()) return false;
        //System.out.println(" Trance. ");
        boolean result = block.leftClick();
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                if(x + i < 0 || y + j < 0 || x + i >= column || y + j >= line) continue; //out-of-bound
                result = dfs(map[x + i][y + j], x + i, y + j);
            }
        }
        return result;
    }

    private boolean checkComplete()
    {
        for(int i = 0; i < column; i++){
            for(int j = 0; j < line; j++){
                if(!map[i][j].isMine() && !map[i][j].isUncovered()) return false;
            }
        }
        return true;
    }

    public void stopControl()
    {
        gameOver = true;
    }

    public void setAllFlagsOn()
    {
        for(int i = 0; i < column; i++){
            for(int j = 0; j < line; j++){
                if(map[i][j].isMine() && !map[i][j].isFlaged())
                    map[i][j].rightClick();
            }
        }
    }

    private class MouseMotionHandler extends MouseMotionAdapter
    {
        Block lastTarget;
        Block target;
        Block buffer;
        @Override
        public void mouseMoved(MouseEvent e)
        {
            buffer = find(e.getX(), e.getY());
            if(buffer == null) return;
            if(target == null) target = buffer;
            else{
                lastTarget = target;
                target = buffer;
                if(target != lastTarget){
                    target.setTarget(true);
                    lastTarget.setTarget(false);
                }
            }
        }
    }

    private class MouseHandler extends MouseAdapter
    {
        @Override
        public void mousePressed(MouseEvent event)
        {
            if(gameOver) return;
            //click
            System.out.println(event.getModifiersEx());
            boolean result = true;
            if(event.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK){
                result = leftClick(event.getX(), event.getY());
            }
            else if(event.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK){
                rightClick(event.getX(), event.getY());
            }
            else if(event.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK + MouseEvent.BUTTON1_DOWN_MASK){
                result = doubleClick(event.getX(), event.getY());
            }


            //check and push message to father
            if(!result) father.updateState(GameState.FAILURE);
            if(checkComplete()) father.updateState(GameState.SUCCESS);
        }
    }

    public int getColumn(){return column;}
    public int getLine(){return line;}
    public int getMineNum(){return mineNum;}
    public int flagCount(){return flagCount;}

    //basic field
    private int column;
    private int line;
    private int mineNum;
    private int flagCount;
    private boolean firstClick;
    private boolean gameOver = false;
    private Block[][] map = new Block[MAX_LINE][MAX_COLUMN];
    private MouseEvent[] clickBuffer = new MouseEvent[2];
    private long[] clickTimeBuffer = new long[2];
    private Rectangle2D[][] blockRect = new Rectangle2D[MAX_LINE][MAX_COLUMN];
    private int leftInterval = DEFAULT_LEFT_INTERVAL;
    private int upInterval = DEFAULT_UP_INTERVAL;
    private int blockInterval = DEFAULT_BLOCK_INTERVAL;
    private SceneInGame father;
    private Color backgroundColor;
    private MouseHandler mouseHandler;
    private MouseMotionHandler mouseMotionHandler;
    private GameSetting gameSetting;

    private static final int DEFAULT_LEFT_INTERVAL = 30;
    private static final int DEFAULT_UP_INTERVAL = 20;
    private static final int DEFAULT_BLOCK_INTERVAL = 3;
    private static final int MAX_LINE = 99;
    private static final int MAX_COLUMN = 99;
    private static final int DEFAULT_MINE_NUM = 99;
}