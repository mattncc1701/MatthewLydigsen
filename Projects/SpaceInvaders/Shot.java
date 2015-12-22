package SpaceInvaders;

public abstract class Shot extends BlockSprite {

    // Repreents the type of alien shot
    public enum shotType{
        TANK_SHOT,
        ALIEN_SHOT};

    // Indicates this shot is off the screen
    protected Boolean offScreen;

    // Indicates this shot has detonated
    public Boolean detonated;

    // Game panel which contains sprite
    protected GamePanel gamePanel;

    public Shot (Integer xStartBlockPosition, Integer yStartBlockPosition,
                 Integer blockWidth, Integer blockHeight, GamePanel gamePanel) {
        super(xStartBlockPosition, yStartBlockPosition, blockWidth, blockHeight);

        this.gamePanel = gamePanel;
        offScreen = false;
        detonated = false;
    } // ctor  
    
    // Called when show collides with another object
    public void detonate ()  {
        detonated = true;
    } // detonate

    public abstract Boolean isOffScreen();

    public abstract shotType determineShotType();
} // TankShot
