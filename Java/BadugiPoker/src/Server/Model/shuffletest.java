package Server.Model;

import static org.junit.Assert.*;

import org.junit.Test;

public class shuffletest {

	@Test
	public void test() {
		Deck_Model deck = new Deck_Model();
		String[] c1 = new String[4];
		String[] c2 = new String[4];
		
		c1 = deck.GiveCards(4);
		c2 = deck.GiveCards(4);

		assertNotEquals(c1, c2);
	}

}
