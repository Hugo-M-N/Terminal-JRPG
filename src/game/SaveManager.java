package game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import game.entity.Entity;
import game.entity.EntityClass;
import game.object.Object;
import game.object.Potion;
import game.object.PotionType;
import game.skill.DamageType;
import game.skill.Skill;

public class SaveManager {
	static String directoryURL= System.getProperty("user.home")+ "/Terminal_JRPG/saves/";
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
						writer.write(skill.getNAME() + "-" + skill.getDMG() + "-" + skill.getCOST() + "-" + skill.getDURATION() + "-" + skill.getDamageType() + ";");
					}					
				} else if (Ally.getSkills().size()==1) writer.write(Ally.getSkills().get(0).getNAME() + "-" + Ally.getSkills().get(0).getDMG() + "-" + Ally.getSkills().get(0).getCOST() + "-" + Ally.getSkills().get(0).getDURATION() + "-" + Ally.getSkills().get(0).getDamageType());
				else if(Ally.getSkills().size()==0) writer.write("º");
				writer.write("#");
				if(Ally.getInventory().size()>1) {
					for(int i=0;i<Ally.getInventory().size();i++) {
						if(Ally.getInventory().get(i) instanceof Potion) {
							Potion p = (Potion) Ally.getInventory().get(i);
							writer.write("Potion-"+p.getNAME()+"-"+p.getDESC()+"-"+p.getAMOUNT()+"-"+p.getPotionType()+";");
						}
					}
				} else if(Ally.getInventory().size()==1) writer.write(Ally.getInventory().get(0).getNAME() + "-" + Ally.getInventory().get(0).getDESC() + "-" + Ally.getInventory().get(0).getAMOUNT());
				else if(Ally.getInventory().size()==0) writer.write("º");
			}
			writer.write("\nEVENTS#");
			for (boolean b : Game.SpecialEvents) {
			    writer.write((b ? "1" : "0") + ";");
			}

			writer.write("\nCONFIG#TEXT_SPEED=" + Config.getTextSpeed());
			writer.close();
			return true;
		} catch (IOException e) {
			System.err.println(e);
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
				String name = parts[0];
				String[] Level = parts[1].split(";");
				String Gold = parts[2];
				String Class = parts[3];
				String[] Health = parts[4].split(";");
				String[] Magic = parts[5].split(";");
				String[] Stats = parts[6].split(";");
				String[] Skills = parts[7].split(";");
				String[] Items = parts[8].split(";");
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
				
				if(!(Skills[0].equalsIgnoreCase("º"))) {					
					for(String s : Skills) {
						String[] skill = s.split("-");
						if(skill.length>0) {
							Ally.addSkill(new Skill(skill[0], Integer.valueOf(skill[1]), DamageType.valueOf(skill[4]), Integer.valueOf(skill[2])));
						}
					}
				}
				
				if(!(Items[0].equalsIgnoreCase("º"))) {
				for(String s : Items) {
					String[] item = s.split("-");
						switch(item[0]) {
							case "Potion":
								Potion p = new Potion(item[1], item[2], PotionType.valueOf(item[4]));
								p.setAMOUNT(Integer.valueOf(item[3]));
								Ally.addToInventory(p);
								break;
						}
					}
				}
				
				Allies.add(Ally);
 			}
			reader.close();
			reader = new BufferedReader(new FileReader(directoryURL+fileName));
			String l;
			while ((l = reader.readLine()) != null) {
			    if (l.startsWith("EVENTS#")) {
			        String[] flags = l.substring(7).split(";");
			        Game.SpecialEvents.clear();
			        for (String flag : flags) {
			            if (!flag.isBlank()) Game.SpecialEvents.add(flag.equals("1"));
			        }
			    } else if (l.startsWith("CONFIG#")) {
			        String[] parts = l.substring(7).split("=");
			        if (parts[0].equals("TEXT_SPEED")) {
			            try {
			                Config.setTextSpeed(Integer.parseInt(parts[1]));
			            } catch (NumberFormatException e) {
			                Config.setTextSpeed(40);
			            }
			        }
			    }
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Allies;
	}
	
	public static void CheckSaveDirectory() {
		if(!directory.exists()) {
			directory.mkdir();
		}
	}
}
