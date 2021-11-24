package Server.Model;

import Server.Model.Player_Model;
import Server.Model.Deck_Model;
import java.util.Random;

public class Rules_Model
{
	public int check 	= 32;
	public int bet 		= 16;
	public int raise 	= 8;
	public int call 	= 4;
	public int fold 	= 2;
	public int all_in 	= 1;
	/**
	 * Przechowuje referencje do modeli graczy po stronie servera.
	 */
	public Player_Model[] player = new Player_Model[6];
	/**
	 * Przechowuje referencje do tali kart.
	 */
	public Deck_Model deck = new Deck_Model();
	/**
	 * Iloœæ graczy w grze.
	 */
	private int player_number = 0;
	/**
	 * Iloœæ graczy bior¹cych udzia³ w rozdaniu.
	 */
	private int player_ingame;
	/**
	 * Numer aktualnego rozdania.
	 */
	private int game_nr = 0;
	/**
	 * Numer aktualnej rundy.
	 */
	private int round_nr;
	/**
	 * Pocz¹tkowy stan konta graczy.
	 */
	private int startbal;
	/**
	 * Najwiekszy zaklad w grze.
	 */
	private int max_bet;
	/**
	 * Zak³ad, z którym rozpoczyna siê runda.
	 */
	private int start_bet;
	/**
	 * Pula w grze.
	 */
	private int pot;
	/**
	 * ID gracza z dealer button'em.
	 */
	private int db_id;
	/**
	 * ID gracza ze small blind'em.
	 */
	private int sb_id;
	/**
	 * ID gracza ze big blind'em.
	 */
	private int bb_id;
	/**
	 * wartoœæ small blind'a
	 */
	private int sb_value;
	/**
	 * wartoœæ big blinda
	 */
	private int bb_value;
	/**
	 * Index gracza rozpoczynaj¹cego turê licytacji.
	 */
	private int startplayerid;
	/**
	 * Zmienna informuj¹ca o stanie gry.
	 * 0- start; 1- start rundy; 2- licytacja; 3- wymiana kart; 4- sprawdzanie kart; 5- wyp³ata pieniêdzy;
	 */
	private int state;
	/**
	 * ID wygranego gracza.
	 */
	private int winnerid;
	/**
	 * Id gracza, który ostatnio podbi³.
	 */
	private int changerid = 9;
	/**
	 * Ilu graczy ma zmienione karty.
	 */
	private int player_served = 0;
	/**
	 * ID gracza, którego kolej jest.
	 */
	private int next_player = startplayerid;
	/**
	 * Liczba graczy, ktorzy sfoldowali.
	 */
	private int folded_players = 0;
	
