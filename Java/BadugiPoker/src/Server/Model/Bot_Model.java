package Server.Model;

public class Bot_Model
{
	private int my_id;
	private String[] c = new String[4];
	private int cchange = 0;
	private Rules_Model rules;
	private boolean exist = true;
	
	public Bot_Model(Rules_Model rules)
	{
		this.rules = rules;
	}
	
	public void setMyID(int id)
	{
		my_id = id;
	}
	
	public int getID()
	{
		return my_id;
	}

	public void setCards(String[] cards)
	{
		c = cards;
	}
		
	public String MoveBot(String msg)
	{
		if (exist == false)
			return "jestem martwy";
			
		
		c = rules.SendCards(my_id);
		int[] tab = new int[3];
		int bdg = 4;
		int k=1, i0=0, m=3;
		String[] c0 = new String[4];
		String[] p = new String[12];
		String[] tochange = new String[4];
		int tochangeid=0;
		
		tab = rules.BotInfo(my_id);
		int acc_bal = tab[0];
		int my_bet = tab[1];
		int max_bet = tab[2];

		String output = "";
		
		c0 = c;
		
		switch(msg.charAt(0))
		{
		case '1':
		break;
		case '2':
			while (k != 0)
			{	
				k=0;
				for (int i=m; i>0; i--)
				{
					for (int j=i-1; j>=0; j--)
					{
						if (c0[i].charAt(0) == c0[j].charAt(0) || c0[i].charAt(1) == c0[j].charAt(1))
						{
							p[i0++] = c0[i];
							p[i0++] = c0[j];
						}
					}
				}
				
				for (int i=i0-1; i>0; i--)
				{
					for (int j=i-1; j>=0; j--)
					{
						if (p[i] == p[j])
						{
							for (int l=0; l<4; l++)
							{
								if (p[i] == c0[l])
								{
									bdg--;
									tochange[tochangeid++] = c0[i];
									c0[i] = c0[bdg];
									c0[bdg] = "0";
									m--;
								}
							}
							p[i] = "0";
							k++;
						}
					}
					p[i]="0";
				}
				p[0]="0";
				i0=0;
			}
			
			m=bdg;
			
			for (int i=m-1; i>0; i--)
			{
				for (int j=i-1; j>=0; j--)
				{
					if (c0[i].charAt(0) == c0[j].charAt(0) || c0[i].charAt(1) == c0[j].charAt(1))
					{
						bdg--;
						tochange[tochangeid++] = c0[i];
						c0[i] = c0[bdg];
						c0[bdg] = "0";
						break;
					}
				}
			}
			
			if (Character.getNumericValue(msg.charAt(1)) == my_id)
			{
				//Licytacja.
				if (tochangeid == 4 || tochangeid == 3)
				{
					if (cchange == 3)
						output = "24"; //fold!;
					else if (cchange == 2)
					{
						if (max_bet == my_bet)
							output = "20";//check;
						else if (max_bet - my_bet <= 20)
						{
							if (acc_bal < 20)
								output = "24";
							else
								output = "23";//call;
						}
						else
							output = "24";//fold;
					}
					else
					{
						if (max_bet == my_bet)
							output = "20";//check;
						else if (max_bet - my_bet <= 30)
						{
							if (acc_bal < 30)
								output = "24";
							else
								output = "23";// call;
						}
						else
							output = "24";//fold;
					}
				}
				else if (tochangeid == 2)
				{
					if (cchange == 3)
					{
						if (max_bet == my_bet)
							output = "20";//check;
						else if (max_bet - my_bet <= 30)
						{
							if (acc_bal < 30)
								output = "24";
							else
								output = "23";//call;
						}
						else
							output = "24";//fold;
					}
					else 
					{
						if (max_bet == my_bet)
							output = "20";//check;
						else if (max_bet - my_bet <= 20)
						{
							if (acc_bal < 20)
								output = "24";
							else
								output = "22;10";//raise 10;
						}
						else if (max_bet - my_bet <= 40)
						{
							if (acc_bal < 40)
								output = "24";
							else
								output = "23";//call;
						}
						else
							output = "24";//fold;
					} 
				}
				else if (tochangeid == 1)
				{
					if (cchange == 3)
					{
						if (max_bet == my_bet)
							output = "20";//check;
						else if (max_bet - my_bet <= 50)
						{
							if (acc_bal < 30)
								output = "25";
							else
								output = "23";//call ;
						}
						else
							output = "24";//fold;
					}
					else 
					{
						if (max_bet == my_bet)
							output = "20";//check;
						else if (max_bet - my_bet <= 40)
						{
							if (acc_bal < 40)
								output = "25";
							else
								output = "22;30";//raise 30;
						}
						else
						{
							if (acc_bal < max_bet - my_bet)
								output = "25";
							else
								output = "23";//call;
						}
					}
				}
				else if (tochangeid == 0)
				{
					if (max_bet == my_bet)
						output = "20";//check;
					else if (max_bet - my_bet <= 50)
					{
						if (acc_bal < 50)
							output = "25";
						else
							output = "22;30";//raise 30;
					}
					else
					{
						if (acc_bal < max_bet - my_bet)
							output = "25";
						else
							output = "23";//call;
					}
				}
			}
		break;	
		case '3':
			while (k != 0)
			{	
				k=0;
				for (int i=m; i>0; i--)
				{
					for (int j=i-1; j>=0; j--)
					{
						if (c0[i].charAt(0) == c0[j].charAt(0) || c0[i].charAt(1) == c0[j].charAt(1))
						{
							p[i0++] = c0[i];
							p[i0++] = c0[j];
						}
					}
				}
				
				for (int i=i0-1; i>0; i--)
				{
					for (int j=i-1; j>=0; j--)
					{
						if (p[i] == p[j])
						{
							for (int l=0; l<4; l++)
							{
								if (p[i] == c0[l])
								{
									bdg--;
									tochange[tochangeid++] = c0[i];
									c0[i] = c0[bdg];
									c0[bdg] = "0";
								}
							}
							p[i] = "0";
							k++;
							m--;
						}
					}
					p[i]="0";
				}
				p[0]="0";
				i0=0;
			}
			
			m=bdg;
			
			for (int i=m-1; i>0; i--)
			{
				for (int j=i-1; j>=0; j--)
				{
					if (c0[i].charAt(0) == c0[j].charAt(0) || c0[i].charAt(1) == c0[j].charAt(1))
					{
						bdg--;
						tochange[tochangeid++] = c0[i];
						c0[i] = c0[bdg];
						c0[bdg] = "0";
						break;
					}
				}
			}
			
			if (Character.getNumericValue(msg.charAt(1)) == my_id)
			{	
				//Wymiana kart.
				//liczba kart do wymiany => tochangeid
				//karty do wymiany
				int[] id = new int[4];
				int l=0;
				for (int i=0; i<tochangeid; i++)
				{
					for (int j=0; j<4; j++)
					{
						if (tochange[i] == c[j])
						{
							id[l++] = j;
						}
					}
				}
				//id zawiera indeksy kart do wymiany.
				output = "3"+tochangeid;
				for (int i=1; i<=tochangeid; i++)
						output += tochange[i];
				cchange++;
			}
		break;
		case '4':
			if (msg.charAt(1) == '4')
			{
				if (my_id == Character.getNumericValue(msg.charAt(2)))
					exist = false;
			}
		break;
		}
		return output;
	}
}
