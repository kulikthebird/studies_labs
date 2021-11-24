package Client.Controller;

import Client.View.*;

public class Main_Controller 
{
	public static Main_View window;
	public static Login_Controller login_ctrl;
	public static Game_Controller game_ctrl;
	public static Server_Controller client;
	
	public static void main(String[] args) 
	{
		window = new Main_View();
		client = new Server_Controller();
		game_ctrl = new Game_Controller(window, client);
		login_ctrl = new Login_Controller(window, client);
		login_ctrl.Start();
	}
}
