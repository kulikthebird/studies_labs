package Client.Model;

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
	 * Flaga oznaczaj?ca jakie znaczniki ma gracz.
	 */
	private int buttons;
	/**
	 * Nick danego gracza.
	 */
	private String nick;
	/**
	 * Tablica z informacj? jakie karty gracz posiada.
	 */
	private String[] cards = new String[4];
	/**
	 * Flaga oznaczaj?ca czy gracz istnieje.
	 */
	private boolean is_existing = false;
	/**
	 * Informacja ile gracz wymieni? kart.
	 */
	private int changed_cards = 0;
	/**
	 * Stan gracza.
	 * f- sfoldowa?; o- gra na niego oczekuje; 
	 */
	private boolean iffold = false;
	
	
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
	public boolean getFold()
	{
		return iffold;
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
	public void setFold(boolean n)
	{
		iffold = n;
	}
	
}
