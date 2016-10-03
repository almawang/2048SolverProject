/** Game2048 - models the 2048 game
 * Originally written by Konstantin Bulenkov http://bulenkov.com/about
 * Modifications by Alma Wang
 */
package solver2048;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.WindowConstants;


public class Game2048Bit {
	
	public static final int LEFT=0;
	public static final int RIGHT=1;
	public static final int UP=2;
	public static final int DOWN=3;
	public static final double PIECE_PROB = .9; // probability of a new piece being a 2
	
	private static final long BITMASK = 0b1111;
	private static final long LINE_BITMASK = 0b1111111111111111;
	private static final int[] TILE_VALUES = {0, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768};
	
	private int target=2048;
	private int myScore = 0;
	private long myTiles;
	private LinkedList<Long> prevMoves=new LinkedList<Long>();
	private LinkedList<Integer> prevScores=new LinkedList<Integer>();
	
	  
	  public Game2048Bit() {
		  resetGame();
	  }
	  
	  /**
	   * Change the target score
	   */
	  public Game2048Bit(int target){
		  this.target=target;
		  resetGame();
	  }
	  
	  /**
	   * Make a copy of a game
	   */
	  public Game2048Bit(Game2048Bit game){
		  myTiles=game.myTiles;
		  myScore=game.myScore;
		  target=game.target;
	  }
	  
	  /**
	   * Reset the board
	   */
	  public void resetGame() {
		    myScore = 0;
		    myTiles = 0;
		    clearPrevs();
		    addTile();
		    addTile();
	  }
	  
	  /**
	   * Clear the previous moves/scores
	   */
	  public void clearPrevs(){
		  prevMoves.clear();
		  prevScores.clear();
	  }
	  
	  /**
	   * Returns a list of possible directions a player can move
	   */
	  public List<Integer> getMoves(){
		  LinkedList<Integer> moves=new LinkedList<Integer>();
		  int[] dirs={LEFT,RIGHT,UP,DOWN};
		  for(int d:dirs)
			  moves.add(d);
		  return moves;
	  }
	  
	  public boolean makeMove(int move){
		  boolean needTile=false;
		  prevMoves.addFirst(myTiles);
		  prevScores.addFirst(myScore);
		  switch(move){
		  	case LEFT:
				  needTile=left();
				  break;
		  	case RIGHT:
		  		myTiles = rotate(180);
		  		needTile=left();
		  		myTiles = rotate(180);
		  		break;
		  	case UP:
			    myTiles = rotate(270);
			    needTile=left();
			    myTiles = rotate(90);
			    break;
		  	case DOWN:
		  		myTiles = rotate(90);
			    needTile=left();
			    myTiles = rotate(270);
		  }
		  return needTile;
	  }
	  
	  public void undoMove(){
		  myTiles=prevMoves.removeFirst();
		  myScore=prevScores.removeFirst();
	  }
	  
	  private boolean left() {
		  boolean needAddTile = false;
		  for (int i = 0; i < 4; i++) {
	    	long line = getLine(i);
			long merged = mergeLine(moveLine(line));
			setLine(i, merged);
			if (!needAddTile && !compare(line, merged)) {
			    needAddTile = true;
			}
		  }
		  return needAddTile;
	  }
	  
	 public int tileAt(int x, int y) {
		return tileAt(x+y*4);
	 }
	 
	 public int tileAt(int index) {
		return TILE_VALUES[(int) (myTiles >>> ((index)*4) & BITMASK)];
	 }
	 
	 private short bitTileAt(int x, int y){
		 return (short)(myTiles >>> ((x+y*4)*4) & BITMASK);
	 }
	 
	 public int getScore(){
		 return myScore;
	 }
	 
	 public boolean gameOver(){
		 return !canMove() || winGame();
	 }
	 
	 public boolean winGame(){
		 for(int i=0;i<16;i++)
			 if(tileAt(i)>=target)
				 return true;
		 return false;
	 }
	 
	 /**
	  * Add a tile at a random location
	  * @return location of the tile
	  */
	 public int addTile() {
		    List<Integer> list = availableSpace();
		    int index=-1;
		    if (!availableSpace().isEmpty()) {
		      index = (int) (Math.random() * list.size()) % list.size();
		      addTile(list.get(index), (Math.random() < PIECE_PROB ? 2 : 4));
		    }
		    return list.get(index);
	 }
	 
	 /**
	  * Add a tile of particular value to a specific location
	  * @param index - location of the tile
	  * @param value - value of the tile (2 or 4)
	  */
	 public void addTile(int index, int value){
		 myTiles |= getBinaryValues(value) << index*4;
	 }
	 
	 /**
	  * Set a tile of a particular index to a particular value
	  * @param index
	  * @param value
	  */
	 private void setTile(int index, int value){
		 removeTile(index);
		 myTiles |= (getBinaryValues(value) << index*4);
	 }
	 
	 private long setTile(long board, int index, int value){
		 long newboard = removeTile(board, index);
		 newboard |= (getBinaryValues(value) << index*4);
		 return newboard;
	 }
	 
