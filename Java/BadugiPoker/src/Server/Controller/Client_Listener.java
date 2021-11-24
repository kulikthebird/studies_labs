package Server.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;

import Client.Exceptions.ConnectException;

/**
 * Nasłuchuje komunikaty od gracza oraz wysyła mu polecenia od serwera.
 * @author Tomek
 *
 */
public class Client_Listener implements Runnable
{
	public Client_Listener(Socket socket, Game_Controller game_ctrl, Terminal terminal)
	{
		this.terminal = terminal;
		this.socket = socket;
		this.game_ctrl = game_ctrl;
	}
	
	@Override
	public void run()
	{
		try 
		{
			CreateIO();
			is_connected = true;
			StartReading();
		} 
		catch (ConnectException e) 
		{
			switch(e.getErr())
			{
				case ConnectException.Communication:
					terminal.WriteToLog("Communication failed. Player's id: "+id);
				break;
				case ConnectException.IOCreating:
					terminal.WriteToLog("Could not create buffers. Player's id: "+id);
				break;
				case ConnectException.IOTransfer:
					terminal.WriteToLog("Could not send/get message, probably socket is down. Player's id: "+id);
				break;
			}
		}
	}
	
	/**
	 * Pętla nasłuchująca na nowe komunikaty od klientów.
	 * @throws ConnectException
	 */
	private void StartReading() throws ConnectException
	{
		String input = "";
		String output = "";
		while(is_connected)
		{
			try 
			{
				input = in.readLine();
				if(input == "" || input == null)
					throw new ConnectException(ConnectException.Communication);
				output = game_ctrl.ReceiveMessage(input, id);
				// Odpowiedz do klienta
				out.println(output);
			} 
			catch (IOException | ConnectException e) 
			{
				game_ctrl.ReceiveMessage("9", id);
				if(is_connected == true)
				{
					is_connected = false;
					throw new ConnectException(ConnectException.IOTransfer);
				}
			}
		}
	}
	
	
	/**
	 * Tworzy bufory wejścia/wyjścia
	 * @throws ConnectException
	 */
	private void CreateIO() throws ConnectException
	{
		try 
		{
			in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ConnectException(ConnectException.IOCreating);
		}
	}
	
	/**
	 * Nadaje ID do danego gniazda.
	 * @param id
	 */
	public void RegisterClient(int id)
	{
		this.id = id;
	}
	
	/**
	 * Usuwa gracza, wysyła mu stosowną instrukcję.
	 * @throws ConnectException
	 */
	public void TerminatePlayer() throws ConnectException
	{
		is_connected = false;
		try 
		{
			socket.close();
			game_ctrl.TerminatePlayer(id);
			terminal.WriteToLog("Player terminated, id: " + id);
		} 
		catch (IOException e) 
		{
			throw new ConnectException(ConnectException.Communication);
		}
	}
	
	private PrintWriter out;
    private BufferedReader in;
	
    private Terminal terminal;
	private boolean is_connected = false;
	private Socket socket;
	private Game_Controller game_ctrl;
	private int id;
}
