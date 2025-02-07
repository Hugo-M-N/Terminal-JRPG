package game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import game.entity.Entity;
import game.entity.EntityClass;
import game.skill.Skill;

public class SaveManager {
	static String directoryURL="saves/";
	static File directory = new File(directoryURL);
	
	
	public static boolean SaveGame(ArrayList<Entity> Allies, String fileName) {
		CheckSaveDirectory();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(directoryURL+fileName+".save"));
			writer.write(fileName);
			for(Entity Ally : Allies) {
				writer.write("\n" + Ally.getNAME());
				writer.write("#" + Ally.getLVL() + ";" + Ally.getXP());
				writer.write("#" + Ally.getGOLD());
				writer.write("#" + Ally.getCLASS());
				writer.write("#" + Ally.getHP() + ";" + Ally.getMAX_HP());
				writer.write("#" + Ally.getMP() + ";" + Ally.getMAX_MP());
				writer.write("#" + Ally.getSTR() + ";" + Ally.getMAG() + ";" + Ally.getDEF() + ";" + Ally.getDEX());
				writer.write("#");
				if (Ally.getSkills().size()>1) {
					for(Skill skill : Ally.getSkills()) {
						writer.write(skill.getNAME() + "|" + skill.getDMG() + "|" + skill.getCOST() + "|" + skill.getDURATION() + "|" + skill.getDamageType() + ";");
					}					
				} else if (Ally.getSkills().size()==1) writer.write(Ally.getSkills().get(0).getNAME() + "|" + Ally.getSkills().get(0).getDMG() + "|" + Ally.getSkills().get(0).getCOST() + "|" + Ally.getSkills().get(0).getDURATION() + "|" + Ally.getSkills().get(0).getDamageType());
				writer.write("#");
				if(Ally.getInventory().size()>1) {
				} else if(Ally.getInventory().size()==1) writer.write(Ally.getInventory().get(0).getNAME() + "|" + Ally.getInventory().get(0).getDESC() + "|" + Ally.getInventory().get(0).getAMOUNT());
			}
			writer.close();
			return true;
		} catch (IOException e) {
			return false;
		}
		
	}
	
	public static ArrayList<Entity> LoadGame(String fileName) {
		CheckSaveDirectory();
		ArrayList<Entity> Allies = new ArrayList<Entity>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(directoryURL+fileName));
			String line=reader.readLine();
			while((line=reader.readLine()) != null) {
				String[] parts = line.split("#");
				for(String s : parts) System.out.println(s);
				String name = parts[0];
				String[] Level = parts[1].split(";");
				String Gold = parts[2];
				String Class = parts[3];
				String[] Health = parts[4].split(";");
				String[] Magic = parts[5].split(";");
				String[] Stats = parts[6].split(";");
				String[] Skills = parts[7].split(";");
				// String[] Items = parts[8].split(";"); // Implement in the save too
				Entity Ally = new Entity(name, Integer.valueOf(Level[0]), Integer.valueOf(Health[1]),
						Integer.valueOf(Magic[1]), Integer.valueOf(Stats[0]), Integer.valueOf(Stats[1]),
						Integer.valueOf(Stats[2]), Integer.valueOf(Stats[3]));
				Ally.setXP(Integer.valueOf(Level[1]));
				Ally.setGOLD(Integer.valueOf(Gold));
				switch (Class) {
					case "WARRIOR":
						Ally.setCLASS(EntityClass.WARRIOR);
						break;
					case "MAGE":
						Ally.setCLASS(EntityClass.MAGE);
						break;
					case "CLERIC":
						Ally.setCLASS(EntityClass.CLERIC);
						break;
					case "ROGUE":
						Ally.setCLASS(EntityClass.ROGUE);
						break;				
				}
				Ally.setHP(Integer.valueOf(Health[0]));
				Ally.setMP(Integer.valueOf(Magic[0]));
				Allies.add(Ally);
 			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Allies;
	}
	
	private static void CheckSaveDirectory() {
		if(!directory.exists()) {
			directory.mkdir();
		}
	}
}
