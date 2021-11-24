package Server.Controller;
import Server.Model.Bot_Model;
import Server.Model.Rules_Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Kontroler gry po stronie serwera.
 * @author Tomasz Kulik, Rafał Dworczak
 *
 */
public class Game_Controller 
{
	public Game_Controller(Client_Seeker client_s, int num_players, Terminal terminal)
	{
		this.terminal = terminal;
		this.client_s = client_s;
		rules = new Rules_Model();
		this.num_players = num_players;
	}
	
	/**
	 * Ustawienie small i big blind.
	 * @param sb - small blind
	 * @param bb - big blind
	 */
	public void setBlinds(int sb, int bb)
	{
		rules.setBlinds(sb, bb);
	}
	
	public void setBal(int x)
	{
		rules.setBal(x);
		// dodac do rulesa
	}
	
	/**
	 * Zaczyna rozgrywkę.
	 */
	public void Begin()
	{
		int[] x = new int[1];
		int[] out = rules.ModelControl(x);
		String msgout;
		
		
		msgout = "40"+out[1]+out[2]+out[3]+out[4];
		
		AddOperation(msgout, 9);
		
		String[] c = new String[4];
				
		for (int i=0; i<player_number; i++)
		{
			c = rules.SendCards(i);
			msgout = "42"+i+c[0]+c[1]+c[2]+c[3]+"0";
			AddOperation( msgout, 9);
		}
		//czeka chwil�.
		msgout = "2"+out[4];
		AddOperation( msgout, 9);
	}
	
	public void TerminatePlayer(int id)
	{
		rules.DeletePlayer(id);
	}
	
