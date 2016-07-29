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


public class Game2048 {
	
	public static final int LEFT=0;
	public static final int RIGHT=1;
	public static final int UP=2;
	public static final int DOWN=3;
	
	private int target=2048;
	private int[] myTiles;
	private int myScore = 0;
	private LinkedList<int[]> prevMoves=new LinkedList<int[]>();
	private LinkedList<Integer> prevScores=new LinkedList<Integer>();
	
	  
	  public Game2048() {
		  resetGame();
	  }
	  
	  /**
	   * Change the target score
	   */
	  public Game2048(int target){
		  this.target=target;
		  resetGame();
	  }
	  
	  /**
	   * Make a copy of a game
	   */
	  public Game2048(Game2048 game){
		  myTiles=game.myTiles.clone();
		  myScore=game.myScore;
		  target=game.target;
	  }
	  
	  /**
	   * Reset the board
	   */
	  public void resetGame() {
		    myScore = 0;
		    myTiles = new int[4 * 4];
		    for (int i = 0; i < myTiles.length; i++) {
		      myTiles[i] = 0;
		    }
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
		  prevMoves.addFirst(myTiles.clone());
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
		      int[] line = getLine(i);
		      int[] merged = mergeLine(moveLine(line));
		      setLine(i, merged);
		      if (!needAddTile && !compare(line, merged)) {
		          needAddTile = true;
		        }
		    }
		    return needAddTile;
		  }
	  
	 public int tileAt(int x, int y) {
		return myTiles[x + y * 4];
	 }
	 
	 public int getScore(){
		 return myScore;
	 }
	 
	 public boolean gameOver(){
		 return !canMove() || winGame();
	 }
	 
	 public boolean winGame(){
		 for(int i=0;i<myTiles.length;i++)
			 if(myTiles[i]>=target)
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
		      myTiles[list.get(index)] = Math.random() < 0.9 ? 2 : 4;
		    }
		    return list.get(index);
	 }
	 
	 /**
	  * Add a tile of particular value to a specific location
	  * @param index - location of the tile
	  * @param value - value of the tile (2 or 4)
	  */
	 public void addTile(int index, int value){
		 myTiles[index]=value;
	 }
	 
	 public void removeTile(int index){
		 myTiles[index]=0;
	 }
	 
	 public List<Integer> availableSpace() {
		    final List<Integer> list = new ArrayList<Integer>(16);
		    for (int x=0;x<myTiles.length;x++) {
		      if (myTiles[x]==0) {
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

		  private boolean compare(int[] line1, int[] line2) {
		    if (line1 == line2) {
		      return true;
		    } else if (line1.length != line2.length) {
		      return false;
		    }

		    for (int i = 0; i < line1.length; i++) {
		      if (line1[i] != line2[i]) {
		        return false;
		      }
		    }
		    return true;
		  }
		  
		  @Override
		  public int hashCode(){
			  return Arrays.hashCode(myTiles);
		  }

		  private int[] rotate(int angle) {
		    int[] newTiles = new int[4 * 4];
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
		        newTiles[(newX) + (newY) * 4] = tileAt(x, y);
		      }
		    }
		    return newTiles;
		  }

		  private int[] moveLine(int[] oldLine) {
		    LinkedList<Integer> l = new LinkedList<Integer>();
		    for (int i = 0; i < 4; i++) {
		      if (oldLine[i]!=0)
		    	l.add(oldLine[i]);
		    }
		    if (l.size() == 0) {
		      return oldLine;
		    } else {
		    int[] newLine = new int[4];
		      for (int i = 0; i < 4; i++) {
		    	  if(!l.isEmpty())
		    		  newLine[i] = l.remove();
		    	  else
		    		  newLine[i]=0;
		      }
		      return newLine;
		    }
		  }

		  private int[] mergeLine(int[] oldLine) {
		    LinkedList<Integer> list = new LinkedList<Integer>();
		    for (int i = 0; i < 4; i++) {
		      int num = oldLine[i];
		      if (i < 3 && oldLine[i] == oldLine[i + 1]) {
		        num *= 2;
		        myScore += num;
		        i++;
		        list.add(num);
		      } else if(oldLine[i]!=0){
		    	  list.add(num);
		      }
		      
		    }
		    if (list.size() == 0) {
		      return oldLine;
		    } else {
		      int[] newLine=new int[4];
		      for(int x=0;x<newLine.length;x++){
		    	  if(!list.isEmpty())
		    		  newLine[x]=list.remove();
		    	  else
		    		  newLine[x]=0;
		      }
		      return newLine;
		    }
		  }

		  private int[] getLine(int index) {
		    int[] result = new int[4];
		    for (int i = 0; i < 4; i++) {
		      result[i] = tileAt(i, index);
		    }
		    return result;
		  }

		  private void setLine(int index, int[] re) {
			  System.arraycopy(re, 0, myTiles, index * 4, 4);
		  }
		  
	 /* public static class Tile {
		    int value;

		    public Tile() {
		      this(0);
		    }

		    public Tile(int num) {
		      value = num;
		    }

		    public boolean isEmpty() {
		      return value == 0;
		    }

		    public Color getForeground() {
		      return value < 16 ? new Color(0x776e65) :  new Color(0xf9f6f2);
		    }

		    public Color getBackground() {
		      switch (value) {
		        case 2:    return new Color(0xeee4da);
		        case 4:    return new Color(0xede0c8);
		        case 8:    return new Color(0xf2b179);
		        case 16:   return new Color(0xf59563);
		        case 32:   return new Color(0xf67c5f);
		        case 64:   return new Color(0xf65e3b);
		        case 128:  return new Color(0xedcf72);
		        case 256:  return new Color(0xedcc61);
		        case 512:  return new Color(0xedc850);
		        case 1024: return new Color(0xedc53f);
		        case 2048: return new Color(0xedc22e);
		      }
		      return new Color(0xcdc1b4);
		    }
		    
		    @Override
		    public int hashCode(){
		    	return value;
		    }
		  }*/

}


