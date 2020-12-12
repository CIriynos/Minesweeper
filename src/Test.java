import com.github.CIriynos.Minesweeper.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

public class Test extends JFrame
{
    public Test(String title)
    {
        setTitle(title);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        setLayout(layout);

        //Add mouseListener
        addMouseListener(new MouseHandler());

        setVisible(true);
    }

    private class MouseHandler extends MouseAdapter
    {
        public void mouseClicked(MouseEvent event)
        {
            //deal with the main game board
            //board.leftClick(event.getX(), event.getY()); //Adapt to board
            System.out.println(event.getX() + " " + event.getY());
        }
    }

    public Board board;
    private Rectangle2D boardRect;
    public static final int DEFAULT_WIDTH = 500;
    public static final int DEFAULT_HEIGHT = 400;

    //for debug
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Test scene = new Test("Minesweeper Test");
                    JButton button = new JButton("F**k you off");
                    scene.add(button, BorderLayout.NORTH);
                   // System.out.println(scene.board.getLocation().toString());
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
