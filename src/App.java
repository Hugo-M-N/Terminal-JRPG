import java.io.File;

import game.Config;
import game.Game;
import game.TextGame;
import game.map.Tileset;


public class App {
	static String directoryURL = System.getProperty("user.home") + "/Terminal_JRPG/";
	static File directory = new File(directoryURL);
	
	public static void main(String[] args) {
		if (!directory.exists()) {
			directory.mkdir();
		}
		
		String MODE="";
		if(args.length>=1) MODE = args[0];
		Tileset.setMode(MODE);
		if(MODE.equalsIgnoreCase("terminal")) {
			TextGame tGame = new TextGame();
			Config.setAPP_MODE(0);
			tGame.run();
		} else {
			Game game = new Game();
			Config.setAPP_MODE(1);
			game.run();
		}
		
	}
}
