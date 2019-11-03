import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;

import org.junit.Test;

public class test {
	String phraseTestFile = "files/phrases_test.txt";
	String subphraseTestFile = "files/subphrases_test.txt";
	List<String> subphraseListTest = new ArrayList<>(); // wuguang, shise, cheshui, malong
	
	public void helperLoadPhrases(String filename) {
		try (BufferedReader br = new BufferedReader(new FileReader(filename)))
		{
			String s;
			while ((s = br.readLine()) != null) {
				subphraseListTest.add(s);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testLoadPhrasesException()  {
		JLabel status = new JLabel("Match");
		GameBoard board = new GameBoard(status);
		try {
			board.loadPhrases("files/phrases_test_wrong_format.txt");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testLoadPhrasesSubphrases() {
		subphraseListTest.clear();
		helperLoadPhrases(subphraseTestFile);
		
		String wuguang = subphraseListTest.get(0);
		String shise = subphraseListTest.get(1);
		String cheshui = subphraseListTest.get(2);
		String malong = subphraseListTest.get(3);
		
		JLabel status = new JLabel("Match");
		GameBoard board = new GameBoard(status);
		board.loadPhrases(phraseTestFile);
		
		// create expected result
		List<String> result = new LinkedList<>();
		for (int i = 0; i < 3; i++) {
			result.add(wuguang);
			result.add(shise);
			result.add(cheshui);
			result.add(malong);
		}
		
		assertEquals(result.size(), board.getRepeatedSubphrases().size());
		//assertEquals(result, board.getRepeatedSubphrases().sort());
	}
	
	@Test
	public void testMatchDisappear() {
		JLabel status = new JLabel("Match");
		GameBoard board = new GameBoard(status);
		board.loadPhrases(phraseTestFile);
		board.loadPhrasesToTiles();
		board.matchDisappear(board.getTileAt(0, 0), board.getTileAt(1, 1));
		
		assertEquals(10, board.getNumTilesOnBoard());
		assertFalse(board.getTileAt(0, 0).isVisible());
		assertFalse(board.getTileAt(1, 1).isVisible());
	}
	
	
	@Test
	public void testMatchWord() {
		subphraseListTest.clear();
		helperLoadPhrases(subphraseTestFile);
		String wuguang = subphraseListTest.get(0);
		String shise = subphraseListTest.get(1);
		String cheshui = subphraseListTest.get(2);
		
		JLabel status = new JLabel("Match");
		GameBoard board = new GameBoard(status);
		board.loadPhrases(phraseTestFile);
		
		board.setTileText(0, 0, wuguang);
		board.setTileText(0, 1, wuguang);
		board.setTileText(0, 2, shise);
		board.setTileText(0, 3, cheshui);
		
		board.setCurrent(0, 0);
		assertTrue(board.matchWord(0, 1));
		assertTrue(board.matchWord(0, 2));
		assertFalse(board.matchWord(0, 3));
		
		board.setCurrent(0, 2);
		assertTrue(board.matchWord(0, 0));
	}
	
	@Test
	public void testMatch1() {
		subphraseListTest.clear();
		helperLoadPhrases(subphraseTestFile);
		String wuguang = subphraseListTest.get(0);
		String shise = subphraseListTest.get(1);
		String cheshui = subphraseListTest.get(2);
		
		JLabel status = new JLabel("Match");
		GameBoard board = new GameBoard(status);
		
		board.setTileText(0, 0, wuguang);
		board.setTileText(1, 0, wuguang);
		board.setTileText(0, 1, wuguang);
		board.setTileText(0, 2, shise);
		board.setTileText(0, 3, cheshui);
		
		board.setCurrent(0, 0);
		assertTrue(board.match1(0, 0, 0, 1)); // adjacent
		assertTrue(board.match1(0, 0, 1, 0)); // adjacent
		assertFalse(board.match1(0, 0, 0, 2)); // obstructed, cannot match
		
		board.setTileVisibility(0, 1, false);
		assertTrue(board.match1(0, 0, 0, 2)); // unobstructed, can match
	}
	
	@Test
	public void testMatch2() {
		subphraseListTest.clear();
		helperLoadPhrases(subphraseTestFile);
		String wuguang = subphraseListTest.get(0);
		String shise = subphraseListTest.get(1);
		String cheshui = subphraseListTest.get(2);
		
		JLabel status = new JLabel("Match");
		GameBoard board = new GameBoard(status);
		
		board.setTileText(0, 0, wuguang);
		board.setTileText(1, 0, wuguang);
		board.setTileText(1, 1, wuguang);
		board.setTileText(0, 1, wuguang);
		board.setTileText(0, 2, shise);
		board.setTileText(0, 3, cheshui);
		
		board.setCurrent(0, 0);
		assertFalse(board.match2(0, 1)); // adjacent, should be match 1
		assertFalse(board.match1(0, 0, 1, 1)); // obstructed, cannot match
		
		board.setTileVisibility(0, 1, false);
		assertTrue(board.match2(1, 1)); // unobstructed, can match
	}
	
	@Test
	public void testMatch3() {
		subphraseListTest.clear();
		helperLoadPhrases(subphraseTestFile);
		String wuguang = subphraseListTest.get(0);
		String shise = subphraseListTest.get(1);
		
		JLabel status = new JLabel("Match");
		GameBoard board = new GameBoard(status);
		
		board.setTileText(0, 0, wuguang);
		board.setTileText(1, 0, wuguang);
		board.setTileText(1, 1, wuguang);
		board.setTileText(0, 1, wuguang);
		board.setTileText(1, 2, shise);
		
		board.setCurrent(0, 0);
		board.setTileVisibility(1, 0, false);
		assertFalse(board.match3(0, 1)); // adjacent, should be match 1
		assertFalse(board.match3(1, 1)); // L-shape, should be match 2
		assertFalse(board.match3(1, 2)); // obstructed, cannot match
		
		board.setTileVisibility(0, 1, false);
		board.setTileVisibility(1, 1, false);
		assertTrue(board.match3(1, 2)); // unobstructed, can match
	}
	
	@Test
	public void testMatch4() {
		subphraseListTest.clear();
		helperLoadPhrases(subphraseTestFile);
		String wuguang = subphraseListTest.get(0);
		String shise = subphraseListTest.get(1);
		
		JLabel status = new JLabel("Match");
		GameBoard board = new GameBoard(status);
		
		board.setTileText(0, 0, wuguang);
		board.setTileText(1, 0, wuguang);
		board.setTileText(1, 1, wuguang);
		board.setTileText(0, 1, wuguang);
		board.setTileText(0, 2, wuguang);
		board.setTileText(1, 2, shise);
		
		board.setCurrent(0, 0);
		board.setTileVisibility(1, 0, false);
		assertTrue(board.match4(0, 1)); // adjacent, but match 4 also works
		assertTrue(board.match4(1, 1)); // L-shape, but match 4 also works
		assertTrue(board.match4(0, 2)); // can match
		assertFalse(board.match4(1, 2)); // obstructed, cannot match
	}
	
	@Test
	public void testStatusChange() {
		JLabel status = new JLabel("Match");
		GameBoard board = new GameBoard(status);
		
		board.reset();
		assertEquals(48, board.getNumTilesOnBoard());
		assertEquals("Match identical characters or word pairs!", board.getStatusText());
		assertEquals(true, board.getPlayingStatus());
		
	}

}
