package Client.View;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Client.Exceptions.InitException;

/**
 * Widok stołu gry.
 * @author Tomek
 *
 */
public class GamePanel_View extends JPanel
{
	
	public GamePanel_View() throws InitException
	{
		loadButtons();
		initPositions();
	}
	
	private void loadButtons() throws InitException
	{
		String path = "././graphics/";
   		try
   		{
    		 deal_img  = ImageIO.read(new File(path+"deal.png"));
    		 small_img = ImageIO.read(new File(path+"small.png"));
    		 big_img   = ImageIO.read(new File(path+"big.png"));
    	}
   		catch(IOException e)
   		{
   			throw new InitException("button file");
    	}
	}
	
	private void initPositions()
	{
		but_pos[0] = new Rectangle(475, 500, 20, 20);
		but_pos[1] = new Rectangle(310, 410, 20, 20);
		but_pos[2] = new Rectangle(330, 250, 20, 20);
		but_pos[3] = new Rectangle(505, 200, 20, 20);
		but_pos[4] = new Rectangle(690, 280, 20, 20);
		but_pos[5] = new Rectangle(670, 440, 20, 20);
		
		deal_pos[0] = new Rectangle(505, 500, 20, 20);
		deal_pos[1] = new Rectangle(330, 400, 20, 20);
		deal_pos[2] = new Rectangle(310, 210, 20, 20);
		deal_pos[3] = new Rectangle(475, 200, 20, 20);
		deal_pos[4] = new Rectangle(670, 250, 20, 20);
		deal_pos[5] = new Rectangle(690, 410, 20, 20);
		
	}
	
	public void setDealer(int id)
	{
		deal = id;
		repaint();
	}
	
	public void setSmall(int id)
	{
		small = id;
		repaint();
	}
	
	public void setBig(int id)
	{
		big = id;
		repaint();
	}
	
	/**
	 * Rysuje widok stołu na ekranie.
	 */
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(big_img, but_pos[big].x, but_pos[big].y, but_pos[big].width, but_pos[big].height, null);
		g.drawImage(small_img, but_pos[small].x, but_pos[small].y, but_pos[small].width, but_pos[small].height, null);
		g.drawImage(deal_img, deal_pos[deal].x, deal_pos[deal].y, deal_pos[deal].width, deal_pos[deal].height, null);
	}
	
	private int deal  = 0;
	private int small = 0;
	private int big   = 0;
	
	private BufferedImage big_img   = null;
	private BufferedImage small_img = null;
	private BufferedImage deal_img  = null;
	private Rectangle[] but_pos	 = new Rectangle[6];
	private Rectangle[] deal_pos = new Rectangle[6];
	
}