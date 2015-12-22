package SpaceInvaders;

import java.util.ListIterator;
import java.util.Random;

public class Game {

    // Define a public attribute called gameOver of type Boolean
    public Boolean gameOver;

    // Number of lives to start with
    protected static final Integer INTIAL_NBR_LIVES = 3;

    // Movement constants which control amount of movement in the game
    protected static final Integer ALIEN_MOVE_HORIZONTAL_DELTA = 10; // blocks
    protected static final Integer ALIEN_MOVE_DOWN_DELTA = 20;// blocks
    protected static final Integer ALIEN_SHOT_MOVE_DELTA = 3;// blocks
    protected static final Integer BONUS_SAUCER_MOVE_DELTA = 2;
    protected static final Integer TANK_MOVE_DELTA = 3;// blocks
    protected static final Integer TANK_SHOT_MOVE_DELTA = 5;// blocks

    // Rate constants which control the speed of the the game
    protected static final Integer PLAY_SLEEP_TIME = 1; // milli seconds
    protected static final Long STARTING_ALIEN_MOVE_RATE = (long) 700; // milli seconds
    protected static final Long ALIEN_SHOT_MOVE_RATE = (long) 15; // milli seconds
    protected static final Long BONUS_SAUCER_MOVE_RATE = (long) 15;// milli seconds
    protected static final Long TANK_MOVE_RATE = (long) 8; // milli seconds
    protected static final Long TANK_SHOT_MOVE_RATE = (long) 10; // milli seconds
    protected static Double ALIEN_SPEED_ADJUSTMENT = .99;//.99

    // The constants for firing shots
    protected static final Integer NBR_OF_TANK_SHOTS = 1;
    protected static final Integer TANK_SHOT_RATE = 150; // milli seconds

    // Random number generator seeds
    protected static final Integer RANDOM_ALIEN_COLUMN_SEED = 10;
    protected static final Integer RANDOM_ALIEN_SHOT_RATE_SEED = 1500;
    //Random number to determine when to fire a alien shot and
    //which alien gets to shot
    Random random = new Random();
    Random randomBonusSaucer = new Random();
    
    Integer randomMovement = 5;
    Integer controlAlienSpeed = 0;
    Integer originalBackUpAlienDownMove = 0;
    Integer secondaryBackUpAlienDownMove = 1;
    
    // Timers used to control speed of game
    protected Timer alienShotRateTimer;

    // Define a protected attribute called alienMoveRateTimer of type Timer
    // to control the speed of movement of the aliens, use ALIEN_MOVE_RATE
    // as the timeout parameter
    protected Timer alienMoveRateTimer;
    protected Timer alienShotMoveRateTimer;
    protected Timer alienResetTimer;
    protected Timer bonusSaucerMoveRateTimer;
    protected Timer  bonusSaucerSwitchTimer;
    // Define a protected attribute called tankMoveRateTimer of type Timer
    // to control the speed of movement of the aliens, use TANK_MOVE_RATE
    // as the timeout parameter
    protected Timer tankMoveRateTimer;
    protected Timer tankShotMoveRateTimer;

    // Define a protected attribute called gamePanel of type GamePanel to hold
    // panel for the game
    protected GamePanel gamePanel;

    // Define a protected attribute called gameWindow of type GameWindow to hold
    // the window for the game
    protected GameWindow gameWindow;

    // Define a protected variable called scoreArea of ScoreArea
    protected ScoreArea scoreArea;

    // Define a protected attribute called currentAlienDirection of type
    // Sprite.MoveDirection to hold the current direction of movement for Aliens
    protected Sprite.MoveDirection currentAlienDirection;
    protected Sprite.MoveDirection currentBonusSaucerDirection;

