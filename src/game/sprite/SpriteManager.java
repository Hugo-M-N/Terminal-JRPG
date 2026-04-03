package game.sprite;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class SpriteManager {
	static File itemFile = new File("src/sprites/items.png");
	static File monstersFile = new File("src/sprites/monsters.png");
	static File peopleFile = new File("src/sprites/rogues.png");
	static Sprite[] items, monsters, people;
	
	private static Sprite[] loadSprites(File spriteFile, int size) {
		BufferedImage tmp;
		ArrayList<Sprite> tmpSprites = new ArrayList<Sprite>();
		try {
			tmp = ImageIO.read(spriteFile);
			int rows = tmp.getHeight()/size;
			int colums = tmp.getWidth()/size;
			
			for(int i = 0; i<rows;i++) {
				for(int j=0; j<colums;j++) {
					BufferedImage tmpSprite = tmp.getSubimage(j*size, i*size, size, size);
					
					scan:
						for (int y = 0; y < tmpSprite.getHeight(); y++) {
							for (int x = 0; x < tmpSprite.getWidth(); x++) {
			    	            int argb = tmpSprite.getRGB(x, y);
			    	            int alpha = (argb >>> 24) & 0xFF;
			    	            if (alpha != 0) {
			    	            	tmpSprites.add(new Sprite(tmpSprite, size, size));
			    	            	break scan;
			    	            }
			    	        }
			    	    }
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (Sprite[]) tmpSprites.toArray(new Sprite[0]);
	}
	
	public static void loadItems() {
		items = loadSprites(itemFile, 32);
	}
	
	public static void loadMonsters() {
		monsters = loadSprites(monstersFile, 32);
	}
	
	public static void loadPeople() {
		people = loadSprites(peopleFile, 32);
	}
	
	public static Sprite[] getItems() {
		return items;
	}
	
	public static Sprite getItem(int ID) {
		return items[ID];
	}
	
	public static Sprite[] getMonsters() {
		return monsters;
	}
	
	public static Sprite getMonster(int ID) {
		return monsters[ID];
	}
	
	public static Sprite[] getPeople() {
		return people;
	}
	
	public static Sprite getPerson(int ID) {
		return people[ID];
	}
}
