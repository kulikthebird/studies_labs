package Client.View;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import Client.Exceptions.InitException;

/**
 * Widok gry.
 * @author Tomek
 *
 */
public class Game_View extends Panel
{
	/**
	 * Inicjalizuje widok gry.
	 */
    public Game_View() 
    {
        try 
        {
        	loadGraphics();
			initComponents();
		} 
        catch (InitException e) {
			JOptionPane.showMessageDialog(null, "Could not load file: "+e.getFile());
			e.printStackTrace();
		}
        ShowMyCards(0,1,2,3);
    }
    
    /**
     * Wczytuje pliki z grafiką kart.
     * @throws InitException
     */
    private void loadGraphics() throws InitException
    {
    	String path = "././graphics/";
    	for(int i=0; i<53; i++)
    	{
    		try
    		{
    			cards[i] = ImageIO.read(new File(path+"card"+i+".png"));
    		}
    		catch(IOException e)
    		{
    			throw new InitException("card"+i+".png");
    		}
    	}
    	Player_View.setCardsImg(cards);
    }
    
    /**
     * Pokazuje który gracz aktualnie wykonuje kolejkę.
     * @param id
     */
    public void setGoingPlayer(int id)
    {	
    	if(id == -1)
    		return;
    	if(last_going_plr == -1)
    	{
    		if(id != my_id)
    			player[((id + (6 - my_id)) % 6) -1].nick_label.setBackground(new Color(255, 213, 66));
    		else
    			cards_panel.setBackground(new Color(255, 213, 66));
    	}
    	else
    	{
    		if(last_going_plr != my_id)
    			player[((last_going_plr + (6 - my_id)) % 6) -1].nick_label.setBackground(new Color(94, 175, 68));
    		else
    			cards_panel.setBackground(new Color(94, 175, 68));
    		if(id != my_id)
    			player[((id + (6 - my_id)) % 6) -1].nick_label.setBackground(new Color(255, 213, 66));
    		else
    			cards_panel.setBackground(new Color(255, 213, 66));
    	}
    	last_going_plr = id;
    }
    
    public void setButtonCall()
    {
    	button1.setLabel("call");
    }
    
    public void setButtonAllIn()
    {
    	button1.setLabel("all in");
    }
    
    public void setButtonBet()
    {
    	button4.setLabel("bet");
    }
    
    public void setButtonRaise()
    {
    	button4.setLabel("raise");
    }
    
    public void ActivateCallButton(boolean b)
    {
    	button1.setEnabled(b);    	
    }
    
    public void ActivateCheckButton(boolean b)
    {
    	button2.setEnabled(b);
    }
    
    public void ActivateFoldButton(boolean b)
    {
    	button3.setEnabled(b);
    }
    
    public void ActivateBetButton(boolean b)
    {
    	button4.setEnabled(b);
    }
    
    public void ActivateChangingButton(boolean b)
    {
    	button5.setEnabled(b);
    }
    
    public void setDealer(int id)
	{
		game_panel.setDealer((id + (6 - my_id)) % 6);
	}
	
	public void setSmall(int id)
	{
		game_panel.setSmall((id + (6 - my_id)) % 6);
	}
	
	public void setBig(int id)
	{
		game_panel.setBig((id + (6 - my_id)) % 6);
	}
    
    public void HideMyCards()
    {
    	cards_panel.hideCards();
    }
    
    public void HidePlayerCards(int id)
    {
    	player[((id + (6 - my_id)) % 6) -1].HideCards();
    }
    
    public void HidePlayersCards()
    {
    	for(int i=0; i<ids_list.size(); i++)
    		player[((ids_list.get(i) + (6 - my_id)) % 6) -1].HideCards();	
    }
    
    public void setMyID(int x)
    {
    	my_id = x;
    }
    
    public void ShowMyCards(int c1, int c2, int c3, int c4)
    {
    	cards_panel.setCards(c1, c2, c3, c4);
    }
    
    public int[] getChangedCards()
    {
    	return cards_panel.getChangedCards();
    }
    
    public void setPlayerBal(int x, int id)
    {
    	if(id == my_id)
    		my_bal_label2.setText(x+"$");
    	else
    		player[((id + (6 - my_id)) % 6) -1].balance_label2.setText(x+"$");
    }
    
