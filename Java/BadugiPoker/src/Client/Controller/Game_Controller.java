package Client.Controller;

import Client.Exceptions.ConnectException;
import Client.Exceptions.GameSyncException;
import Client.Model.Connection_Model;
import Client.Model.Rules_Model;
import Client.View.*;
import Server.Controller.Client_Seeker;

import java.awt.event.*;

import javax.swing.JOptionPane;

/**
 * Klasa implementująca kontroler całej rozgrywki po stronie klienta.
 * 
 * @author Tomasz Kulik, Rafał Dworczak
 *
 */

public class Game_Controller implements Messenger
{
	/**
	 * Metoda zwraca tablicę indeksów kart, jakie gracz wybrał do wymiany.
	 * @return Tablica indeksów kart
	 */
	public int[] CardChoose()
	{
		int[] c = new int[5];
		c = game_view.getChangedCards();
		return c;
	}
	
	
	/**
	 * Implemenutuje kontroler gry, tworzy model połączenia z serwerem.
	 * @param window Referencja do głównego okna gry
	 * @param client Referencja do kontrolera sterującego połączeniem z serwerem
	 */
	public Game_Controller(Main_View window, Server_Controller client)
	{
		this.window = window;
		this.rules = new Rules_Model();
		this.transfer = new Connection_Model(rules, this);
		this.client = client;
		CreateListeners();
	}
	
