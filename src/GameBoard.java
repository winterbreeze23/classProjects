import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

/*
 * phrase:    a 4-character Chinese fixed phrase that expresses a commonly known idea,
 *            takes the form of ABCD
 * subphrase: a phrase that is the first or second half of a phrase.
 *            e.g. for phrase ABCD, the two subphrases are AB and CD
 * word match logic: AB - AB (everyone would be able to match)
 *                   CD - CD (everyone would be able to match)
 *                   AB - CD (Chinese speakers / learners would be able to match)
 *                   CD - AB (Chinese speakers / learners would be able to match)
 * line match logic: 1. tiles can be connected by one straight unobstructed line
 *                      X ----- X OR X
 *                                   |
 *                                   |
 *                                   X
 *                   2. tiles can be connected by two straight unobstructed lines
 *                      X -----| OR  X
 *                             |     |
 *                             X     |-----X
 *                   3. tiles can be connected by three straight unobstructed lines
 *                      X ---|                |-----X
 *                           |                |
 *                           |----- X OR X ---|
 *                   4. tiles can be connected by three straight unobstructued lines
 *                      X            X---------
 *                      |   X  OR             |
 *                      |   |            X----|
 *                      |----
 */

@SuppressWarnings("serial")
public class GameBoard extends JPanel {
	
	// the state of game
	    private boolean playing = false; // whether the game is running 
	    private JLabel status; // Current status text, i.e. "Running..."
	    private int numTilesOnBoard = 0;
	    private JButton current;
	    private int rowCurr; // rowIndex of current tile
	    private int colCurr; // colIndex of current tile

	    // Game constants
	    public static final int BOARD_WIDTH = 960; // 6 columns
	    public static final int BOARD_HEIGHT = 640; // 8 rows
	    public static final int TILE_WIDTH = 160; 
	    public static final int TILE_HEIGHT = 80; 

	    // Update interval for timer, in milliseconds
	    public static final int INTERVAL = 35;
			
		// Game contents
	        // 8 different phrases; 16 different subphrases
	        // 6 different phrases; 12 different subphrases
		private final int NUM_PHRASE = 8; 
		private final int NUM_SUBPHRASE = NUM_PHRASE * 2;
		private final int NUM_TILES = 48;
		private final int NUM_ROWS = 8;
		private final int NUM_COLS = 6;
		private final int NUM_REPEATS = NUM_TILES / NUM_SUBPHRASE; // num repeats of a tile with the same subphrase
		
		private List<String> phraseListAll = new ArrayList<>(); // ArrayList coz want access to index
		private List<String> phraseListUsed = new ArrayList<>(); // ArrayList coz want access to index
		private String filename = "files/phrases.txt";
		private String reportFile = "files/report.txt";
		private List<String> repeatedSubphrases = new ArrayList<>(); // list coz want repeat, ArrayList for easy access
		private Set<String> reportPhrases = new TreeSet<>(); // list coz don't want repeat 
		
		// getters
		
		public List<String> getRepeatedSubphrases() {
			return repeatedSubphrases;
		}
		
		public int getNumTilesOnBoard() {
			return numTilesOnBoard;
		}
		
		public List<String> getPhraseListUsed() {
			return phraseListUsed;
		}
		
		public List<String> getPhraseListAll() {
			return phraseListAll;
		}
		
		public static String[] shuffleStringArr(String[] arr) {
			List<String> stringList = Arrays.asList(arr);
			Collections.shuffle(stringList);
			String[] shuffledArr = (String[]) stringList.toArray();
			return shuffledArr;
		}
		
