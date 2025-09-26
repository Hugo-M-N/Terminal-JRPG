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
import game.item.Item;
import game.item.Potion;
import game.item.PotionType;
import game.skill.DamageType;
import game.skill.Skill;
import game.zone.ZoneManager;
import game.zone.ZoneZZ;

public class SaveManager {
	static String directoryURL= System.getProperty("user.home")+ "/Terminal_JRPG/saves/";
	static File directory = new File(directoryURL);
	
	
	public static boolean SaveGame(ArrayList<Entity> Allies, String fileName) {
		CheckSaveDirectory();
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(directoryURL+fileName+".save"));
			writer.write(fileName);
			writer.write("\n"+ZoneManager.getCurrentZone().getName());
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
						writer.write(skill.getNAME() + "-" + skill.getEFFECT() + "-"+ skill.getSTR() + "-" + skill.getCOST() + "-" + skill.getDURATION() + "-" + skill.getDamageType() + "-"+ skill.getDESCRIPTION() + ";");
					}					
				} else if (Ally.getSkills().size()==1) writer.write(Ally.getSkills().get(0).getNAME() + "-" + "-" + Ally.getEFFECT()  + Ally.getSkills().get(0).getSTR() + "-" + Ally.getSkills().get(0).getCOST() + "-" + Ally.getSkills().get(0).getDURATION() + "-" + Ally.getSkills().get(0).getDamageType() + "-" + Ally.getSkills().get(0).getDESCRIPTION());
				else if(Ally.getSkills().size()==0) writer.write("º");
				writer.write("#");
				if(Ally.getInventory().size()>=1) {
					for(int i=0;i<Ally.getInventory().size();i++) {
						if(Ally.getInventory().get(i) instanceof Potion) {
							Potion p = (Potion) Ally.getInventory().get(i);
							writer.write("Potion-"+p.getNAME()+"-"+p.getDESC()+"-"+p.getPRICE()+"-"+p.getAMOUNT()+"-"+p.getPotionType()+";");
						}
					}
				} else if(Ally.getInventory().size()==0) writer.write("º");
			}
			writer.write("\nEVENTS#");
			for (Event b : Event.values()) {
			    writer.write((b.getStatus() ? b.name()+"=true" : b.name()+"=false") + ";");
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
			ZoneManager.setCurrentZone(reader.readLine());
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
							Ally.addSkill(new Skill(skill[0], skill[1], Integer.valueOf(skill[2]), Integer.valueOf(skill[3]), Integer.valueOf(skill[4]), DamageType.valueOf(skill[5]), skill[6]));
						}
					}
				}
				
				if(!(Items[0].equalsIgnoreCase("º"))) {
				for(String s : Items) {
					String[] item = s.split("-");
						switch(item[0]) {
							case "Potion":
								Potion p = new Potion(item[1], item[2], Integer.valueOf(item[3]), PotionType.valueOf(item[5]));
								p.setAMOUNT(Integer.valueOf(item[4]));
								Ally.addToInventory(p);
								break;
						}
					}
				}
				
				Allies.add(Ally);
 			}
			reader.close();
			reader = new BufferedReader(new FileReader(directoryURL+fileName));
			while ((line = reader.readLine()) != null) {
			    if (line.startsWith("EVENTS#")) {
			        String[] parts = line.substring(7).split(";");
			        for (String s : parts) {
			           Event e = Event.valueOf(s.split("=")[0]);
			           e.setStatus(Boolean.valueOf(s.split("=")[1]));
			        }
			    } else if (line.startsWith("CONFIG#")) {
			        String[] parts = line.substring(7).split("=");
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
