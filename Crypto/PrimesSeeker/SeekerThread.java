import java.math.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.security.SecureRandom;

public class SeekerThread implements Runnable
{
	private Thread t = null;
	private int bits = 5;
	private BigInteger i;
	private int accuracy = 6;
	private int thread_amount = 1;
	private int thread_id = 0;
	private boolean work = true;
	private boolean random_search = true;
	private ArrayList<BigInteger> found_primes;
	
	private int div_2, div_3, div_5;
	
	BigInteger big_two = new BigInteger("2");
	BigInteger big_three = new BigInteger("3");
	BigInteger big_five = new BigInteger("5");
	
	@Override
	public void run()
	{
		System.out.println("Starting seeker thread no. " + thread_id);
		if(random_search == true)
		{
			BigInteger lower_bound = new BigInteger("2");
			lower_bound = lower_bound.pow(bits-1);
			BigInteger n = new BigInteger("2");
			n = n.pow(bits);
			BigInteger spec = n.subtract(lower_bound);
			n = n.subtract(BigInteger.ONE);
	
			while(work)
			{
				int nlen = lower_bound.bitLength();
				BigInteger nm1 = spec;
				BigInteger ss;
				SecureRandom rnd = new SecureRandom();
				do {
				    ss = new BigInteger(nlen + 100, rnd);
				    i = ss.mod(spec);
				} while (ss.subtract(i).add(nm1).bitLength() >= nlen + 100);
				i = i.add(lower_bound);
				
				if(i.mod(big_two) == BigInteger.ZERO || i.mod(big_three) == BigInteger.ZERO || i.mod(big_five) == BigInteger.ZERO )
					continue;
				
				if(checkPrime(accuracy))
				{
					if(checkPrime(40))
					{
						synchronized(this)
						{
							if(!found_primes.contains(i))
								found_primes.add(i);
						}
					}
				}
			}
		}
		else
		{
			i = new BigInteger("2");
			i = i.pow(bits-1);
			BigInteger n = new BigInteger("2");
			n = n.pow(bits);
			n = n.subtract(BigInteger.ONE);
	
			for(setIteratorState(); work &&  (i.compareTo(n) == -1 || i.compareTo(n) == 0) ; i = i.add(nextIteration(thread_amount-1)))
			{
				if(checkPrime(accuracy))
				{
					if(checkPrime(40))
					{
						synchronized(this)
						{
							found_primes.add(i);
						}
					}
				}
			}
		}
		t = null;
		work = false;
		System.out.println("Stopping seeker thread no. " + thread_id);
	}
	
	public void stopWork()
	{
		work = false;
	}
	
	public boolean getStatus()
	{
		return t!=null?true:false;
	}
	
	public void startSearching(int id, int th_amount, int bits, boolean random_search, ArrayList<BigInteger> primes)
	{
		if(t != null)
		{
			System.out.println("Thread is still working.");
		}
		else
		{
			this.thread_amount  = th_amount;
			this.thread_id 		= id;
			this.bits			= bits;
			this.work 			= true;
			this.random_search  = random_search;
			this.found_primes	= primes;
			t = new Thread(this, "SeekerThread" + id);
			t.start();
		}
	}
	
	private BigInteger nextIteration(int skip)
	{
		int next = 1;
		BigInteger result;
		div_2 = (div_2 + 1) % 2;
		div_3 = (div_3 + 1) % 3;
		div_5 = (div_5 + 1) % 5;
		
		int a=0;
		while(true)
		{
			if(div_2 !=0 && div_3 !=0 && div_5 != 0)
			{
				if(a==skip)
					break;
				a++;
			}
			div_2 = (div_2 + 1) % 2;
			div_3 = (div_3 + 1) % 3;
			div_5 = (div_5 + 1) % 5;
			next++;
		}
		
		result = new BigInteger(Integer.toString(next));
		return result;
	}
	
	private void setIteratorState()
	{
		BigInteger two = new BigInteger("2");
		BigInteger three = new BigInteger("3");
		BigInteger five = new BigInteger("5");
		div_2 = i.mod(two).intValue();
		div_3 = i.mod(three).intValue();
		div_5 = i.mod(five).intValue();
		BigInteger added_value = BigInteger.ZERO;
		added_value = added_value.add(nextIteration(thread_id));
		i = i.add(added_value);
	}
	
	private boolean checkPrime(int accuracy)
	{
		BigInteger n = i.subtract(BigInteger.ONE);
		BigInteger two_pow_s = new BigInteger("2");
		
		int s;
		for(s=1; ; s++, two_pow_s = two_pow_s.multiply(big_two))
		{
			if(n.mod(two_pow_s) != BigInteger.ZERO)
				break;
		}
		two_pow_s = two_pow_s.shiftRight(1);
		s -= 1;
		
		BigInteger t = n.divide(two_pow_s);
		
		for(int ii=0; ii<accuracy; ii++)
		{
			int nlen = i.bitLength();
			BigInteger nm1 = n;
			BigInteger a, ss;
			Random rnd = new Random();
			do {
			    ss = new BigInteger(nlen + 100, rnd);
			    a = ss.mod(i.subtract(big_two));
			} while (ss.subtract(a).add(nm1).bitLength() >= nlen + 100);
			a = a.add(big_two);

			BigInteger power_result = fastPower(a, t);
			if(power_result.compareTo(BigInteger.ONE) == 0 || power_result.compareTo(n) == 0)
				continue;
			else
			{
				int x;
				for(x=1; x < s; x++)
				{
					power_result = power_result.multiply(power_result);
					power_result = power_result.mod(i);
					if(power_result.compareTo(n) == 0)
						break;
					if(power_result.compareTo(BigInteger.ONE) == 0)
						return false;
				}
				if(x == s)
					return false;
			}
		}
		return true;
	}
	
	public BigInteger fastPower(BigInteger a, BigInteger t)
	{
		BigInteger result = BigInteger.ONE;
		while(t.compareTo(BigInteger.ZERO) == 1)
		{
			if(t.getLowestSetBit() == 0)
			{
				result = result.multiply(a);
			}
			
			a = a.multiply(a);
			t = t.shiftRight(1);
			a = a.mod(i);
			result = result.mod(i);
		}
		return result;
	}
}
