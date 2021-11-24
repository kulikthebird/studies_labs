package WolfAndHare;

import java.awt.Graphics;
import java.awt.GridLayout;


import javax.swing.JApplet;

@SuppressWarnings("serial")
public class WilkZajac extends JApplet
{
	
	private int rows, cols, time = 100, wolves = 10, hares = 30;
	public WilkZajacGui gui;
	public Mapa map;
	
	public void init()
	{
		try
		{
		if(getParameter("rows") != null) rows = Integer.parseInt(getParameter("rows"));  
		if(getParameter("cols") != null) cols = Integer.parseInt(getParameter("cols"));
		if(getParameter("wolves") != null) wolves = Integer.parseInt(getParameter("wolves"));
		if(getParameter("hares") != null) hares = Integer.parseInt(getParameter("hares"));
		if(getParameter("time") != null) time = Integer.parseInt(getParameter("time"));
		} catch(NumberFormatException e)
		{
			return;
		}
        setLayout(new GridLayout(1, 1));
        gui = new WilkZajacGui(rows,cols);
		add(gui);
		setVisible(true);
	}
	 
	public void start()
	{
		rows = 100; 
		cols = 100; 
		time = 100; 
		wolves = 10; 
		hares = 30;
		
		try
		{
		if(getParameter("rows") != null) rows = Integer.parseInt(getParameter("rows"));  
		if(getParameter("cols") != null) cols = Integer.parseInt(getParameter("cols"));
		if(getParameter("wolves") != null) wolves = Integer.parseInt(getParameter("wolves"));
		if(getParameter("hares") != null) hares = Integer.parseInt(getParameter("hares"));
		if(getParameter("time") != null) time = Integer.parseInt(getParameter("time"));
		} catch(NumberFormatException e)
		{
			return;
		}
		
		map = new Mapa(gui,wolves, hares, rows, cols, time);
	}
	
	public void stop()
	{
		
	}
	 
	public void destroy()
	{
		
	}
	 
	public void paint(Graphics g)
	{
		
	}
}
