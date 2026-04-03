package game.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import game.skill.Skill;
import game.skill.SkillManager;

public class EnemyManager {
	private static HashMap<String,Entity> Enemies = new HashMap<String,Entity>();
	
	public static void loadEnemies() {
		File EnemiesFile = new File("src/entity/Enemies.csv");
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(EnemiesFile));
			String line = reader.readLine();
			while((line=reader.readLine()) != null) {
				String[] parts = line.split(";");
				Entity Enemy = new Entity(parts[1], Integer.valueOf(parts[2]), Integer.valueOf(parts[3]),
						Integer.valueOf(parts[4]), Integer.valueOf(parts[5]), Integer.valueOf(parts[6]),
						Integer.valueOf(parts[7]), Integer.valueOf(parts[8]));
				if(!(parts[9].equalsIgnoreCase("null"))) {
					String[] skills = parts[9].split("#");
					for (String skill : skills) {
						Skill tmp = SkillManager.getSkill(skill);
						Enemy.addSkill(tmp);
					}
				}
				
				Enemies.put(parts[0], Enemy);				
			}
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Entity getEnemy(String EnemyID) {
		return new Entity(Enemies.get(EnemyID));
	}
}
