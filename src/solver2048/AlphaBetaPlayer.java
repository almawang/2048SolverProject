/**
 * AlphaBetaPlayer.java - uses alpha beta pruning to determine move where adversary's moves are
 *	different tile placements
 * Author: Alma Wang
 */
package solver2048;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class AlphaBetaPlayer implements Player{
	
	
	// Heuristics
	private final boolean BLANKSPACES=true; // Prefers moves that provide more empty spaces
	private final boolean EDGES=false;	// Higher value tiles on the edges and corners
	private final boolean MAXCORNER=true;	// The highest value tile in a corner
	private final boolean MONOTONIC=true;	// Penalty if edges not either decreasing or increasing
	
	// Limit for probability of set tile values occurring not to test. Only used with piece utility.	
	private final double PROBCONSTANT=.00001;
	// Only predict new tiles will have the values = 2
	private final boolean NOFOURS=true; 
	
	private int maxDepth;
	private int nodesVisited=0;
	private int movesMade=0;
	Map<Integer,Integer> transpositionTable=new HashMap<Integer,Integer>();
	
	
	public AlphaBetaPlayer(int d){
		maxDepth=d;
	}


	@Override
	public int getMove(Game2048Bit game) {
		// Shuffles moves to prevent getting stuck with equal utilities
		List<Integer> moves=game.getMoves();
		Collections.shuffle(moves);
		int bestMove=moves.get(0);		
		int bestUtility=0;
		for(int move:moves){
			if(game.makeMove(move)){
				int util=newPieceUtility(game,0,0,Integer.MIN_VALUE,Integer.MAX_VALUE);
				if(util>=bestUtility){
					bestUtility=util;
					bestMove=move;
				}
			}
			game.undoMove();
		}	
		movesMade++;
		game.clearPrevs();
		return bestMove;	
	}
	
	/**
	 * 
	 * @param game
	 * @param d
	 * @return
	 */
	private int maxUtility(Game2048Bit game, int depth, int numFours, int alpha, int beta){
		nodesVisited++;
		if(cutoffTest(game,depth)){
			if(transpositionTable.containsKey(game.hashCode()))
				return transpositionTable.get(game.hashCode());
			int util=utility(game);
			transpositionTable.put(game.hashCode(), util);
			return util;
		}
		
		List<Integer> moves=game.getMoves();
		int bestUtility=0;
		for(int move:moves){
			if(game.makeMove(move)){
				int util=newPieceUtility(game,depth+1,numFours,alpha,beta);
				if(util>=bestUtility){
					bestUtility=util;
				}
				if(bestUtility>=beta){
					game.undoMove();
					return bestUtility;
				}
				if(bestUtility>=alpha)
					beta=bestUtility;
			}
			game.undoMove();
		}	
		return bestUtility;	
	}


	private int newPieceUtility(Game2048Bit game, int depth, int numFours, int alpha, int beta) {
		int minUtility=Integer.MAX_VALUE;
		List<Integer> availableTiles=game.availableSpace();
		for(Integer t:availableTiles){
			// Tile with value 2
			game.addTile(t, 2);
			int utility;
			if(transpositionTable.containsKey(game.hashCode())){
				utility=transpositionTable.get(game.hashCode());
			}else{
				utility=maxUtility(game,depth,numFours,alpha,beta);
			}
			
			if(utility<minUtility)
				minUtility=utility;
			if(minUtility<=alpha){
				game.removeTile(t);
				return minUtility;
			}
			if(minUtility<=beta)
				beta=minUtility;
			
			game.removeTile(t);
			
			if(!NOFOURS){
				if(Math.pow(.10000000, numFours+1)*Math.pow(.9000000, depth-numFours)>PROBCONSTANT){
					// Tile with value 4
					game.addTile(t, 4);
					if(transpositionTable.containsKey(game.hashCode())){
						utility=transpositionTable.get(game.hashCode());
					}else{
						utility=maxUtility(game,depth,numFours,alpha,beta);
					}
					
					if(utility<minUtility)
						minUtility=utility;
					if(minUtility<=alpha){
						game.removeTile(t);
						return minUtility;
					}
					if(minUtility<=beta)
						beta=minUtility;
					
					game.removeTile(t);
				}
			}
		}
		return minUtility;
	}
	
	public void printStats(){
		System.out.println("Number of nodes visited: "+nodesVisited+
				"\nNumber of moves: "+ movesMade);
	}
	
	private int utility(Game2048Bit game) {
		if(game.gameOver() && !game.winGame())
			return 0;
		int score=game.getScore();
		if(BLANKSPACES)
			score+=10*game.availableSpace().size();
		if(EDGES){
			for(int x=0;x<4;x++){
				score+=game.tileAt(x,0);
				score+=game.tileAt(x,3);
				score+=game.tileAt(0, x);
				score+=game.tileAt(3, x);
			}
		}
		if(MAXCORNER){		
			int maxVal=0;
			int[] coors=new int[2];
			for(int x=0;x<4;x++){
				for(int y=0;y<4;y++){
					int val=game.tileAt(x,y);
					if(val>maxVal){
						maxVal=val;
						coors[0]=x;
						coors[1]=y;
					}
				}
			}
			if(coors[0]==0 && coors[1]==0 || coors[0]==0 && coors[1]==3 ||
					coors[0]== 3 && coors[1]==0 || coors[0]==3 && coors[1]==3){
				score+=maxVal;
			}
		}
		if(MONOTONIC){
			int[][] prevDif=new int[4][3];
			
			// Check left/right directions
			for(int i=0;i<4;i++){
				for(int j=0;j<3;j++){
					prevDif[i][j]=game.tileAt(i,j+1)-game.tileAt(i,j);
				}
			}
			for(int i=0;i<4;i++){
				int maxDif=0;
				boolean nonMono=false;
				for(int j=0;j<3;j++){
					if(Math.abs(prevDif[i][j])>maxDif)
						maxDif=Math.abs(prevDif[i][j]);
					if(j>0)
						if(prevDif[i][j-1]*prevDif[i][j]<0)
							nonMono=true;
				}
				if(nonMono)
					score-=maxDif;	
			}
			// Check up/down direction
			for(int i=0;i<4;i++){
				for(int j=0;j<3;j++){
					prevDif[i][j]=game.tileAt(j+1,i)-game.tileAt(j,i);
				}
			}
			for(int i=0;i<4;i++){
				int maxDif=0;
				boolean nonMono=false;
				for(int j=0;j<3;j++){
					if(Math.abs(prevDif[i][j])>maxDif)
						maxDif=Math.abs(prevDif[i][j]);
					if(j>0)
						if(prevDif[i][j-1]*prevDif[i][j]<0)
							nonMono=true;
				}
				if(nonMono)
					score-=maxDif;	
			}
		}
		return score;
	}

	private boolean cutoffTest(Game2048Bit game, int depth) {
		return !game.canMove() || depth>=maxDepth || game.winGame(); 
	}

}