	/**
	 * Metoda zarz¹dzaj¹ca ca³¹ rozgrywk¹.
	 * @param msg wiadomoœæ o tym co siê sta³o.
	 * @return informacje o stanie gry.
	 */
	public int[] ModelControl(int[] msg)
	{
		if (state == 0)		//Start rozgrywki.
		{
			DeleteLoosers();
			StartGame();
			StartRound();
			
			int[] response = new int[5];
			
			response[0] = 0;
			response[1] = db_id;
			response[2] = sb_id;
			response[3] = bb_id;
			response[4] = startplayerid;
			
			changerid = bb_id;
			
			return response;
		}
		else if (state == 1)//Start rundy.
		{
			StartRound();
			
			int[] response = new int[2];
			response[0] = 1;
			response[1] = startplayerid;
			
			return response;
		}
		else if (state == 2)//Licytacja.
		{
			int move = AvalibleMoves(msg[0]);
			if (msg[1] == 0)
			{
				if ((move & check) == check)
				{
					player[msg[0]].setMove(0);
					
					int[] response = new int[2];
					
					response[1] = NextPlayer();
					
					
					if(changerid == msg[0] && round_nr == 4)
					{
						state = 4;
						response[0] = 6;
						
						return response;
					}
					else if(changerid == msg[0])
					{
						state = 3;
						response[0] = 3;
						
						return response;
					}
					
					PlayersInGame();
					
					response[0] = 2;
					
					return response;
				}
				else
				{
					int[] response = new int[2];
					response[0] = 8;
					response[1] = 1;
					
					return response;
				}
			}
			else if (msg[1] == 1)
			{
				int p;
				if ((move & bet) == bet)
				{
					player[msg[0]].setMove(1);
					max_bet += msg[2];
					p = player[msg[0]].getInpot() + msg[2];
					player[msg[0]].setInpot(p);
					p = player[msg[0]].getAccBal() - msg[2];
					player[msg[0]].setAccBal(p);
					
					changerid = msg[0];
					
					int[] response = new int[2];
					
					response[1] = NextPlayer();
					
					PlayersInGame();
					
					response[0] = 2;
					
					return response;
				}
				else
				{
					int[] response = new int[2];
					response[0] = 8;
					response[1] = 1;
					
					return response;
				}
			}
			else if (msg[1] == 2)
			{
				int p;
				if ((move & raise) == raise)
				{
					changerid = msg[0];
					
					player[msg[0]].setMove(2);
					max_bet += msg[2];
					p = player[msg[0]].getAccBal() - (max_bet - player[msg[0]].getInpot());
					player[msg[0]].setAccBal(p);
					player[msg[0]].setInpot(max_bet);
					
					
					int[] response = new int[2];
					response[0] = 2;
					response[1] = NextPlayer();
					
					PlayersInGame();
					
					return response;
				}
				else
				{
					int[] response = new int[2];
					response[0] = 8;
					response[1] = 1;
					
					return response;
				}
			}
			else if (msg[1] == 3)
			{
				int p;
				if ((move & call) == call)
				{
					player[msg[0]].setMove(3);
					p = player[msg[0]].getAccBal() - (max_bet - player[msg[0]].getInpot());
					player[msg[0]].setAccBal(p);
					player[msg[0]].setInpot(max_bet);
					
					int[] response = new int[2];
					response[1] = NextPlayer();
					
					
					PlayersInGame();
					
					response[0] = 2;
					
					return response;
				}
				else
				{
					int[] response = new int[2];
					response[0] = 8;
					response[1] = 1;
					
					return response;
				}
			}
			else if (msg[1] == 4)
			{
				if ((move & fold) == fold)
				{
					player[msg[0]].setMove(4);
					player[msg[0]].setState('f');
					
					int[] response = new int[2];
					
					folded_players++;
					if (folded_players == player_number)
					{
						folded_players = 0;
						state = 0;
						response[0] = 10;
						return response;
					}
					
					if (folded_players == player_number-1)
					{
						folded_players = 0;
						state = 0;
						response[0] = 7;
						for (int i=0; i<player_number; i++)
							if (player[i].getState() != 'f')
								response[1] = i;
						return response;
					}
					
					response[1] = NextPlayer();

					
					PlayersInGame();
					
					response[0] = 2;
					
					return response;
				}
				else
				{
					int[] response = new int[2];
					response[0] = 8;
					response[1] = 1;
					
					return response;
				}
			}
			else if (msg[1] == 5)
			{
				int p;
				if ((move & all_in) == all_in)
				{
					player[msg[0]].setMove(5);
					p = player[msg[0]].getAccBal() + player[msg[0]].getInpot(); 
					player[msg[0]].setInpot(p);
					player[msg[0]].setAccBal(0);
					
					int[] response = new int[2];
					response[1] = NextPlayer();
					
					if(changerid == msg[0] && round_nr == 4)
					{
						state = 4;
						response[0] = 6;
						
						return response;
					}
					else if(changerid == msg[0])
					{
						state = 3;
						response[0] = 3;
						
						return response;
					}
					
					PlayersInGame();
					
					response[0] = 2;
					
					return response;
				}
				else
				{
					int[] response = new int[2];
					response[0] = 8;
					response[1] = 1;
					
					return response;
				}
			}
		}
		else if (state == 3)//Wymiana kart.
		{
			int[] p = new int[msg[1]];
			for (int i=0; i<msg[1]; i++) // tworzy tablicê z indeksami kart do wymiany.
				p[i] = msg[i+2];
			player[msg[0]].setCards(deck.ChangeCards(player[msg[0]].getCards(), p, msg[1]));
			player[msg[0]].setCardChanged(msg[1]);
			
			
			
			int[] response = new int[3];
			response[0] = 4;
			response[1] = msg[0]; // id gracza.
			response[2] = NextPlayer();
			
			player_served++;
			
			if (player_served == player_ingame)
			{
				player_served = 0;
				StartRound();
				response[0] = 5;
				response[2] = startplayerid;
			}

			changerid = (startplayerid+player_ingame-1) % player_ingame;
			return response;
		}
		else if (state == 4)//Sprawdzenie kart.
		{
			int x;
			int w=0;
			int[] r = new int[2];
			
			for (int i=1; i<player_number; i++)
			{
				if (player[w].getState() == 'f');
				else
				{
					if (player[i].getState() == 'f');
					else
					{
						x = deck.getBetter(player[w].getCards(), player[i].getCards());
						if (x == 0);
						else if (x == 1)
						{
							w = i;
							r[0] = 0;
						}
						else
						{
							r[0] = 1;
							r[1] = i;
						}
					}
				}
			}
			
			if(r[0] == 1)
				winnerid = 6;
			else
				winnerid = w;
		
			int[] response = new int[3];
			response[0] = 8;
			response[1] = 6;
			response[2] = 6;
			
			if (winnerid < 6)
			{
				player[winnerid].setAccBal(player[winnerid].getAccBal() + pot);
				response[1] = winnerid;
			}
			
			DeleteLoosers();
			
			int h=0;
			for (int i=0; i<player_number; i++)
			{
				if (player[i].getExist() == true)
					h++;
			}
			if (h <= 1)
			{
				response[2] = 0;
				state = 5;
			}
			else
			{
				pot = 0;
				state = 0;
			}
			
			
			return response;
		}
		else if (state == 5)
		{
			int[] response = new int[1];
			response[0] = 99;
			return response;
		}
		return new int[1];
	}
	
