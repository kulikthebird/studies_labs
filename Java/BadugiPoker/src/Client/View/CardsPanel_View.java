package Client.View;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.*;

import javax.swing.*;

/**
 * Widok panelu z kartami gracza.
 * @author Tomek
 *
 */
public class CardsPanel_View extends JPanel implements MouseListener
{
	/**
	 * Inicjalizuje widok kart.
	 * @param cards  Referencja do tablicy z pobranymi grafikami kart.
	 */
	public CardsPanel_View(BufferedImage[] cards)
	{
		this.cards = cards;
		displayed_cards[0]= -1;
		displayed_cards[1]= -1;
		displayed_cards[2]= -1;
		displayed_cards[3]= -1;
		
		pos_normal[0] = new Rectangle(10, 30, 60, 90);
		pos_normal[1] = new Rectangle(80, 30, 60, 90);
		pos_normal[2] = new Rectangle(150, 30, 60, 90);
		pos_normal[3] = new Rectangle(220, 30, 60, 90);
		
		pos_up[0] = new Rectangle(10, 10, 60, 90);
		pos_up[1] = new Rectangle(80, 10, 60, 90);
		pos_up[2] = new Rectangle(150, 10, 60, 90);
		pos_up[3] = new Rectangle(220, 10, 60, 90);
		
		chosen_card[0] = false;
		chosen_card[1] = false;
		chosen_card[2] = false;
		chosen_card[3] = false;
		
		addMouseListener(this);
		
	}
	
	/**
	 * Ustawia karty, które rozdał serwer.
	 * @param c1
	 * @param c2 
	 * @param c3
	 * @param c4
	 */
	public void setCards(int c1, int c2, int c3, int c4)
	{
		displayed_cards[0] = c1;
		displayed_cards[1] = c2;
		displayed_cards[2] = c3;
		displayed_cards[3] = c4;
		draw_cards = true;
		repaint();
	}
	
	
	/**
	 * Zakrywa karty.
	 */
	public void hideCards()
	{
		draw_cards = false;
		for(int i=0; i<4; i++)
			chosen_card[i] = false;
		this.repaint();
	}
	
	/**
	 * Pobiera indeksy kart wybranych przez gracza do wymiany.
	 * @return Tablica indeksów kart
	 */
	public int[] getChangedCards()
	{
		int[] cards = new int[5];
		int c = 1;
		for(int i =0; i<4; i++)
		{
			if(chosen_card[i] == true)
				cards[c++] = i;
		}
		cards[0] = c - 1;
		return cards;
	}
	
	/**
	 * Wybrana przez klienta karta
	 * @param x  Indeks karty
	 */
	private void CardChosen(int x)
	{
		chosen_card[x] = true;
		this.repaint();
	}
	
	/**
	 * Pozostawiona przez gracza karta.
	 * @param x  Indeks karty
	 */
	private void CardBack(int x)
	{
		chosen_card[x] = false;
		this.repaint();
	}
	
	/**
	 * Obsługa kliknięcia myszą.
	 */
	public void mouseClicked(MouseEvent e) 
	{
		if(draw_cards == false)
			return;
		int x = e.getX();
		int y = e.getY();
		
		for(int i=0; i< 4; i++)
		{
			if(!chosen_card[i])
			{
				if( x > pos_normal[i].x && x < pos_normal[i].width + pos_normal[i].x && y > pos_normal[i].y && y < pos_normal[i].height + pos_normal[i].y)
				{
					// kliknieto w ktoras karte, ktora jest w normalnej pozycji
					CardChosen(i);
					break;
				}
			}
			else
			{
				if( x > pos_up[i].x && x < pos_up[i].width + pos_up[i].x && y > pos_up[i].y && y < pos_up[i].height + pos_up[i].y)
				{
					// kliknieto w ktoras karte, ktora jest potencjalnie wybrana
					CardBack(i);
					break;
				}
			}
		}
		
	} 
	
	
    public void mouseEntered (MouseEvent mouseEvent) {} 
    public void mousePressed (MouseEvent mouseEvent) {} 
    public void mouseReleased (MouseEvent mouseEvent) {}  
    public void mouseExited (MouseEvent mouseEvent) {}  
	
    /**
     * Rysuje na ekranie panel z kartami gracza.
     */
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		for(int i=0; i<4; i++)
		{
			if(draw_cards == true)
			{
				if(!chosen_card[i])
					g.drawImage(cards[displayed_cards[i]], pos_normal[i].x, pos_normal[i].y, pos_normal[i].width, pos_normal[i].height, null);
				else g.drawImage(cards[displayed_cards[i]], pos_up[i].x, pos_up[i].y, pos_up[i].width, pos_up[i].height, null);
			}
			else
				g.drawImage(cards[52], pos_normal[i].x, pos_normal[i].y, pos_normal[i].width, pos_normal[i].height, null);
		}
				
	}
	
	
	private boolean[] chosen_card = new boolean[4];
	private Rectangle[] pos_normal= new Rectangle[4];
	private Rectangle[] pos_up = new Rectangle[4];
	private boolean draw_cards = false;
	private BufferedImage[] cards;
	private int[] displayed_cards = new int[4];
}
