package WolfAndHare;
import java.util.Random;

/**
 * Klasa zawierajoca sekcjo krytyczno aplikacji, posiada informacje o stanie obiektow na planszy.
 * @author Tomek
 *
 */

public class Mapa
{
	public int [][] cordZajac;
	public int [][] cordWilk;
	public int [][] mapa;
	
	
	public int liczba_zajecy;
	private Wilk[] wilk;
	private Zajac[] zajac;
	public int zycie_zajaca[];
	public WilkZajacGui gui;
	public int ile_wilkow;
	public int ile_zajecy;
	public int x,y;
	public int k;
	
	public void ZabijZajaca(int id)
	{
		zycie_zajaca[id] = 0;
		cordZajac[id][0] = 9999;
		cordZajac[id][1] = 9999;
		liczba_zajecy--;
	}
	
	
	Mapa(WilkZajacGui g, int wil, int zaj, int x, int y, int k)
	{
		ile_wilkow = wil;
		ile_zajecy = zaj;
		gui = g;
		liczba_zajecy = zaj;
		Thread watek[] = new Thread[ile_wilkow + ile_zajecy]; 
		wilk = new Wilk[ile_wilkow];
		zajac = new Zajac[ile_zajecy];
		mapa = new int[x][y];
		cordWilk = new int[ile_wilkow][2];
		cordZajac = new int[ile_zajecy][2];
		zycie_zajaca = new int[ile_zajecy];
		this.x = x;
		this.y = y;
		this.k = k;
		
		
		Random generator = new Random();
		
		for(int i=0; i<y; i++)
			for(int j=0; j<x; j++)
				mapa[j][i] = 0;
		
		for(int i=0; i<ile_wilkow; i++)
		{
			wilk[i] = new Wilk(i, this);
			watek[i] = new Thread(wilk[i]);
			while(true)
			{
				cordWilk[i][0] = generator.nextInt(x);
				cordWilk[i][1] = generator.nextInt(y);
				if(mapa[cordWilk[i][0]][cordWilk[i][1]] == 0)
				{
					mapa[cordWilk[i][0]][cordWilk[i][1]] = 2;
					gui.ZmienKolorPola(cordWilk[i][0], cordWilk[i][1], 2);
					break;
				}
			}
		}
		for(int i=0; i<ile_zajecy; i++)
		{
			zycie_zajaca[i] = 1;
			zajac[i] = new Zajac(i, this);
			watek[i+ile_wilkow] = new Thread(zajac[i]);
			while(true)
			{
				cordZajac[i][0] = generator.nextInt(x);
				cordZajac[i][1] = generator.nextInt(y);
				if(mapa[cordZajac[i][0]][cordZajac[i][1]] == 0)
				{
					mapa[cordZajac[i][0]][cordZajac[i][1]] = 1;
					gui.ZmienKolorPola(cordZajac[i][0], cordZajac[i][1], 1);
					break;
				}
			}
		}
		for(int i=0; i<ile_wilkow+ile_zajecy; i++)
		{
			watek[i].start();
		}
	}
	
}