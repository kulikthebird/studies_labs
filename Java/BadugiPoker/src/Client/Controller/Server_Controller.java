package Client.Controller;

import java.net.Socket;
import java.net.UnknownHostException;
import java.io.*;

import javax.swing.JOptionPane;

import Client.Exceptions.*;


/**
 * Kontroler komunikujący klienta z serwerem.
 * @author Tomek
 *
 */
public class Server_Controller implements Runnable 
{
	public Server_Controller()
	{
		
	}
	
	
	@Override
	public void run()
	{
		while(running)
		{	
			try
			{
				while(!connect_allow)
				{
					Thread.sleep(200);
				}
				Connect();
				CreateIO();
				listening = true;
				Pinger();
			}
			catch(ConnectException e)
			{
				switch(e.getErr())
				{
					case ConnectException.IOCreating:
						msg = "Cannot create buffers to comunicate by the socket!";
					break;
					case ConnectException.IOTransfer:
						if(listening == false)
							continue;
						msg = "Cannot get/send a message from/to the server! Connection refused.";
					break;
					case ConnectException.SocketCreating:
						msg = "Cannot connect to the host!";
					break;
					case ConnectException.WrongHostIP:
						msg = "Wrong format of adress. Try host(ip):port";
					break;
					case ConnectException.Communication:
						msg = "Unknown problem when trying to log in!";
					break;
				}
				JOptionPane.showMessageDialog(null, msg);
				StopClient();
				continue;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Zezwala na połączenie.
	 */
	public void TryConnect()
	{
		connect_allow = true;
	}
	
	
	/**
	 * Zatrzymuje kontroler komunikujący, zamyka gniazdo.
	 */
	public void StopClient()
	{
		if(socket!=null)
		{
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		listening = false;
		connect_allow = false;
		socket = null;
	}
	
	/**
	 * Ustawia hosta oraz port do połączenia.
	 * @param hostip
	 * @throws ConnectException
	 */
	public void SetHostIP(String hostip) throws ConnectException
	{
		String[] temporary = hostip.split(":");
		if(temporary.length != 2)
			throw new ConnectException(ConnectException.WrongHostIP);
		host = temporary[0];
		try
		{
			ip = Integer.parseInt(temporary[1]);
		}
		catch(NumberFormatException e)
		{
			throw new ConnectException(ConnectException.WrongHostIP);
		}
	}
	
	/**
	 * Pętla nasłuchująca na komunikaty od serwera.
	 * @throws ConnectException
	 */
	private void Pinger() throws ConnectException
	{
		while(listening)
		{
			try
			{
				if(msg.length() != 0)
				{
					out.println(msg);
					msg = "";
				}
				else 
				{
					// ping
					out.println("4");
				}
				if((input = in.readLine()) == null)
					break;
				ctrl.ReceiveServerMessage(input);
				Thread.sleep(300);
				
			} 
			catch(ConnectException e)
			{
				throw e;
			}
			catch(IOException e)
			{
				throw new ConnectException(ConnectException.IOTransfer);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Łączy klienta z serwerem.
	 * @throws ConnectException
	 */
	private void Connect() throws ConnectException
	{
		try 
		{
			socket = new Socket(host, ip);
		} 
		catch (UnknownHostException e) 
		{
			throw new ConnectException(ConnectException.SocketCreating);
		} 
		catch (IOException e) 
		{
			throw new ConnectException(ConnectException.SocketCreating);
		}
	}
	
	/**
	 * Tworzy bufory wejścia/wyjścia do gniazda.
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
			throw new ConnectException(ConnectException.IOCreating);
		}
	}
	
	public boolean isConnected()
	{
		return connected;
	}
	
	public void SetController(Messenger ctrl)
	{
		this.ctrl = ctrl;
	}
	
	/**
	 * Wysyła komunikat do serwera.
	 * @param output  Komunikat do wysłania
	 */
	public void SendMsg(String output)
	{
		msg = output;
	}
	
	private PrintWriter out;
    private BufferedReader in;
    private Socket socket = null;
    
    
    private String msg = "";
    private Messenger ctrl;
    private String input;
    private String host;
    private int ip;
    private boolean running = true;
    private boolean connect_allow = false;
    private boolean listening = true;
    private boolean connected = false;
    
}
