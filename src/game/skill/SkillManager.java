package game.skill;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class SkillManager {
	static HashMap<String, Skill> SkillList = new HashMap<String, Skill>();
	
	
	public static void loadSkills() {
		File SkillsFile = new File("src/game/skill/Skills.csv");
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(SkillsFile));
			String line = reader.readLine();
			while((line=reader.readLine())!=null) {
				String[] parts = line.split(";");
				Skill skill = new Skill(parts[0], parts[1],parts[2], Integer.valueOf(parts[3]), Integer.valueOf(parts[4]), Integer.valueOf(parts[5]), DamageType.valueOf(parts[6]), parts[7]);
				SkillList.put(skill.getNAME().toUpperCase(), skill);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static Skill getSkill(String key) {
		return SkillList.get(key);
	}
}
