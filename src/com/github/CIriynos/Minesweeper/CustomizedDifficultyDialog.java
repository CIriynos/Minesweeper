package com.github.CIriynos.Minesweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CustomizedDifficultyDialog extends JDialog
{
    public CustomizedDifficultyDialog(SceneInGame owner)
    {
        super(owner, "Customize Difficulty", true);
        this.owner = owner;
        addComponent();
        updatePosition();
    }

    private void addComponent()
    {
        setLayout(new BorderLayout());
        //set ListPane
        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        JLabel notice = new JLabel("Input the column and line you prefer:");
        textColumn = new JTextField("30", 5);
        textLine = new JTextField("16", 5);
        textMine = new JTextField("99", 5);
        JLabel labelColumn = new JLabel("Column: ");
        JLabel labelLine = new JLabel("Line: ");
        JLabel labelMine = new JLabel("Mine:");

        //create Panel
        JPanel columnPanel = new JPanel();
        JPanel linePanel = new JPanel();
        JPanel minePanel = new JPanel();
        columnPanel.add(labelColumn);
        columnPanel.add(textColumn);
        linePanel.add(labelLine);
        linePanel.add(textLine);
        minePanel.add(labelMine);
        minePanel.add(textMine);

        listPane.add(notice);
        listPane.add(Box.createRigidArea(new Dimension(0, 10)));
        listPane.add(columnPanel);
        listPane.add(Box.createRigidArea(new Dimension(0, 10)));
        listPane.add(linePanel);
        listPane.add(Box.createRigidArea(new Dimension(0, 10)));
        listPane.add(minePanel);
        listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(listPane, BorderLayout.NORTH);

        //Add Actions to two Button
        Action cancelButtonAction = new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                CustomizedDifficultyDialog.this.setVisible(false);
            }
        };
        Action setButtonAction = new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    owner.updateDifficulty(SceneInGame.CUSTOM);
                    int column = Integer.parseInt(textColumn.getText());
                    int line = Integer.parseInt(textLine.getText());
                    int mineNum = Integer.parseInt(textMine.getText());
                    if(column == 0 || line == 0 || mineNum == 0)
                        throw new NumberFormatException();
                    owner.setColumn(column);
                    owner.setLine(line);
                    owner.setMineNum(mineNum);
                    CustomizedDifficultyDialog.this.setVisible(false);
                }
                catch(NumberFormatException exception){
                    JOptionPane.showMessageDialog(CustomizedDifficultyDialog.this, "Invalid Input, Please Input Integer (0 - 99)!",
                            "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    textColumn.setText("");
                    textLine.setText("");
                    textMine.setText("");
                }
            }
        };
        cancelButtonAction.putValue(Action.NAME, "Cancel");
        setButtonAction.putValue(Action.NAME, "Set");

        //create button
        JButton cancelButton = new JButton(cancelButtonAction);
        JButton setButton = new JButton(setButtonAction);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(setButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void updatePosition(){
        setLocation(owner.getX() + 50, owner.getY() + 50);
        pack();
    }

    SceneInGame owner;
    JTextField textColumn;
    JTextField textLine;
    JTextField textMine;
    public static final int DEFAULT_WIDTH = 500;
    public static final int DEFAULT_HEIGHT = 400;
}
