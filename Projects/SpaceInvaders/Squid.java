package SpaceInvaders;

import java.awt.Color;

public class Squid extends Alien {

    public Squid(Integer xStartBlockPosition, Integer yStartBlockPosition,
                 GamePanel gamePanel) {
        // Add 1 so these aliens are centered in the formation
        super(xStartBlockPosition, yStartBlockPosition, gamePanel);

        drawAlienArmsUp();
    } // ctor

    @Override
    public Integer scoreValue() {
        // Return 10 for the value when this alien is killed
        return 40;
    }; // scoreValue

    @Override
    protected final void drawAlienArmsUp() {
        clear();

        // Add an extra block to center the aliens
        drawBlockRow(Color.green, 3+1, 0, 2);
        drawBlockRow(Color.green, 2+1, 1, 4);
        drawBlockRow(Color.green, 1+1, 2, 6);
        drawBlockRow(Color.green, 0+1, 3, 2);
        drawBlock(Color.red, 2+1, 3);
        drawBlockRow(Color.green, 3+1, 3, 2);
        drawBlock(Color.red, 5+1, 3);
        drawBlockRow(Color.green, 6+1, 3, 2);
        drawBlockRow(Color.green, 0+1, 4, 8);
        drawBlock(Color.green, 2+1, 5);
        drawBlock(Color.green, 5+1, 5);
        drawBlock(Color.green, 1+1, 6);
        drawBlockRow(Color.green, 3+1, 6, 2);
        drawBlock(Color.green, 6+1, 6);
        drawBlock(Color.green, 0+1, 7);
        drawBlock(Color.green, 2+1, 7);
        drawBlock(Color.green, 5+1, 7);
        drawBlock(Color.green, 7+1, 7);
    } // drawAlienUpPoistion

    @Override
    protected void drawAlienArmsDown() {
        clear();
        
        // Add an extra block to center the aliens
        drawBlockRow(Color.green, 3+1, 0, 2);
        drawBlockRow(Color.green, 2+1, 1, 4);
        drawBlockRow(Color.green, 1+1, 2, 6);
        drawBlockRow(Color.green, 0+1, 3, 2);
        drawBlock(Color.red, 2+1, 3);
        drawBlockRow(Color.green, 3+1, 3, 2);
        drawBlock(Color.red, 5+1, 3);
        drawBlockRow(Color.green, 6+1, 3, 2);
        drawBlockRow(Color.green, 0+1, 4, 8);
        drawBlock(Color.green, 1+1, 5);
        drawBlock(Color.green, 6+1, 5);
        drawBlock(Color.green, 0+1, 6);
        drawBlock(Color.green, 7+1, 6);
        drawBlock(Color.green, 1+1, 7);
        drawBlock(Color.green, 6+1, 7);
    } // end method drawAlienUpPoistion
} // Squid

