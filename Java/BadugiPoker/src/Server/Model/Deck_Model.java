package Server.Model;

import java.util.*;

public class Deck_Model {
	/**
	 * Talia 52 kart, ka¿da karta w formie flagi [kolor][numer].
	 * C- kier; K- karo; T- trefl; P- pik;
	 * A- as; J- walet; Q- królowa; K- król;
	 */
	private String[] deck = { "C1","C2","C3","C4","C5","C6","C7","C8","C9","CA","CB","CC","CD",
			"K1","K2","K3","K4","K5","K6","K7","K8","K9","KA","KB","KC","KD",
			"T1","T2","T3","T4","T5","T6","T7","T8","T9","TA","TB","TC","TD",
			"P1","P2","P3","P4","P5","P6","P7","P8","P9","PA","PB","PC","PD" };
	/**
	 * Talia kart oddanych przez graczy przy wymianie.
	 */
	private String[] usedcards = new String[52];
	/**
	 * Index karty na górze talii.
	 */
	private int topcardi = 51;
	/**
	 * licznik ile kart jest w tablicy usedcards.
	 */
	private int usedcounter = 0;
	
	/**s
	 * Metoda tasuj¹ca taliê dla x kart,
	 * @param x - liczba kart do potasowania.
	 */
	public void Shuffle(int x)
	{
		int n = 0; 	// zmienna dla losowanej liczby.
		String p; 	// zmienna do przechowania karty.
		Random rand = new Random();
		
		for (int i=0; i<x; i++)
		{
			n = rand.nextInt(x);
			p = deck[i];
			deck[i] = deck[n];
			deck[n] = p;
		}
	}
	
	/**
	 * Metoda zwracaj¹ca n kart w formie tablicy.
	 * @param n
	 */
	public String[] GiveCards(int n) 
	{
		String[] cards = new String[n];
		
		if (topcardi+2 > n)	//sprawdza czy jest wystarczaj¹ca iloœæ kart w tali.
		{
			for (int i=0; i<n; i++)
			{
				if (deck[topcardi] == "0")
				{
					topcardi--;
					i--;
				}
				else
				{
					cards[i] = deck[topcardi];	//Podaje kartê do nowej tablicy.
					deck[topcardi] = "0";
					topcardi--;					//Usuwa kartê z talii i nastêpnie zminiejsza licznik topcardi.
				}
			}
		}
		else	//Gdy kart w talii jest za ma³o.
		{
			int m = topcardi;
			int p = n-topcardi;
			String[] c = new String[p];
			
			for (int i=0; i<m; i++)	//Podaje tyle kart ile zosta³o w talii.
			{
				if (deck[topcardi] == "0")
				{
					topcardi--;
					i--;
				}
				else
				{
					cards[i] = deck[topcardi];
					deck[topcardi--] = "0";
				}
			}
			
			for (int i=0; i<=usedcounter; i++)	//Podstawia kartê u¿ytych kart do pustej talii;
			{
				deck[i] = usedcards[i];
			}
			topcardi = usedcounter;
			usedcounter = 0;
			
			Shuffle(topcardi+1);	//Tasuje taliê.
			
			for (int j=m; j<n; j++)	//Dobiera resztê kart.
			{
				c = GiveCards(1);
				cards[j] = c[0];
			}
		}
		
		cards = SortCards(cards, n);
		return cards;
	}
	
	/**
	 * Funkcja, która wymienia karty oddane przez gracza na pierwsze z talii.
	 * @param c - tablica kart od gracza.
	 * @param m - tablica z indeksami wymienianych kart.
	 * @param n - liczba wymienianych kart.
	 * @return zwraca n nowych kart.
	 */
	public String[] ChangeCards(String c[], int m[], int n)
	{
		String[] card = new String[n];
		
		for (int i=0; i<n; i++)
		{
			usedcards[usedcounter++] = c[m[i]];
			card = GiveCards(1);
			c[m[i]] = card[0];
		}
		
		c = SortCards(c, 4);
		return c;
	}
	
