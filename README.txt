The game plays like Mahjong, but instead of Mahjong tiles or Pokemons, each tile is half of a 4-character fixed phrase in the Chinese language. Players can match identical tiles just like they do in a game of Mahjong, or they can match the two subphrases that make up a fixed phrase. Through mix-and-match, players can get acquainted with an important part of Chinese culture. 

The classes in the program are Game and GameBoard.

The 4 core concepts are 
- 2D array (which I use to hold and make reference to the grid of buttons)
- Collections (which I use to store lists of phrases to check matchings, repeated subphrases to create repeated tiles. I also make use of the Collections.shuffle() method to randomize the phrases used in a game each time)
- File I/O (which I use to read in files that contain Chinese phrases. One can provide a file that contains a large number of Chinese phrases, and the program will randomly use 8 of them in any new game.)
- JUnit testing 

Left-click on a subphrase tile to select and left-click on another to match. Matched tiles will disappear from board. If the user eliminates all the tiles on the board, the user wins. 

Left-click on "New Game" button to start a new game with new phrases and different arrangements.

Left-click on "Get Learning Report" to get a report of the correct and wrong matches made, which will help the user improve his grasp of these phrases.

/*
 * phrase:    a 4-character Chinese fixed phrase that expresses a commonly known idea,
 *            takes the form of ABCD
 * subphrase: a phrase that is the first or second half of a phrase.
 *            e.g. for phrase ABCD, the two subphrases are AB and CD
 * word match logic: AB - AB (everyone would be able to match)
 *                   CD - CD (everyone would be able to match)
 *                   AB - CD (Chinese speakers / learners and 
 *                            anyone who refers to the list provided at the right, would be able to match)
 *                   CD - AB (Chinese speakers / learners and 
 *                            anyone who refers to the list provided at the right, would be able to match)
 * line match logic: 1. tiles can be connected by one straight unobstructed line
 *                      X ----- X OR X
 *                                   |
 *                                   |
 *                                   X
 *                   2. tiles can be connected by two straight unobstructed lines in a L-shape
 *                      X -----| OR  X
 *                             |     |
 *                             X     |-----X
 *                   3. tiles can be connected by three straight unobstructed lines in a Z-shape
 *                      X ---|                |-----X
 *                           |                |
 *                           |----- X OR X ---|
 *                   4. tiles can be connected by three straight unobstructed lines in a U-shape
 *                      X            X---------
 *                      |   X  OR             |
 *                      |   |            X----|
 *                      |----
 */