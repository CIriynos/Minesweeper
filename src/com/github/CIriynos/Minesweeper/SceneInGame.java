package com.github.CIriynos.Minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

/*
    @Author Tang_Wenqi
    @Date 2020/12/12

    CLASS SceneInGame
    provide some function about the whole game, like time, game state, etc.
 */

public class SceneInGame extends JFrame
{
    public SceneInGame(String title)
    {
        //Frame setting
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        setLayout(layout);

        setMenuBar();
        updateState(GameState.WAITING);
        adjustFrameSize();

        setVisible(true);
    }

    private void setMenuBar()
    {
        //menuBar setting
        menuBar = new JMenuBar();
        menuBar.setLayout(new FlowLayout(FlowLayout.LEFT));

        //set settingMenu
        JMenu settingMenu = new JMenu("Setting");

        //set settingMenu -> difficultySetting
        JMenuItem difficultySetting = new JMenuItem("Difficulty");
        difficultySetting.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int res = JOptionPane.showOptionDialog(SceneInGame.this, "Select Difficulty:",
                        "Difficult Setting", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, difficultyOptions, null);
                if(res == JOptionPane.CLOSED_OPTION) return;
                difficulty = res;
            }
        });
        //settingMenu.addSeparator();
        settingMenu.add(difficultySetting);
        menuBar.add(settingMenu);

        //set gameMenu
        JMenu gameMenu = new JMenu("Game");
        //set gameMenu -> exit
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Confirm Dialog
                int res = JOptionPane.showConfirmDialog(SceneInGame.this, "Do you surly want to exit?",
                        "Exit Confirm.", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                if(res == JOptionPane.YES_OPTION)
                    System.exit(0);
            }
        });
        gameMenu.add(exit);
        //set gameMenu -> restart
        JMenuItem restart = new JMenuItem("Restart");
        restart.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Confirm Dialog
                int res = JOptionPane.showConfirmDialog(SceneInGame.this, "Do you surly want to restart?",
                        "Restart Comfirm.", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(res == JOptionPane.YES_OPTION){
                    updateState(GameState.WAITING);
                }
            }
        });
        gameMenu.add(restart);
        menuBar.add(gameMenu);

        add(menuBar, BorderLayout.NORTH);
    }

    private int getColumn(){
        if(difficulty == 0) return DEFAULT_EASY_COLUMN;
        if(difficulty == 1) return DEFAULT_MEDIUM_COLUMN;
        if(difficulty == 2) return DEFAULT_HARD_COLUMN;
        if(difficulty == 3) return DEFAULT_EXTREME_COLUMN;
        return DEFAULT_HARD_COLUMN; //if ERROR
    }

    private int getLine(){
        if(difficulty == 0) return DEFAULT_EASY_LINE;
        if(difficulty == 1) return DEFAULT_MEDIUM_LINE;
        if(difficulty == 2) return DEFAULT_HARD_LINE;
        if(difficulty == 3) return DEFAULT_EXTREME_LINE;
        return DEFAULT_HARD_LINE; //if ERROR
    }

    private int getMineNum(){
        if(difficulty == 0) return DEFAULT_EASY_MINE;
        if(difficulty == 1) return DEFAULT_MEDIUM_MINE;
        if(difficulty == 2) return DEFAULT_HARD_MINE;
        if(difficulty == 3) return DEFAULT_EXTREME_MINE;
        return DEFAULT_HARD_MINE; //if ERROR
    }

    private void adjustFrameSize()
    {
        pack();
    }

    private void gameEndMessage(GameState state)
    {
        if(state == GameState.SUCCESS){
            String msg = "YOU SUCCEED in " + Double.toString((double)deltaTime / 1000) + " sec(s).\n";
            JOptionPane.showMessageDialog(this, msg, "Congratulation!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void updateState(GameState s)
    {
        gameState = s;
        if(gameState == GameState.WAITING)
        {
            System.out.println("Game has not started yet!");
            //refresh the board
            if(board != null) remove(board);
            board = new Board(getColumn(), getLine(), getMineNum(),this);
            add(board, BorderLayout.SOUTH);
            adjustFrameSize();
            repaint();
        }
        else if(gameState == GameState.ONGOING) {
            System.out.println("Game starts!");
            startTiming();
        }
        else if(gameState == GameState.FAILURE){
            board.setMineVisible();
            System.out.println("You fail!");
            endTiming();
            board.stopControl();
        }
        else if(gameState == GameState.SUCCESS){
            board.setAllFlagsOn();
            System.out.println("You Succeed!");
            endTiming();
            gameEndMessage(gameState);
        }
    }

    private void startTiming()
    {
        startTime = System.currentTimeMillis();
    }

    private void endTiming()
    {
        endTime = System.currentTimeMillis();
        deltaTime = endTime - startTime;
        System.out.println("Total Time: " + deltaTime + "ms");
    }

    public JMenuBar menuBar;
    public GameState gameState = GameState.WAITING;
    public Board board = null;
    private long startTime;
    private long endTime;
    private long deltaTime;
    private Rectangle2D boardRect;
    private Object[] difficultyOptions = {"Easy", "Medium", "Hard", "Extreme"};
    private int difficulty = DEFAULT_DIFFICULTY; //0->Easy, 1->Medium, 2->Hard, 3->Extreme

    public static final int DEFAULT_DIFFICULTY = 2;

    public static final int DEFAULT_EASY_COLUMN = 10;
    public static final int DEFAULT_EASY_LINE = 10;
    public static final int DEFAULT_MEDIUM_COLUMN = 20;
    public static final int DEFAULT_MEDIUM_LINE = 16;
    public static final int DEFAULT_HARD_COLUMN = 30;
    public static final int DEFAULT_HARD_LINE = 16;
    public static final int DEFAULT_EXTREME_COLUMN = 50;
    public static final int DEFAULT_EXTREME_LINE = 20;

    public static final int DEFAULT_EASY_MINE = 10;
    public static final int DEFAULT_MEDIUM_MINE = 50;
    public static final int DEFAULT_HARD_MINE = 99;
    public static final int DEFAULT_EXTREME_MINE = 199;

    //for debug
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    SceneInGame scene = new SceneInGame("Minesweeper Test");

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
