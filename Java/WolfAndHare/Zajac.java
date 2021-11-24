package WolfAndHare;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.Random;


public class Zajac implements Runnable
{
	private int id;
	private Mapa map;
	
	public Zajac(int id, Mapa m)
	{
        this.id = id;
        map = m;
    }

	@Override
	public void run() 
	{
		while(true)
		{	
			synchronized(map)
			{
				if(map.zycie_zajaca[id] == 0) break;
				int min = 300, indeks=0, dist;
				for(int i=0; i<map.ile_wilkow; i++)
				{
					dist = (int) sqrt(pow(map.cordZajac[id][0] - map.cordWilk[i][0],2) + pow(map.cordZajac[id][1] - map.cordWilk[i][1],2));
					if(dist < min) 
					{
						indeks = i;
						min = dist;
					}
				}
				
				int dx=0, dy=0;
				double tan;
				
				double tgx, tgy;
				tgy = abs(map.cordZajac[id][1] - map.cordWilk[indeks][1]);
				tgx = abs(map.cordZajac[id][0] - map.cordWilk[indeks][0]);
				if(tgx != 0) tan = tgy / tgx;
				else tan = 99;
				
				
				if(tan <= 0.5) { dx = 1; }
				else if(tan > 0.5 && tan <= 2) { dx = 1; dy = 1; }
				else dy = 1;
				
				if(map.cordWilk[indeks][1] > map.cordZajac[id][1])
					dy *= -1;
				else dy *= 1;
				if(map.cordWilk[indeks][0] > map.cordZajac[id][0])
					dx *= -1;
				else dx *= 1;
				
				if(map.cordZajac[id][0] + 1 >= map.x || map.cordZajac[id][0] - 1 < 0 || map.cordZajac[id][1] + 1 >= map.y || map.cordZajac[id][1] - 1 < 0)
				{	
					dx = 0;
					dy = 0;
					if(map.cordZajac[id][0] + 1 >= map.x)  
						dx = -1;
					if(map.cordZajac[id][0] - 1 < 0)
						dx = 1;
					if(map.cordZajac[id][1] + 1 >= map.y)
						dy = -1;
					if(map.cordZajac[id][1] - 1 < 0)
						dy = 1;
					Random generator = new Random();
					
					if(dx == 0)
						dx = (generator.nextInt(3)) -1;
					else 
						dx = dx * (generator.nextInt(2));
					
					if(dy == 0)
						dy = (generator.nextInt(3)) -1;
					else 
						dy = dy * (generator.nextInt(2));
				
				}
				
				if(map.mapa[map.cordZajac[id][0] + dx][map.cordZajac[id][1] + dy] == 0)
				{
					//Pole puste
					map.mapa[map.cordZajac[id][0]][map.cordZajac[id][1]] = 0;
					map.gui.ZmienKolorPola(map.cordZajac[id][0], map.cordZajac[id][1], 0);
					map.cordZajac[id][0] += dx;
					map.cordZajac[id][1] += dy;
					map.mapa[map.cordZajac[id][0]][map.cordZajac[id][1]] = 1;
					map.gui.ZmienKolorPola(map.cordZajac[id][0], map.cordZajac[id][1], 1);
					
				}
		
			}
			try {
				Random generator = new Random();
				Thread.sleep((long)((generator.nextDouble()+0.2)*map.k));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}