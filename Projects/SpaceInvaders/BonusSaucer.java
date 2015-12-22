package SpaceInvaders;

import java.awt.Color;
import java.util.Random;

public class BonusSaucer extends BlockSprite {

    private static final Integer BLOCK_WIDTH = 16; // blocks
    private static final Integer BLOCK_HEIGHT = 7; // blocks

    Random randomBonusPoints = new Random();
    
    public Boolean killed;
 
    protected GamePanel gamePanel;
    
    public BonusSaucer(Integer xStartBlockPosition, Integer yStartBlockPosition, 
        GamePanel gamePanel) {
        super(xStartBlockPosition, yStartBlockPosition, BLOCK_WIDTH, BLOCK_HEIGHT);
        
        this.gamePanel = gamePanel;
        killed = false;
        // Not implmented yet
    } // ctor

    public Boolean atScreenEdge(MoveDirection direction) {
        if (killed) { // if killed the alien can never be at the screen edge
            return false;
        } // if

        else if ((direction == MoveDirection.RIGHT) &&
                 (xPosition >= (gamePanel.getWidth()))) {
            return true;
        } // else if

        else if ((direction == MoveDirection.LEFT) &&
                 (xPosition <= -30)) {
            return true;
        } // else if

        else {
            return false;
        } // else
    } // atLeftScreenEdge


    public void kill() {
        killed = true;
    } // kill
    
    public int determineBonusScore(){
        return (randomBonusPoints.nextInt(24) + 1) * 10;
    }
    
    protected final void drawBonusSaucer(){
        clear();
        
        drawBlockRow(Color.orange, 5, 0, 6);
        drawBlockRow(Color.orange, 3, 1, 10);
        drawBlockRow(Color.orange, 2, 2, 12);
        drawBlockRow(Color.orange, 1, 3, 2);
        drawBlock(Color.yellow, 3, 3);
        drawBlockRow(Color.orange, 4, 3, 2);
        drawBlock(Color.yellow, 6, 3);
        drawBlockRow(Color.orange, 7, 3, 2);
        drawBlock(Color.yellow, 9, 3);
        drawBlockRow(Color.orange, 10, 3, 2);
        drawBlock(Color.yellow, 12, 3);
        drawBlockRow(Color.orange, 13, 3, 2);
        drawBlockRow(Color.orange, 0, 4, 16);
        drawBlockRow(Color.orange, 2, 5, 3);
        drawBlockRow(Color.orange, 7, 5, 2);
        drawBlockRow(Color.orange, 11, 5, 3);
        drawBlock(Color.orange, 3, 6);
        drawBlock(Color.orange, 12, 6);
    }
    
} // BonusSaucer