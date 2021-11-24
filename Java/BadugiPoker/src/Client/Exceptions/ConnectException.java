package Client.Exceptions;


/**
 * Wyjątek rzucany w razie kłopotów z komunikacją między klientem i serwerem.
 * @author Tomek
 *
 */
public class ConnectException extends Exception
{
	public static final int WrongHostIP = 0;
	public static final int IOCreating = 1;
	public static final int IOTransfer = 2;
	public static final int SocketCreating = 3;
	public static final int Communication = 4;
	
	
	public ConnectException(int err)
	{
		this.err = err;
	}
	
	public int getErr()
	{
		return err;
	}
	
	private int err;
}
