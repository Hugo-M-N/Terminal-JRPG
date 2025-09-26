package game.zone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class ZoneManager {
	static final String ZoneFolder = "src/zones/";
	static HashMap<String, String> Zones = new HashMap<String, String>();
	static Zone CurrentZone;
	
	public static void loadZones() {
		String[] zones = new File(ZoneFolder).list();
		for(String s : zones) {
			Zones.put(s.split("\\.")[0].toUpperCase(), s);
		}
	}
	
	public static Zone getCurrentZone() {
		return CurrentZone;
	}
	
	public static void setCurrentZone(String key) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(ZoneFolder + Zones.get(key)));
			String line;
			line = reader.readLine();
			String name = line.split(";")[1];
			line = reader.readLine();
			String[] fights = line.split(";");
			CurrentZone = new Zone(name, fights);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
