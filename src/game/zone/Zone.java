package game.zone;

import java.util.HashMap;

import game.map.Map;
import game.npc.Npc;

public class Zone {
	String id;
	String name;
	String[] encounters;
	String[] connections;
	HashMap<String, Npc> npcs;
	Map map;
	
	
	public String getName() {
		return name;
	}
	
	public String[] getFights() {
		return encounters;
	}
	
	public String[] getEncounters() {
		return encounters;
	}

	public String[] getConnections() {
		return connections;
	}

	public HashMap<String, Npc> getNpcs() {
		return npcs;
	}

	public Map getMap() {
		return map;
	}

	public Zone(String name, String[] encounters, String[] connections, HashMap<String,Npc> npcs, Map map) {
		this.name=name;
		this.encounters=encounters;
		this.connections=connections;
		this.npcs=npcs;
		this.map=map;
	}
}
