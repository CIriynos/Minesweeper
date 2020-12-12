package com.github.CIriynos.Minesweeper;

import javax.swing.*;
import java.awt.*;
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
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        layout.setVgap(10);
        layout.setHgap(10);
        setLayout(layout);

        //Add Board
        board = new Board(30, 16, 60,  this);
        boardRect = board.getBounds();
        add(board, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    public void updateState(GameState s)
    {
        gameState = s;
        if(gameState == GameState.ONGOING) {
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

    public GameState gameState = GameState.WAITING;
    public Board board;
    private long startTime;
    private long endTime;
    private long deltaTime;
    private Rectangle2D boardRect;


    public static final int DEFAULT_WIDTH = 800;
    public static final int DEFAULT_HEIGHT = 400;

    //for debug
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    SceneInGame scene = new SceneInGame("Minesweeper Test");
                    scene.board.leftClick(0, 0);
                    scene.board.leftClick(2, 3);
                    //System.out.println(scene.board.dfsTimes);

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
