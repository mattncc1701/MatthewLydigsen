package SpaceInvaders;

import java.awt.Color;

public class Skull extends Alien {

    public Skull(Integer xStartBlockPosition, Integer yStartBlockPosition,
                 GamePanel gamePanel) {
        super(xStartBlockPosition, yStartBlockPosition, gamePanel);

        drawAlienArmsUp();
    } // ctor

    @Override
    public Integer scoreValue() {
        // Return 10 for the value when this alien is killed
        return 10;
    }; // scoreValue

    @Override
    protected final void drawAlienArmsUp() {
        clear();

        drawBlockRow(Color.green, 4, 0, 4);
        drawBlockRow(Color.green, 1, 1, 10);
        drawBlockRow(Color.green, 0, 2, 12);
        drawBlockRow(Color.green, 0, 3, 3);
        drawBlockRow(Color.red, 3, 3, 2);
        drawBlockRow(Color.green, 5, 3, 2);
        drawBlockRow(Color.red, 7, 3, 2);
        drawBlockRow(Color.green, 9, 3, 3);
        drawBlockRow(Color.green, 0, 4, 12);
        drawBlockRow(Color.green, 3, 5, 2);
        drawBlockRow(Color.green, 7, 5, 2);
        drawBlockRow(Color.green, 2, 6, 2);
        drawBlockRow(Color.green, 5, 6, 2);
        drawBlockRow(Color.green, 8, 6, 2);
        drawBlockRow(Color.green, 0, 7, 2);
        drawBlockRow(Color.green, 10, 7, 2);
    } // drawAlienUpPoistion

    @Override
    protected void drawAlienArmsDown() {
        clear();

        drawBlockRow(Color.green, 4, 0, 4);
        drawBlockRow(Color.green, 1, 1, 10);
        drawBlockRow(Color.green, 0, 2, 12);
        drawBlockRow(Color.green, 0, 3, 3);
        drawBlockRow(Color.red, 3, 3, 2);
        drawBlockRow(Color.green, 5, 3, 2);
        drawBlockRow(Color.red, 7, 3, 2);
        drawBlockRow(Color.green, 9, 3, 3);
        drawBlockRow(Color.green, 0, 4, 12);
        drawBlockRow(Color.green, 2, 5, 3);
        drawBlockRow(Color.green, 7, 5, 3);
        drawBlockRow(Color.green, 2, 6, 2);
        drawBlockRow(Color.green, 1, 6, 2);
        drawBlockRow(Color.green, 9, 6, 2);
        drawBlockRow(Color.green, 2, 7, 2);
        drawBlockRow(Color.green, 8, 7, 2);
    } // drawAlienUpPoistion 
} // Skull