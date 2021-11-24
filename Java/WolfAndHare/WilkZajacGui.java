package WolfAndHare;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.*;


// @SuppressWarnings("serial")
public class WilkZajacGui extends JPanel
{
	private JPanel[][] panel;
	private Color grey, blue, red, white;

	WilkZajacGui(int x, int y)
	{
		grey = new Color(127,127,127);
		blue = new Color(40,40, 200);
		red = new Color(200,40,40);
		white = new Color(200, 200, 200);
		setLayout(new GridLayout(x,y));
		
		panel = new JPanel[x][y];
		setBackground(white);
		
		for(int i=0; i<y; i++)
			for(int j =0; j<x; j++)
			{
				panel[j][i] = new JPanel();
				panel[j][i].setBackground(white);
				add(panel[j][i]);
			}
		setVisible(true);
	}
	
	public void ZmienKolorPola(int x, int y, int kolor)
	{
		if(kolor == 0) panel[x][y].setBackground(white);
		if(kolor == 1) panel[x][y].setBackground(blue);
		if(kolor == 2) panel[x][y].setBackground(red);
		if(kolor == 3) panel[x][y].setBackground(grey);
	}
}