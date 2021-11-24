package Client.Model;

import javax.swing.JOptionPane;

import Client.Controller.Game_Controller;
import Client.Exceptions.*;

/**
 * Model połączenia obsługujący komunikaty, które przesyła serwer.
 * @author Tomek
 *
 */
public class Connection_Model 
{
	/**
	 * Tworzy model połączeń.
	 * @param rules  Referencja do modelu Rules
	 * @param game_ctrl  Referencja do kontrolera gry
	 */
	public Connection_Model(Rules_Model rules, Game_Controller game_ctrl)
	{
		this.rules = rules;
		this.game_ctrl = game_ctrl;
	}
	
	/**
	 * Obsługuje komunikat od serwera.
	 * @param msg  komunikat od serwera.
	 * @throws GameSyncException
	 * @throws ConnectException
	 */
	public void ReceiveMsg(String msg) throws GameSyncException, ConnectException
	{
		switch(msg.charAt(0))
		{
			case '1':	// logowanie
				if(msg.charAt(1) == '8') // nieudane logowanie.
					throw new GameSyncException();
				else if(msg.charAt(1) == '9')
				{
					int bal, small, big, my_id;
					String[] data = msg.split(";");
					if(data.length < 6)
						throw new ConnectException(ConnectException.Communication);
					bal		= Integer.parseInt(data[1]);
					small 	= Integer.parseInt(data[2]);
					big 	= Integer.parseInt(data[3]);
					my_id 	= Integer.parseInt(data[4]);
					
					int[] id = new int[6];
					String[] nick = new String[6];
					int index = 0;
					game_ctrl.setMyID(my_id);
					rules.setMyID(my_id);
					rules.StartParam(bal, small, big); // Dodaj bal small i big do rulesa
					rules.AddPlayer("nicki", my_id);
					
					for (int i=5; i<11; i++)
					{
						if (data[i].charAt(0) == '9')
								break;
						else if (i%2 == 1)
							id[index] = Integer.parseInt(data[i]);
						else
						{
							nick[index++] = data[i];
							rules.AddPlayer(nick[index-1], id[index-1]);
							game_ctrl.AddPlayer(nick[index-1], id[index-1]);
						}
					}
				}
				else if(msg.charAt(1) == '4')
				{
					int id = Character.getNumericValue(msg.charAt(2));
					rules.DeletePlayer(id);
				}
			break;
			case '2':	// licytacja
				if(msg.charAt(1) == '8')
					throw new GameSyncException();
				int xd = Character.getNumericValue(msg.charAt(1));
				rules.TurnId(xd);
			break;
			case '3':
				if(msg.charAt(1) == '8')
					throw new GameSyncException();
				xd = Character.getNumericValue(msg.charAt(1));
				rules.TurnId(xd);
				
				if (xd == rules.MyID())
				{
					rules.SetChangability(true);
				}
			break;
			case '4':
				switch(msg.charAt(1))
				{
					case '0':		//rozpocz�cie gry, rozdanie button�w oraz wybranie gracza rozpoczynaj�cego.
						int dbid = Character.getNumericValue(msg.charAt(2));
						int sbid = Character.getNumericValue(msg.charAt(3));
						int bbid = Character.getNumericValue(msg.charAt(4));
						int startid = Character.getNumericValue(msg.charAt(5));
						
						rules.SetButtons(dbid, sbid, bbid, startid);
						
						rules.setShowAll(false);
						
						rules.GameStart();
					break;
					case '1':		//odebranie informacji kto co zrobi� podczas licytacji i za ile.
						int id = Character.getNumericValue(msg.charAt(2));
						int move = Character.getNumericValue(msg.charAt(3));
						
						if (move == 0 || move == 3 || move == 4 || move == 5)
							rules.Auction(id, move, 0);
						else
						{
							String[] value = msg.split(";");
							int p = Integer.parseInt(value[1]);
							rules.Auction(id, move, p);
						}
					break;
					case '2': // wymiana kart.
						id = Character.getNumericValue(msg.charAt(2));
						String[] cards = new String[4];
						
						cards[0] = "" + msg.charAt(3) + msg.charAt(4);
						cards[1] = "" + msg.charAt(5) + msg.charAt(6);
						cards[2] = "" + msg.charAt(7) + msg.charAt(8);
						cards[3] = "" + msg.charAt(9) + msg.charAt(10);
						
						int n = Character.getNumericValue(msg.charAt(11));
						
						rules.UpdateCards(id, cards, n);
					break;
					case '3': // zwyci�sca jednej rozgrywki.
						id = Character.getNumericValue(msg.charAt(2));
						rules.setShowAll(true);
						if ( id == 6)
						{
							msg = "Draw, pot goes to the casino.";
							//JOptionPane.showMessageDialog(null, msg);
						}
							// remis!!!
						else
						{
							if (id == rules.MyID())
							{
								msg = "You won!";
								String oko = rules.Winner(id);
							}
							else
								msg = rules.Winner(id)+" has won this round.";
							//JOptionPane.showMessageDialog(null, msg);
						}
							// wy�wietl id jako zwyci�zc�.
						rules.DeleteLoosers();
					break;
					case '4': // Gracz odpadaj�cy z gry lub zwyci�sca za�ej gry.
						id = Character.getNumericValue(msg.charAt(2));
						if (msg.charAt(3) == '0')
							rules.DeletePlayer(id);
						else
						{
							msg = rules.Winner(id)+" has won the game.";
							//JOptionPane.showMessageDialog(null, msg);
						}
							// wy�wietla zwyci�zc� ca�ej gry.
					break;
					case '8': // dodanie nowopowsta�ego gracza.
						String[] msgarray = msg.split(";");
						id = Integer.parseInt(msgarray[1]);
						
						String nick;
						nick = msgarray[2];
						
						rules.AddPlayer(nick, id);
						game_ctrl.AddPlayer(nick, id);
					break;
				}
			break;
		}
	}
	
	private Game_Controller game_ctrl;
	private Rules_Model rules;
}