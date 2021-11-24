package Client.Controller;

import java.awt.event.ActionEvent;

import Client.Exceptions.ConnectException;
import Client.Model.Login_Model;
import Client.View.*;

public class Login_Controller implements Messenger
{
	/** Flaga określająca czy ten kontroler został już zainicjowany */
	static boolean init = false;
	
	/**
	 * Tworzy kontroler obsługujący fazę logowania się gracza.
	 * @param window  Referencja do głównego okna gry.
	 * @param client  Referencja do kontrolera obługującego komunikację z serwerem
	 */
	public Login_Controller(Main_View window, Server_Controller client)
	{
		if(!init)
		{
			this.client = client;
			this.window = window;
			CreateModel();
			CreateListeners();
			init = true;
		}
	}
	
	/**
	 * Pokazuje widok logowania.
	 */
	public void Start()
	{
		window.ShowLoginView();
	}
	
	/**
	 * Chowa widok logowania.
	 */
	public void Stop()
	{
		window.HideLoginView();
	}
	
	private void CreateModel()
	{
		login_model = new Login_Model();
	}
	
	/**
	 * Dodaje metody obsługujące przyciski.
	 */
	public void CreateListeners()
	{
		login_view = window.login;
		login_view.button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ActionButtonStart(evt);
            }
        });
	}
	
	/**
	 * Obsługuje komunikaty od serwera.
	 * 
	 * @param input  Dane od serwera
	 */
	public void ReceiveServerMessage(String input) throws ConnectException
	{
		// obsluga komunikatu z serwera
		if((input.substring(0, 1)).equals("19")) 
			return;
		// jak poszlo dobrze to startujemy gre
		Stop();
		Main_Controller.game_ctrl.setMyNick(login_view.getNick());
		Main_Controller.game_ctrl.ReceiveServerMessage(input);
		Main_Controller.game_ctrl.Start();
	}
	
	
	private void ActionButtonStart(ActionEvent e)
	{
		// Klikniecie przycisku start w panelu logowania...
		// trzeba sprawdzic poprawnosc loginu w modelu, ale go nie ma jeszcze
		
		try 
		{
			if(client_thread == null)
			{
				client_thread = new Thread(client);
				client_thread.start();
				client.SetController(this);
			}
			client.SetHostIP(login_view.getHost());
			client.TryConnect();
			client.SendMsg("1"+";"+login_view.getNick());
			
			
			
		} catch (ConnectException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private Thread client_thread;
	private Login_Model login_model;
	private Server_Controller client;
	private Login_View login_view;
	private Main_View window;
}
