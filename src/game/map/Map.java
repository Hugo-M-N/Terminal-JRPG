package game.map;

import java.io.BufferedReader;
import java.io.FileReader;

import game.trigger.TriggerManager;

public class Map {
	int width;
	int height;
	boolean[][] collisions;
	int[][] mapTiles;
	int initialX, initialY;
	String[] enemies;
	String[] NPCs;
	String[] triggerEntries;
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public boolean getCollision(int x, int y) {
		return collisions[y][x];
	}

	public boolean[][] getCollisions() {
		return collisions;
	}

	public void setCollisions(boolean[][] collisions) {
		this.collisions = collisions;
	}

	public int getMapTile(int x, int y) {
		return mapTiles[y][x];
	}
	
	public int[][] getMapTiles() {
		return mapTiles;
	}

	public void setMapTiles(int[][] mapTiles) {
		this.mapTiles = mapTiles;
	}
	
	public String[] getEnemies() {
		return enemies;
	}
	
	public String[] getNPCs() {
		return NPCs;
	}
	
	public Map(String mapRoute) {

		try {
			BufferedReader reader = new BufferedReader(new FileReader(mapRoute));
			String line = reader.readLine();
			String[] parts = line.split(";");
			width = Integer.valueOf(parts[0].split(":")[0]);
			height = Integer.valueOf(parts[0].split(":")[1]);
			enemies = reader.readLine().split("#");
			NPCs = reader.readLine().split("#");
			triggerEntries = reader.readLine().split("#");
			TriggerManager.load(triggerEntries);
			collisions = new boolean[height][width];
			reader.readLine();
			for(int i=0; i<height; i++) {
				line = reader.readLine();
				String[] tmp = line.split("");
				for(int j=0; j<tmp.length;j++) {
					collisions[i][j] = "1".equals(tmp[j]);
				}
			}
			mapTiles = new int[height][width];
			reader.readLine();
			for(int i=0; i<height; i++) {
				line = reader.readLine();
				String[] tmp = line.split(" ");
				for(int j=0; j<width;j++) {
					mapTiles[i][j] = Integer.valueOf(tmp[j]);
				}
			}
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	
	}
}
