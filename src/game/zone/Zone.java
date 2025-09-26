package game.zone;

public class Zone {
	String Name;
	String[] Fights;
	
	
	public String getName() {
		return Name;
	}
	
	public String[] getFights() {
		return Fights;
	}
	
	public Zone(String name, String[] fights) {
		Name=name;
		Fights=fights;
	}
}
