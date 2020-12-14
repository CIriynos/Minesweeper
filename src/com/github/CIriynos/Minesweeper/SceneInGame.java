package com.github.CIriynos.Minesweeper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

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

        loadTexture();
        updateDifficulty(HARD);
        setMenuBar();
        updateState(GameState.WAITING);
        adjustFrameSize();

        setVisible(true);
    }

    private void loadTexture()
    {
        gameSetting = GameSetting.STYLE1;
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
        difficultySetting.addActionListener(e -> {
            int res = JOptionPane.showOptionDialog(SceneInGame.this, "Select Difficulty:",
                    "Difficult Setting", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, difficultyOptions, null);
            if(res == JOptionPane.CLOSED_OPTION) return;
            updateDifficulty(res);
        });

        //settingMenu.addSeparator();
        settingMenu.add(difficultySetting);

        JMenuItem customizedDifficulty = new JMenuItem("Customize Difficulty");
        customizedDifficulty.addActionListener(e -> {
            if(customDialog == null)
                customDialog = new CustomizedDifficultyDialog(SceneInGame.this);
            customDialog.updatePosition();
            customDialog.setVisible(true);
        });
        settingMenu.add(customizedDifficulty);
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
        restart.addActionListener(event -> {
            //Confirm Dialog
            int res = JOptionPane.showConfirmDialog(SceneInGame.this, "Do you surly want to restart?",
                    "Restart Confirm.", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(res == JOptionPane.YES_OPTION){
                updateState(GameState.WAITING);
            }
        });
        gameMenu.add(restart);
        menuBar.add(gameMenu);

        //add About
        String aboutString = "<html>" +
                "<font size = 3>" +
                "<b><p>" +
                "Author: Tang Wenqi (twq_email@163.com) <br><br>" +
                "College: Tianjin University (<abbr> TJU </abbr>) <br><br>" +
                "Click here for the newest version <br>" +
                "<a href=\"\">" +
                "https://github.com/CIriynos/Minesweeper" +
                "</a> " +
                "</font><br><br>" +
                "</html>";
        JMenuItem about = new JMenuItem("About");
        JLabel aboutText = new JLabel(aboutString);
        about.addActionListener(event -> {
            JOptionPane.showMessageDialog(SceneInGame.this, aboutText
            , "About", JOptionPane.INFORMATION_MESSAGE, null);
        });
        menuBar.add(about);
        add(menuBar, BorderLayout.NORTH);
    }

    private void gameEndMessage(GameState state)
    {
        if(state == GameState.SUCCESS){
            String msg = "YOU SUCCEED in " + Double.toString((double)deltaTime / 1000) + " sec(s).\n";
            JOptionPane.showMessageDialog(this, msg, "Congratulation!", JOptionPane.INFORMATION_MESSAGE);
        }
        else if(state == GameState.FAILURE){
            int res = JOptionPane.showConfirmDialog(SceneInGame.this, "You failed! Try Again?",
                    "Notice", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(res == JOptionPane.YES_OPTION){
                updateState(GameState.WAITING);
            }
        }
    }

    public void updateDifficulty(int difficulty)
    {
        this.difficulty = difficulty;
        if(difficulty == EASY ){
            column = DEFAULT_EASY_COLUMN;
            line = DEFAULT_EASY_LINE;
            mineNum = DEFAULT_EASY_MINE;
        }
        else if(difficulty == MEDIUM){
            column = DEFAULT_MEDIUM_COLUMN;
            line = DEFAULT_MEDIUM_LINE;
            mineNum = DEFAULT_MEDIUM_MINE;
        }
        else if(difficulty == HARD){
            column = DEFAULT_HARD_COLUMN;
            line = DEFAULT_HARD_LINE;
            mineNum = DEFAULT_HARD_MINE;
        }
        else if(difficulty == EXTREME){
            column = DEFAULT_EXTREME_COLUMN;
            line = DEFAULT_EXTREME_LINE;
            mineNum = DEFAULT_EXTREME_MINE;
        }
    }

    public void updateState(GameState s)
    {
        gameState = s;
        if(gameState == GameState.WAITING)
        {
            System.out.println("Game has not started yet!");
            //refresh (create) the board
            if(board != null) remove(board);
            board = new Board(getColumn(), getLine(), getMineNum(), gameSetting, this);
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
            gameEndMessage(gameState);
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

    private void adjustFrameSize()
    {
        pack();
    }

    public void setColumn(int num){ column = num; }
    public void setLine(int num){ line = num; }
    public void setMineNum(int num){ mineNum = num; }
    public int getColumn(){ return column; }
    public int getLine(){ return line; }
    public int getMineNum(){ return mineNum; }

    public JMenuBar menuBar;
    public GameState gameState = GameState.WAITING;
    public Board board = null;
    private int column;
    private int line;
    private int mineNum;
    private long startTime;
    private long endTime;
    private long deltaTime;
    private Rectangle2D boardRect;
    private Object[] difficultyOptions = {"Easy", "Medium", "Hard", "Extreme"};
    private int difficulty = DEFAULT_DIFFICULTY; //0->Easy, 1->Medium, 2->Hard, 3->Extreme 4->custom
    private Image imageMine;
    private Image[] imageNum = new Image[9];
    private GameSetting gameSetting;
    private CustomizedDifficultyDialog customDialog;

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

    public static final int EASY = 0;
    public static final int MEDIUM = 1;
    public static final int HARD = 2;
    public static final int EXTREME = 3;
    public static final int CUSTOM = 4;

    //for debug
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    //Load Texture and Resource
                    GameSetting.initGameSetting();

                    //Create Scene
                    SceneInGame scene = new SceneInGame("Minesweeper Test");

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
