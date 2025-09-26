package game.item;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import game.entity.EntityClass;

public class ItemManager {
	private static HashMap<String, Weapon> Weapons = new HashMap<String, Weapon>();
	private static HashMap<String, Armor> Armors = new HashMap<String, Armor>();
	
	public static void loadItemFiles() {
		File weaponsFile = new File("src/items/Weapons.csv");
		File armorsFile = new File("src/items/Armors.csv");
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(weaponsFile));
			String line = reader.readLine();
			while((line=reader.readLine()) != null) {
				String[] parts = line.split(";");
				Weapon weapon = new Weapon(parts[1], parts[2], Integer.valueOf(parts[3]), Integer.valueOf(parts[4]),
						Integer.valueOf(parts[5]), Integer.valueOf(parts[6]), Integer.valueOf(parts[7]),
						Integer.valueOf(parts[8]), Integer.valueOf(parts[9]), EntityClass.valueOf(parts[10]));
				Weapons.put(parts[0], weapon);
			}
			reader.close();
			reader = new BufferedReader(new FileReader(armorsFile));
			line = reader.readLine();
			while((line=reader.readLine()) != null) {
				String[] parts = line.split(";");
				Armor armor = new Armor(parts[1], parts[2], Integer.valueOf(parts[3]), Integer.valueOf(parts[4]),
						Integer.valueOf(parts[5]), Integer.valueOf(parts[6]), Integer.valueOf(parts[7]),
						Integer.valueOf(parts[8]), Integer.valueOf(parts[9]));
				Armors.put(parts[0], armor);
			}
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Weapon getWeapon(String WeaponID) {
		return Weapons.get(WeaponID);
	}
	
	public static Armor getArmor(String ArmorID) {
		return Armors.get(ArmorID);
	}
}
