package game.zone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import game.map.Map;
import game.npc.Npc;
import game.npc.NpcManager;

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

	public static Zone getZone(String key) {
		setCurrentZone(key.toUpperCase());
		return CurrentZone;
	}
	
	public static void setCurrentZone(String key) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(ZoneFolder + Zones.get(key)));
			String line;
			line = reader.readLine();
			String name = line.split(";")[1];
			line = reader.readLine();
			String[] encounters = line.split(";");
			line = reader.readLine();
			String[] connections = line.split(";");
			line = reader.readLine();
			String[] npcs = line.split(";");
			HashMap<String, Npc> npcList = new HashMap<String, Npc>();
			for(String s : npcs) npcList.put(s, NpcManager.getNpc(s));
			line = reader.readLine();
			Map map = new Map(line.split(";")[1]);
			CurrentZone = new Zone(name, encounters, connections, npcList, map);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
