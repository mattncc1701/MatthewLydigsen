package SpaceInvaders;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class LivesArea extends BlockSprite {

    private static final Integer BLOCK_WIDTH = 123; // blocks
    private static final Integer BLOCK_HEIGHT = 17; // blocks

    // define a public variable called currentNbrLives of integer
    public Integer currentNbrLives;

    protected static Font scoreFont = new Font("Arial", Font.BOLD, 30);


    public LivesArea (
        Integer xStartBlockPosition, Integer yStartBlockPosition,
        Integer startingNbrLives, GamePanel gamePanel) {
        super(xStartBlockPosition, yStartBlockPosition, BLOCK_WIDTH, BLOCK_HEIGHT);

        // set currentNbrLives to startingNbrLives parameter
        currentNbrLives = startingNbrLives;

        // Call drawLives to draw lives
        drawLives();
    } // ctor

    public void decrementLives() {
        // Decrement currentNbrLives
        currentNbrLives = currentNbrLives - 1;

        // drawLives to draw lives
        drawLives();
    } // decrementLives
    
    public void incrementLives() {
        // Decrement currentNbrLives
        currentNbrLives = currentNbrLives + 1;

        // drawLives to draw lives
        drawLives();
    } // decrementLives


    protected final void drawLives() {
        clear();

        Graphics2D g2d = (Graphics2D) image.getGraphics();

        g2d.setFont(scoreFont);
        g2d.setColor(Color.white);
        g2d.drawString(String.valueOf( currentNbrLives), 20, 37);

        // For tank equalk to 1, while tank <= currentNbrLives {
        for (Integer tank = 1; tank <= currentNbrLives; tank++) {
            // Call draw tank with tank*20, 5 to draw the tank
            drawTank(tank*20, 5);
        }; // for
        
    } // drawLives

    protected void drawTank(
        Integer xStartBlockPosition, Integer yStartBlockPosition) {

        drawBlock(Color.green, xStartBlockPosition+6, yStartBlockPosition+0);
        drawBlockRow(Color.green, xStartBlockPosition+5, yStartBlockPosition+1, 3);
        drawBlockRow(Color.green, xStartBlockPosition+5, yStartBlockPosition+2, 3);
        drawBlockRow(Color.green, xStartBlockPosition+5, yStartBlockPosition+3, 3);
        drawBlockRow(Color.green, xStartBlockPosition+1, yStartBlockPosition+4, 11);
        drawBlockRow(Color.green, xStartBlockPosition+0, yStartBlockPosition+5, 13);
        drawBlockRow(Color.green, xStartBlockPosition+0, yStartBlockPosition+6, 13);
        drawBlockRow(Color.green, xStartBlockPosition+0, yStartBlockPosition+7, 13);
        drawBlockRow(Color.green, xStartBlockPosition+0, yStartBlockPosition+8, 13);
    } // drawTank

} // LivesArea
