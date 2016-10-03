package solver2048;

import java.util.Scanner;

public class HumanPlayer implements Player{
	
	private Scanner scan;
	
	public HumanPlayer() {
		scan= new Scanner(System.in);
		System.out.println("constructed Human Player");
	}

	@Override
	public int getMove(Game2048Bit game) {
		System.out.println("Left is: " + Game2048Bit.LEFT
				+ "\nRight is: " + Game2048Bit.RIGHT
				+ "\nUp is: " + Game2048Bit.UP
				+ "\nDown is: " + Game2048Bit.DOWN);
		int move = scan.nextInt();
		return move;
	}

	@Override
	public void printStats() {
		System.out.println("Human Player");
	}

}
