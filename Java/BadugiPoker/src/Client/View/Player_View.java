package Client.View;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * Widok przeciwników.
 * @author Tomek
 *
 */
public class Player_View extends JPanel
{	
	public Player_View(int id)
	{
		if(!init)
			InitPlayerView();
		InitComponents(id);
	}
	
	/**
	 * Ustawia karty wczytane przez widok gry.
	 * @param _cards  Referencja do tablicy z kartami.
	 */
	public static void setCardsImg(BufferedImage[] _cards)
	{
		cards = _cards;
	}
	
	/**
	 * Rozdaje karty od serwera.
	 * @param c1
	 * @param c2
	 * @param c3
	 * @param c4
	 */
	public void ShowCards(int c1, int c2, int c3, int c4)
	{
		displayed_cards[0] = c1;
		displayed_cards[1] = c2;
		displayed_cards[2] = c3;
		displayed_cards[3] = c4;
		draw_cards = true;
		this.repaint();
	}
	
	/**
	 * Zakrywa karty.
	 */
	public void HideCards()
	{
		draw_cards = false;
		this.repaint();
	}
	
	
	/**
	 * Inicjalizuje rozmieszczenie kart oraz przeciwników na ekranie.
	 */
	public void InitPlayerView()
	{
		dims[2] = new Rectangle(390, 10, 220, 180);
		dims[1] = new Rectangle(50, 100, 220, 180);
		dims[0] = new Rectangle(50, 310, 220, 180);
		dims[3] = new Rectangle(730, 100, 220, 180);
		dims[4] = new Rectangle(730, 310, 220, 180);
		
		pos[0] = new Rectangle(10, 110, 40, 50);
		pos[1] = new Rectangle(60, 110, 40, 50);
		pos[2] = new Rectangle(110, 110, 40, 50);
		pos[3] = new Rectangle(160, 110, 40, 50);
		
		init = true;
	}
	
	/**
	 * Inicjalizuje widok gracza.
	 * @param id  ID wyświetlanego gracza.
	 */
	private void InitComponents(int id)
	{
        balance_label1 = new java.awt.Label();
        inpot_label1 = new java.awt.Label();
        cards_label1 = new java.awt.Label();
        nick_label = new java.awt.Label();
        balance_label2 = new java.awt.Label();
        inpot_label2 = new java.awt.Label();
        cards_label2 = new java.awt.Label();
		
		setBackground(new java.awt.Color(94, 175, 68));
        setLayout(null);
        setBounds(dims[id]);

        balance_label1.setText("Balance:");
        add(balance_label1);
        balance_label1.setBounds(10, 30, 52, 20);

        inpot_label1.setText("Bet:");
        add(inpot_label1);
        inpot_label1.setBounds(10, 52, 34, 20);

        cards_label1.setText("Changed cards:");
        add(cards_label1);
        cards_label1.setBounds(10, 73, 92, 20);

        nick_label.setFont(new java.awt.Font("Times New Roman", 0, 18));
        nick_label.setText("Nick");
        add(nick_label);
        nick_label.setBounds(10, 0, 180, 28);

        balance_label2.setText("   0$");
        add(balance_label2);
        balance_label2.setBounds(103, 30, 45, 20);

        inpot_label2.setText("   0$");
        add(inpot_label2);
        inpot_label2.setBounds(103, 51, 45, 20);

        cards_label2.setText("0");
        add(cards_label2);
        cards_label2.setBounds(103, 73, 11, 20);

        setVisible(true);
	}

	/**
	 * Rysuje panel na ekranie.
	 */
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		for(int i=0; i<4; i++)
		{	
			if(draw_cards == true)
				g.drawImage(cards[displayed_cards[i]], pos[i].x, pos[i].y, pos[i].width, pos[i].height, null);
			else 
				g.drawImage(cards[52], pos[i].x, pos[i].y, pos[i].width, pos[i].height, null);
		}
	}
	
	private int[] displayed_cards = new int[4];
	private boolean draw_cards = false;
	private static BufferedImage[] cards;
	
	private static Rectangle[] pos = new Rectangle[4];
	private static Rectangle[] dims = new Rectangle[5];
	private static boolean init = false;
	
	private java.awt.Label balance_label1;
    public java.awt.Label balance_label2;
    private java.awt.Label cards_label1;
    public java.awt.Label cards_label2;
    private java.awt.Label inpot_label1;
    public java.awt.Label inpot_label2;
    public java.awt.Label nick_label;
}