    public Game(Graphics g) {
         random = new Random();

        // Initalize gamePanel to a new GamePanel using INITIAL_NBR_LIVES as
        // the initailNbrLives parameter
        gamePanel = new GamePanel(INTIAL_NBR_LIVES, g);
       
        // Initalize gameWindow to a new GameWindow using GamePanel.FRAME_WIDTH
        // and GamePanel.FRAME_HEIGHT as the width and height parameters
        // and gamePanel as the gamePanel parameter
        gameWindow = new GameWindow();

        // Initalize gameOVer to false to indicate the game is not over
        gameOver = false;

        // Initailize the currentAlienDirection to Sprite.MoveDirection.RIGHT
        // so the aliens begin by moving right
        currentAlienDirection = Sprite.MoveDirection.RIGHT;
        currentBonusSaucerDirection = Sprite.MoveDirection.RIGHT;
        
        alienShotRateTimer = new Timer(new Long(
            random.nextInt(RANDOM_ALIEN_SHOT_RATE_SEED)));
        alienMoveRateTimer = new Timer(STARTING_ALIEN_MOVE_RATE);
        alienShotMoveRateTimer = new Timer(ALIEN_SHOT_MOVE_RATE);
        alienResetTimer = new Timer((long)10);
        bonusSaucerMoveRateTimer = new Timer(BONUS_SAUCER_MOVE_RATE);
        bonusSaucerSwitchTimer = new Timer((long)2000);
        tankMoveRateTimer = new Timer(TANK_MOVE_RATE);
        tankShotMoveRateTimer = new Timer(TANK_SHOT_MOVE_RATE);
    } // ctpr

    public void updateAlienShots() {
        // Remove any shots that are off the screen
        ListIterator<AlienShot> alienShotsIterator = gamePanel.alienShots.listIterator();
        AlienShot alienShot;

        while (alienShotsIterator.hasNext()) {
            alienShot = alienShotsIterator.next();

            // If the current shot is off the screen remove it from the list
            if (alienShot.isOffScreen()) {
                alienShotsIterator.remove();
            } // if
        } // while

        // Randomly select a column for the next shot
        Integer randomAlienColumn = random.nextInt(RANDOM_ALIEN_COLUMN_SEED);

        if (alienShotRateTimer.expired()) {
            if(!gamePanel.tank.killed){
                if(gamePanel.aliens[4][randomAlienColumn].killed == false){
                     gamePanel.alienShots.add(gamePanel.aliens[4][randomAlienColumn].shoot());
                }
                else if(gamePanel.aliens[3][randomAlienColumn].killed == false){
                     gamePanel.alienShots.add(gamePanel.aliens[3][randomAlienColumn].shoot());
                }
                else if(gamePanel.aliens[2][randomAlienColumn].killed == false){
                     gamePanel.alienShots.add(gamePanel.aliens[2][randomAlienColumn].shoot());
                }
                else if(gamePanel.aliens[1][randomAlienColumn].killed == false){
                     gamePanel.alienShots.add(gamePanel.aliens[1][randomAlienColumn].shoot());
                }
                else if(gamePanel.aliens[0][randomAlienColumn].killed == false){
                     gamePanel.alienShots.add(gamePanel.aliens[0][randomAlienColumn].shoot());
                }
            }
             // Reset shot rate timer to a random time
             alienShotRateTimer.set(new Long(
                random.nextInt(RANDOM_ALIEN_SHOT_RATE_SEED)));
        } // if
    } // updateAlienShots

    public void updateTankShots() {
        // Remove any shots that are off the screen
        ListIterator<TankShot> tankShotsIterator = gamePanel.tankShots.listIterator();
        TankShot tankShot;

        while (tankShotsIterator.hasNext()) {
            tankShot = tankShotsIterator.next();

            // If the current shot is off the screen remove it from the list
            if (tankShot.isOffScreen()) {
                tankShotsIterator.remove();
            } // if
        } // while

        // Perform tank shot if the number of tank shots has not been
        // exceed and the fire key has been pressed
        if (tankShotMoveRateTimer.expired() &&
            (!gamePanel.tank.destroyed) &&
            (gamePanel.tankShots.size() < NBR_OF_TANK_SHOTS) &&
            gameWindow.fireKeyPressed && !gamePanel.tank.killed) {
            gamePanel.tankShots.add(gamePanel.tank.shoot());
            tankShotMoveRateTimer.reset();
        } // if
    } // updateTankShots

