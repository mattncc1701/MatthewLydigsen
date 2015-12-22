package SpaceInvaders;

import java.awt.Color;

public class AlienShot extends Shot {

    private static final Integer BLOCK_WIDTH = 3; // blocks
    private static final Integer BLOCK_HEIGHT = 7; // blocks

    Boolean twist1Drawn = true;

    public AlienShot (
        Integer xStartBlockPosition, Integer yStartBlockPosition, GamePanel gamePanel) {
        super(xStartBlockPosition, yStartBlockPosition, BLOCK_WIDTH, BLOCK_HEIGHT, gamePanel);
    } // ctor

    private void drawAlienTwist1(){
        clear();
        drawBlock(Color.green, 0, 0);
        drawBlock(Color.green, 1, 1);
        drawBlock(Color.green, 2, 2);
        drawBlock(Color.green, 1, 3);
        drawBlock(Color.green, 0, 4);
        drawBlock(Color.green, 1, 5);
        drawBlock(Color.green, 2, 6);
    } // drawAlienTwist1

    private void drawAlienTwist2(){
        clear();
        drawBlock(Color.green, 2, 0);
        drawBlock(Color.green, 1, 1);
        drawBlock(Color.green, 0, 2);
        drawBlock(Color.green, 1, 3);
        drawBlock(Color.green, 2, 4);
        drawBlock(Color.green, 1, 5);
        drawBlock(Color.green, 0, 6);
    } // drawAlienTwist2

    public Boolean isOffScreen() {
        if (yPosition >= GamePanel.FRAME_HEIGHT - 50) {
            return true;
        } // if

        else { // Bot off screen
            return false;
        } // else
    } // isOffScreen

    @Override
    public Shot.shotType determineShotType(){
        return Shot.shotType.ALIEN_SHOT;
    } // determineShotType

    @Override
    public void move (MoveDirection direction, Integer delta) {
        // Check if shot has moved off the screen, been detonated, or should
        // be moved

        if (detonated) {
            clear(); // Remove from screen
        } // if

        else if(yPosition >= gamePanel.getHeight()) {
            offScreen = true;
            clear(); // Remove from screen
        } // else if

        else { // ok to move shot
            // Call base class to update position of alien shot
            super.move(Sprite.MoveDirection.DOWN, delta);
            if(twist1Drawn == false){
                drawAlienTwist1();
                twist1Drawn = true;
            } // if

            else{
                drawAlienTwist2();
                twist1Drawn = false;
            } // else
        } // else
    } // move

} // TankShot
