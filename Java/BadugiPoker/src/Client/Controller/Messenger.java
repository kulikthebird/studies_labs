package Client.Controller;

import Client.Exceptions.ConnectException;


public interface Messenger
{
	void ReceiveServerMessage(String input) throws ConnectException;
}
