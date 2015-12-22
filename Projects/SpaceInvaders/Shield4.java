package SpaceInvaders;

import java.awt.Color;
import java.awt.Graphics2D;

public class Shield4 extends ShieldPart {

      public Shield4 (Integer xStartBlockPosition, Integer yStartBlockPosition) {
        super(xStartBlockPosition, yStartBlockPosition);
    } // Shield

    public void draw1(){
        drawBlockRow(Color.GRAY, 1, 0, 5);
        drawBlockRow(Color.GRAY, 0, 1, 6);
        drawBlockRow(Color.GRAY, 0, 2, 6);
        drawBlockRow(Color.GRAY, 0, 3, 6);
        drawBlockRow(Color.GRAY, 0, 4, 6);
        drawBlockRow(Color.GRAY, 0, 5, 6);

    }
} // Shield