	 public void removeTile(int index){
		 myTiles &= ~(BITMASK << ((index)*4));
	 }
	 
	 private long removeTile(long board, int index){
		 board &= ~(BITMASK << ((index)*4));
		 return board;
	 }
	 
	 public List<Integer> availableSpace() {
		    final List<Integer> list = new ArrayList<Integer>(16);
		    for (int x = 0;x < 16;x++) {
		      if (tileAt(x)==0) {
		        list.add(x);
		      }
		    }
		    return list;
	 }

	  private boolean isFull() {
	    return availableSpace().size() == 0;
	  }

	  public boolean canMove() {
	    if (!isFull()) {
	      return true;
	    }
	    for (int x = 0; x < 4; x++) {
	      for (int y = 0; y < 4; y++) {
	        int t = tileAt(x, y);
	        if ((x < 3 && t == tileAt(x + 1, y))
	          || ((y < 3) && t == tileAt(x, y + 1))) {
	          return true;
	        }
	      }
	    }
	    return false;
	  }

	  private boolean compare(long line1, long line2) {
	    return (line1 == line2);
	  }
	  
	  @Override
	  // incorporate score and prev moves into hashing?
	  public int hashCode(){
		  return Long.hashCode(myTiles);
	  }

	  private long rotate(int angle) {
	    long newTiles = 0;
	    int offsetX = 3, offsetY = 3;
	    if (angle == 90) {
	      offsetY = 0;
	    } else if (angle == 270) {
	      offsetX = 0;
	    }

	    double rad = Math.toRadians(angle);
	    int cos = (int) Math.cos(rad);
	    int sin = (int) Math.sin(rad);
	    for (int x = 0; x < 4; x++) {
	      for (int y = 0; y < 4; y++) {
	        int newX = (x * cos) - (y * sin) + offsetX;
	        int newY = (x * sin) + (y * cos) + offsetY;
	        newTiles = setTile(newTiles, 4*newY+newX, tileAt(x, y));
	      }
	    }
	    return newTiles;
	  }

	  private long moveLine(long oldLine) {
	    LinkedList<Integer> l = new LinkedList<Integer>();
	    for (int i = 0; i < 4; i++) {
	    	int value = (int)((oldLine >>> i*4) & BITMASK);
		    if (value != 0)
		    	l.add(value);
	    }
	    if (l.size() == 0) {
	      return oldLine;
	    } else {
	    long newLine = 0;
	      for (int i = 0; i < 4; i++) {
	    	  if(!l.isEmpty())
	    		  newLine |= l.remove() << i*4;
	      }
	      return newLine;
	    }
	  }

	  private long mergeLine(long oldLine) {
	    LinkedList<Integer> list = new LinkedList<Integer>();
	    for (int i = 0; i < 4; i++) {
	      int num = (int)((oldLine >>> i*4) & BITMASK);
	      if (i < 3 && num != 0 && num == (int)((oldLine >>> (i+1)*4) & BITMASK)) {
	        int value = num;
	        myScore += TILE_VALUES[value];
	        list.add(num+1);
	        i++;
	      } else if(num != 0) {
	    	  list.add(num);
	      }
	    }
	    if (list.size() == 0) {
	      return oldLine;
	    } else {
		    long newLine = 0;
		      for (int i = 0; i < 4; i++) {
		    	  if(!list.isEmpty())
		    		  newLine |= list.remove() << i*4;
		      }
		      return newLine;
	    }
	  }

	  private long getLine(int i) {
	    return LINE_BITMASK & (myTiles >>> i*16);
	  }

	  private void setLine(int index, long line) {
		  myTiles &= ~(LINE_BITMASK << index*16);
		  myTiles |= line << index*16;
	  }
	  
	  // Returns binary corresponding to particular tile value. If a tile value is invalid, returns 1
	  private long getBinaryValues(int tileValue) {
		  switch (tileValue){
		  	case 0:		return 0b0000;
		  	case 2:		return 0b0001;
		  	case 4: 	return 0b0010;
		  	case 8: 	return 0b0011;
		  	case 16:	return 0b0100;
		  	case 32:	return 0b0101;
		  	case 64:	return 0b0110;
		  	case 128: 	return 0b0111;
		  	case 256:	return 0b1000;
		  	case 512: 	return 0b1001;
		  	case 1024:	return 0b1010;
		  	case 2048: 	return 0b1011;
		  	case 4096:	return 0b1100;
		  	case 8192:	return 0b1101;
		  	case 16384: return 0b1111;
		  }
		  return 1;
	  }
	  
	  public static void main(String args[]) {
		  Scanner scan = new Scanner(System.in);
		  Game2048Bit game2048 = new Game2048Bit();
		  Game2048View view = new Game2048View(game2048);
			JFrame game = new JFrame();
		    game.setTitle("2048 Game");
		    game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		    game.setSize(340, 400);
		    game.setResizable(false);
		    game.add(view);
		    game.setLocationRelativeTo(null);
		    game.setVisible(true);
		  while(true){
			  int i = scan.nextInt();
			  game2048.myTiles = game2048.rotate(i);
			  view.repaint();
		  }
	  }
}


