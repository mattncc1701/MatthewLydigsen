package SpaceInvaders;

import java.awt.Color;

public class Tank extends BlockSprite {

    private static final Integer BLOCK_WIDTH = 13;
    private static final Integer BLOCK_HEIGHT = 9;

    // Game panel which contains sprite
    protected GamePanel gamePanel;

    public Tank (
        Integer xStartBlockPosition, Integer yStartBlockPosition, GamePanel gamePanel) {
        super(xStartBlockPosition, yStartBlockPosition, BLOCK_WIDTH, BLOCK_HEIGHT);

        this.gamePanel = gamePanel;

        drawTank();
    } // ctor

    protected final void drawTank(){
        drawBlock(Color.green, 6, 0);
        drawBlockRow(Color.green, 5, 1, 3);
        drawBlockRow(Color.green, 5, 2, 3);
        drawBlockRow(Color.green, 5, 3, 3);
        drawBlockRow(Color.green, 1, 4, 11);
        drawBlockRow(Color.green, 0, 5, 13);
        drawBlockRow(Color.green, 0, 6, 13);
        drawBlockRow(Color.green, 0, 7, 13);
        drawBlockRow(Color.green, 0, 8, 13);
    } // drawTank
} // Tank