import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

public class RSA 
{
	private ArrayList<BigInteger> primes_list;
	private BigInteger n;
	private BigInteger phi;
	private BigInteger e;
	private BigInteger d;
	
	private int bits;
	private int threads_amount;
	
	private byte[] buffer;
	
	private File file;

	
	public void Gen(int k, int d, int threads_amount, String keystorename)
	{
		bits = d;
		threads_amount = k;
		PrimeNumberSeeker pns = new PrimeNumberSeeker();
		pns.seekForPrimes(d, k, threads_amount, true, false);
		primes_list = pns.found_prime_numbers;
		n = BigInteger.ONE;
		phi = BigInteger.ONE;
		for(int i=0; i<k; i++)
			n = n.multiply(primes_list.get(i));
		for(int i=0; i<k; i++)
			phi = phi.multiply(primes_list.get(i).subtract(BigInteger.ONE));
		do
		{
			e = randomInt();
		} while(e.gcd(phi).compareTo(BigInteger.ONE) != 0);
		this.d = e.modInverse(phi);
		FileOutputStream keystore;
		try {
			keystore = new FileOutputStream(keystorename);
			keystore.write(this.e.toString().getBytes());
			keystore.write(new String("\n").getBytes());
			keystore.write(this.d.toString().getBytes());
			keystore.write(new String("\n").getBytes());
			keystore.write(this.n.toString().getBytes());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
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
			a = a.mod(n);
			result = result.mod(n);
		}
		return result;
	}
	
	public void readKeystore(String keystorename)
	{
		FileInputStream keystore;
		BufferedReader in;
		InputStreamReader isr;
		try {
			keystore = new FileInputStream(keystorename);
			isr = new InputStreamReader(keystore);
			in = new BufferedReader(isr);
			e = new BigInteger(in.readLine());
			d = new BigInteger(in.readLine());
			n = new BigInteger(in.readLine());
			isr.close();
			in.close();
			keystore.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void Enc(String input_filename, String output_filename, String keystorename)
	{
		readKeystore(keystorename);
		buffer = new byte[n.bitLength()/8 - (n.bitLength()%8==0?1:0)];
		int out_bytes = n.bitLength()/8 + (n.bitLength()%8!=0?1:0);
		
		FileInputStream input;
		FileOutputStream output;
		BigInteger number;
		try {
			input = new FileInputStream(input_filename);
			output = new FileOutputStream(output_filename);
			
			int bytes = 0;
			while( (bytes = input.read(buffer)) != -1)
			{
				if(bytes != buffer.length)
					for(int i=bytes; i< buffer.length; i++)
						buffer[i] = 0;
				number = new BigInteger(1, buffer);
				number = fastPower(number, e);
				byte[] num_bytes = number.toByteArray();
				if(num_bytes[0] == 0 && num_bytes.length != 1)
					num_bytes = Arrays.copyOfRange(num_bytes, 1, num_bytes.length);
				for(int i=0; i<out_bytes-num_bytes.length; i++)
					output.write(new byte[]{0});
				output.write(num_bytes);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void Dec(String input_filename, String output_filename, String keystorename)
	{
		readKeystore(keystorename);
		buffer = new byte[n.bitLength()/8 + (n.bitLength()%8!=0?1:0)];
		int out_bytes = n.bitLength()/8 - (n.bitLength()%8==0?1:0);
		
		FileInputStream input;
		FileOutputStream output;
		BigInteger number;
		try {
			input = new FileInputStream(input_filename);
			output = new FileOutputStream(output_filename);
			
			while(input.read(buffer) != -1)
			{
				number = new BigInteger(1, buffer);
				number = fastPower(number, d);
				byte[] num_bytes = number.toByteArray();
				//num_bytes = Arrays.copyOfRange(num_bytes, 1, num_bytes.length);
				if(num_bytes[0] == 0 && num_bytes.length != 1)
					num_bytes = Arrays.copyOfRange(num_bytes, 1, num_bytes.length);
				for(int i=0; i<out_bytes - num_bytes.length; i++)
					output.write(new byte[]{0});
				output.write(num_bytes);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private BigInteger randomInt()
	{
		BigInteger result;
		int nlen = phi.bitLength();
		BigInteger nm1 = phi;
		BigInteger ss;
		SecureRandom rnd = new SecureRandom();
		do {
		    ss = new BigInteger(nlen + 100, rnd);
		    result = ss.mod(phi);
		} while (ss.subtract(result).add(nm1).bitLength() >= nlen + 100);
		return result;
	}
}
