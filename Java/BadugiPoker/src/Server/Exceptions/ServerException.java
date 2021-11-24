package Server.Exceptions;

public class ServerException extends Exception
{
	public static final int InputData = 0;
	
	
	public ServerException(int err)
	{
		this.err = err;
	}
	
	public int getErr()
	{
		return err;
	}
	
	private int err;
}