    // This method moves the aliens in the indicated direction and delta
    public void moveAliens(Sprite.MoveDirection direction, Integer delta) {
        // Define a local variable called row of type Integer to  be used to
        // index the alien array, initailize row to 0
        Integer row = 0;

        // Loop while row is not equal to the gamePanel.aliens.length (this
        // is the number of rows
        while (row != gamePanel.aliens.length) {
            // Define a local variable called row of type Integer to  be used to
            // index the alien array, initailize row to 0
            Integer column = 0;

            // Loop while column is not equal to gamePanel.aliens[row].length
            // (this is the number of columns)
            while (column != gamePanel.aliens[row].length) {

                // Call  gamePanel.aliens[row][column].move with the direction
                // and delta parameters to move each alien
                if(!gamePanel.tank.killed){
                    gamePanel.aliens[row][column].move(direction, delta);
                }
                // Increment the column local variable by 1 to move to
                // next column
                for (Integer shield=0; shield < GamePanel.NBR_OF_SHEILDS; shield++) {
                    // for shield starting at 0, while less than NBR_OF_SHEILD_PARTS,
                    // increment shild by 1
                    for (Integer shieldPart=0; shieldPart < GamePanel.NBR_OF_SHEILD_PARTS; shieldPart++) {
                        if(gamePanel.aliens[row][column].yPosition + 30  > gamePanel.shields[shield][shieldPart].yPosition &&
                               !gamePanel.aliens[row][column].killed ){
                            gamePanel.shields[shield][shieldPart].kill();
                            gamePanel.shields[shield][shieldPart].explosionState = ShieldPart.ExplosionState.EXPLOSION_DONE;
                        }
                    }
                }
                column = column + 1;
            } // end while loop

            // Increment the row local variable by 1 to move to
            // next row
            row = row + 1;
        } // End while loop
    } // moveAliens

    public void updateAlienPositions() {
        // Control rate of alien movement based on timer
        
        // If the alienMoveRateTimer has expired() then the aliens can be moved
        if (alienMoveRateTimer.expired()) {
            // Define a local variable called row of type Integer to  be used to
            // index the alien array, initailize row to 0
            Integer row = 0;

            // Check if any of the aliens are at the edge of the screen

            // Define a local variable called alienAtEdge of type Boolean to
            // be used to indicate if any aliens wer found at the edge of
            // screen, initailize alienAtEdge to false
            Boolean alienAtEdge = false;

            if(alienResetTimer.expired() && controlAlienSpeed == 1){
                 ALIEN_SPEED_ADJUSTMENT = ALIEN_SPEED_ADJUSTMENT - .01;
                 currentAlienDirection = Sprite.MoveDirection.RIGHT;
                 alienMoveRateTimer.set(STARTING_ALIEN_MOVE_RATE);       
                 alienResetTimer.reset();
                 gamePanel.nbrOfAliensKilled = 0;
                 controlAlienSpeed = controlAlienSpeed - 1;
            }
            
             if(gamePanel.nbrOfAliensKilled == 55 && controlAlienSpeed == 0){
                 if(gamePanel.livesArea.currentNbrLives < 3){
                    gamePanel.livesArea.incrementLives();
                 }
                 alienResetTimer.set((long)12);
                 controlAlienSpeed = controlAlienSpeed + 1;
             }
            
            // Loop while row is not equal to the gamePanel.aliens.length (this
            // is the number of rows
            while (row != gamePanel.aliens.length) {
                // Define a local variable called row of type Integer to be
                // used to index the alien array, initailize row to 0
                Integer column = 0;

                // Loop while column is not equal to gamePanel.aliens[row].length
                // (this is the number of columns)
                while (column != gamePanel.aliens[row].length) {

                    // if calling gamePanel.aliens[row][column].atScreenEdge
                    // with currentAlienDirection as the direction parameter
                    // indicates this alien is at the edge
                    if (gamePanel.aliens[row][column].
                        atScreenEdge(currentAlienDirection)) {
                        // Set alienAtEdge to true to indicate at least one
                        // alien is at the edge
                        alienAtEdge = true;
                    } // if

                    // Increment the column local variable by 1 to move to
                    // next column
                    column = column + 1;
                } // end while loop

                // Increment the row local variable by 1 to move to
                // next row
                row = row + 1;
            } // End while loop
            
            
            // If an alien is at edge move aliens down and reverse direction
            if (alienAtEdge) {
                // Move all aliens down
                originalBackUpAlienDownMove = gamePanel.aliens[1][1].yPosition;
                moveAliens(Sprite.MoveDirection.DOWN, ALIEN_MOVE_DOWN_DELTA);
                secondaryBackUpAlienDownMove = gamePanel.aliens[1][1].yPosition;
                // Reverse current alien direction
//                System.out.println("original " + originalBackUpAlienDownMove);
//                System.out.println("secondary " + secondaryBackUpAlienDownMove);
                // If the currentAlienDirection is Sprite.MoveDirection.RIGHT
                // then we need to change the direction to left
                if (currentAlienDirection == Sprite.MoveDirection.RIGHT) {
                    // Set currentAlienDirection to Sprite.MoveDirection.LEFT
                    currentAlienDirection = Sprite.MoveDirection.LEFT;
                } // End if

                // Else the new alian direction is rgith
                else {
                     // Set currentAlienDirection to Sprite.MoveDirection.RIGHT
                    currentAlienDirection = Sprite.MoveDirection.RIGHT;
                } // End else
            } // End if
            // Else not aliens are at the edge so continue moving in same direction
            else {
                // Call moveAliens with currentAlienDirection as the direction
                // parameter and ALIEN_MOVE_HORIZONTAL_DELTA as the delta
                // paremeter to move all aliens horizontally in the current
                // direction
                if(alienResetTimer.expired()){
                    moveAliens(currentAlienDirection, ALIEN_MOVE_HORIZONTAL_DELTA);
                }
            } // End else
            
            if(originalBackUpAlienDownMove == secondaryBackUpAlienDownMove){
                originalBackUpAlienDownMove = gamePanel.aliens[1][1].yPosition;
                moveAliens(Sprite.MoveDirection.DOWN, ALIEN_MOVE_DOWN_DELTA);
                secondaryBackUpAlienDownMove = gamePanel.aliens[1][1].yPosition;
            }
            // Call alienMoveRateTimer.reset() to reset the alien move rate
            // timer
            alienMoveRateTimer.reset();
        } // End if
    } // updateAlienPositions

