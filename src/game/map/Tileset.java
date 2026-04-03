package game.map;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Tileset {
	static String MODE;
	static int size = 32;
	static File file = new File("32rogues/tiles.png");
	static final File textFile = new File("src/maps/Tileset.txt");
	static HashMap<Integer, String> tileSet = new HashMap<Integer, String>();
	static BufferedImage[] tiles;
	
	public static void loadTileSet() throws IOException {
		if(MODE.equalsIgnoreCase("terminal")){
			BufferedReader reader = new BufferedReader(new FileReader(textFile));
			String line = reader.readLine();
			while((line=reader.readLine())!=null) {
				if(line.charAt(0)!='#') {
					String[] parts = line.split(";");
					parts[2] = setColor(parts[2], parts[3]);
					parts[2] = setBgColor(parts[2], parts[4]);
					tileSet.put(Integer.parseInt(parts[0],16), parts[2]);					
				}
			}
			reader.close();
		} else {			
			BufferedImage tileSheet = ImageIO.read(file);
			int rows = tileSheet.getHeight()/size;
			int colums = tileSheet.getWidth()/size;
			tiles = new BufferedImage[rows * colums];
			
			for(int i = 0; i<rows;i++) {
				for(int j=0; j<colums;j++) {
					tiles[i*colums + j] = tileSheet.getSubimage(j*size, i*size, size, size);
				}
			}
		}
	}

	public static void setMode(String mode) {
		MODE = mode;
	}
	
	static String setColor(String input, String color) {
		switch(color) {
			case "black":
				return "\033[30m" + input + "\033[0m";
			case "red":
				return "\033[31m" + input + "\033[0m";
			case "green":
				return "\033[32m" + input + "\033[0m";
			case "yellow":
				return "\033[33m" + input + "\033[0m";
			case "blue":
				return "\033[34m" + input + "\033[0m";
			case "magenta":
				return "\033[35m" + input + "\033[0m";
			case "cyan":
				return "\033[36m" + input + "\033[0m";
			case "white":
				return "\033[37m" + input + "\033[0m";
			case "bright_black":
				return "\033[90m" + input + "\033[0m";
			case "bright_red":
				return "\033[91m" + input + "\033[0m";
			case "bright_green":
				return "\033[92m" + input + "\033[0m";
			case "bright_yellow":
				return "\033[93m" + input + "\033[0m";
			case "bright_blue":
				return "\033[94m" + input + "\033[0m";
			case "bright_magenta":
				return "\033[95m" + input + "\033[0m";
			case "bright_cyan":
				return "\033[96m" + input + "\033[0m";
			case "bright_white":
				return "\033[97m" + input + "\033[0m";
		}
		return input;
	}
	
	static String setBgColor(String input, String color) {
		switch(color) {
		case "black":
			return "\033[40m" + input + "\033[0m";
		case "red":
			return "\033[41m" + input + "\033[0m";
		case "green":
			return "\033[42m" + input + "\033[0m";
		case "yellow":
			return "\033[43m" + input + "\033[0m";
		case "blue":
			return "\033[44m" + input + "\033[0m";
		case "magenta":
			return "\033[45m" + input + "\033[0m";
		case "cyan":
			return "\033[46m" + input + "\033[0m";
		case "white":
			return "\033[47m" + input + "\033[0m";
		case "bright_black":
			return "\033[100m" + input + "\033[0m";
		case "bright_red":
			return "\033[101m" + input + "\033[0m";
		case "bright_green":
			return "\033[102m" + input + "\033[0m";
		case "bright_yellow":
			return "\033[103m" + input + "\033[0m";
		case "bright_blue":
			return "\033[104m" + input + "\033[0m";
		case "bright_magenta":
			return "\033[105m" + input + "\033[0m";
		case "bright_cyan":
			return "\033[106m" + input + "\033[0m";
		case "bright_white":
			return "\033[107m" + input + "\033[0m";
		}
		return input;
	}
	
	public static int getSize() {
		return size;
	}

	public static void setSize(int size) {
		Tileset.size = size;
	}

	public static File getFile() {
		return file;
	}

	public static void setFile(File file) {
		Tileset.file = file;
	}

	public static BufferedImage[] getTiles() {
		return tiles;
	}
	
	public static HashMap<Integer, String> getTextTiles(){
		return tileSet;
	}

	public static void setTiles(BufferedImage[] tiles) {
		Tileset.tiles = tiles;
	}

	public static BufferedImage getTile(int ID) {
		return tiles[ID];
	}
	
	public static String getTextTile(int ID) {
		return tileSet.get(ID);
	}
}
