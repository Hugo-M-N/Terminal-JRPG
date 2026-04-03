package game.item;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import game.entity.ClassManager;
import game.entity.EntityClass;
import game.skill.SkillManager;

public class ItemManager {
	private static HashMap<String, Accessory> Accesories = new HashMap<String, Accessory>();
	private static HashMap<String, Armor> Armors = new HashMap<String, Armor>();
	private static HashMap<String, Weapon> Weapons = new HashMap<String, Weapon>();
	private static HashMap<String, Potion> Potions = new HashMap<String, Potion>();
	
	public static void loadItemFiles() {
		File accesoryFile = new File("src/items/Accessories.csv");
		File armorsFile = new File("src/items/Armors.csv");
		File weaponsFile = new File("src/items/Weapons.csv");
		File potionsFile = new File("src/items/Potions.csv");
		
		try {
			// Accessory
			BufferedReader reader = new BufferedReader(new FileReader(accesoryFile));
			String line = reader.readLine();
			while((line=reader.readLine())!= null) {
				String[] parts = line.split(";");
				Accessory accessory = new Accessory(parts[0], parts[1], parts[2], Integer.valueOf(parts[3]), Integer.valueOf(parts[4]),
						Integer.valueOf(parts[5]), Integer.valueOf(parts[6]), Integer.valueOf(parts[7]), Integer.valueOf(parts[8]),
						Integer.valueOf(parts[9]), SkillManager.getSkill(parts[10]));
				Accesories.put(parts[0], accessory);
			}
			reader.close();
			
			// Armors
			reader = new BufferedReader(new FileReader(armorsFile));
			line = reader.readLine();
			while((line=reader.readLine()) != null) {
				String[] parts = line.split(";");
				Armor armor = new Armor(parts[0], parts[1], parts[2], Integer.valueOf(parts[3]), Integer.valueOf(parts[4]),
						Integer.valueOf(parts[5]), Integer.valueOf(parts[6]), Integer.valueOf(parts[7]),
						Integer.valueOf(parts[8]), Integer.valueOf(parts[9]));
				Armors.put(parts[0], armor);
			}
			reader.close();
			
			// Weapons
			reader = new BufferedReader(new FileReader(weaponsFile));
			line = reader.readLine();
			while((line=reader.readLine()) != null) {
				String[] parts = line.split(";");
				Weapon weapon = new Weapon(parts[0], parts[1], parts[2], Integer.valueOf(parts[3]), Integer.valueOf(parts[4]),
						Integer.valueOf(parts[5]), Integer.valueOf(parts[6]), Integer.valueOf(parts[7]),
						Integer.valueOf(parts[8]), Integer.valueOf(parts[9]), ClassManager.getClasses().get(parts[10]));
				Weapons.put(parts[0], weapon);
			}
			reader.close();
			
			//Potions
			reader = new BufferedReader(new FileReader(potionsFile));
			line = reader.readLine();
			while((line=reader.readLine()) != null) {
				String[] parts = line.split(";");
				Potion potion = new Potion(parts[0], parts[1], parts[2], Integer.valueOf(parts[3]), PotionType.valueOf(parts[4]));
				Potions.put(parts[0], potion);
			}
			reader.close();
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Accessory getAccessory(String AccessoryID) {
		return Accesories.get(AccessoryID);
	}
	
	public static Armor getArmor(String ArmorID) {
		return Armors.get(ArmorID);
	}
	
	public static Weapon getWeapon(String WeaponID) {
		return Weapons.get(WeaponID);
	}
	
	
	public static Potion getPotion(String PotionID) {
		return Potions.get(PotionID);
	}
}