    public void moveBonusSaucer() {
        
        if(gamePanel.bonusSaucer.killed){
            gamePanel.bonusSaucer.clear();
        }
        
        if(gamePanel.tank.killed){
            gamePanel.bonusSaucer.clear();
        }
        else{
            if(gamePanel.bonusSaucer.atScreenEdge(currentBonusSaucerDirection) && bonusSaucerSwitchTimer.expired()){
                gamePanel.bonusSaucer.move(currentBonusSaucerDirection, 0);
                randomMovement = randomBonusSaucer.nextInt(4000);
                gamePanel.bonusSaucer.clear();
            }
            if(gamePanel.bonusSaucer.atScreenEdge(currentBonusSaucerDirection) && randomMovement == 5 && bonusSaucerSwitchTimer.expired()){
                if(currentBonusSaucerDirection == Sprite.MoveDirection.RIGHT){
                     currentBonusSaucerDirection = Sprite.MoveDirection.LEFT;
                }
                else{
                     currentBonusSaucerDirection = Sprite.MoveDirection.RIGHT;
                }
                bonusSaucerSwitchTimer.reset();
            }

            if (gamePanel.bonusSaucer.killed == false && bonusSaucerMoveRateTimer.expired() && randomMovement == 5 && gamePanel.bonusSaucerInitialMoveTimer.expired()) {
                gamePanel.bonusSaucer.drawBonusSaucer();
                gamePanel.bonusSaucer.move(currentBonusSaucerDirection, BONUS_SAUCER_MOVE_DELTA);
                bonusSaucerMoveRateTimer.reset();
            } // End if
        }
    }
    
    public void updateTankShotPositions() {
        // Control rate of tank shot movement based on timer
        if (tankShotMoveRateTimer.expired()) {
            // Loop through all the tank shots and move them
            for (int shot = 0; shot < gamePanel.tankShots.size(); shot++) {
                // Timer has expired so its time to move the shot
                gamePanel.tankShots.get(shot).move(TANK_SHOT_MOVE_DELTA);
            } // for

            tankShotMoveRateTimer.reset();
        } // if
    } // tankShotPositions