	/**
	 * Kasuje przegranych.
	 */
	public void DeleteLoosers()
	{
		for (int i=0; i<player_number; i++)
		{
			if (player[i].getAccBal() <= 0 && player[i].getExist() == true)
				DeletePlayer(i);
		}
	}
	
	/**
	 * Metoda rozpoczynaj¹ca rozdanie, u¿ywa metod w celu rozdania button'a i blindów,
	 * potasowania talii oraz rozdania graczom po 4 karty.
	 */
	public void StartGame()
	{
		round_nr = 0;
		start_bet = 0;
		max_bet = 0;
		winnerid = 8;
		game_nr++;
		folded_players = 0;
		
		if (game_nr == 1)
			GiveButtons(-1);
		else
			GiveButtons(db_id);
		
		deck.ResetDeck();
		deck.Shuffle(52);
		
		for (int i=0; i<player_number; i++)
		{
			player[i].setState('o');
			UpdateCards(i, deck.GiveCards(4));
		}
		
		state = 1;
	}
	
	/**
	 * Rozpoczêtcie kolejnej rundy rozdania.
	 */
	public void StartRound()
	{
		round_nr++;
		
		int id = sb_id;
		
		PlayersInGame();
		
		if (round_nr == 1)
		{
			start_bet = 0;
			id = (bb_id+1)%player_number;
			while (player[id].getState() == 'f')
				id = (id+1)%player_number;
			startplayerid = id;
			next_player = startplayerid; 
			changerid = startplayerid-1;
		}
		else
		{
			start_bet = max_bet;
			id = sb_id;
			while (player[id].getState() == 'f')
				id = (id+1)%player_number;
			startplayerid = sb_id;
			next_player = startplayerid;
			changerid = startplayerid-1;
		}
		
		state = 2;
	}
	
	/**
	 * Metoda licz¹ca ile graczy wci¹¿ ma wp³yw na rozgrywkê, tj. nie sfoldowali.
	 */
	public void PlayersInGame()
	{
		int j = 0;
		for (int i=0; i<player_number; i++)
		{
			if (player[i].getState() != 'f')
				j++;
		}
		player_ingame = j;
	}
	
	/**
	 * Metoda rozdaj¹ca button i blind'y.
	 * @param id - Gdy id == -1 oznacza, ¿e jest to pierwsza runda. PóŸniej jest to id gracza
	 * z dealer button'em w ostatnim rozdaniu.
	 */
	public void GiveButtons(int id)
	{
		int dbid, sbid, bbid;
		if (id == -1)
		{
			Random rand = new Random();
			dbid = rand.nextInt(player_number);
		}
		else
			dbid = (1+id)%player_number;
		sbid = (dbid+1)%player_number;
		bbid = (sbid+1)%player_number;
		SetButtons(dbid, sbid, bbid);
		
	}
	
	/**
	 * Metoda zwraca id gracza, który ma dealer button.
	 * @return
	 */
	public int GetDButton()
	{
		return db_id;
	}
	
	/**
	 * Metoda zwraca id gracza, który ma small blind.
	 * @return
	 */
	public int GetSBlind()
	{
		return sb_id;
	}
	
	/**
	 * Metoda zwraca id gracza, który ma big blind.
	 * @return
	 */
	public int GetBBlind()
	{
		return bb_id;
	}
	
	/**
	 * Tworzy gracza i przypisuje mu odpowiednie id.
	 * @param nick tworzonego gracza.
	 * @return jego nowe id.
	 */
	public int CreatePlayer(String nick)
	{
		int id = player_number++;
		if (player_number < 7)
			AddPlayer(nick, id);
		else
			id = -1;
		return id;
	}
	
	/**
	 * Dodaje gracza do modelu zarzadzania gra.
	 * 
	 * @param nick Nick dodawanego gracza
	 * @param id Id dodawanego gracza
	 */
	public void AddPlayer(String nick, int id)
	{
		player[id] = new Player_Model();
		player[id].setExist(true);
		player[id].setNick(nick);
		player[id].setAccBal(startbal);
	}
	
	/**
	 * Usuwa gracza.
	 * @param id Id gracza do usuniêcia.
	 */
	public void DeletePlayer(int id)
	{
		--player_ingame;
		player[id].setExist(false);
		player[id].setState('f');
	}
	
	
	
