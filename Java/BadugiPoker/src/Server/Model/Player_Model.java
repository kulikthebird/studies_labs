package Server.Model;

public class Player_Model 
{
	/**
	 * Stan konta gracza.
	 */
	private int acc_bal;
	/**
	 * Informacja ile gracz ma w puli aktualnie.
	 */
	private int inpot;
	/**
	 * Flaga oznaczaj¹ca jakie znaczniki ma gracz.
	 */
	private int buttons;
	/**
	 * Nick danego gracza.
	 */
	private String nick;
	/**
	 * Tablica z informacj¹ jakie karty gracz posiada.
	 */
	private String[] cards = new String[4];
	/**
	 * Flaga oznaczaj¹ca czy gracz istnieje.
	 */
	private boolean is_existing = false;
	/**
	 * Informacja ile gracz wymieni³ kart.
	 */
	private int changed_cards;
	/**
	 * Stan gracza.
	 * f- sfoldowa³; o- gra na niego oczekuje; 
	 */
	private char state = 'o';
	/**
	 * Ostatnio wykonany ruch podczas licytacji.
	 * 0- check; 1- bet; 2- raise; 3- call; 4- fold; 5- all-in;
	 */
	private int movemade;
	
	public Player_Model()
	{
	
	}
	
	public int getAccBal()
	{
		return acc_bal;
	}
	public int getInpot()
	{
		return inpot;
	}
	public int getButtons()
	{
		return buttons;
	}
	public String[] getCards()
	{
		return cards;
	}
	public int getCardChanged()
	{
		return changed_cards;
	}
	public boolean getExist()
	{
		return is_existing;
	}
	public String getNick()
	{
		return nick;
	}
	public int getMove()
	{
		return movemade;
	}
	public char getState()
	{
		return state;
	}
	
	
	public void setExist(boolean n)
	{
		is_existing = n;
	}
	
	public void setNick(String n)
	{
		nick = n;
	}
	
	public void setAccBal(int n)
	{
		acc_bal = n;
	}
	public void setInpot(int n)
	{
		inpot = n;
	}
	public void setButtons(int n)
	{
		buttons = n;
	}
	public void setCards(String[] n)
	{
		cards = n;
	}
	public void setCardChanged(int n)
	{
		changed_cards = n;
	}
	public void setMove(int n)
	{
		movemade = n;
	}
	public void setState(char n)
	{
		state = n;
	}
}
