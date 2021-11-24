import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.lang.*;

public class PrimeNumberSeeker implements Runnable
{
	private boolean is_working = false;
	private boolean random_search = true;
	private SeekerThread[] st;
	private int d;
	private int k;
	private int threads_amount;
	private Thread t = null;
	public ArrayList<BigInteger> found_prime_numbers;
	
	public void seekForPrimes(int d, int k, int threads_amount, boolean random_search, boolean onthread)
	{
		if(is_working)
		{
			return;
		}
		else
		{
			this.d = d;
			this.k = k;
			this.threads_amount = threads_amount;
			this.found_prime_numbers = new ArrayList<BigInteger>();
			this.random_search = random_search;
			is_working = true;
			if(onthread)
			{
				t = new Thread(this, "PrimeSeekerManager");
				t.start();
			}
			else
			{
				run();
			}
		}
	}
	
	public void blockUntilSearchEnd()
	{
		while(is_working)
		{
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public boolean getState()
	{
		return is_working;
	}

	@Override
	public void run() 
	{
		st = new SeekerThread[threads_amount];
		for(int i=0; i<threads_amount; i++)
			st[i] = new SeekerThread();
		for(int i=0; i<threads_amount; i++)
			st[i].startSearching(i, threads_amount, d, random_search, found_prime_numbers);
		boolean threads_working = true;
		while(threads_working)
		{
			threads_working = false;
			try 
			{
				Thread.sleep(50);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			if(found_prime_numbers.size() >= k)
				break;
			for(int i=0; i<threads_amount; i++)
			{
				threads_working = threads_working || st[i].getStatus();
			}
		}
		for(int i=0; i<threads_amount; i++)
			st[i].stopWork();
		threads_working = true;
		while(threads_working)
		{
			threads_working = false;
			try 
			{
				Thread.sleep(50);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			for(int i=0; i<threads_amount; i++)
			{
				threads_working = threads_working || st[i].getStatus();
			}
		}
		is_working = false;
	}
}
