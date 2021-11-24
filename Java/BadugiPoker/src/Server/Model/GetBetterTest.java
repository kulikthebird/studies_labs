package Server.Model;

import static org.junit.Assert.*;

import org.junit.Test;

public class GetBetterTest {

	@Test
	public void test() {
		Deck_Model deck = new Deck_Model();
		String[] c0 = { "C1","C2","K4","T7" };
		String[] c1 = { "C1","C4","K4","T6" };
		int winner;
		winner = deck.getBetter(c0, c1);
		
		assert winner != 0;
	}
	
	@Test
	public void test1() {
		Deck_Model deck = new Deck_Model();
		String[] c0 = { "C1","C2","K4","T7" };
		String[] c1 = { "C1","K1","K4","T6" };
		int winner;
		winner = deck.getBetter(c0, c1);
		
		assert winner != 0;
	}
	
	@Test
	public void test2() {
		Deck_Model deck = new Deck_Model();
		String[] c0 = { "C1","C2","C4","T5" };
		String[] c1 = { "C1","C4","K4","T6" };
		int winner;
		winner = deck.getBetter(c0, c1);
		
		assert winner != 0;
	}
	
	@Test
	public void test3() {
		Deck_Model deck = new Deck_Model();
		String[] c0 = { "P4","C4","K4","T5" };
		String[] c1 = { "C1","C4","K4","T6" };
		int winner;
		winner = deck.getBetter(c0, c1);
		
		assert winner != 0;
	}
}