	/**
	 * Zwraca flagi z mo¿liwymi dzialaniami gracza.
	 * 012345 => 0- check; 1- bet; 2- raise; 3- call; 4- fold; 5- all-in;
	 * oznaczane 0 lub 1;
	 * @return Mo¿liwe dzialania.
	 */
	public int AvalibleMoves(int id)
	{
		int flag = 0;
		if (player[id].getState() != 'f')
		{
			flag = 2;			// Fold zawsze dostêpne, flaga = 000010.
			if (player[id].getInpot() == max_bet)
				flag |= 32;	//check dostêpne.
			if (start_bet == max_bet && player[id].getAccBal() > 0)
				flag |= 16;		//bet dostêpne.
			if (player[id].getInpot() <= max_bet && player[id].getAccBal() > 0 && start_bet != max_bet)
				flag |= 8;		//raise dostêpne.
			if (player[id].getInpot() < max_bet && (max_bet-player[id].getInpot()) <= player[id].getAccBal())
				flag |= 4;		//call dostêpne.
			if (player[id].getAccBal() < max_bet)
				flag |= 1;			//all-in dostêpne.
		}
		
		return flag;
	}
	
	/**
	 * Ustawia pod parametry button'a i blind'ów id graczy, którzy je posiadaj¹.
	 * 
	 * @param dbid - id gracza, który otrzyma³ dealer button.
	 * @param sbid - id gracza, który otrzyma³ small blind.
	 * @param bbid - id gracza, który otrzyma³ big blind.
	 */
	public void SetButtons(int dbid, int sbid, int bbid)
	{
		db_id = dbid;
		sb_id = sbid;
		player[sb_id].setAccBal(player[sb_id].getAccBal() - sb_value);
		player[sb_id].setInpot(sb_value);
		bb_id = bbid;
		player[bb_id].setAccBal(player[bb_id].getAccBal() - bb_value);
		player[bb_id].setInpot(bb_value);
		max_bet = bb_value;
	}
	
	/**
	 * Metoda ustawiaj¹ca jak¹ wartoœæ maj¹ mieæ small i big blind.
	 * @param sbv
	 * @param bbv
	 */
	public void setBlinds(int sbv, int bbv)
	{
		sb_value = sbv;
		bb_value = bbv;
	}
	
	/**
	 * Zale¿nie od parametru metoda zwraca pocz¹tkowy stan konta, wartoœæ small lub big blinda.
	 * @param x 0, 1 lub 2
	 * @return wartoœæ.
	 */
	public int valueOf(int x)
	{
		if (x == 0)
			return startbal;
		else if (x == 1)
			return sb_value;
		else if (x == 2)
			return bb_value;
		else
			return -1;
	}
	
	/**
	 * Metoda ustawiaj¹ca stan startowy konta graczy dla rozgrywki.
	 * @param bal
	 */
	public void setBal(int bal)
	{
		startbal = bal;
	}
	
	/**
	 * Aktualizuje karty w rece gracza.
	 * @param id ID gracza, któremu zmieniamy karty.
	 * @param card Karty gracza (w liczbie 4).
	 */
	private void UpdateCards(int id, String[] card)
	{
		player[id].setCards(card);
	}
	
	/**
	 * Metoda zwracaj¹ca karty jakie ma dany gracz.
	 * @param id
	 * @return
	 */
	public String[] SendCards(int id)
	{
		return player[id].getCards();
	}
	
	/**
	 * Zwraca podastowe iformacje o graczach.
	 * @return talibce id;nick;id;nick...
	 */
	public String getPlayersInfo(int id)
	{
		String info = "";
		for (int i=0; i<player_number; i++)
			if (i != id)
				info = info+i+";"+player[i].getNick()+";";
		info = info+"9";
		return info;
	}
	
	/**
	 * Metoda zwraca nick gracza o podanym id.
	 * @param id
	 * @return
	 */
	public String getPlayerNick(int id)
	{
		return player[id].getNick();
	}
	
	/**
	 * Metoda okreœla, którego gracza jest teraz kolei.
	 * @return zwraca jego id.
	 */
	public int NextPlayer()
	{
		next_player = (next_player+1)%player_number;
		while (player[next_player].getState() == 'f')
			next_player = (next_player+1)%player_number;
		return next_player;
	}
	
	/**
	 * Metoda zwraca ile jest graczy w grze.
	 * @return
	 */
	public int PlayerNumber()
	{
		return player_number;
	}
	
	public int[] BotInfo(int id)
	{
		int[] tab = new int[3];
		tab[0] = player[id].getAccBal();
		tab[1] = player[id].getInpot();
		tab[2] = max_bet;
		return tab;
	}
}