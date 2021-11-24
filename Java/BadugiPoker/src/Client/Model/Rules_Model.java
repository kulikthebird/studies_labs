package Client.Model;



/**
 * 
 * @author Staœ i Nel
 *
 */
public class Rules_Model 
{
	public final int change 	= 64;
	public final int check 		= 32;
	public final int bet 		= 16;
	public final int raise 		= 8;
	public final int call 		= 4;
	public final int fold		= 2;
	public final int all_in 	= 1;
	
	/**
	 * Przechowuje referencje do modeli graczy po stronie klienta.
	 */
	public Player_Model[] player = new Player_Model[6];
	/**
	 * Ilosc graczy w grze.
	 */
	private int player_number=0;
	/**
	 * Id gracza powiazanego z tym klientem.
	 */
	private int my_id;
	/**
	 * nick gracza.
	 */
	private String my_nick;
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
	private int pot=0;
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
	 * Wartoœæ small blind'a.
	 */
	private int sb_value;
	/**
	 * Wartoœæ big blind'a.
	 */
	private int bb_value;
	/**
	 * ID gracza rozpoczynaj¹cego rundê.
	 */
	private int start_id;
	/**
	 * Czy gracz mo¿e wymieniæ karty.
	 */
	private boolean ifchange = false;
	/**
	 * Zmienna informuj¹ca czy mo¿emy siê ruszyæ.
	 */
	private int turn_id = start_id;
	/**
	 * Informuje czy pokazywaæ karty wszystkich graczy.
	 */
	private boolean showall = false;
	/**
	 * Pocz¹tkowy stan konta dla graczy.
	 */
	private int acc_bal;
	
	
	/**
	 * Metoda ustawia pocz¹tkowe parametry dla stanu kont graczy oraz wartoœci small i big blind.
	 * @param acc
	 * @param sbvalue
	 */
	public void StartParam(int acc, int sbvalue, int bbvalue)
	{
		acc_bal = acc;
		sb_value = sbvalue;
		bb_value = bbvalue;
	}
	
	/**
	 * Metoda ustawiaj¹ca startowe parametry dla gry.
	 */
	public void GameStart()
	{
		max_bet = 0;
		start_bet = 0;
		pot = 0;
		player[sb_id].setAccBal(player[sb_id].getAccBal() - sb_value);
		player[sb_id].setInpot(sb_value);
		player[bb_id].setAccBal(player[bb_id].getAccBal() - bb_value);
		player[bb_id].setInpot(bb_value);
		pot += 3*sb_value;
		max_bet = bb_value;
	}
	
	public void NextRound()
	{
		start_bet=max_bet;
	}
	
	public void NextGame()
	{
		pot = 0;
		for (int i=0; i<player_number; i++)
			player[i].setInpot(0);
	}
	
	/**
	 * Dodaje gracza do modelu zarzadzania gra.
	 * 
	 * @param nick Nick dodawanego gracza
	 * @param id Id dodawanego gracza
	 */
	public void AddPlayer(String nick, int id)
	{
		++player_number;
		player[id] = new Player_Model();
		player[id].setExist(true);
		if (my_id == id)
			player[id].setNick(my_nick);
		else
			player[id].setNick(nick);
		player[id].setAccBal(acc_bal);
	}
	
	/**
	 * Usuwa gracza.
	 * 
	 * @param id Id gracza
	 */
	public void DeletePlayer(int id)
	{
		player[id].setExist(false);
	}
	
