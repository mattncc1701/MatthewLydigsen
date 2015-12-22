package SpaceInvaders;
import 

public class GameMain {

    public void paint(Graphics g){
        // Define a local variable called game of type Game and intialize it
        // to a new Game object
        Game game = new Game(g);
        
        // Loop while the game is not over
        // Test game.gameOver is false to detemine if the game is over
        while (!game.gameOver) {
            // Call game.play() to execute game play
            game.play();
        } // End while loop
       
    } // main
} // GameMain