    public void updateAlienShotPositions() {
        // Control rate of alien shot movement based on timer
        if (alienShotMoveRateTimer.expired()) {
            // Loop through all the alien shots and move them
            for (int shot = 0; shot < gamePanel.alienShots.size(); shot++) {
                // Timer has expired so its time to move the shot
                gamePanel.alienShots.get(shot).move(Sprite.MoveDirection.DOWN,
                    ALIEN_SHOT_MOVE_DELTA);
            } // for

            alienShotMoveRateTimer.reset();
        } // if
    } // updateAlienShotPositions

    public void updateTankPosition() {
        // Control rate of tank movement based on timer

        if (gamePanel.tank.destroyed &&
            (gamePanel.livesArea.currentNbrLives > 0)) {
            gamePanel.displayTank();
        } // End if

        // If the tankMoveRateTimer has expired() then the tank can be moved
        if (tankMoveRateTimer.expired()) {
            // Move tank in direction based on key press if the tank is
            // not at the edge of the screen

            // If the gameWindow.leftKeyPressed() indicates the left key has
            // been pressed and gamePanel.tank.atLeftScreenEdge() indicates
            // the tank is not at the left edge of the screen then move the
            // tank left
            if (gameWindow.leftKeyPressed &&
                (!gamePanel.tank.atLeftScreenEdge())) {

                // Call gamePanel.tank.move with Sprite.MoveDirection.LEFT as
                // the direction parameter and TANK_MOVE_DELTA as the delta
                // parameter to move the tank left
                if(gamePanel.tank.killed == false){
                     gamePanel.tank.move(Sprite.MoveDirection.LEFT, TANK_MOVE_DELTA);
                }
            } // End if

            // If the gameWindow.rightKeyPressed() indicates the right key has
            // been pressed and gamePanel.tank.atRightScreenEdge() indicates
            // the tank is not at the right edge of the screen then move the
            // tank right
            if (gameWindow.rightKeyPressed &&
                (!gamePanel.tank.atRightScreenEdge())) {
                // Call gamePanel.tank.move with Sprite.MoveDirection.RIGHT as
                // the direction parameter and TANK_MOVE_DELTA as the delta
                // parameter to move the tank right
                if(gamePanel.tank.killed == false){
                    gamePanel.tank.move(Sprite.MoveDirection.RIGHT, TANK_MOVE_DELTA);
                }
            } // End if

            // Call alienMoveRateTimer.reset() to reset the tank move rate
            // timer
            tankMoveRateTimer.reset();
        } // End if
    } // updateTankPosition

    public void detemineAlienShotCollisions() {

        // Remove any shots that are off the screen
        ListIterator<AlienShot> alienShotsIterator =
            gamePanel.alienShots.listIterator();

        AlienShot alienShot;

        while (alienShotsIterator.hasNext()) {
            alienShot = alienShotsIterator.next();
            if(gamePanel.tank.killed){
                alienShotsIterator.remove();
            }
            // If the current shot is off the screen remove it from the list
            if (gamePanel.tank.collides(alienShot)) {
                alienShotsIterator.remove();
                gamePanel.tank.kill();
                gamePanel.livesArea.decrementLives(); 
            } // if
        } // End while

        // TODO Set alienShotsIterator to gamePanel.alienShots.listIterator
        // to reset the alien shot iterator
        alienShotsIterator =
            gamePanel.alienShots.listIterator();

        // TODO while alienShotsIterator.hasNext() indicating there are more
        // alien shots to check
        while (alienShotsIterator.hasNext()) {
            // TODO Set slienShot to alienShotsIterator.next() to get the next 
            // shot
            alienShot = alienShotsIterator.next();

            // TODO Define a local variable called shieldHit of type Boolean
            // and initailise to false
            Boolean shieldHit = false;

            // TODO Define a local variable called hitShield of type Boolean
            // and initailise to false
            Integer hitShield = 0;
            
            // TODO Define a local variable called hitShieldPart of type Boolean
            // and initailise to false
            Integer hitShieldPart = 0;

            // TODO for shield starting at 0, while less than NBR_OF_SHEILDS,
            // increment shild by 1
            for (Integer shield=0; shield < GamePanel.NBR_OF_SHEILDS; shield++) {

                // TODO for shield starting at 0, while less than NBR_OF_SHEILD_PARTS,
                // increment shild by 1
                for (Integer shieldPart=0; shieldPart < GamePanel.NBR_OF_SHEILD_PARTS; shieldPart++) {

                    // TODO if the current sheild part (gamePanel.shields[shield][shieldPart])
                    // collides with the alienShot
                    if (gamePanel.shields[shield][shieldPart].collides(alienShot)) {
                        // Sheild hit by alien shot
                        // TODO Set sheildHit to true
                        shieldHit = true;
                        // TODO Set hitSheild and hitShieldPart to save the
                        // which sheild was hit
                        hitShield = shield;
                        hitShieldPart = shieldPart;
                    } // End if
                } // End for loop
            } // End for loop

            // TODO if sheildHit is true then
            if (shieldHit) {
                // TODO Call gamePanel.shields[hitShield][hitShieldPart].kill()
                // to kill the alien
                gamePanel.shields[hitShield][hitShieldPart].changeExplosionState = gamePanel.shields[hitShield][hitShieldPart].changeExplosionState + 1;
                if(gamePanel.shields[hitShield][hitShieldPart].changeExplosionState == 5){
                    gamePanel.shields[hitShield][hitShieldPart].kill();
                }
                
                // TODO Call alienShotsIterator.remove() to remoive the
                // alien shot
                alienShotsIterator.remove();  
            } // End if
        } // End while

    } // detemineAlienShotCollisions

