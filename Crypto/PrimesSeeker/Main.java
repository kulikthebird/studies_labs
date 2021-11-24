import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Main
{

	public static void main(String[] args) throws InterruptedException
	{
		test_EncryptDecrypt();
	}
	
	/* Tests */
	
	public static void generateInput(int bytes)
	{
		try {
			FileOutputStream file = new FileOutputStream("input.txt");
			Random rnd = new Random();
			for(int i=0; i<bytes; i++)
			{
				file.write(new byte[]{(byte)rnd.nextInt(256)});
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean checkOutput()
	{
		try {
			FileInputStream file_in = new FileInputStream("input.txt");
			FileInputStream file_out = new FileInputStream("result.txt");
			
			byte[] bb1 = new byte[1];
			byte[] bb2 = new byte[1];
			
			while(file_in.read(bb1) != -1 && file_out.read(bb2) != -1)
			{
				if(bb1[0] != bb2[0])
				{
					System.out.println("checkOutput test failed.");
					return false;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public static void test_EncryptDecrypt()
	{
		for(int i=0; i<20; i++)
		{
			generateInput((int)Math.pow(2, i));
			RSA test = new RSA();
			test.Gen(5, 14, 1, "keystore.key");
			test.Enc("input.txt", "output.txt", "keystore.key");
			test.Dec("output.txt", "result.txt", "keystore.key");
			if(!checkOutput())
				break;
		}
	}
}
