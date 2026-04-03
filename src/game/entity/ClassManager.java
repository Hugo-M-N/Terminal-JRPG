package game.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import game.skill.Skill;
import game.skill.SkillManager;

public class ClassManager {
	private static HashMap<String, EntityClass> classes = new HashMap<String, EntityClass>();

	public static void LoadClasses() {
		File ClassesFile= new File("src/entity/Classes.CSV");

		try {
			BufferedReader reader = new BufferedReader(new FileReader(ClassesFile));
			String line = reader.readLine();
			while((line = reader.readLine())!=null) {
				String parts[] = line.split(";");
				HashMap<Integer,Skill> lvSkills = new HashMap<Integer, Skill>();
				String[] skills = parts[8].split("#");
				if(parts[8].equals("TODO")) continue;
				for(String s : skills) {
					int lvl = Integer.valueOf(s.split(":")[0]);
					Skill skill = SkillManager.getSkill(s.split(":")[1]);
					lvSkills.put(lvl, skill);
				}
				EntityClass ec = new EntityClass(parts[0], parts[1], Integer.valueOf(parts[2]), Integer.valueOf(parts[3]), Integer.valueOf(parts[4]), Integer.valueOf(parts[5]), Double.valueOf(parts[6]), Double.valueOf(parts[7]), lvSkills);
				if (parts.length > 9 && !parts[9].isBlank()) ec.setSprites(parseSprites(parts[9]));
				classes.put(parts[0], ec);
			}
			reader.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/** Parsea "0:5-9:17-20:22" → {0,5,6,7,8,9,17,18,19,20,22} */
	private static int[] parseSprites(String raw) {
		ArrayList<Integer> ids = new ArrayList<>();
		for (String part : raw.split(":")) {
			part = part.trim();
			if (part.contains("-")) {
				String[] bounds = part.split("-");
				int from = Integer.parseInt(bounds[0].trim());
				int to   = Integer.parseInt(bounds[1].trim());
				for (int i = from; i <= to; i++) ids.add(i);
			} else if (!part.isEmpty()) {
				ids.add(Integer.parseInt(part));
			}
		}
		return ids.stream().mapToInt(Integer::intValue).toArray();
	}

	public static HashMap<String, EntityClass> getClasses() {
		return classes;
	}

	public static EntityClass getClass(String ID) {
		return classes.get(ID);
	}

	public static void setClasses(HashMap<String, EntityClass> classes) {
		ClassManager.classes = classes;
	}
}
