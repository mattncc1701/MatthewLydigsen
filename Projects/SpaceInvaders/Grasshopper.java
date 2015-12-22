package SpaceInvaders;

import java.awt.Color;

public class Grasshopper extends Alien {

    public Grasshopper(Integer xStartBlockPosition, Integer yStartBlockPosition,
                       GamePanel gamePanel) {
        super(xStartBlockPosition, yStartBlockPosition, gamePanel);

        drawAlienArmsUp();
    } // ctor

    @Override
    public Integer scoreValue() {
        // return 10 for the value when this alien is killed
        return 20;
    }; // scoreValue

    @Override
    protected final void drawAlienArmsUp() {
        clear();

        drawBlock(Color.green, 2, 0);
        drawBlock(Color.green, 3, 1);
        drawBlock(Color.green, 0, 1);
        drawBlock(Color.green, 10, 1);
        drawBlock(Color.green, 0, 2);
        drawBlock(Color.green, 10, 2);
        drawBlock(Color.green, 8, 0);
        drawBlock(Color.green, 7, 1);
        drawBlockRow(Color.green, 2, 2, 7);
        drawBlockRow(Color.green, 0, 3, 3);
        drawBlock(Color.red, 3, 3);
        drawBlockRow(Color.green, 4, 3, 3);
        drawBlock(Color.red, 7, 3);
        drawBlockRow(Color.green, 8, 3, 3);
        drawBlockRow(Color.green, 0, 4, 11);
        drawBlockRow(Color.green, 2, 5, 7);
        drawBlock(Color.green, 2, 6);
        drawBlock(Color.green, 8, 6);
        drawBlock(Color.green, 1, 7);
        drawBlock(Color.green, 9, 7);
    } // drawAlienUpPoistion

    @Override
    protected void drawAlienArmsDown() {
        clear();

        drawBlock(Color.green, 2, 0);
        drawBlock(Color.green, 3, 1);
        drawBlock(Color.green, 8, 0);
        drawBlock(Color.green, 7, 1);
        drawBlockRow(Color.green, 2, 2, 7);
        drawBlockRow(Color.green, 1, 3, 2);
        drawBlock(Color.red, 3, 3);
        drawBlockRow(Color.green, 4, 3, 3);
        drawBlock(Color.red, 7, 3);
        drawBlockRow(Color.green, 8, 3, 2);
        drawBlockRow(Color.green, 0, 4, 11);
        drawBlock(Color.green, 0, 5);
        drawBlockRow(Color.green, 2, 5, 7);
        drawBlock(Color.green, 10, 5);
        drawBlock(Color.green, 0, 6);
        drawBlock(Color.green, 2, 6);
        drawBlock(Color.green, 8, 6);
        drawBlock(Color.green, 10, 6);
        drawBlockRow(Color.green, 3, 7, 2);
        drawBlockRow(Color.green, 6, 7, 2);
    } // drawAlienUpPoistion

} // Grasshopper