	/**
	 * Zwraca flagi z mo¿liwymi dzialaniami gracza.
	 * 012345 => 0- check; 1- bet; 2- raise; 3- call; 4- fold; 5- all-in;
	 * oznaczane 0 lub 1;
	 * @return Mo¿liwe dzialania.
	 */
	public int AvalibleMoves()
	{
		int flag;
		if (turn_id == my_id)
		{
			if ( ifchange == true)
				flag = 64;
			else
			{
				flag = 0;
				if (player[my_id].getFold() == false)
				{
					flag = 2;			// Fold zawsze dostêpne, flaga = 000010.
					if ( ifchange == true)
						flag |= 64; //gracz ma wymieniæ karty.
					if (player[my_id].getInpot() == max_bet)
						flag |= 32;	//check dostêpne.
					if (start_bet == max_bet && player[my_id].getAccBal() > 0)
						flag |= 16;		//bet dostêpne.
					if (player[my_id].getInpot() <= max_bet && player[my_id].getAccBal() > 0 && start_bet != max_bet)
						flag |= 8;		//raise dostêpne.
					if (player[my_id].getInpot() < max_bet && (max_bet-player[my_id].getInpot()) <= player[my_id].getAccBal())
						flag |= 4;		//call dostêpne.
					if (player[my_id].getAccBal() < max_bet)
						flag |= 1;			//all-in dostêpne.
				}
			}
		}
		else
			flag = 128; // gdy nie mozemy siê ruszyæ.
		return flag;
	}
	
	public void SetChangability(boolean x)
	{
		ifchange = x;
	}
	
	/**
	 * Ustawia pod parametry button'a i blind'ów id graczy, którzy je posiadaj¹.
	 * 
	 * @param dbid - id gracza, który otrzyma³ dealer button.
	 * @param sbid - id gracza, który otrzyma³ small blind.
	 * @param bbid - id gracza, który otrzyma³ big blind.
	 */
	public void SetButtons(int dbid, int sbid, int bbid, int startid)
	{
		db_id = dbid;
		sb_id = sbid;
		bb_id = bbid;
		start_id = startid;
	}
	
	/**
	 * Aktualizuje karty gracza.
	 * @param id gracza, któremu zmieniamy karty.
	 * @param card - tabela nowych kart
	 * @param n - iloœ wymienionych kart
	 */
	public void UpdateCards(int id, String[] card, int n)
	{
		player[id].setCards(card);
		player[id].setCardChanged(n);
	}
	
	public int MyID()
	{
		return my_id;
	}
	
	public void setMyID(int id)
	{
		my_id = id;
	}
	
	public void SetMyNick(String nick)
	{
		my_nick = nick;
	}
	
	/**
	 * Wykonuje ruch podanego gracza w fazie licytacji.
	 * @param id gracza, który wykona³ ruch
	 * @param action - rodzaj ruchu
	 * @param value - wartoœæ
	 */
	public void Auction(int id, int action, int value)
	{
		//int move = AvalibleMoves(id);
		int p;
		if (action == 0) // check
		{
			
		}
		else if (action == 1) // bet
		{
			max_bet += value;
			pot += max_bet - player[id].getInpot();
			p = player[id].getInpot() + value;
			player[id].setInpot(p);
			p = player[id].getAccBal() - value;
			player[id].setAccBal(p);
		}
		else if (action == 2) // raise
		{
			max_bet += value;
			pot += max_bet - player[id].getInpot();
			p = player[id].getAccBal() - (max_bet - player[id].getInpot());
			player[id].setAccBal(p);
			player[id].setInpot(max_bet);
			
		}
		else if (action == 3) // call
		{
			p = player[id].getAccBal() - (max_bet - player[id].getInpot());
			pot += (max_bet - player[id].getInpot());
			player[id].setAccBal(p);
			player[id].setInpot(max_bet);
		}
		else if (action == 4) // fold
		{
			player[id].setFold(true);
		}
		else if (action == 5) // all-in
		{
			pot += player[id].getAccBal();
			p = player[id].getAccBal() + player[id].getInpot(); 
			player[id].setInpot(p);
			player[id].setAccBal(0);
		}
	}
	
	public String Winner(int id)
	{
		player[id].setAccBal(pot + player[id].getAccBal());
		String nick = player[id].getNick();
		return nick;
	}
	
