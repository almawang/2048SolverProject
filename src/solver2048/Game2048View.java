/**
 * Game2048View.java - provides graphical dispay of 2048 game
 * Original Author: Konstantin Bulenkov http://bulenkov.com/about
 * Modified by Alma Wang
 */
package solver2048;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class Game2048View extends JPanel{

	private static final long serialVersionUID = 1L;
	private static final Color BG_COLOR = new Color(0xbbada0);
	  private static final String FONT_NAME = "Arial";
	  private static final int TILE_SIZE = 64;
	  private static final int TILES_MARGIN = 16;
	  
	  private Game2048 game;
	  
	  public Game2048View(Game2048 game){
		  this.game=game;
	  }
	  
	  @Override
	  public void paint(Graphics g) {
	    super.paint(g);
	    g.setColor(BG_COLOR);
	    g.fillRect(0, 0, this.getSize().width, this.getSize().height);
	    for (int y = 0; y < 4; y++) {
	      for (int x = 0; x < 4; x++) {
	        drawTile(g, game.tileAt(x, y), x, y);
	      }
	    }
	  }
	  
	private void drawTile(Graphics g2, int tile, int x, int y) {
	    Graphics2D g = ((Graphics2D) g2);
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
	    int value = tile;
	    int xOffset = offsetCoors(x);
	    int yOffset = offsetCoors(y);
	    g.setColor(getBackground(tile));
	    g.fillRoundRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE, 14, 14);
	    g.setColor(getForeground(tile));
	    final int size = value < 100 ? 36 : value < 1000 ? 32 : 24;
	    final Font font = new Font(FONT_NAME, Font.BOLD, size);
	    g.setFont(font);

	    String s = String.valueOf(value);
	    final FontMetrics fm = getFontMetrics(font);

	    final int w = fm.stringWidth(s);
	    final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];

	    if (value != 0)
	      g.drawString(s, xOffset + (TILE_SIZE - w) / 2, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 - 2);

	    if (game.winGame() || !game.canMove()) {
	      g.setColor(new Color(255, 255, 255, 30));
	      g.fillRect(0, 0, getWidth(), getHeight());
	      g.setColor(new Color(78, 139, 202));
	      g.setFont(new Font(FONT_NAME, Font.BOLD, 48));
	      if (game.winGame()) {
	        g.drawString("You won!", 68, 150);
	        g.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
	        g.setColor(new Color(128, 128, 128, 128));
	        g.drawString("Press ESC to play again", 80, getHeight() - 40);
	      }
	      else{
	        g.drawString("Game over!", 50, 130);
	        g.drawString("You lose!", 64, 200);
	      }
	    }
	    g.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
	    g.drawString("Score: " + game.getScore(), 200, 365);

	  }
	
    public Color getForeground(int value) {
	      return value < 16 ? new Color(0x776e65) :  new Color(0xf9f6f2);
	    }

	    public Color getBackground(int value) {
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
	
	private static int offsetCoors(int arg) {
	    return arg * (TILES_MARGIN + TILE_SIZE) + TILES_MARGIN;
	  }

}