    public void detemineTankShotCollisions() {
        // Define a local variable called row of type Integer to  be used to
        // index the alien array, initailize row to 0
        Integer row = 0;
        // Loop while row is not equal to the gamePanel.aliens.length (this
        // is the number of rows
        while (row != gamePanel.aliens.length) {
            // Define a local variable called column of type Integer to be
            // used to index the alien array, initailize row to 0
            Integer column = 0;

            TankShot tankShot;

            // Loop while column is not equal to gamePanel.aliens[row].length
            // (this is the number of columns)
            while (column != gamePanel.aliens[row].length) {
                // Remove any shots that are off the screen

                ListIterator<TankShot> tankShotsIterator =
                    gamePanel.tankShots.listIterator();

                while (tankShotsIterator.hasNext()) {
                    tankShot = tankShotsIterator.next();

                   if(gamePanel.bonusSaucer.collides(tankShot)){
                        gamePanel.bonusSaucer.kill();
                        tankShotsIterator.remove();
                        gamePanel.scoreArea.incrementScore(gamePanel.bonusSaucer.determineBonusScore());
                   }
                    
                    // If the current shot is off the screen remove it from the list
                    if (gamePanel.aliens[row][column].collides(tankShot)) {
                        gamePanel.aliens[row][column].kill();
                        tankShotsIterator.remove();

                        gamePanel.scoreArea.incrementScore(
                            gamePanel.aliens[row][column].scoreValue());
                        
                        // Speed up game, reduce timeout by 90%
                        alienMoveRateTimer.set((long)(alienMoveRateTimer.timeout()*
                            ALIEN_SPEED_ADJUSTMENT));
                    } // if
                } // End while
                
                // Set tankShotsIterator to gamePanel.alienShots.listIterator
                // to reset the alien shot iterator
                tankShotsIterator =
                    gamePanel.tankShots.listIterator();

                // while tankShotsIterator.hasNext() indicating there are more
                // alien shots to check
                while (tankShotsIterator.hasNext()) {
                    // Set slienShot to tankShotsIterator.next() to get the next
                    // shot
                    tankShot = tankShotsIterator.next();

                    // Define a local variable called shieldHit of type Boolean
                    // and initailise to false
                    Boolean shieldHit = false;

                    // Define a local variable called hitShield of type Boolean
                    // and initailise to false
                    Integer hitShield = 0;

                    // Define a local variable called hitShieldPart of type Boolean
                    // and initailise to false
                    Integer hitShieldPart = 0;

                    // for shield starting at 0, while less than NBR_OF_SHEILDS,
                    // increment shild by 1
                    for (Integer shield=0; shield < GamePanel.NBR_OF_SHEILDS; shield++) {

                        // for shield starting at 0, while less than NBR_OF_SHEILD_PARTS,
                        // increment shild by 1
                        for (Integer shieldPart=0; shieldPart < GamePanel.NBR_OF_SHEILD_PARTS; shieldPart++) {

                            // if the current sheild part (gamePanel.shields[shield][shieldPart])
                            // collides with the tankShot
                            if (gamePanel.shields[shield][shieldPart].collides(tankShot)) {
                                // Sheild hit by alien shot
                                // Set sheildHit to true
                                shieldHit = true;
                                // Set hitSheild and hitShieldPart to save the
                                // which sheild was hit
                                hitShield = shield;
                                hitShieldPart = shieldPart;
                            } // End if
                        } // End for loop
                    } // End for loop

                    // if sheildHit is true then
                    if (shieldHit) {
                        // Call gamePanel.shields[hitShield][hitShieldPart].kill()
                        // to kill the alien
                        gamePanel.shields[hitShield][hitShieldPart].changeExplosionState = gamePanel.shields[hitShield][hitShieldPart].changeExplosionState + 1;
                        if(gamePanel.shields[hitShield][hitShieldPart].changeExplosionState == 5){
                            gamePanel.shields[hitShield][hitShieldPart].kill();
                        }

                        // Call alienShotsIterator.remove() to remoive the
                        // alien shot
                        tankShotsIterator.remove();
                    } // End if
                } // End while

                // Increment the column local variable by 1 to move to
                // next column
                column = column + 1;
            } // end while loop

            // Increment the row local variable by 1 to move to
            // next row
            row = row + 1;
        } // End while loop

    } // detemineTankShotCollisions

