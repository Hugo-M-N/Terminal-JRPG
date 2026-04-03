package game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import game.entity.ClassManager;
import game.entity.Entity;
import game.item.Accessory;
import game.item.Armor;
import game.item.ItemManager;
import game.item.Potion;
import game.item.Weapon;
import game.skill.Skill;
import game.skill.SkillManager;
import game.zone.ZoneManager;

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
				writer.write("#" + Ally.getCLASS().getID());
				writer.write("#" + Ally.getHP() + ";" + Ally.getMAX_HP());
				writer.write("#" + Ally.getMP() + ";" + Ally.getMAX_MP());
				writer.write("#" + Ally.getSTR() + ";" + Ally.getMAG() + ";" + Ally.getDEF() + ";" + Ally.getDEX());
				writer.write("#");
				if (Ally.getSkills().size()>0) {
					for(int i=0; i<Ally.getSkills().size(); i++) {
						Skill tmp = Ally.getSkills().get(i);
						writer.write(tmp.getID());
						if(i<Ally.getSkills().size()-1) writer.write(";");
					}
				} else writer.write("o");
				writer.write("#");
				if(Ally.getInventory().size()>0) {
					for(int i=0;i<Ally.getInventory().size();i++) {
						switch(Ally.getInventory().get(i)) {
							case Accessory a -> writer.write("Accesory-"+a.getID()+"-"+a.getAMOUNT());
							case Armor a -> writer.write("Armor-"+a.getID()+"-"+a.getAMOUNT());
							case Weapon w -> writer.write("Weapon-"+w.getID()+"-"+w.getAMOUNT());
							case Potion p -> writer.write("Potion-"+p.getID()+"-"+p.getAMOUNT());
							default -> {}
						}
						if(i<Ally.getInventory().size()-1) writer.write(";");
					}
				} else writer.write("o");
				writer.write("#" + Ally.getSpriteIdx());
			writer.write("#" + (Ally.getWeapon()   != null ? Ally.getWeapon().getID()   : "o"));
			writer.write(";" + (Ally.getArmor()    != null ? Ally.getArmor().getID()    : "o"));
			writer.write(";" + (Ally.getAccesory() != null ? Ally.getAccesory().getID() : "o"));
			}
			writer.write("\nEVENTS#");
			for (Event b : Event.values()) {
			    writer.write((b.getStatus() ? b.name()+"=true" : b.name()+"=false") + ";");
			}

			writer.write("\nCONFIG#TEXT_SPEED=" + Config.getTEXT_SPEED());
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
			ZoneManager.setCurrentZone(reader.readLine().toUpperCase());
			TextGame.setCurrentZone(ZoneManager.getCurrentZone());
			while((line=reader.readLine()) != null) {
				if (line.startsWith("EVENTS#") || line.startsWith("CONFIG#")) break;
				String[] parts = line.split("#");
				if (parts.length < 9) continue;
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
				Ally.setCLASS(ClassManager.getClasses().get(Class));
				Ally.setHP(Integer.valueOf(Health[0]));
				Ally.setMP(Integer.valueOf(Magic[0]));

				if(!(Skills[0].equalsIgnoreCase("o"))) {
					for(String s : Skills) {
						Ally.addSkill(SkillManager.getSkill(s));
					}
				}

				if(!(Items[0].equalsIgnoreCase("o"))) {
					for(String s : Items) {
						if (s == null || s.trim().isEmpty()) continue;
						String[] item = s.split("-");
						switch(item[0]) {
							case "Accesory" -> {
								Accessory a = ItemManager.getAccessory(item[1]);
								a.setAMOUNT(Integer.valueOf(item[2]));
								Ally.addToInventory(a);
							}
							case "Armor" -> {
								Armor a = ItemManager.getArmor(item[1]);
								a.setAMOUNT(Integer.valueOf(item[2]));
								Ally.addToInventory(a);
							}
							case "Weapon" -> {
								Weapon w = ItemManager.getWeapon(item[1]);
								w.setAMOUNT(Integer.valueOf(item[2]));
								Ally.addToInventory(w);
							}
							case "Potion" -> {
								Potion p = ItemManager.getPotion(item[1]);
								p.setAMOUNT(Integer.valueOf(item[2]));
								Ally.addToInventory(p);
							}
							default -> {}
						}
					}
				}

				if (parts.length >= 10) {
					try { Ally.setSpriteIdx(Integer.parseInt(parts[9])); }
					catch (NumberFormatException ignored) {}
				}

				if (parts.length >= 11) {
					String[] equip = parts[10].split(";");
					String weaponId    = equip.length > 0 ? equip[0] : "o";
					String armorId     = equip.length > 1 ? equip[1] : "o";
					String accessoryId = equip.length > 2 ? equip[2] : "o";
					for (game.item.Item item : Ally.getInventory()) {
						if (!weaponId.equals("o")    && item instanceof Weapon    w && w.getID().equals(weaponId))    Ally.setWeapon(w);
						if (!armorId.equals("o")     && item instanceof Armor     a && a.getID().equals(armorId))     Ally.setArmor(a);
						if (!accessoryId.equals("o") && item instanceof Accessory c && c.getID().equals(accessoryId)) Ally.setAccesory(c);
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
			                Config.setTEXT_SPEED(Integer.parseInt(parts[1]));
			            } catch (NumberFormatException e) {
			                Config.setTEXT_SPEED(40);
			            }
			        }
			    }
			}
			reader.close();
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

	public static String[] getFileNames() {
		return directory.list();
	}
}
