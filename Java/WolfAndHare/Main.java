package WolfAndHare;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.*;


public class Main
{ 
    static private Mapa map;
    public static void main(String[] args)
    {
        WilkZajacGui game = new WilkZajacGui(80,80);
        JFrame frame = new JFrame();
        frame.add(game);
        frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        map = new Mapa(game, 5, 5, 80, 80, 400);
    }

}