    public void checkIfGameOver() {
        // Check if gameWindow.escKeyPressed is true then
        // "Esc" key was pressed
        if (gameWindow.escKeyPressed) {
            // Set gameOver to true to end game
            gameOver = true;
        } // End if

        // Else if gamePanel.livesArea.currentNbrLives equals 0
        // then there are no more lives and the game is over
        else if (gamePanel.livesArea.currentNbrLives == 0) {
            // Set gameOver to true to end game
            gameOver = true;
        } // End else if

        else { // Else the Game is not over
            // Set gameOver to false to end game
            gameOver = false;
        } // End else

        // Define a local variable called row of type Integer to  be used to
        // index the alien array, initailize row to 0
        Integer row = 0;

        // Loop while row is not equal to the gamePanel.aliens.length (this
        // is the number of rows
        while (row != gamePanel.aliens.length) {
            // Define a local variable called row of type Integer to be
            // used to index the alien array, initailize row to 0
            Integer column = 0;

            // Loop while column is not equal to gamePanel.aliens[row].length
            // (this is the number of columns)
            while (column != gamePanel.aliens[row].length) {

                // if the gamePanel.aliens[row][column].killed indicates
                // the alien is not killed and gamePanel.aliens[row][column].yPosition
                // plus Alien.BLOCK_HEIGHT is greater than or equal to gamePanel.tank.yPosition
                // the aliens have reached the tank so the game is over
                if ((gamePanel.aliens[row][column].killed != true) &&
                    ((gamePanel.aliens[row][column].yPosition + Alien.BLOCK_HEIGHT) >=
                     gamePanel.tank.yPosition)){
                    gameOver = true;
                } // End if

                // Increment the column local variable by 1 to move to
                // next column
                column = column + 1;
            } // end while loop

            // Increment the row local variable by 1 to move to
            // next row
            row = row + 1;
        } // End while loop

        // if gameOver equals true display a game over message
        if (gameOver) {
            // Call gamePanel.displayGameover() to display the game
            // over message
            gamePanel.displayGameover();
        } // End if
    } // checkIfGameOver

    public void play() {

        // Create alien and tank shots
        updateAlienShots();
        updateTankShots();

        // Move all object on the screen
        updateAlienPositions();
        updateAlienShotPositions();
        moveBonusSaucer();
        updateTankShotPositions();
        updateTankPosition();

        // Detemine shot collisions
        detemineAlienShotCollisions();
        detemineTankShotCollisions();

        // Have frame repaint all updated aliens, shots, sheilds and the tank
        gameWindow.repaint();

        // Sleep for shot time to allow other programs to run, for
        // example the programs that capture keystrokes
        try {
            Thread.sleep(PLAY_SLEEP_TIME);
        } //try
        catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } // catch

        // Check if the game is over
        checkIfGameOver();
    } // play
} // Game