package game.skill;

import game.entity.Entity;

public class Skill {

	// Variables
	String ID;
	String NAME;
	String EFFECT;
	int STR;
	int COST;
	int DURATION;
	DamageType TYPE;
	String DESCRIPTION;
	
	// Functions
	public void Use(Entity target, int Stat) {
		// This is only for testing
		switch(EFFECT) {
			case "DAMAGE":
				if(this.DURATION==0) {
					int dmg =(int) (this.STR * Stat * 0.33);
					target.setHP(target.getHP()-dmg);
					if (target.getHP()<0) target.setHP(0);
					System.out.printf("%s made %d dmg to %s\n",this.NAME, dmg, target.getNAME());
				} else {
					// To-do: Implement Skills with duration
				}
				break;
			case "HEAL":
				if(this.DURATION==0) {
					int hp =(int) (this.STR * Stat * 0.4);
					target.setHP(target.getHP()+hp);
					if (target.getHP()>target.getMAX_HP()) target.setHP(target.getMAX_HP());
					System.out.printf("%s gave %d hp to %s\n",this.NAME, hp, target.getNAME());

				}
				break;
		}
		
	}
	
	// Setters & Getters
	
	public void setID(String ID) {
		this.ID = ID;
	}
	
	public String getID() {
		return this.ID;
	}
	
	public void setNAME(String NAME) {
		this.NAME = NAME;
	}
	
	public String getNAME() {
		return this.NAME;
	}
	
	public void setEFFECT(String EFFECT) {
		this.EFFECT = EFFECT;
	}
	
	public String getEFFECT() {
		return this.EFFECT;
	}
	
	public void setSTR(int STR) {
		this.STR = STR;
	}
	
	public int getSTR() {
		return this.STR;
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
	
	public void setDESCRIPTION(String DESCRIPTION) {
		this.DESCRIPTION = DESCRIPTION;
	}
	
	public String getDESCRIPTION() {
		return this.DESCRIPTION;
	}
	
	// Constructors
	
	public Skill(String ID, String NAME, String EFFECT, int STR, int COST, int DURATION, DamageType TYPE, String DESCRIPTION){
		this.ID = ID;
		this.NAME = NAME;
		this.EFFECT = EFFECT;
		this.STR = STR;
		this.COST = COST;
		this.DURATION = DURATION;
		this.TYPE = TYPE;
		this.DESCRIPTION = DESCRIPTION;
	}
	
}