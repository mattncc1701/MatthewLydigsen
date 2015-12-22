package SpaceInvaders;

import static SpaceInvaders.LivesArea.scoreFont;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.JPanel;

public class GamePanel {

    // Size of the game window
    public static final Integer FRAME_WIDTH = 800;
    public static final Integer FRAME_HEIGHT = 650;
    Integer nbrOfAliensKilled = 0 ;
    Integer bonusScore = 0;
    Integer oldBonusSaucerXPosition = 0;
    Integer oldBonusSaucerYPosition = 0;
    Integer resetBonusTime = 0;
    
    protected Timer bonusScoreDisplay;
    protected Timer bonusSaucerKillTimer;
    protected Timer bonusSaucerInitialMoveTimer;
    
     Random randomBonusSaucerTime = new Random();
    
    protected BonusSaucer bonusSaucer;

    protected LinkedList<TankShot> tankShots;

    protected LinkedList<AlienShot> alienShots;

    // Define a public attribute called aliens as a two dimensional
    // array of type Alien classes
    public Alien[][] aliens;

    // TODO Define a protected attribute called sheilds as a two dimensional
    // array of type ShieldPart classes
    public ShieldPart[][] shields;

    // Define a protected attribute called tank of type PlayerTank
    protected PlayerTank tank;

    // Sprites for the game panel

    // Deine a private attribute called gameover or type Boolean
    private Boolean displayGameover;

    // Define a public attribute called scoreArea of type ScoreArea
    public ScoreArea scoreArea;

    // Define a public attribute called livesArea of type LivesArea
    public LivesArea livesArea;

    // Define a attribute called gameOverFont of type Font using
    // "Arial", Font.BOLD, and 50 as the construtor parameters
    static Font gameOverFont = new Font("Arial", Font.BOLD, 50);

    // Veritcal screen layout constants

    protected static final Integer INITIAL_ALIEN_Y_BLOCK_POSITION = 35;//20
    protected static final Integer INITIAL_TANK_X_BLOCK_POSITION = 80;
    protected static final Integer INITIAL_TANK_Y_BLOCK_POSITION = 173;
    protected static final Integer INITIAL_SAUCER_X_BLOCK_POSITION = -16;
    protected static final Integer INITIAL_SAUCER_Y_BLOCK_POSITION = 20;
    protected static final Integer SHEILD1_X_BLOCK_POSITION = 40;
    protected static final Integer SHEILD2_X_BLOCK_POSITION = 90;
    protected static final Integer SHEILD3_X_BLOCK_POSITION = 140;
    protected static final Integer SHEILD4_X_BLOCK_POSITION = 190;
    protected static final Integer SHEILD_Y_BLOCK_POSITION = 150;
    protected static final Integer LIVES_AREA_X_BLOCK_POSITION = 0;
    protected static final Integer LIVES_AREA_Y_BLOCK_POSITION = 188;
    protected static final Integer SCORE_AREA_X_BLOCK_POSITION = 164;
    protected static final Integer SCORE_AREA_Y_BLOCK_POSITION = 188;

    // Constants for seperation between and rows and columns
    protected static final Integer ROW_SEPERATION_BLOCK = 4;
    protected static final Integer COLUMN_SEPERATION_BLOCK = 6;

    // Number of aliens
    protected static final Integer NBR_OF_ALIEN_ROWS = 5;
    protected static final Integer NBR_OF_ALIEN_COLUMNS = 11;

    // Number of sheilds
    public static final Integer NBR_OF_SHEILDS = 4;
    public static final Integer NBR_OF_SHEILD_PARTS = 9;

