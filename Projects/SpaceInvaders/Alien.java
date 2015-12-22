package SpaceInvaders;

// This class represents a generic alien, speciifc aliens are derived from

import java.awt.Color;
import java.awt.Graphics2D;

// this base class
public abstract class Alien extends BlockSprite {

    public static final Integer BLOCK_WIDTH = 12; // blocks
    public static final Integer BLOCK_HEIGHT = 8; // blocks

    // Used to determine the position of the alien arms
    public boolean alienInUpPosition;

    // Indicates if the alien is killed
    public Boolean killed;
    public Boolean resetKilled = false;

    protected static final Long EXPLOSION_CHANGE_RATE = (long) 400; // milli seconds

    protected Timer explosionChangeRateTimer = new Timer(EXPLOSION_CHANGE_RATE);

    public enum ExplosionState {
        NOT_DRAWN,
        EXPLOSION_1_DRAWN,
        EXPLOSION_2_DRAWN,
        EXPLOSION_DONE
    } // enum

    ExplosionState explosionState;

    // Game panel which contains sprite
    protected GamePanel gamePanel;

    public Alien(Integer xStartBlockPosition, Integer yStartBlockPosition,
                 GamePanel gamePanel) {
        super(xStartBlockPosition, yStartBlockPosition, BLOCK_WIDTH, BLOCK_HEIGHT);

        this.gamePanel = gamePanel;
        killed = false;
        alienInUpPosition = false;
        explosionState = ExplosionState.NOT_DRAWN;
    } // ctor

    public Boolean atScreenEdge(MoveDirection direction) {
        // Check if alien is a right or left edge of screen
        if (killed) { // if killed the alien can never be at the screen edge
            return false;
        } // if

        else if ((direction == MoveDirection.RIGHT) &&
                 (xPosition >= (gamePanel.getWidth() - width()))) {
            return true;
        } // else if

        else if ((direction == MoveDirection.LEFT) &&
                 (xPosition <= 0)) {
            return true;
        } // else if

        else {
            return false;
        } // else
    } // atScreenEdge

    public AlienShot shoot() {
        return new AlienShot(xBlockPosition()+(blockWidth/2), 
                             yBlockPosition()+blockHeight, gamePanel);
    } // TankShot

    public void kill() {
        killed = true;
        resetKilled = true;
        explosionChangeRateTimer.reset();
    } // Kill

    //TODO2
    public abstract Integer scoreValue();

    protected abstract void drawAlienArmsUp();

    protected abstract void drawAlienArmsDown();

    protected void drawAlienExploded1() {
        clear();

        drawBlock(Color.yellow, 1, 0);
        drawBlock(Color.yellow, 7, 0);
        drawBlock(Color.yellow, 2, 1);
        drawBlock(Color.yellow, 4, 1);
        drawBlock(Color.yellow, 6, 1);
        drawBlock(Color.yellow, 0, 2);
        drawBlock(Color.yellow, 3, 2);
        drawBlock(Color.yellow, 5, 2);
        drawBlock(Color.yellow, 8, 2);
        drawBlock(Color.yellow, 2, 3);
        drawBlock(Color.yellow, 4, 3);
        drawBlock(Color.yellow, 6, 3);
        drawBlock(Color.yellow, 1, 4);
        drawBlock(Color.yellow, 7, 4);
    } // drawAlienExploded1

    protected void drawAlienExploded2() {
        clear();

        drawBlock(Color.yellow, 0, 0);
        drawBlock(Color.yellow, 3, 0);
        drawBlock(Color.yellow, 7, 0);
        drawBlock(Color.yellow, 10, 0);
        drawBlock(Color.yellow, 1, 1);
        drawBlock(Color.yellow, 4, 1);
        drawBlock(Color.yellow, 6, 1);
        drawBlock(Color.yellow, 9, 1);
        drawBlock(Color.yellow, 2, 2);
        drawBlock(Color.yellow, 8, 2);
        drawBlock(Color.yellow, 0, 3);
        drawBlock(Color.yellow, 10, 3);
        drawBlock(Color.yellow, 2, 4);
        drawBlock(Color.yellow, 8, 4);
        drawBlock(Color.yellow, 1, 5);
        drawBlock(Color.yellow, 4, 5);
        drawBlock(Color.yellow, 6, 5);
        drawBlock(Color.yellow, 9, 5);
        drawBlock(Color.yellow, 0, 6);
        drawBlock(Color.yellow, 3, 6);
        drawBlock(Color.yellow, 7, 6);
        drawBlock(Color.yellow, 10, 6);
    } // drawAlienExploded2

    @Override
    public Boolean collides(Sprite otherSprite) {
        if (killed) {
            return false;
        } // if
        
        else { // Not killed
            return super.collides(otherSprite);
        } // else
    } // collides

    @Override
    public void draw(Graphics2D g2d) {

        if (!killed) { // Not killed so draw alien
            // Call superclass to draw alien
            super.draw(g2d);
        } // if

        // Check if explosion should be drawn
        else if (killed) {

            switch (explosionState) {

                case NOT_DRAWN:
                    explosionState =  ExplosionState.EXPLOSION_1_DRAWN;
                    explosionChangeRateTimer.reset();

                    drawAlienExploded1();
                    super.draw(g2d);
                    break;

                case EXPLOSION_1_DRAWN:
                    if (explosionChangeRateTimer.expired()) {
                        explosionState = ExplosionState.EXPLOSION_2_DRAWN;
                        explosionChangeRateTimer.reset();
                    }; // if

                    drawAlienExploded1();
                    super.draw(g2d);
                    break;

                case EXPLOSION_2_DRAWN:
                    if (explosionChangeRateTimer.expired()) {
                        explosionState = ExplosionState.EXPLOSION_DONE;
                    }; // if

                    drawAlienExploded2();
                    super.draw(g2d);
                    break;

                case EXPLOSION_DONE:
                    break;
            } // switch
        } // if
    }; // draw

    @Override
    public void move (MoveDirection direction, Integer delta) {
        // Call base class to update position of alien
        super.move(direction, delta);

        // Detemine how to draw alien
        if(alienInUpPosition == false){
            drawAlienArmsUp();
            alienInUpPosition = true;
        } // of

        else{
            drawAlienArmsDown();
            alienInUpPosition = false;
        } // else
    } // move
} // Alien
