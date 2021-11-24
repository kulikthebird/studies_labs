package Client.Exceptions;

/**
 * Wyjątek rzucany gdy nie udała się inicjalizacja narzędzi potrzebnych do prawidłowego działania aplikacji.
 * @author Tomek
 *
 */
public class InitException extends Exception 
{
	public InitException(String msg)
	{
		this.msg = msg;
	}
	
	public String getFile()
	{
		return msg;
	}
	
	private String msg;
}