    public GamePanel(Integer initailNbrLives, Graphics g) {
        // Call the JPanl superclasses constructor
        super();

        bonusScoreDisplay = new Timer((long) 1);
        
        bonusSaucerKillTimer = new Timer((long) 200);
        
        bonusSaucerInitialMoveTimer = new Timer((long)5000);
        
        // Initailize gameover to false
        displayGameover =  false;

        // Create bonus saucer
        bonusSaucer = new BonusSaucer(
            INITIAL_SAUCER_X_BLOCK_POSITION, INITIAL_SAUCER_Y_BLOCK_POSITION, this);
        

        // Create list for tank and alien shots
        // Use a dynamic linked list for shots since they come and go
        // very quickly
        tankShots = new LinkedList<TankShot>();
        alienShots = new LinkedList<AlienShot>();

        // Initalize the aliens array attribute using the NBR_OF_ALIEN_ROWS and
        // NBR_OF_ALIEN_COLUMNS constants to size the array
        aliens = new Alien[NBR_OF_ALIEN_ROWS][NBR_OF_ALIEN_COLUMNS];

        // Define a local variable called row of type Integer to  be used to
        // index the alien array, initailize row to 0
        Integer row = 0;

        // Define a local variable called yBlockPosition of type Integer to
        // be used to calculate the initail position of the alien, initailize
        // yblockPosition to INITIAL_ALIEN_Y_BLOCK_POSITION
        Integer yBlockPosition = INITIAL_ALIEN_Y_BLOCK_POSITION;

        // Loop while row is not eual to the NBR_OF_ALIEN_ROWS
        while (row != NBR_OF_ALIEN_ROWS) {
            // Define a local variable called row of type Integer to  be used to
            // index the alien array, initailize row to 0
            Integer column = 0;

            // Define a local variable called xBlockPosition of type Integer to
            // be used to calculate the initail position of the alien,
            // initailize xblockPosition to 0
            Integer xBlockPosition = 0;

            // Loop while column is not eual to the NBR_OF_ALIEN_COLUMNS
            while (column != NBR_OF_ALIEN_COLUMNS) {

                // If row is equal to 0 then
                if (row == 0) {
                    // Initailize aliens[row][column] to a new Squid alien using
                    // xBlockPosition, yBlockPosition as the block position
                    // parameters and this are the gamePanel parameter
                    aliens[row][column] =  new Squid(xBlockPosition, yBlockPosition, this);
                } // End if

                // Else If row is equal to 1 or 2 then
                else if ((row == 1) || (row == 2)) {
                    // Initailize aliens[row][column] to a new Grasshopper alien
                    // using xBlockPosition, yBlockPosition as the block position
                    // parameters and this are the gamePanel parameter
                    aliens[row][column] =  new Grasshopper(xBlockPosition, yBlockPosition, this);
                } // End else if

                // Else this must be rows 3 or 4
                else {
                    // Initailize aliens[row][column] to a new Skull alien using
                    // xBlockPosition, yBlockPosition as the block position
                    // parameters and this are the gamePanel parameter
                    aliens[row][column] =  new Skull(xBlockPosition, yBlockPosition, this);
                } // End else

                
                // Increment the column local variable by 1 to move to
                // next column
                column = column + 1;

                // Increment the xBlockPosition local variable by Alien.BLOCK_WIDTH
                // and COLUMN_SEPERATION_BLOCK
                xBlockPosition = xBlockPosition + Alien.BLOCK_WIDTH + COLUMN_SEPERATION_BLOCK;
            } // End while loop

            // Increment the row local variable by 1 to move to
            // next row
            row = row + 1;

            // Increment the yBlockPosition local variable by Alien.BLOCK_HEIGHT
            // and ROW_SEPERATION_BLOCK
            yBlockPosition = yBlockPosition + Alien.BLOCK_HEIGHT + ROW_SEPERATION_BLOCK;
        } // End while loop

        // TODO Initalize the sheilds array attribute using the NBR_OF_SHEILDS
        // and NBR_OF_SHEILD_PARTS constant to size the array
        shields = new ShieldPart[NBR_OF_SHEILDS][NBR_OF_SHEILD_PARTS];
        
        // TODO for shield starting at 0, while less than NBR_OF_SHEILDS,
        // increment shild by 1
        for (Integer shield = 0; shield < NBR_OF_SHEILDS; shield++) {
            // TODO Define a local variable called blockPosition of type Integer,
            // initailize it to 0
            Integer blockPosition = 0;

            // TODO switch on the value of shield
            switch (shield) {
                // TODO case the valus of sheild is 0
                case 0:
                    // TODO Set blockPosition to SHEILD1_X_BLOCK_POSITION
                    blockPosition = SHEILD1_X_BLOCK_POSITION;
                    // TODO break
                    break;
                // TODO case the valus of sheild is 1
                case 1:
                    // TODO Set blockPosition to SHEILD2_X_BLOCK_POSITION
                    blockPosition = SHEILD2_X_BLOCK_POSITION;
                    // TODO break
                    break;
                // TODO case the valus of sheild is 2
                case 2:
                    // TODO Set blockPosition to SHEILD3_X_BLOCK_POSITION
                    blockPosition = SHEILD3_X_BLOCK_POSITION;
                    // TODO break
                    break;
                // TODO case the valus of sheild is 3
                case 3:
                    // TODO Set blockPosition to SHEILD4_X_BLOCK_POSITION
                    blockPosition = SHEILD4_X_BLOCK_POSITION;
                    // TODO break
                    break;
            } // End switch

            // TODO Explain how this array is being built, not coding needed
            shields[shield][0] =
                new Shield1(blockPosition, SHEILD_Y_BLOCK_POSITION);
            shields[shield][1] =
                new Shield2(blockPosition + 7, SHEILD_Y_BLOCK_POSITION);
            shields[shield][2] =
                new Shield3(blockPosition + 13, SHEILD_Y_BLOCK_POSITION);
            shields[shield][3] =
                new Shield4(blockPosition - 2, SHEILD_Y_BLOCK_POSITION + 5);
            shields[shield][4] =
                new Shield5(blockPosition + 4, SHEILD_Y_BLOCK_POSITION + 5);
            shields[shield][5] =
                new Shield6(blockPosition + 10, SHEILD_Y_BLOCK_POSITION + 5);
            shields[shield][6] =
                new Shield7(blockPosition + 16, SHEILD_Y_BLOCK_POSITION + 5);
            shields[shield][7] =
                new Shield8(blockPosition - 2, SHEILD_Y_BLOCK_POSITION + 11);
            shields[shield][8] =
                new Shield9(blockPosition + 14, SHEILD_Y_BLOCK_POSITION + 11);
        } // End for

        // Call displayTank to display the tank
        displayTank();

        // Initailize livesArea attribute to a new LivesArea using
        // LIVES_AREA_X_BLOCK_POSITION and LIVES_AREA_Y_BLOCK_POSITION as the
        // block position parameters and the initailNbrLives parameter for the
        // initailNbrLives parameter and "this" as the gamePanel parameter
        livesArea = new LivesArea(LIVES_AREA_X_BLOCK_POSITION,
                LIVES_AREA_Y_BLOCK_POSITION, initailNbrLives, this);
        
        // Initail the scoreArea attribute using SCORE_AREA_X_BLOCK_POSITION
        // as the XBlockPosition parameter and SCORE_AREA_Y_BLOCK_POSITION as
        // the yBlockPosition parameter
        scoreArea = new ScoreArea(SCORE_AREA_X_BLOCK_POSITION,
            SCORE_AREA_Y_BLOCK_POSITION);
    } // ctor

