
import game.Game;

public class Launcher {
	public static void main(String[] args) throws  Exception
	{
		Game game  = new Game("The Last Chess Game", 0.6f); //Change the number to alter the screen size
		
		game.start();	
	}
}
