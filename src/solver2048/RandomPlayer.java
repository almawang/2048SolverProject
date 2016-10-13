/**
 * RandomPlayer-determines best move by finding the average utility of a sequence of random
 *  moves until the end is reached
 * Author: Alma Wang
 */
package solver2048;

import java.util.Collections;
import java.util.List;

public class RandomPlayer implements Player{
	
	private int movesMade=0;
	private int nodesVisited=0;
	private int runs;
	
	public RandomPlayer(int r){
		runs=r;
	}
	
	@Override
	public int getMove(Game2048Bit game) {
		// Shuffles moves to prevent getting stuck with equal utilities
		int[] moves=game.getMoves();
		int bestMove=moves[(int)(moves.length*Math.random())];
		
		int bestUtility=0;
		for(int move:moves){
			int sum=0;
			for(int i=0;i<runs;i++){
				Game2048Bit newGame=(new Game2048Bit(game));
				if(newGame.makeMove(move)){
					newGame.addTile();
					sum+=randomUtility(newGame);
				}
			}
		int util=sum/runs;
			if(util>=bestUtility){
				bestUtility=util;
				bestMove=move;
			}
		}	
		movesMade++;
		return bestMove;	
	}

	/**
	 * Recursively performs a random run
	 */
	private int randomUtility(Game2048Bit game) {
		nodesVisited++;
		if(cutoffTest(game))
			return utility(game);
		// Shuffles moves to prevent getting stuck with equal utilities
		int[] moves=game.getMoves();
		if(game.makeMove(moves[(int)(Math.random()*moves.length)]))
			game.addTile();
		return randomUtility(game);
		
	}

	private int utility(Game2048Bit game) {
		return game.getScore();
	}
	
	private boolean cutoffTest(Game2048Bit game) {
		return game.gameOver();
	}
	
	@Override
	public void printStats(){
		System.out.println("Number of nodes visited: "+nodesVisited+
				"\nNumber of moves: "+ movesMade);
	}

}
