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
	public int getMove(Game2048 game) {
		// Shuffles moves to prevent getting stuck with equal utilities
		List<Integer> moves=game.getMoves();
		Collections.shuffle(moves);
		int bestMove=moves.get(0);
		
		int bestUtility=0;
		for(int move:moves){
			int sum=0;
			for(int i=0;i<runs;i++){
				Game2048 newGame=(new Game2048(game));
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
	private int randomUtility(Game2048 game) {
		nodesVisited++;
		if(cutoffTest(game))
			return utility(game);
		// Shuffles moves to prevent getting stuck with equal utilities
		List<Integer> moves=game.getMoves();
		Collections.shuffle(moves);
		if(game.makeMove(moves.get(0)))
			game.addTile();
		return randomUtility(game);
		
	}

	private int utility(Game2048 game) {
		return game.getScore();
	}
	
	private boolean cutoffTest(Game2048 game) {
		return game.gameOver();
	}
	
	@Override
	public void printStats(){
		System.out.println("Number of nodes visited: "+nodesVisited+
				"\nNumber of moves: "+ movesMade);
	}

}
