package Client.View;

import javax.swing.*;

/**
 * Widok logowania.
 * @author Tomek
 *
 */
public class Login_View extends JPanel 
{
    public Login_View() 
    {
        initComponents();
    }
    
    /**
     * Inicjalizuje widok logowania.
     */
    private void initComponents() 
    {
    	setLayout(null);
    	
    	setBounds(0,0,700,450);
    	
        textField1 = new java.awt.TextField();
        textField2 = new java.awt.TextField();
        button1 = new java.awt.Button();
        label1 = new java.awt.Label();
        label2 = new java.awt.Label();

        textField1.setText("My nick");
        add(textField1);
        textField1.setBounds(170, 60, 170, 20);

        textField2.setText("127.0.0.1:26676");
        add(textField2);
        textField2.setBounds(170, 90, 170, 20);

        button1.setLabel("Start");
        add(button1);
        button1.setBounds(280, 120, 57, 24);

        label1.setText("Host name (IP):");
        add(label1);
        label1.setBounds(80, 90, 90, 20);

        label2.setText("Your nick:");
        add(label2);
        label2.setBounds(110, 60, 60, 20);
        
        setVisible(false);
    }
    
    public String getNick()
    {
    	return textField1.getText();
    }
    
    public String getHost()
    {
    	return textField2.getText();
    }
    
    
    public java.awt.Button button1;
    private java.awt.Label label1;
    private java.awt.Label label2;
    private java.awt.TextField textField1;
    private java.awt.TextField textField2;                   
}