	public void DeleteLoosers()
	{
		for (int i=0; i<player_number; i++)
		{
			if (player[i].getAccBal() == 0 && player[i].getExist() == true)
				DeletePlayer(i);
		}
	}
	
	/**
	 * Metoda, która zwraca tablicê intów zawieraj¹c¹ informacje o graczach.
	 * @return [iloœæ graczy][pula][najwy¿szy zak³ad][[balance], [inpot], [changed cards]] x ilosc graczy
	 */
	public int[] getPlayersState()
	{
		int[] result = new int[666];
		int index=4;
		
		result[0] = player_number;
		result[1] = pot;
		result[2] = max_bet;
		result[3] = turn_id;
		
		for (int i=0; i<player_number; i++)
		{
			result[index++] = i;
			result[index++] = player[i].getAccBal();
			result[index++] = player[i].getInpot();
			result[index++] = player[i].getCardChanged();
		}
		
		return result;
	}
	
	public int[] getCards()
	{
		int[] result = new int[676]; // <3
		int[] x = new int [4];
		int index=1;
		String[] c = new String[4];
		String c0 = new String();
		
		if (pot == 0)
		{
			result[0] = -1;
			return result;
		}
		if (showall == true)
		{
			result[0] = player_number;
			for (int i=0; i<player_number; i++)
			{
				c = player[i].getCards();
				c0 = c[0]+c[1]+c[2]+c[3];
				x = TranslateModel2View(c0);
				result[index++] = i;
				result[index++] = x[0];
				result[index++] = x[1];
				result[index++] = x[2];
				result[index++] = x[3];
			}
		}
		else
		{
			c = player[my_id].getCards();
			c0 = c[0]+c[1]+c[2]+c[3];
			x = TranslateModel2View(c0);
			result[0] = 1;
			result[1] = my_id;
			result[2] = x[0];
			result[3] = x[1];
			result[4] = x[2];
			result[5] = x[3];
		}
		// widze to tak (sorry za brak polskich znakow, juz po 11 listopada koniec tego dobrego)
		// [0] = -2 to chowa karty wszystkich graczy
		// [0] = -1 chowa karty wszystkich oprócz naszych kart (tego klienta)
		// [0] = 1 <- pokazujesz tylko moje karty
		// [0] > 1 pokazujesz karty moje i innych graczy (dajesz tu ilosc graczy, ktorych karty pokazujesz)
		// [ [karta], [karta], [karta], [karta] ] x ilosc graczy
		
		
		
		return result;
	}
	
	public int getBigID()
	{
		return bb_id;
	}
	
	public int getSmallID()
	{
		return sb_id;
	}
	
	public int getDealerID()
	{
		return db_id;
	}
	
	public int[] TranslateModel2View(String cards)
	{
		int[] result = new int[4];
		
		for(int i=0; i<4; i++)
		{
			char card = cards.charAt(i*2+1);
			switch(cards.charAt(i*2))
			{
				case 'C':
					result[i] = 0;
				break;
				case 'K':
					result[i] = 13;
				break;
				case 'T':
					result[i] = 26;
				break;
				case 'P':
					result[i] = 39;
				break;
			}
			if(card >= '2' && card <= '9')
				result[i] += card - '0' - 1;
			else
				switch(card)
				{
					case 'A':
						result[i] += 8;
					break;
					case 'B':
						result[i] += 9;
					break;
					case 'C':
						result[i] += 10;
					break;
					case 'D':
						result[i] += 11;
					break;
					case '1':
						result[i] += 12;
					break;
				}	
		}
		return result;
	}
	
	public void TurnId(int x)
	{
		turn_id = x;
	}
	
	public int WhoseTurn()
	{
		return turn_id;
	}
	
	public void setShowAll(boolean n)
	{
		showall = n;
	}
	
	public int[] getAcc()
	{
		int[] a = new int[3];
		a[0] = player[my_id].getAccBal();
		a[1] = player[my_id].getInpot();
		a[2] = max_bet;
		return a;
	}
}