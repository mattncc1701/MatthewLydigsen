package SpaceInvaders;

import java.awt.Color;
import java.awt.Graphics2D;

public class Shield6 extends ShieldPart {

       public Shield6 (Integer xStartBlockPosition, Integer yStartBlockPosition) {
        super(xStartBlockPosition, yStartBlockPosition);
    } // Shield
     public void draw1(){
        drawBlockRow(Color.GRAY, 0, 0, 6);
        drawBlockRow(Color.GRAY, 0, 1, 6);
        drawBlockRow(Color.GRAY, 0, 2, 6);
        drawBlockRow(Color.GRAY, 0, 3, 6);
        drawBlockRow(Color.GRAY, 2, 4, 4);
        drawBlockRow(Color.GRAY, 3, 5, 3);

    }

} // Shield