	/**
	 * Sortuje tablicê n kart.
	 * @param c - tablica kart.
	 * @param n - iloœæ kart do posortowania.
	 * @return posortowana talica.
	 */
	public String[] SortCards(String c[], int n)
	{
		String p;
		
		for (int j=n-1; j>0; j--)
		{
			for (int i=0; i<j; i++)
			{
				if (c[i].charAt(1) > c[i+1].charAt(1))
				{
					p = c[i];
					c[i] = c[i+1];
					c[i+1] = p;
				}
			}
		}
		
		return c;
	}
	
	
	public int getBetter(String c0[], String c1[])
	{
		int bdg0 = 4;
		int bdg1 = 4;
		int m, k0=1, k1=1, i0=0, i1=0, m0=3, m1=3;
		String[] p0 = new String[12];
		String[] p1 = new String[12];
		
		while (k0 != 0 || k1 != 0)
		{
			k0=0;
			k1=0;
			for (int i=m0; i>0; i--)
			{
				for (int j=i-1; j>=0; j--)
				{
					if (c0[i].charAt(0) == c0[j].charAt(0) || c0[i].charAt(1) == c0[j].charAt(1))
					{
						p0[i0++] = c0[i];
						p0[i0++] = c0[j];
					}
				}
			}
			
			for (int i=m1; i>0; i--)
			{
				for (int j=i-1; j>=0; j--)
				{
					if (c1[i].charAt(0) == c1[j].charAt(0) || c1[i].charAt(1) == c1[j].charAt(1))
					{
						p1[i1++] = c1[i];
						p1[i1++] = c1[j];
					}
				}
			}
			
			for (int i=i0-1; i>0; i--)
			{
				for (int j=i-1; j>=0; j--)
				{
					if (p0[i] == p0[j])
					{
						for (int l=0; l<4; l++)
						{
							if (p0[i] == c0[l])
							{
								bdg0--;
								c0[l] = c0[bdg0];
								c0[bdg0] = "0";
								m0--;
							}
						}
						p0[i] = "0";
						k0++;
					}
				}
				p0[i]="0";
			}
			p0[0]="0";
			i0=0;
			
			for (int i=i1-1; i>0; i--)
			{
				for (int j=i-1; j>=0; j--)
				{
					if (p1[i] == p1[j])
					{
						for (int l=0; l<4; l++)
						{
							if (p1[i] == c1[l])
							{
								bdg1--;
								c1[l] = c1[bdg1];
								c1[bdg1] = "0";
								m1--;
							}
						}
						p1[i] = "0";
						k1++;
					}
				}
			}
			p1[0]="0";
			i1=0;
		}
		
		m=bdg0;
		
		for (int i=m-1; i>0; i--)
		{
			for (int j=i-1; j>=0; j--)
			{
				if (c0[i].charAt(0) == c0[j].charAt(0) || c0[i].charAt(1) == c0[j].charAt(1))
				{
					bdg0--;
					c0[i] = c0[bdg0];
					c0[bdg0] = "0";
					break;
				}
			}
		}
		
		m=bdg1;
		
		for (int i=m-1; i>0; i--)
		{
			for (int j=i-1; j>=0; j--)
			{
				if (c1[i].charAt(0) == c1[j].charAt(0) || c1[i].charAt(1) == c1[j].charAt(1))
				{
					bdg1--;
					c1[i] = c1[bdg1];
					c1[bdg1] = "0";
					break;
				}
			}
		}
		
		if (bdg0 > bdg1)
			return 0;
		else if (bdg0 < bdg1)
			return 1;
		else
		{
			if (bdg0 == 4)
			{
				for (int i=3; i>0; i--)
				{
					if (c0[i].charAt(1) > c1[i].charAt(1))
						return 1;
					else if (c0[i].charAt(1) < c1[i].charAt(1))
						return 0;
				}
				
				if (c0[0].charAt(1) < c1[0].charAt(1))
					return 0;
				else if (c0[0].charAt(1) > c1[0].charAt(1))
					return 1;
				else
					return 2;
			}
			else if (bdg0 == 3)
			{
				for (int i=2; i>0; i--)
				{
					if (c0[i].charAt(1) > c1[i].charAt(1))
						return 1;
					else if (c0[i].charAt(1) < c1[i].charAt(1))
						return 0;
				}
				
				if (c0[0].charAt(1) < c1[0].charAt(1))
					return 0;
				else if (c0[0].charAt(1) > c1[0].charAt(1))
					return 1;
				else
					return 2;
			}
			else if (bdg0 == 2)
			{
				if (c0[1].charAt(1) > c1[1].charAt(1))
					return 1;
				else if (c0[1].charAt(1) < c1[1].charAt(1))
					return 0;
				else if (c0[0].charAt(1) < c1[0].charAt(1))
					return 0;
				else if (c0[0].charAt(1) > c1[0].charAt(1))
					return 1;
				else
					return 2;
			}
			else
			{
				if (c0[0].charAt(1) < c1[0].charAt(1))
					return 0;
				else if (c0[0].charAt(1) > c1[0].charAt(1))
					return 1;
				else
					return 2;
			}
		}
	}
	
	
	/**
	 * Resetuje taliê do pierwotnej wersji 52 kart i resetuje liczniki.
	 */
	public void ResetDeck()
	{
		deck = new String[]{ "C1","C2","C3","C4","C5","C6","C7","C8","C9","CA","CB","CC","CD",
				"K1","K2","K3","K4","K5","K6","K7","K8","K9","KA","KB","KC","KD",
				"T1","T2","T3","T4","T5","T6","T7","T8","T9","TA","TB","TC","TD",
				"P1","P2","P3","P4","P5","P6","P7","P8","P9","PA","PB","PC","PD" };
		topcardi = 51;
		usedcounter = 0;
	}
}
