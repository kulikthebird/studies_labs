package WolfAndHare;
import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.Random;


public class Wilk implements Runnable
{
	private int id;
	private Mapa map;

	
	public Wilk(int id, Mapa m)
	{
        this.id = id;
        map = m;
    }

	@Override
	public void run() 
	{
		boolean spij = false;
		int licznik = 0;
		while(true)
		{	
		synchronized(map)
			{
			if(!spij)
			{
			if(map.liczba_zajecy == 0) break;
			
			int min = 300, indeks=0, dist;
			for(int i=0; i<map.ile_zajecy; i++)
			{
				dist = (int) sqrt(pow(map.cordZajac[i][0] - map.cordWilk[id][0],2) + pow(map.cordZajac[i][1] - map.cordWilk[id][1],2));
				if(dist < min) 
				{
					indeks = i;
					min = dist;
				}
			}
			
			int dx=0, dy=0;
			double tan;
			double tgx, tgy;
			
			tgy = abs(map.cordZajac[indeks][1] - map.cordWilk[id][1]);
			tgx = abs(map.cordZajac[indeks][0] - map.cordWilk[id][0]);
			if(tgx != 0) tan = tgy / tgx;
			else tan = 99;
			
			if(tan <= 0.5) { dx = 1; }
			else if(tan > 0.5 && tan <= 2) { dx = 1; dy = 1; }
			else dy = 1;
			
			if(map.cordWilk[id][1] > map.cordZajac[indeks][1])
				dy *= -1;
			else dy *= 1;
			if(map.cordWilk[id][0] > map.cordZajac[indeks][0])
				dx *= -1;
			else dx *= 1;
			
			
			if(map.cordWilk[id][0] + dx >= map.x)  
				dx = 0;
			if(map.cordWilk[id][0] + dx < 0)
				dx = 0;
			if(map.cordWilk[id][1] + dy >= map.y)
				dy = 0;
			if(map.cordWilk[id][1] + dy < 0)
				dy = 0;
			
			if(map.mapa[map.cordWilk[id][0] + dx][map.cordWilk[id][1] + dy] == 0) 
			{
				map.mapa[map.cordWilk[id][0]][map.cordWilk[id][1]] = 0;
				map.gui.ZmienKolorPola(map.cordWilk[id][0], map.cordWilk[id][1], 0);
				map.cordWilk[id][0] += dx;
				map.cordWilk[id][1] += dy;
				map.mapa[map.cordWilk[id][0]][map.cordWilk[id][1]] = 2;
				map.gui.ZmienKolorPola(map.cordWilk[id][0], map.cordWilk[id][1], 2);
				
			}
			else if(map.mapa[map.cordWilk[id][0] + dx][map.cordWilk[id][1] + dy] == 2) 
			{
				// Wilk
			}
			else if(map.mapa[map.cordWilk[id][0] + dx][map.cordWilk[id][1] + dy] == 1) 
			{
				// Zajac
				
				map.mapa[map.cordWilk[id][0]][map.cordWilk[id][1]] = 0;
				map.gui.ZmienKolorPola(map.cordWilk[id][0], map.cordWilk[id][1], 0);
				map.cordWilk[id][0] += dx;
				map.cordWilk[id][1] += dy;
				map.mapa[map.cordWilk[id][0]][map.cordWilk[id][1]] = 2;
				map.gui.ZmienKolorPola(map.cordWilk[id][0], map.cordWilk[id][1], 3);
				
				// Zabij zajaca!
				map.ZabijZajaca(indeks);
				spij = true;
			}
			} else { licznik++; if(licznik > 5){ spij = false; licznik = 0; }}
			} 
			
			try {
				Random generator = new Random();
				Thread.sleep((long)((generator.nextDouble()+0.5)*map.k));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}
	}
	
}