package Server.Controller;

import java.io.IOException;
import java.net.*;
import java.util.*;

import Server.Exceptions.ServerException;
import Client.Exceptions.*;
import Client.Exceptions.ConnectException;

/**
 * Czeka na nowych graczy oraz tworzy odpowiednie wątki komunikujące się z nimi.
 * @author Tomek
 *
 */
public class Client_Seeker implements Runnable
{
	public Client_Seeker(Terminal terminal, int port, int num_plrs)
	{
		this.terminal = terminal;
		this.port = port;
		game_ctrl = new Game_Controller(this, num_plrs, terminal);
	}
	
	public void setOptions(int bal, int small, int big)
	{
		game_ctrl.setBal(bal);
		game_ctrl.setBlinds(small, big);
	}
	
	@Override
	public void run()
	{
		try 
		{
			CreateServerSocket();
			while(running)
			{
				CreateSocket();
			}
		} 
		catch(ConnectException e)
		{
			// TODO Do something, when shit happens
		}
	}
	
	public void StopRegisteringNewClients()
	{
		stop_registering = true;
	}
	
	public void StartRegisteringNewClients()
	{
		stop_registering = false;
	}
	
	public void RegisterClient(int old_id, int new_id)
	{
		if(old_id != new_id)
		{
			clients[new_id] = clients[old_id];
			clients[old_id] = null;
		}
		clients[new_id].RegisterClient(new_id);
	}
	
	public void AddBot()
	{
		game_ctrl.AddBot();
	}
	
	public void Begin()
	{
		game_ctrl.Begin();
	}
	
	public int getPlayersCounter()
	{
		return game_ctrl.getPlayerCounter(); 
	}

	/**
	 * Usuwa gracza.
	 * @param id  ID gracza
	 * @throws ServerException
	 */
	public void TerminatePlayer(int id) throws ServerException
	{
		try 
		{
			if(id<0 || id>5 || clients[id] == null)
				throw new ServerException(ServerException.InputData);
			clients[id].TerminatePlayer();
		} catch (ConnectException e) 
		{
			terminal.WriteToLog("Error apeared while terminating player. Player's id: "+id);
		}
	}
	
	/**
	 * Zatrzymuje serwer.
	 * @throws ConnectException
	 */
	public void StopServer() throws ConnectException
	{
		running = false;
		try 
		{
			server_socket.close();
			for(int i=0; i<players; i++)
			{
				if(clients[i] != null)
					clients[i].TerminatePlayer();
			}
		} 
		catch (IOException e) 
		{
			throw new ConnectException(ConnectException.Communication);
		}
	}
	
	/**
	 * Tworzy główne gniazdo serwera.
	 * @throws ConnectException
	 */
	private void CreateServerSocket() throws ConnectException
	{
		try 
		{
			server_socket = new ServerSocket(port);
		} catch (IOException e) 
		{
			throw new ConnectException(ConnectException.SocketCreating);
		}
	}
	
	/**
	 * Tworzy nowy socket do nowego gracza; nasłuchuje nowych klientów.
	 * @throws ConnectException
	 */
	private void CreateSocket() throws ConnectException
	{
		Socket socket = null;
		try 
		{
			socket = server_socket.accept();
		}
		catch (IOException e) 
		{	
			throw new ConnectException(ConnectException.SocketCreating);
		}
		if(stop_registering)
		{
			try 
			{
				socket.close();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		else
		{
			Client_Listener client = new Client_Listener(socket, game_ctrl, terminal);
			Thread thread = new Thread(client);
			thread.start();
			clients_threads.add(thread);
			
			clients[next_id] = client;
			client.RegisterClient(next_id);
			players++;
			for(int i=0; i<6; i++)
			{
				if(clients[i] == null)
				{
					next_id = i;
					break;
				}
			}
		}
	}
	
	/**
	 * Pyta się terminal, czy administrator zarządził rozpoczęcie rozgrywki.
	 * @return
	 */
	public boolean Ask2Start()
	{
		return terminal.AskToStart();
	}
	
	private int next_id = 0;
	private int players = 0;
	
	private List<Thread> clients_threads = new ArrayList<Thread>();
	private Client_Listener[] clients = new Client_Listener[6];
	private ServerSocket server_socket;
	private int port;
	private boolean running = true;
	private boolean stop_registering = false;
	
	private Terminal terminal;
	private Game_Controller game_ctrl;
}
