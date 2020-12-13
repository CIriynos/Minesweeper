package com.github.CIriynos.Minesweeper;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

/*
    @Author Tang_Wenqi
    @Date 2020/12/12

    CLASS GameSetting
    This class provide some static methods, which could record some global setting
    (Texture, Font, etc..)
 */
public class GameSetting
{
    public void setNumberImage(Image image, int i) { numberImage[i] = image; }
    public void setMineImage(Image image) { mineImage = image; }
    public void setFlagImage(Image image) { flagImage = image; }
    public Image getNumberImage(int i){ return numberImage[i]; }
    public Image getMineImage(){ return mineImage; }
    public Image getFlagImage(){ return flagImage; }

    public static void initGameSetting() throws IOException
    {
        STYLE1 = new GameSetting();
        for(int i = 1; i <= 8; i++){
            File file1 = new File("src\\texture\\" + Integer.toString(i) + ".png");

            STYLE1.setNumberImage((ImageIO.read(file1)), i);
        }
        File file2 = new File("src\\texture\\mine1.png");
        STYLE1.setMineImage((ImageIO.read(file2)));
        File file3 = new File("src\\texture\\flag.png");
        STYLE1.setFlagImage(ImageIO.read(file3));
    }

    public static GameSetting STYLE1;
    private Image[] numberImage = new Image[10];
    private Image mineImage;
    private Image flagImage;
}
