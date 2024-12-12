package game.skill;

public class Skill {

	// Variables
	String NAME;
	int DMG;
	int COST;
	int DURATION;
	
	// Setters & Getters
	
	public void setNAME(String NAME) {
		this.NAME = NAME;
	}
	
	public String getNAME() {
		return this.NAME;
	}
	
	public void setDMG(int DMG) {
		this.DMG = DMG;
	}
	
	public int getDMG() {
		return this.DMG;
	}
	
	public void setCOST(int COST) {
		this.COST = COST;
	}
	
	public int getCOST() {
		return this.COST;
	}
	
	public void setDURATION(int DURATION) {
		this.DURATION = DURATION;
	}
	
	public int getDURATION() {
		return this.DURATION;
	}
	
	// Constructors
	
	public Skill(String NAME, int DMG, int COST, int DURATION){
		this.NAME = NAME;
		this.DMG = DMG;
		this.COST = COST;
		this.DURATION = DURATION;
	}
	
	public Skill(String NAME, int DMG, int COST){
		this.NAME = NAME;
		this.DMG = DMG;
		this.COST = COST;
		this.DURATION = 0;
	}
}