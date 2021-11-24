package Client.View;


import javax.swing.*;

/**
 * Okno całej gry.
 * @author Tomek
 *
 */
public class Main_View extends JFrame
{
	public Main_View()
	{
		super("Badugi");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 102, 0));
        setSize(new java.awt.Dimension(1024, 768));
        setLayout(null);
        
        login = new Login_View();
        add(login);
        game = new Game_View();
        add(game);
        
        setVisible(true);
	}
	
	public void ShowLoginView()
	{
		login.setVisible(true);
	}
	
	public void ShowGameView()
	{
		game.setVisible(true);
	}
	
	public void HideLoginView()
	{
		login.setVisible(false);	
	}
	
	public void HideGameView()
	{
		game.setVisible(false);
	}
	
	
	
	public Login_View login;
	public Game_View game; 
	
}
