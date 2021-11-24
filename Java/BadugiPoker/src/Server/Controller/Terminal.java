package Server.Controller;

import java.io.*;

import Server.Exceptions.ServerException;
import Client.Exceptions.ConnectException;


/**
 * Klasa terminalu serwera, zajmuje się komunikacją z administratorem stołu.
 * @author Tomasz Kulik
 *
 */
public class Terminal 
{
	public Terminal()
	{
		setReader();
	}

	public boolean AskToStart()
	{
		return begin;
	}
	
	private void setReader()
	{
		reader = new BufferedReader(new InputStreamReader(System.in));
	}
	
	private String read()
	{
		try 
		{
			return reader.readLine();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private void write(String str)
	{
		System.out.print(str + "\n");
	}
	
	private void writep(String str)
	{
		System.out.print(str);
	}
	
	private void ReadCommand()
	{
		writep("\nCommand> ");
		cmd = read();
	}
	
	private void RecognizeCmd() throws ServerException
	{
		if(cmd.length() == 0) 
			return;
		String[] cmd = this.cmd.split(" ");
		switch(cmd[0])
		{
			case "show_server_log":
				ShowLog();
				work_background = false;
			break;
			case "close":
				begin = false;
				start = false;
				if(client_s != null)
				{
					try {
						client_s.StopServer();
					} catch (ConnectException e) {
						write("Error appeared while stopping the server.");
					}
				}
				client_s = null;
			break;
			case "bot":
				if(!(begin == false && start == true))
					break;
				client_s.AddBot();
			break;
			case "start":
				if(start == true)
					break;
				String big = "40", small = "20", bal = "1500", port = "26676";
				
				for(int i=0; i< cmd.length; i++)
				{
					if(cmd[i].equals("-s") && (i+1) != cmd.length)
						small = cmd[i+1];
					if(cmd[i].equals("-b") && (i+1) != cmd.length)
						big = cmd[i+1];
					if(cmd[i].equals("-c") && (i+1) != cmd.length)
						bal = cmd[i+1];
					if(cmd[i].equals("-p") && (i+1) != cmd.length)
						port = cmd[i+1];
				}
				
				StartServer(port, "");
				try
				{
					client_s.setOptions(Integer.parseInt(bal), Integer.parseInt(small), Integer.parseInt(big));
				}
				catch(NumberFormatException e)
				{
					throw new ServerException(ServerException.InputData);
				}
				start = true;
				write("Press enter to type a next command.");
				read();
				work_background = true;
			break;
			case "kick":
				if(start == true)
				{
					if(cmd.length == 2)
					{
						try
						{
							client_s.TerminatePlayer(Integer.parseInt(cmd[1]));
						} catch(NumberFormatException e)
						{
							throw new ServerException(ServerException.InputData);
						} catch(ServerException e)
						{
							write("Player's id is wrong!");
						}
					}
				}
				else
				{
					write("You cannot kick players before the game's begin.");
				}
			break;
			case "exit":
				ExitTerminal();
			break;
			case "help":
				WriteHelp();
			break;
			case "begin":
				if(start == true && begin == false && client_s.getPlayersCounter() >= 2)
				{
					client_s.StopRegisteringNewClients();
					client_s.Begin();
				}
			break;
		}
	}
	
	private void WriteHelp()
	{
		write(    "\nbegin -  Starting a game (at least 2 players must be connected).\n"
				+ "close -  Close socket."
				+ "exit -  Quit server.\n"
				+ "kick -  Terminates player. Takes one argument with id of the player to terminate.\n"
				+ "show_server_log -  Shows log from server\n"
				+ "start -  Starts a server. Arguments: -s small blind, -b big blind -c start balance -p port to connect. There is no need to specify any argument, there are default values provided.");
	}
	
	private void ExitTerminal()
	{
		write("See you soon! \n");
		if(client_s != null)
		{
			try {
				client_s.StopServer();
			} catch (ConnectException e) {
				write("Error appeared while stopping the server.");
			}
		}
		running = false;
	}
	
	private void TerminalLoop()
	{
		while(running)
		{
			ReadCommand();
			try 
			{
				RecognizeCmd();
			} 
			catch (ServerException e) 
			{
				switch(e.getErr())
				{
					case ServerException.InputData:
						write("Wrong data!");
					break;
				}
			}
		}
	}
	
	private void ShowLog()
	{
		write(server_log+"\n");
	}
	
	private void StartServer(String port, String plr_num) throws ServerException
	{
		try
		{
			//int plrs = Integer.parseInt(plr_num);
			int int_port = Integer.parseInt(port);
			client_s = new Client_Seeker(this, int_port, 6);
			server_thread = new Thread(client_s);
			server_thread.start();
		}
		catch(NumberFormatException e)
		{
			throw new ServerException(ServerException.InputData);
		}
	}
	
	
	public void StartTerminal()
	{
		write("Welcome to Badugi server's terminal!\n"
				+ "Type help if you don't know how to use it.\n"
				+ "(C) Rafal Dworczak & Tomasz Kulik all rights reserved.\n"
				+ "\n");
		TerminalLoop();
	}
	
	public void WriteToLog(String str)
	{
		server_log += str + "\n";
		if(!work_background)
		{
			write(str + "\n");
		}
	}
	
	private boolean running = true;
	private Thread server_thread;
	private BufferedReader reader;
	private String cmd = "";
	private Client_Seeker client_s;
	private String server_log = "";
	private boolean work_background = false;
	private boolean begin = false;
	private boolean start = false;
}
