/**
 * Game2048Driver.java - runs the 2048 game with desired player
 * Original author: Konstantin Bulenkov http://bulenkov.com/about
 * Modified by Alma Wang
 */
package solver2048;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;


public class Game2048Driver{
	
	private static void benchmark(int n){
		int wins=0;
		int losses=0;
		for(int i=0;i<n;i++){
			
			// Set target score of the game
		     Game2048 game2048=new Game2048();
		    // Game2048 game2048=new Game2048(4096);
		    // Game2048 game2048=new Game2048(50000); // Test maximum achievable score
			
			// Uncomment type of player used   
		    Player player=new ExpectedMaxPlayer(3);
		   // Player player=new AlphaBetaPlayer(5);
		   // Player player=new RandomPlayer(100);
		    
		    long startTime=System.currentTimeMillis();
		    
		    while(!game2048.gameOver()){
		    	if(game2048.makeMove(player.getMove(game2048)))
		    		game2048.addTile();
		    }
		    if(game2048.winGame()){
		    	wins++;
		    	System.out.println("win");
		    }
		    else{
		    	System.out.println("lose");
		    	losses++;
		    }
		    long time=System.currentTimeMillis()-startTime;
		    player.printStats();
		    System.out.println("Time: "+time/1000.0000);
		    System.gc();
		}
		System.out.println("Wins: "+wins +" Losses: "+losses);
	}
	
	public static void main(String[] args) {
		//benchmark(10);
	    JFrame game = new JFrame();
	    game.setTitle("2048 Game");
	    game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    game.setSize(340, 400);
	    game.setResizable(false);
	    
	    // Set target score of the game
	     Game2048 game2048=new Game2048();
	    // Game2048 game2048=new Game2048(4096);
	    // Game2048 game2048=new Game2048(50000); // Test maximum achievable score
	    
	    Game2048View view=new Game2048View(game2048);
	    game.add(view);
	    game.setLocationRelativeTo(null);
	    game.setVisible(true);
	    
	    
	    // Uncomment type of player used   
	   // Player player=new ExpectedMaxPlayer(3);
	   // Player player=new AlphaBetaPlayer(5);
	    Player player=new RandomPlayer(100);
	    
	    long startTime=System.currentTimeMillis();
	    long prevTime=startTime;
	    int move=0;
	    
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    while(!game2048.gameOver()){
	    	move++;
	    	if(game2048.makeMove(player.getMove(game2048)))
	    		game2048.addTile();
	    	long newTime=System.currentTimeMillis();
	    	
	    	// Print stats as player makes moves
	    	System.out.println("Time taken to make move "+move+": "+(newTime-prevTime)/1000.0000);
	    	player.printStats();
	    	
	    	prevTime=newTime;
		    view.repaint();	
		    
		 //   System.gc();
		    
		    // Prevent threading issues with display
	    	try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	    }
	    if(game2048.winGame())
	    	System.out.println("Game Won!");
	    else
	    	System.out.println("Lost");
	    player.printStats();
	    long time=System.currentTimeMillis()-startTime;
	    System.out.println("Time: "+time/1000.0000);
	    
	  }

}