	public synchronized  String ReceiveMessage(String input, int id_client)
	{
		String output = "";
		String tolog  = "";
		String msgout = "";
		String nick = "";
		
		switch(input.charAt(0))
		{
			case '1':	// logowanie
				String[] data = input.split(";");
				
				int new_id = rules.CreatePlayer(data[1]);// id przypisane graczowi.
				player_number++;
				
				if ( new_id != -1) // gdy poprawnie przyznano id.
				{
					client_s.RegisterClient(id_client, new_id);
					//					startaccbal			smallblind			bigblind			id gracza	id;nick;id;nick; wszystkich graczy
					output = "19"+";"+rules.valueOf(0)+";"+rules.valueOf(1)+";"+rules.valueOf(2)+";"+new_id+";"+rules.getPlayersInfo(new_id);
					
					counter_operations[new_id] = last_operations.size();
				}
				else;	//error
				
				if ( new_id > 0 )
				{
					tolog = "Player "+new_id+" "+data[1]+" log on.";
					terminal.WriteToLog(tolog);
					
					msgout = "48"+";"+new_id+";"+data[1];
					AddOperation(msgout, new_id);
				}
				else
				{
					tolog = "Player "+new_id+" "+data[1]+" log on.";
					terminal.WriteToLog(tolog);
				}
					//czeka i czeka na kolejnego gracza.
			break;
			case '2':	// licytacja
				int [] msg = new int[3];
				msg[0] = id_client; //czy to jest id gracza?
				int x = Character.getNumericValue(input.charAt(1));
				msg[1] = x; //ruch jaki zosta� wykonany podczas licytacji.
				if (x == 0 || x == 3 || x == 4 || x == 5)
					msg[2] = 0;
				else
				{
					data = input.split(";");
					String svalue = data[1];
					msg[2] = Integer.parseInt(svalue); // warto�� dla ruchu.
				}
				
				int[] response = rules.ModelControl(msg);
				
				nick = rules.getPlayerNick(id_client);
				
				if (response[0] != 8) // licytacja przesz�a pomy�lnie
				{
					if (x == 0 || x == 3 || x == 4 || x == 5)
					{
						if (x == 0)
							tolog = "Player "+msg[0]+" "+nick+" has checked.";
						else if (x == 3)
							tolog = "Player "+msg[0]+" "+nick+" has called.";
						else if (x == 4)
							tolog = "Player "+msg[0]+" "+nick+" has folded.";
						else
							tolog = "Player "+msg[0]+" "+nick+" went all in.";
						terminal.WriteToLog(tolog);
						
						msgout = "41"+id_client+x;
					}
					else
					{
						String[] value = input.split(";");
						int p = Integer.parseInt(value[1]);
						if (x == 1)
							tolog = "Player "+msg[0]+" "+nick+" has bet for "+p+".";
						else
							tolog = "Player "+msg[0]+" "+nick+" has raised for "+p+".";
						terminal.WriteToLog(tolog);
						
						msgout = "41"+id_client+x+";"+p;
					}
					AddOperation(msgout, id_client);
					
					//po wys�aniu informacji o licytacji gracza.
					if (response[0] == 2)// nast�pny gracz mo�e licytowa�.
					{
						msgout = "2"+response[1];
						AddOperation( msgout, 9);
					}
					else if (response[0] == 3) //przechodzimy do wymiany kart
					{
						tolog = "Cards change phase.";
						terminal.WriteToLog(tolog);
						
						msgout = "3"+response[1];
						AddOperation( msgout, 9);
					}
					else if (response[0] == 6) //ko�czymy rund�, sprawdzamy karty.
					{
						tolog = "End of the round.";
						terminal.WriteToLog(tolog);
						
						String[] c = new String[4];
						
						for (int i=0; i<player_number; i++)
						{
							c = rules.SendCards(i);
							msgout = "42"+i+c[0]+c[1]+c[2]+c[3]+"0";
							AddOperation( msgout, 9);
						}
						int[] y = new int[1];
						int[] out = rules.ModelControl(y);
						nick = rules.getPlayerNick(out[1]);
						
						tolog = "Player "+out[1]+" "+nick+" has won this round.";
						terminal.WriteToLog(tolog);
						
						msgout = "43"+out[1];
						AddOperation(msgout, 9);
						sleep = true;
						old_time = System.currentTimeMillis();
						
						if (out[2] == 0)
						{	
							tolog = "Player "+out[1]+" "+nick+" has won the game.";
							terminal.WriteToLog(tolog);
							
							msgout = "44"+out[1]+"1";
							AddOperation(msgout, 9);
						}
					}
					else if(response[0] == 7) // gdy wszyscy opr�cz jednego sfoldowali.
					{
						String[] c = new String[4];
						
						for (int i=0; i<player_number; i++)
						{
							c = rules.SendCards(i);
							
							msgout = "42"+i+c[0]+c[1]+c[2]+c[3]+"0";
							AddOperation( msgout, 9);
						}
						nick = rules.getPlayerNick(response[1]);
						tolog = "Player "+response[1]+" "+nick+" has won this round.";
						terminal.WriteToLog(tolog);
						msgout = "43"+response[1]+"";
						AddOperation(msgout, 9);
						sleep = true;
						old_time = System.currentTimeMillis();
					}
					else if (response[0] == 10) //wszyscy gracze sfoldowali.
					{
						msgout = "436";
						AddOperation(msgout, 9);
					}
					output = "46";
				}
				else
					output = "28";
			break;
			case '3':	// wymiana kart
				msg = new int[6];
				msg[0] = id_client; //czy to jest id gracza?
				x = Character.getNumericValue(input.charAt(1));
				msg[1] = x; // ilo�� zmienionych kart.
				for (int i=2; i<x+2; i++) // do kolejnych pozycji msg[] dopisuje indeksy wymienionych kart.
					msg[i] = Character.getNumericValue(input.charAt(i));
				
				response = rules.ModelControl(msg);
				
				String[] c = new String[4];
				c = rules.SendCards(response[1]);
				output = "42"+response[1]+c[0]+c[1]+c[2]+c[3]+x;
				msgout = "42"+response[1]+"xxxxxxxx"+x;
				AddOperation(msgout, id_client);
				
				nick = rules.getPlayerNick(id_client);
				tolog = "Player "+id_client+" "+nick+" changed "+x+ " cards.";
				terminal.WriteToLog(tolog);
				//after a while.
				if (response[0] == 4)
				{
					msgout = "3"+response[2];
					AddOperation(msgout, 9);
				}
				else if (response[0] == 5)
				{
					msgout = "2"+response[2];
					AddOperation(msgout, 9);
				}
			break;
			case '4': // update
				if(sleep == true)
				{
					if(System.currentTimeMillis() - old_time > 5000)
					{
						int[] k = new int[1];
						int[] out = rules.ModelControl(k);
						
						if (out[0] == 33)
						{
							msgout = "44" + out[1];
						}
						else
						{
							msgout = "40"+out[1]+out[2]+out[3]+out[4];
							
							AddOperation(msgout, 9);
							
							c = new String[4];
									
							for (int i=0; i<player_number; i++)
							{
								c = rules.SendCards(i);
								msgout = "42"+i+c[0]+c[1]+c[2]+c[3]+"0";
								AddOperation( msgout, 9);
							}
							msgout = "2"+out[4];
							AddOperation( msgout, 9);
						}
						sleep = false;
					}
				}
				if(counter_operations[id_client] < last_operations.size())
					output = last_operations.get(counter_operations[id_client]++);
				
				else output = "5";
			break;
			case '9':
				rules.DeletePlayer(id_client);
				String out= "14"+id_client;
				AddOperation(out, id_client);
			break;
		}
		
		return output;
	}
	
	public void AddOperation(String opr, int id)
	{
		if (id!=9)
			counter_operations[id]++;
		last_operations.add(opr);
		for(int i=0; i<bot_num; i++)
		{
			String result = bots[i].MoveBot(opr);
			if(result != "")
			{
				AddOperation(result, bots[i].getID());
			}
		}
	}
	
	public void AddBot()
	{
		bots[bot_num] = new Bot_Model(rules);
		bots[bot_num].setMyID(rules.CreatePlayer("Bot "+bot_num));
		AddOperation("1;Bot "+bot_num, 9);
		bot_num++;
		player_number++;
	}
	
	public int getPlayerCounter()
	{
		return player_number;
	}
	
	private boolean sleep = false;
	private long old_time = 0;
	
	private int bot_num = 0;
	private Bot_Model[] bots = new Bot_Model[6];
	private Terminal terminal;
	private int player_number=0;
	private int num_players;
	private Rules_Model rules;
	private Client_Seeker client_s = null;
	private int[] counter_operations = {0,0,0,0,0,0};
    private List<String> last_operations = new ArrayList<String>();
	
}