    public void setPlayerInpot(int x, int id)
    {
    	if(id == my_id)
    		my_inpot_label2.setText(x+"$");
    	else
    		player[((id + (6 - my_id)) % 6) -1].inpot_label2.setText(x+"$");
    }
    
    public void setPlayerChgdCards(int x, int id)
    {
    	if(my_id != id)
    		player[((id + (6 - my_id)) % 6) -1].cards_label2.setText(""+x);
    }
    
    public void setMainPot(int x)
    {
    	main_pot_label2.setText(x+"$");
    }
    
    public void setMaxInpot(int x)
    {
    	max_pot_label2.setText(x+"$");
    }
    
    public void setMyNick(String nick)
    {
    	my_nick_label.setText(nick);
    }
    
    
    /**
     * Dodaje nowego gracza do widoku.
     * @param nick  Nick nowego gracza
     * @param id  ID nowego gracza
     */
    public void AddPlayer(String nick, int id)
    {
    	ids_list.add(id);
    	int iid = ((id + (6 - my_id)) % 6) -1;
    	player[iid] = new Player_View(iid);
    	player[iid].nick_label.setText(nick);
    	game_panel.add(player[iid]);
    }
    
    public String getMyBet()
    {
    	return pot_label.getText();
    }
    
    /**
     * Rozdaje graczowi karty.
     * @param id  ID gracza
     * @param c1
     * @param c2
     * @param c3
     * @param c4
     */
    public void ShowPlayerCards(int id, int c1, int c2, int c3, int c4)
    {
    	if(id != my_id)
    		player[((id + (6 - my_id)) % 6) -1].ShowCards(c1, c2, c3, c4);
    	else
    		ShowMyCards(c1, c2, c3, c4);
    }
    
    
    /**
     * Inicjalizuje widok panelu gry.
     * @throws InitException
     */
    private void initComponents() throws InitException
    {
        game_panel = new GamePanel_View();
        pot_panel = new Panel();
        main_pot_label1 = new Label();
        main_pot_label2 = new Label();
        max_pot_label1 = new Label();
        max_pot_label2 = new Label();
        menu_panel = new Panel();
        
        cards_panel = new CardsPanel_View(cards);
        
        exit_panel = new Panel();
        exit_button = new Button();
        menu_button = new Button();
        options_panel = new Panel();
        button2 = new Button();
        button4 = new Button();
        button1 = new Button();
        button3 = new Button();
        button5 = new Button();
        pot_label = new TextField();
        my_inpot_label1 = new Label();
        my_inpot_label2 = new Label();
        my_nick_label = new Label();
        my_bal_label1 = new Label();
        my_bal_label2 = new Label();
        
        
        setBounds(0,0,1024,768);
        setLayout(null);

        
        game_panel.setBackground(new java.awt.Color(0, 102, 0));
        game_panel.setName("game_panel");
        game_panel.setLayout(null);

        pot_panel.setBackground(new java.awt.Color(255, 102, 0));
        pot_panel.setName("");
        pot_panel.setLayout(null);

        main_pot_label1.setFont(new java.awt.Font("Times New Roman", 0, 24));
        main_pot_label1.setText("Pot:");
        pot_panel.add(main_pot_label1);
        main_pot_label1.setBounds(20, 10, 110, 32);

        main_pot_label2.setFont(new java.awt.Font("Times New Roman", 0, 24));
        main_pot_label2.setText("   0$");
        pot_panel.add(main_pot_label2);
        main_pot_label2.setBounds(40, 40, 60, 32);

        max_pot_label1.setFont(new java.awt.Font("Times New Roman", 0, 24));
        max_pot_label1.setText("Max bet:");
        pot_panel.add(max_pot_label1);
        max_pot_label1.setBounds(10, 90, 110, 32);

        max_pot_label2.setFont(new java.awt.Font("Times New Roman", 0, 24));
        max_pot_label2.setText("   0$");
        pot_panel.add(max_pot_label2);
        max_pot_label2.setBounds(40, 120, 60, 32);

        game_panel.add(pot_panel);
        pot_panel.setBounds(425, 270, 150, 180);

        add(game_panel);
        game_panel.setBounds(0, 0, 1008, 569);

        menu_panel.setBackground(new java.awt.Color(0, 102, 0));
        menu_panel.setName("menu_panel");
        menu_panel.setLayout(null);

        cards_panel.setBackground(new java.awt.Color(94, 175, 68));
        cards_panel.setPreferredSize(new java.awt.Dimension(100, 100));
        cards_panel.setLayout(null);
        menu_panel.add(cards_panel);
        cards_panel.setBounds(430, 10, 290, 140);

        exit_panel.setBackground(new java.awt.Color(94, 175, 68));
        exit_panel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        exit_panel.setPreferredSize(new java.awt.Dimension(100, 100));
        exit_panel.setLayout(null);

        exit_button.setLabel("Exit");
        exit_panel.add(exit_button);
        exit_button.setBounds(10, 10, 75, 24);

        menu_button.setLabel("Main Menu");
        exit_panel.add(menu_button);
        menu_button.setBounds(10, 44, 75, 24);

        menu_panel.add(exit_panel);
        exit_panel.setBounds(730, 10, 90, 140);

        options_panel.setBackground(new java.awt.Color(213, 38, 38));
        options_panel.setLayout(null);

        button2.setActionCommand("but1");
        button2.setLabel("check");
        options_panel.add(button2);
        button2.setBounds(80, 61, 60, 16);

        button4.setActionCommand("but2");
        button4.setLabel("bet");
        options_panel.add(button4);
        button4.setBounds(80, 91, 60, 16);

        button1.setActionCommand("but3");
        button1.setLabel("call");
        options_panel.add(button1);
        button1.setBounds(10, 61, 60, 16);

        button3.setActionCommand("but4");
        button3.setLabel("fold");
        options_panel.add(button3);
        button3.setBounds(10, 91, 60, 16);

        button5.setActionCommand("but5");
        button5.setLabel("change cards");
        options_panel.add(button5);
        button5.setBounds(40, 110, 80, 30);
        
        pot_label.setText("0");
        options_panel.add(pot_label);
        pot_label.setBounds(150, 61, 56, 20);

        my_inpot_label1.setText("Your bet:");
        options_panel.add(my_inpot_label1);
        my_inpot_label1.setBounds(150, 91, 68, 20);

        my_inpot_label2.setText("   0$");
        options_panel.add(my_inpot_label2);
        my_inpot_label2.setBounds(219, 91, 53, 20);

        my_bal_label2.setText("   0$");
        options_panel.add(my_bal_label2);
        my_bal_label2.setBounds(220, 110, 53, 20);
        
        my_bal_label1.setText("Ballance:");
        options_panel.add(my_bal_label1);
        my_bal_label1.setBounds(150, 110, 60, 20);
        
        my_nick_label.setFont(new java.awt.Font("Baskerville Old Face", 0, 18));
        my_nick_label.setText("Your_Nick");
        options_panel.add(my_nick_label);
        my_nick_label.setBounds(10, 10, 196, 23);

        menu_panel.add(options_panel);
        options_panel.setBounds(140, 10, 280, 140);

        add(menu_panel);
        menu_panel.setBounds(0, 569, 1008, 160);
        
        
        setVisible(false);
    }

    public Button button1;
    public Button button2;
    public Button button3;
    public Button button4;
    public Button button5;
    public Button exit_button;
    public Button menu_button;
    
    private Panel exit_panel;
    private CardsPanel_View cards_panel;
    private GamePanel_View game_panel;
    private Panel menu_panel;
    private Panel options_panel;
    private Panel pot_panel;
    
    
    private TextField pot_label;
    private Label main_pot_label1;
    private Label main_pot_label2;
    private Label my_inpot_label1;
    private Label my_inpot_label2;
    private Label my_nick_label;
    private Label max_pot_label1;
    private Label max_pot_label2;
    private Label my_bal_label1;
    private Label my_bal_label2;
    
    private Player_View[] player = new Player_View[5];
    private int my_id = -1;
    
    private int last_going_plr = -1;
    private BufferedImage[] cards = new BufferedImage[53];
    private List<Integer> ids_list = new ArrayList<Integer>();
                  
}