    public void displayGameover()  {
        // Set gameover to true
        displayGameover = true;
    } // displayGameover

    public final void displayTank() {
        // Initailize tank attribute to a new PlayerTank using
        // INITIAL_TANK_X_BLOCK_POSITION and INITIAL_TANK_Y_BLOCK_POSITION as
        // the block position parameters and "this" are the gamePanel parameter
        tank = new PlayerTank(INITIAL_TANK_X_BLOCK_POSITION,
            INITIAL_TANK_Y_BLOCK_POSITION, this);
    } // displayTank

    public void clear(Graphics g) {
        // Define a local variable called gd2 of type Graphics2D and
        // initialize it to g using a cast to Graphics2D
        Graphics g2d = (Graphics2D) g;

        // Call g2d.setColor to set the color to Color.black
        g2d.setColor(Color.black);
        // Call g2d.drawRect with 0, 0 are the x and y location parameters and
        // getWidth() and getHeight() as the width and height parameters
        g2d.drawRect(0, 0, getWidth(), getHeight());
        // Call g2d.drawRect with 0, 0 are the x and y location parameters and
        // getWidth() and getHeight() as the width and height parameters
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // g2d.repaint to update the screen with these changes
        repaint();
    } // clearScreen

    @Override
    public void paint(Graphics g) {
        // Create an accelerated image to use for double buffering to make
        // screen updates smooth
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().
            getDefaultScreenDevice().getDefaultConfiguration();
        Image image = gc.createCompatibleImage(getWidth(), getWidth(), Transparency.BITMASK);
        Graphics2D g2d = (Graphics2D) image.getGraphics();

        // Call clear with g2d local variable to clear the current contents of window
        clear(g2d);

        // Draw bonus saucer
        bonusSaucer.draw(g2d);
        
        if(bonusSaucer.killed && resetBonusTime == 0){
            bonusSaucerKillTimer.reset();
            bonusSaucerInitialMoveTimer = new Timer((long)(randomBonusSaucerTime.nextInt(10) + 6) * 1000);
            oldBonusSaucerXPosition = bonusSaucer.xPosition;
            oldBonusSaucerYPosition = bonusSaucer.yPosition;
            resetBonusTime = resetBonusTime + 1;
        }
        
        if(!bonusSaucerKillTimer.expired() && resetBonusTime == 1){
            bonusScore = bonusSaucer.determineBonusScore();
             bonusSaucer = new BonusSaucer(
                  INITIAL_SAUCER_X_BLOCK_POSITION, INITIAL_SAUCER_Y_BLOCK_POSITION, this);
             bonusScoreDisplay.set((long)1000);
             resetBonusTime = resetBonusTime - 1;
        }
        
        if(!bonusScoreDisplay.expired()){
             g2d.setFont(scoreFont);
             g2d.setColor(Color.white);
             g2d.drawString(String.valueOf(bonusScore), oldBonusSaucerXPosition, oldBonusSaucerYPosition + 20);
        }
        
        if(tank.killed){
            bonusSaucer = new BonusSaucer(
                  INITIAL_SAUCER_X_BLOCK_POSITION, INITIAL_SAUCER_Y_BLOCK_POSITION, this);
        }
        
        // Draw aliens
        // Define a local variable called row of type Integer and initailize it
        // to 0, this is used to access the aliens by row
        Integer row = 0;
        Integer yBlockPosition = INITIAL_ALIEN_Y_BLOCK_POSITION;
        // Loop while row is not eual to the NBR_OF_ALIEN_ROWS
        while (row != aliens.length) { // Number of rows

            // Define a local variable called column of type Integer and
            // initailize it to 0, this is used to access the aliens by column
            Integer column = 0;
            Integer xBlockPosition = 0;
            // Loop while column is not eual to the NBR_OF_ALIEN_COLUMNS
            while (column != aliens[row].length) { // Number of columns
                // Call aliens[row][column].draw with g2d local variable
                // to draw alien on screen
                aliens[row][column].draw(g2d);
                
                if(aliens[row][column].resetKilled){
                    aliens[row][column].resetKilled = false;
                    nbrOfAliensKilled++;
                }
                if(nbrOfAliensKilled == 55){
                     if (row == 0) {
                    // Initailize aliens[row][column] to a new Squid alien using
                    // xBlockPosition, yBlockPosition as the block position
                    // parameters and this are the gamePanel parameter
                    aliens[row][column] =  new Squid(xBlockPosition, yBlockPosition, this);
                    } // End if

                    // Else If row is equal to 1 or 2 then
                    else if ((row == 1) || (row == 2)) {
                        // Initailize aliens[row][column] to a new Grasshopper alien
                        // using xBlockPosition, yBlockPosition as the block position
                        // parameters and this are the gamePanel parameter
                        aliens[row][column] =  new Grasshopper(xBlockPosition, yBlockPosition, this);
                    } // End else if

                    // Else this must be rows 3 or 4
                    else {
                        // Initailize aliens[row][column] to a new Skull alien using
                        // xBlockPosition, yBlockPosition as the block position
                        // parameters and this are the gamePanel parameter
                        aliens[row][column] =  new Skull(xBlockPosition, yBlockPosition, this);
                    } // End else
                }
                // Increment the column local variable by 1
                column = column + 1;
                xBlockPosition = xBlockPosition + Alien.BLOCK_WIDTH + COLUMN_SEPERATION_BLOCK;
            } // End while loop

            // Increment the row local variable by 1
            row = row + 1;
            yBlockPosition = yBlockPosition + Alien.BLOCK_HEIGHT + ROW_SEPERATION_BLOCK;
            
        } // End while loop
        
        // Draw shields
        // TODO for shield starting at 0, while less than NBR_OF_SHEILDS,
        // increment shild by 1
        for (Integer shield=0; shield < NBR_OF_SHEILDS; shield++) {
            // TODO for shield starting at 0, while less than NBR_OF_SHEILD_PARTS,
            // increment shild by 1
            for (Integer shieldPart=0; shieldPart < NBR_OF_SHEILD_PARTS; shieldPart++) {
                // TODO Call shields[shield][shieldPart].draw with g2d as the parameter
                if(shields[shield][shieldPart].explosionState != ShieldPart.ExplosionState.EXPLOSION_DONE){
                      shields[shield][shieldPart].draw(g2d);
                }
            } // End for loop
        } // End for loop

        // Draw tank shots
        for (int i=0; i < tankShots.size(); i++) {
            tankShots.get(i).draw(g2d);
        } // for
        
        // Draw aliens shots
        for (int i=0; i < alienShots.size(); i++) {
            alienShots.get(i).draw(g2d);
        } // for
        
        bonusSaucer.draw(g2d);
        // Draw tank
        // Call tank.draw with g2d local variable to draw tank on screen
        tank.draw(g2d);

        // Driaw line at bottom of screen
        g2d.setColor(Color.white);
        g2d.fillRect(0, 560, GamePanel.FRAME_WIDTH, 3);

        // Draw lives area
        // Call livesArea.draw with g2d local variable to draw tank on screen
        livesArea.draw(g2d);
        
        // Draw score area
        // Call scoreArea.draw with g2d local variable to draw score area
        // on the screen
        scoreArea.draw(g2d);

        if (displayGameover == true) {
            g2d.setFont(gameOverFont);
            g2d.setColor(Color.white);
            g2d.drawString("Game Over", 260, 290);
        } // End if

        // Draw over entire screen
        g.drawImage(image, 0, 0, null);
    } // paint

    @Override
    public void update(Graphics g) {
        // The default update clears the screen on each repaint, this
        // would cause flickering we don't want so just have it call paint
        paint(g);
    } // update

} // GamePanel

