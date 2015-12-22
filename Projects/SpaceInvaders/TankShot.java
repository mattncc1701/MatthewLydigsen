package SpaceInvaders;

import java.awt.Color;

public class TankShot extends Shot {

    private static final Integer BLOCK_WIDTH = 1;
    private static final Integer BLOCK_HEIGHT = 7;

    public TankShot (
        Integer xStartBlockPosition, Integer yStartBlockPosition, GamePanel gamePanel) {
        super(xStartBlockPosition, yStartBlockPosition, BLOCK_WIDTH, BLOCK_HEIGHT, gamePanel);

         // Draw the tank shot
        drawBlockRow(Color.green, 0, 0, 1);
        drawBlockRow(Color.green, 0, 1, 1);
        drawBlockRow(Color.green, 0, 2, 1);
        drawBlockRow(Color.green, 0, 3, 1);
        drawBlockRow(Color.green, 0, 4, 1);
        drawBlockRow(Color.green, 0, 5, 1);
        drawBlockRow(Color.green, 0, 6, 1);
    } // ctor

    public Boolean isOffScreen() {
        if (yPosition <= 0) {
            return true;
        } // if

        else { // Bot off screen
            return false;
        } // else
    } // isOffScreen

    @Override
    public Shot.shotType determineShotType(){
        return Shot.shotType.TANK_SHOT;
    } // determineShotType
    
    public void move (Integer delta) {
        // Check if shot has moved off the screen, been detonated, or should
        // be moved
         if (detonated) {
            clear(); // Remove from screen
        } // if

        else if (yPosition <= 0) {
            offScreen = true;
            clear(); // Remove from screen
        } // else if

        else { // move shot
            // Call base class to update position of tank shot
            super.move(Sprite.MoveDirection.UP, delta);
        } // else
    } // move

} // TankShot