	/**
	 * Podłącza metody obsługujące kliknięcia przycisków w widoku gry.
	 */
	private void CreateListeners()
	{
		game_view = window.game;
		
		game_view.button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionButtonCall(evt);
            }
        });
		game_view.button2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionButtonCheck(evt);
            }
        });
		game_view.button3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionButtonFold(evt);
            }
        });
		game_view.button4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionButtonBet(evt);
            }
        });
		game_view.button5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionButtonChange(evt);
            }
        });
		
		game_view.exit_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionButtonExit(evt);
            }
        });
		game_view.menu_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionButtonMenu(evt);
            }
        });
		
	}
	
	
	public void setMyID(int id)
	{
		game_view.setMyID(id);
	}
	
	public void setMyNick(String nick)
	{
		game_view.setMyNick(nick);
		rules.SetMyNick(nick);
	}
	
	/**
	 * Obsługuje komunikaty otrzymane od serwera
	 * 
	 * @param input Łańcuch znaków zawierający komunikat od serwera. 
	 */
	public void ReceiveServerMessage(String input) throws ConnectException
	{
		try {
			if(input == "5")
				return;
			transfer.ReceiveMsg(input);
		} catch (GameSyncException e) {
			JOptionPane.showMessageDialog(null, "There are different data in the server and this client!");
		} 
		
		game_view.setBig(rules.getBigID());
		game_view.setSmall(rules.getSmallID());
		game_view.setDealer(rules.getDealerID());
		
		int[] status = rules.getPlayersState();
		
		game_view.setMainPot(status[1]);
		game_view.setMaxInpot(status[2]);
		game_view.setGoingPlayer(status[3]);
		
		for(int i=0; i<status[0]; i++)
		{
			game_view.setPlayerBal(status[4 + (i * 4) + 1], status[4 + (i * 4) + 0]);
			game_view.setPlayerChgdCards(status[4 + (i * 4) + 3], status[4 + (i * 4) + 0]);
			game_view.setPlayerInpot(status[4 + (i * 4) + 2], status[4 + (i * 4) + 0]);
		}
		
		int[] cards = rules.getCards();
		
		if(cards[0] < 2)
		{
			game_view.HidePlayersCards();
		}
		for(int i=0; i<cards[0]; i++)
		{
			game_view.ShowPlayerCards(cards[1 + (i * 5) + 0], cards[1 + (i * 5) + 1],
					cards[1 + (i * 5) + 2], cards[1 + (i * 5) + 3], cards[1 + (i * 5) + 4]);
		}
		
		int moves = rules.AvalibleMoves();
		if(moves == 0)
			;
		if((moves & rules.call) != 0)
		{
			call = true;
			game_view.setButtonCall();
		}
		else 
		{
			call = false;
			game_view.setButtonAllIn();
		}
		
		if((moves & rules.bet) != 0)
		{
			bet = true;
			game_view.setButtonBet();
		}
		else 
		{
			bet = false;
			game_view.setButtonRaise();
		}
		
		if((moves & rules.call) != 0 || (moves & rules.all_in) != 0)
			game_view.ActivateCallButton(true);
		else
			game_view.ActivateCallButton(false);
		
		if((moves & rules.raise) != 0 || (moves & rules.bet) != 0)
			game_view.ActivateBetButton(true);
		else
			game_view.ActivateBetButton(false);
		
		if((moves & rules.fold) != 0)
			game_view.ActivateFoldButton(true);
		else
			game_view.ActivateFoldButton(false);
		if((moves & rules.check) != 0)
			game_view.ActivateCheckButton(true);
		else
			game_view.ActivateCheckButton(false);
		if((moves & rules.change) != 0)
		game_view.ActivateChangingButton(true);
		else
			game_view.ActivateChangingButton(false);
	}
	
	/**
	 * Dodaje nowego gracza do widoku.
	 * @param nick Nick nowego gracza
	 * @param id ID nowego gracza
	 */
	public void AddPlayer(String nick, int id)
	{
		game_view.AddPlayer(nick, id);
	}
	
	/**
	 * Pokazuje widok rozgrywki oraz ustawia główny kontroler przyjmujący polecenia serwera na siebie.
	 */
	public void Start()
	{
		client.SetController(this);
		window.ShowGameView();
	}
	
	/**
	 * Chowa widok rozgrywki
	 */
	public void Stop()
	{
		window.HideGameView();
	}
	
	
	//
	//  ObsĹ‚uga przyciskĂłw w rozgrywce.
	//
	
	private void actionButtonCheck(ActionEvent evt)
	{
		rules.Auction(rules.MyID(), 0, 0);
		String output = new String();
		output = "20";
		rules.TurnId(-1);
		client.SendMsg(output); // do serwera
	}
	
	private void actionButtonBet(ActionEvent evt)
	{
		String output = new String();
		String my_bet = game_view.getMyBet();
		int x = Integer.parseInt(my_bet);
		int[] a = new int[3];
		a = rules.getAcc();
		
		if (x > 0 && a[0] > ((a[2] - a[1]) + x))
			if(bet)
			{
				rules.Auction(rules.MyID(), 1, x);//bet
				output = "21;"+x;
				rules.TurnId(-1);
				client.SendMsg(output);
			}
			else
			{
				rules.Auction(rules.MyID(), 2, x);//raise
				output = "22;"+x;
				rules.TurnId(-1);
				client.SendMsg(output);
			}
		else
			JOptionPane.showMessageDialog(null, "Invalid value.");
	}
	private void actionButtonCall(ActionEvent evt)
	{
		String output = new String();
		if(call)
		{
			rules.Auction(rules.MyID(), 3, 0);//call
			output = "23";
			rules.TurnId(-1);
			client.SendMsg(output);
		}	
		else
		{
			rules.Auction(rules.MyID(), 5, 0);//all in
			output = "25";
			rules.TurnId(-1);
			client.SendMsg(output);
		}
	}
	
	private void actionButtonFold(ActionEvent evt)//4
	{
		rules.Auction(rules.MyID(), 4, 0);
		String output = new String();
		output = "24";
		rules.TurnId(-1);
		client.SendMsg(output);
	}
	
	private void actionButtonExit(ActionEvent evt)
	{
		client.SendMsg("9");
		client.StopClient();
		Main_Controller.window.dispose();
	}
	
	private void actionButtonMenu(ActionEvent evt)
	{
		client.StopClient();
		this.Stop();
		Main_Controller.login_ctrl.Start();
	}
	
	private void actionButtonChange(ActionEvent evt)
	{
		int[] c = new int[5];
		c = CardChoose();
		// wymiana kart
		String msgout = "3"+c[0]; // do zrobienia.
		for (int i=1; i<=c[0]; i++)
			msgout += c[i];
		client.SendMsg(msgout);
		rules.TurnId(-1);
		rules.SetChangability(false);
		rules.NextRound();
	}
	
	/** Flaga pokazująca czy można aktualnie zrobić 'call' czy 'all in' */
	private boolean call  = true;
	/** Flaga pokazująca czy można aktualnie zrobić 'bet' czy 'raise' */
	private boolean bet   = true;
	
	private Game_View game_view;
	private Main_View window;
	private Connection_Model transfer;
	private Rules_Model rules;
	private Server_Controller client;

}
