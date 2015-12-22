package SpaceInvaders;

import java.awt.Color;
import java.awt.Graphics2D;

public class BlockSprite extends Sprite {

    // Block size
    public static final Integer BLOCK_SIZE = 3;

    // Block size of the sprite
    public Integer blockWidth;
    public Integer blockHeight;
  
    public BlockSprite (Integer xStartBlockPosition, Integer yStartBlockPosition,
        Integer blockWidth, Integer blockHeight) {

        super(xStartBlockPosition * BLOCK_SIZE,
              yStartBlockPosition * BLOCK_SIZE,
              blockWidth * BLOCK_SIZE,
              blockHeight * BLOCK_SIZE);
        
        this.blockHeight = blockHeight;
        this.blockWidth = blockWidth;
    } // ctor

    public Integer xBlockPosition() {
        return xPosition/BLOCK_SIZE;
    } // getXBlockPosition

    public Integer yBlockPosition() {
        return yPosition/BLOCK_SIZE;
    } // getYBlockPosition

    protected void drawBlock(Color color,
        Integer xBlockPosition, Integer yBlockPosition) {
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        
        g2d.setColor(color);
        g2d.fillRect(xBlockPosition * BLOCK_SIZE, yBlockPosition * BLOCK_SIZE,
            BLOCK_SIZE, BLOCK_SIZE);
    } // drawSquare

    protected void drawBlockRow(Color color, Integer xBlockPosition,
        Integer yBlockPosition, Integer NbrBlocks) {
        // Loop and calculate next block position by adding block width
        for (Integer block = 0; block < NbrBlocks; block++) {
            drawBlock(color, xBlockPosition+block, yBlockPosition);
        } // for
     } // drawSquare
} // BlockSprite
