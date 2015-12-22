package SpaceInvaders;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class ScoreArea extends BlockSprite {

    private static final Integer BLOCK_WIDTH = 100; // blocks
    private static final Integer BLOCK_HEIGHT = 17; // blocks

    // Define a public attribute called totalScore of type Integer
    public Integer totalScore;

    private static Font scoreFont = new Font("Arial", Font.BOLD, 30);

    public ScoreArea (
        Integer xStartBlockPosition, Integer yStartBlockPosition) {
        super(xStartBlockPosition, yStartBlockPosition, BLOCK_WIDTH, BLOCK_HEIGHT);

        // Initailize totalScore to 0
        totalScore = 0;
        
        // Call updateScore to update the score
        updateScore();
    } // ctor

    public void incrementScore(Integer value){
        // increment the totalScrore by value
        totalScore = totalScore + value;

        // Call updateScore to update the score
        updateScore();
    } // incrementScore

    protected final void updateScore() {
        clear();
        Graphics2D g2d = (Graphics2D) image.getGraphics();

        // Call setfont to set the scoreFont
        g2d.setFont(scoreFont);
        // Call ssetColor to Color.white
        g2d.setColor(Color.white);
        // Call draw string with paramers "Score", 80, 37
        g2d.drawString("Score", 80, 37);
        // Call draw string with paramers String.valueOf(totalScore), 200, 37
        g2d.drawString(String.valueOf(totalScore), 200, 37);
    } // draw

} // ScoreArea
