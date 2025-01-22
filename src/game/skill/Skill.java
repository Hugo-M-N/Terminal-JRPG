package game.skill;

import game.entity.Entity;

public class Skill {

	// Variables
	String NAME;
	int DMG;
	int COST;
	int DURATION;
	DamageType TYPE;
	
	// Functions
	public void Use(Entity target, int Stat) {
		// This is only for testing
		if(this.DURATION==0) {
			int dmg =(int) (this.DMG * Stat * 0.33);
			target.setHP(target.getHP()-dmg);
			System.out.printf("%s made %d dmg to %s\n",this.NAME, dmg, target.getNAME());
		} else {
			// To-do: Implement Skills with duration
		}
	}
	
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
	
	public void setDamageType(DamageType TYPE) {
		this.TYPE = TYPE;
	}
	
	public DamageType getDamageType() {
		return this.TYPE;
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
	
	public Skill(String NAME, int DMG, DamageType TYPE, int COST, int DURATION){
		this.NAME = NAME;
		this.DMG = DMG;
		this.TYPE = TYPE;
		this.COST = COST;
		this.DURATION = DURATION;
	}
	
	public Skill(String NAME, int DMG, DamageType TYPE, int COST){
		this.NAME = NAME;
		this.DMG = DMG;
		this.TYPE=TYPE;
		this.COST = COST;
		this.DURATION = 0;
	}
}