		public void loadPhrases(String filename) {
			// read in all phrases
			try (BufferedReader br = new BufferedReader(new FileReader(filename)))
			{
				String s;
				while ((s = br.readLine()) != null) {
					phraseListAll.add(s);
					
					// check the length, if length is not 2-4, throw exception 
					if (s.length() < 2 || s.length() > 4) {
						throw new IllegalArgumentException("Length of string must be 2, 3, 4");
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			
			phraseListUsed.clear();
			repeatedSubphrases.clear();
			
			// shuffle all of the phrases, put NUM_PHRASE number of phrases into another list to be used
			// this makes the phrases used each game random
			Collections.shuffle(phraseListAll);
			for (int i = 0; i < NUM_PHRASE; i++) {
				
				// in case the list of all phrases is shorter than required, repeated load from start
				if (i >= phraseListAll.size()) {
					//throw new IllegalArgumentException("Number of phrases is too few");
					break;
				}
				phraseListUsed.add(phraseListAll.get(i));
			}
			
			for (int i = 0; i < phraseListUsed.size(); i++) {
				String phrase = phraseListUsed.get(i);
				String a;
				String b;
				
				if (phrase.length() == 4) {
					a = phrase.substring(0, 2);
					b = phrase.substring(2, 4);
				} else if (phrase.length() == 2) {
					a = phrase.substring(0, 1);
					b = phrase.substring(1, 2);
				} else if (phrase.length() == 3) {
					a = phrase.substring(0, 2);
					b = phrase.substring(2, 3);
				} else {
					throw new IllegalArgumentException("Invalid number of characters in phrase");
				}
				
				// create NUM_REPEATS number of repeated tiles for each subphrase
				for (int j = 0; j < NUM_REPEATS; j ++) {
					repeatedSubphrases.add(a);
					repeatedSubphrases.add(b);
				}
			}			
		}
		
		// Splits phrases into subphrases, loads into repeatedTiles
		public void loadPhrases(String[] phraseArr) {
			
			String[] shuffledArr = shuffleStringArr(phraseArr);
			
			// if the length of phraseArr is greater than NUM_PHRASE required by the game, ignore rest
			for (int i = 0; i < Math.min(NUM_PHRASE, shuffledArr.length); i++) {
				String a = shuffledArr[i].substring(0, 2);
				String b = shuffledArr[i].substring(2, 4);
				
				// create NUM_REPEATS number of repeated tiles for each subphrase
				for (int j = 0; j < NUM_REPEATS; j ++) {
					repeatedSubphrases.add(a);
					repeatedSubphrases.add(b);
				}
			}
		}
		
		// Game tiles
		private JButton[][] tiles = new JButton[NUM_ROWS][NUM_COLS];
		
		public void createTiles() {
			/*
			JPanel tiles_panel = new JPanel();
			this.add(tiles_panel, BorderLayout.WEST);
			tiles_panel.setLayout(new GridLayout(8, 6));
			*/
			
			for (int i = 0; i < NUM_TILES; i++) {
				JButton tile = new JButton();
				tile.setContentAreaFilled(false);
		        tile.setOpaque(false);
				this.add(tile);
				
				/*
				 * load into 2D array in order to refer to and check tiles
				 * tiles[0][0] is at the top left corner; tiles[7][5] is at the bottom right corner
				 */
				int rowIndex = i / NUM_COLS; // whole number quotient when divided by 6
				int colIndex = i % NUM_COLS; // remainder when divided by 6
				tiles[rowIndex][colIndex] = tile;
				
				tiles[rowIndex][colIndex].addActionListener(e -> {
					
					// if this tile is visible and there is no current selected, set current to this
					if (tiles[rowIndex][colIndex].isVisible() && current == null) {
						current = tiles[rowIndex][colIndex];
						rowCurr = rowIndex;
						colCurr = colIndex;
						System.out.println("Current is " + "row " + rowCurr + " col " + colCurr);
					// if this tile is visible and this is not the same tile as the current selected
					} else if (tiles[rowIndex][colIndex].isVisible() && current != null &&
							!(rowCurr == rowIndex && colCurr == colIndex)) {
						match(rowIndex, colIndex);
					} else if (tiles[rowIndex][colIndex].isVisible() && current != null &&
							(rowCurr == rowIndex && colCurr == colIndex)) {
						current = tiles[rowIndex][colIndex];
						rowCurr = rowIndex;
						colCurr = colIndex;
						System.out.println("Current is " + "row " + rowCurr + " col " + colCurr);
					}
				});
			}
		}
		
		public void loadPhrasesToTiles() {
			Collections.shuffle(repeatedSubphrases);
			
			int k = 0;
			// reset numTilesOnBoard to 0
			numTilesOnBoard = 0; 
			
			for (int i = 0; i < NUM_ROWS; i++) {
				for (int j = 0; j < NUM_COLS; j++) {
					if (k < repeatedSubphrases.size()) {
						tiles[i][j].setFont(new Font("Arial", Font.PLAIN, 40));
						tiles[i][j].setText(repeatedSubphrases.get(k));
						tiles[i][j].setVisible(true);
						numTilesOnBoard++;
						k++;
					} else break;
				}
			}
		}
		
		public void loadTiles() {
			Collections.shuffle(repeatedSubphrases);
			
			for (int i = 0; i < repeatedSubphrases.size(); i++) {
				JButton tile = new JButton(repeatedSubphrases.get(i));
				tile.setFont(new Font("Arial", Font.PLAIN, 40));
				this.add(tile);
				numTilesOnBoard++;
				
				/*
				 * load into 2D array in order to refer to and check tiles
				 * tiles[0][0] is at the top left corner; tiles[7][5] is at the bottom right corner
				 */
				int rowIndex = i / NUM_COLS; // whole number quotient when divided by 6
				int colIndex = i % NUM_COLS; // remainder when divided by 6
				tiles[rowIndex][colIndex] = tile;
				
				tiles[rowIndex][colIndex].addActionListener(e -> {
					
					// if this tile is visible and there is no current selected, set current to this
					if (tiles[rowIndex][colIndex].isVisible() && current == null) {
						current = tiles[rowIndex][colIndex];
						rowCurr = rowIndex;
						colCurr = colIndex;
						System.out.println("Current is " + "row " + rowCurr + " col " + colCurr);
					// if this tile is visible and this is not the same tile as the current selected
					} else if (tiles[rowIndex][colIndex].isVisible() && current != null &&
							!(rowCurr == rowIndex && colCurr == colIndex)) {
						match(rowIndex, colIndex);
					} else if (tiles[rowIndex][colIndex].isVisible() && current != null &&
							(rowCurr == rowIndex && colCurr == colIndex)) {
						current = tiles[rowIndex][colIndex];
						rowCurr = rowIndex;
						colCurr = colIndex;
						System.out.println("Current is " + "row " + rowCurr + " col " + colCurr);
					}
				});
			}
		}
		
		
		public void createPhrasePanel() {
			JPanel phrases_panel = new JPanel();
			this.add(phrases_panel, BorderLayout.EAST);
			
			phrases_panel.setLayout(new BoxLayout(phrases_panel, BoxLayout.Y_AXIS));
	        
	        for (String s : phraseListUsed) {
	        	JLabel phraseLabel = new JLabel(s);
	        	phraseLabel.setFont(new Font("Arial", Font.PLAIN, 35));
	        	phraseLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
	        	phraseLabel.setAlignmentY(JComponent.CENTER_ALIGNMENT);
	        	phraseLabel.setForeground(Color.GRAY);
	        	phraseLabel.setBorder(new EmptyBorder(20, 20, 20, 20));
	        	phrases_panel.add(phraseLabel);
	        }
		}
		
		public void writeToReport() {
			for (String s : reportPhrases) {
				System.out.println(s);
			}
			
			Writer out = null;
			
			// read in all phrases
			try 
			{
				out = new FileWriter(new File(reportFile));
				for (String s : reportPhrases) {
					out.write(s);
					out.write(System.lineSeparator());
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		// for testing purposes
		public void setCurrent(int rowIndex, int colIndex) {
			rowCurr = rowIndex;
			colCurr = colIndex;
			current = tiles[rowIndex][colIndex];
		}
		
		// for testing purposes
		public void setTileText(int rowIndex, int colIndex, String subphrase) {
			tiles[rowIndex][colIndex].setText(subphrase);
		}
		
		// for testing purposes
		public void setTileVisibility(int rowIndex, int colIndex, boolean isVisible) {
			tiles[rowIndex][colIndex].setVisible(isVisible);
		}
		
		public JButton getTileAt(int rowIndex, int colIndex) {
			return tiles[rowIndex][colIndex];
		}
		 
		public void setNumTilesOnBoard(int numTilesOnBoard) {
			this.numTilesOnBoard = numTilesOnBoard;
		}
		
		public String getStatusText() {
			return status.getText();
		}
		
		public boolean getPlayingStatus() {
			return playing;
		}

		public void match(int row2, int col2) {
			// if there is a current selected tile and the second tile is not the current, try match
			if (current != null && !(rowCurr == row2 && colCurr == col2)
				// check if strings are the same or are subphrases of the same phrase
				&& matchWord(row2, col2) &&
				  (match1(rowCurr, colCurr, row2, col2) ||
				   match2(row2, col2) ||
				   match3(row2, col2) ||
				   match4(row2, col2)))
				   {
						matchDisappear(tiles[rowCurr][colCurr], tiles[row2][col2]);
						// after matching, set current to null
						current = null;
						rowCurr = -1;
						colCurr = -1;
						System.out.println("Current is null");
			   // if match fails, set current to null
			} else {
				current = tiles[row2][col2];
				rowCurr = row2;
				colCurr = col2;
				System.out.println("Current is " + "row " + rowCurr + " col " + colCurr);
			}
		}
		
		public boolean matchWord(int row2, int col2) {
			String str1 = current.getText();
			String str2 = tiles[row2][col2].getText();
			
			if (str1.equals(str2)) {
				// if user matches identical characters, do not add to reportPhrases
				return true;
			
				// multiple if block to find the correct ordering of subphrases in the actual Chinese phrase
			} else if (phraseListUsed.contains(str1 + str2)) {
				reportPhrases.add("Correct: " + str1 + str2 + "\n");
				return true;
		    } else if (phraseListUsed.contains(str2 + str1)) {
				reportPhrases.add("Correct: " + str2 + str1 + "\n");
				return true;
			} else {
				reportPhrases.add("Wrong: " + str1 + str2 + '\n');
				return false;
			}	
		}
		

		public boolean match1(int row1, int col1, int row2, int col2) {
			// row1, col1, row2, col2
			
			// if on the same row
			if (row1 == row2) {
				// if adjacent, return true
				if (Math.abs(col1 - col2) == 1) {
					return true;
				}
				// loop through tiles between them horizontally
				for (int i = Math.min(col1, col2) + 1; i < Math.max(col1, col2); i++) {
					// if there is a tile between them, return false
					if (tiles[row1][i].isVisible()) {
						//System.out.println("row " + row1 + " col " + i + " " + tiles[row1][i].isVisible());
						return false;
					}
				}
				// looped through all the tiles btw, no obstruction, return true
				return true;
			} else if (col1 == col2) {
				// if adjacent, return true
				if (Math.abs(row1 - row2) == 1) {
					return true;
				}
				// loop through tiles between them horizontally
				for (int i = Math.min(row1, row2) + 1; i < Math.max(row1, row2); i++) {
					// if there is a tile between them, return false
					if (tiles[i][col1].isVisible()) {
						System.out.println("row " + i + " col " + col1 + " false");
						return false;
					}
				}
				// looped through all the tiles btw, no obstruction, return true
				return true;
			} else return false; // not in same row or col
		}
		

		public boolean match2(int row2, int col2) {
		    // two turning points are tiles[rowCurr][col2] and tiles[row2][colCurr]
				
			// check L-shape going through first corner
			if (!tiles[rowCurr][col2].isVisible() && // check corners as match1 does no check corner
					match1(rowCurr, colCurr, rowCurr, col2) && match1(rowCurr, col2, row2, col2)) {
				return true;
			// check L-shape going through second corner
			} else if (!tiles[row2][colCurr].isVisible() && // check corners as match1 does no check corner
					match1(rowCurr, colCurr, row2, colCurr) && match1(row2, colCurr, row2, col2)) {
				return true;
			}
			return false;
		}
		

		public boolean match3(int row2, int col2) {
			// two sets of multiple turning points are 
			// A: two t.p. on the same col, one on the same row as current tile, the other as the tile 2
			// B: two t.p. on the same row, one on the same col as current tile, the other as the tile 2
			
			int leftCol = Math.min(colCurr, col2);
			int rightCol = Math.max(colCurr, col2);
			int topRow = Math.min(rowCurr, row2);
			int bottomRow = Math.max(rowCurr, row2);
			
			// set A
			for (int i = leftCol + 1; i < rightCol; i++) {
				// check match1 for the 3 straight lines
				if (!tiles[rowCurr][i].isVisible() && // check corners as match1 does no check corner
					!tiles[row2][i].isVisible() &&
					match1(rowCurr, colCurr, rowCurr, i) &&
					match1(rowCurr, i, row2, i) &&
					match1(row2, i, row2, col2)) {
					return true;
				}
			}
			
			// set B
			for (int i = topRow + 1; i < bottomRow; i++) {
				// check match1 for the 3 straight lines
				if (!tiles[i][colCurr].isVisible() && // check corners as match1 does no check corner
					!tiles[i][col2].isVisible() &&
					match1(rowCurr, colCurr, i, colCurr) &&
					match1(i, colCurr, i, col2) &&
					match1(i, col2, row2, col2)) {
					return true;
				}
			}
			return false;
		}
		
		
		public boolean match4(int row2, int col2) {
			/*
			 * 4 sets of multiple t.p.
			 * A: 2 t.p. on the same row, each above current tile and tile 2
			 * B: 2 t.p. on the same row, each below current tile and tile 2
			 * C: 2 t.p. on the same col, each to the right of current tile and tile 2
			 * D: 2 t.p. on the same col, each to the left of current tile and tile 2
			 */
			
			int leftCol = Math.min(colCurr, col2);
			int rightCol = Math.max(colCurr, col2);
			int topRow = Math.min(rowCurr, row2);
			int bottomRow = Math.max(rowCurr, row2);
			
			// set A - above
			// first check for both tiles, if each if at the top row or it is clear above that tile, return true
			if ((rowCurr == 0 || 
					(match1(rowCurr, colCurr, 0, colCurr) && !tiles[0][colCurr].isVisible())) 
			   &&
				 (row2 == 0 || 
				 	(match1(row2, col2, 0, col2) && !tiles[0][col2].isVisible()))) {
				return true;
			} else {
				for (int i = topRow - 1; i > -1; i--) {
					if (!tiles[i][colCurr].isVisible() && // check corners as match1 does no check corner
						!tiles[i][col2].isVisible() &&
						match1(rowCurr, colCurr, i, colCurr) &&
						match1(i, colCurr, i, col2) &&
						match1(i, col2, row2, col2)) {
						return true;
					}
				}
			}
			
			// set B - below
			if ((rowCurr == NUM_ROWS - 1 || 
					(match1(rowCurr, colCurr, NUM_ROWS - 1, colCurr) && !tiles[NUM_ROWS - 1][colCurr].isVisible())) 
			   &&
				 (row2 == NUM_ROWS - 1 || 
				    (match1(row2, col2, NUM_ROWS - 1, col2) && !tiles[NUM_ROWS - 1][col2].isVisible()))) {
					return true;
			} else {
				for (int i = bottomRow + 1; i < NUM_ROWS; i++) {
					if (!tiles[i][colCurr].isVisible() && // check corners as match1 does no check corner
						!tiles[i][col2].isVisible() &&
						match1(rowCurr, colCurr, i, colCurr) &&
						match1(i, colCurr, i, col2) &&
						match1(i, col2, row2, col2)) {
						return true;
					}
				}
			}
			
			// set C - right
			if ((colCurr == NUM_COLS - 1 || 
					(match1(rowCurr, colCurr, rowCurr, NUM_COLS - 1) && !tiles[rowCurr][NUM_COLS - 1].isVisible()))
			   &&
				 (col2 == NUM_COLS - 1 || 
				    (match1(row2, col2, row2, NUM_COLS - 1) && !tiles[row2][NUM_COLS - 1].isVisible()))) {
				return true;
			} else {
				for (int i = rightCol + 1; i < NUM_COLS; i++) {
					if (!tiles[rowCurr][i].isVisible() && // check corners as match1 does no check corner
						!tiles[row2][i].isVisible() &&
						match1(rowCurr, colCurr, rowCurr, i) &&
						match1(rowCurr, i, row2, i) &&
						match1(row2, i, row2, col2)) {
						return true;
					}
				}
			}
			
			// set D - left
			
			if ((colCurr == 0 || 
					(match1(rowCurr, colCurr, rowCurr, 0) && !tiles[rowCurr][0].isVisible()))
			   &&
				 (col2 == 0 || 
				    (match1(row2, col2, row2, 0) && !tiles[row2][0].isVisible()))) {
				return true;
			} else {
				for (int i = leftCol - 1; i > -1; i--) {
					if (!tiles[rowCurr][i].isVisible() && // check corners as match1 does no check corner
						!tiles[row2][i].isVisible() &&
						match1(rowCurr, colCurr, rowCurr, i) &&
						match1(rowCurr, i, row2, i) &&
						match1(row2, i, row2, col2)) {
						return true;
					}
				}
			}
			return false;
		}
		

		public void matchDisappear(JButton a, JButton b) {
			if (a.isVisible() && b.isVisible()) {
				a.setVisible(false);
				b.setVisible(false);
				numTilesOnBoard -= 2;
			}
		}

		// if no String[] phraseArr is passed in, read phrases from file
	    public GameBoard(JLabel status) {
	    	// create timer
	        Timer timer = new Timer(INTERVAL, new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                tick();
	            }
	        });
	        timer.start(); 
	        this.status = status;
	        createTiles();
	        
	        // clear the reportPhrases every time the program is opened
	        reportPhrases.clear();
	        reportPhrases.add("----- Your Learning Report -----" + "\n");
	        
	        //createPhrasePanel();
	    }

	    /**
	     * (Re-)set the game to its initial state.
	     */
	    public void reset() {
	    	loadPhrases(filename);
	        loadPhrasesToTiles();
	        playing = true;
	        status.setText("Match identical characters or word pairs!");
	    }

	    /**
	     * This method is called every time the timer defined in the constructor triggers.
	     */
	    void tick() {
	        if (playing) {
	            // check for the game end conditions
	            if (numTilesOnBoard == 0) {
	                playing = false;
	                status.setText("You win!");
	            } 
	            // update the display
	            repaint();
	        }
	    }

	    @Override
	    public Dimension getPreferredSize() {
	        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
	    }
}