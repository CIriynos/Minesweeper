package com.github.CIriynos.Minesweeper;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
            java.net.URL url = GameSetting.class.getResource("/texture/" + Integer.toString(i) + ".png");
            //System.out.println(file1.getAbsolutePath());
            STYLE1.setNumberImage(ImageIO.read(url), i);
        }
        //File file2 = new File("texture\\mine1.png");
        java.net.URL url2 = GameSetting.class.getResource("/texture/mine1.png");
        STYLE1.setMineImage((ImageIO.read(url2)));
       // File file3 = new File("texture\\flag.png");
        java.net.URL url3 = GameSetting.class.getResource("/texture/flag.png");
        STYLE1.setFlagImage(ImageIO.read(url3));
    }

    public static GameSetting STYLE1;
    private Image[] numberImage = new Image[10];
    private Image mineImage;
    private Image flagImage;
}
