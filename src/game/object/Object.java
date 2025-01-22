package game.object;

import game.entity.Entity;

public class Object {
	
	// Variables
	String NAME;
	String DESC;
	int AMOUNT;
	
	// Functions
	public void Use(Entity target) {}
	
	public void Equip() {}
	
	public void add() {AMOUNT++;}
	
	public void add(int amount) {AMOUNT+= amount;}
	
	public void remove() {AMOUNT--;}
	
	public void remove(int amount) {AMOUNT-=amount;}
	
	// Setters & Getters
	public void setNAME(String NAME) {
		this.NAME = NAME;
	}
	
	public String getNAME() {
		return this.NAME;
	}
	
	public void setDESC(String DESC) {
		this.DESC = DESC;
	}
	
	public String getDESC() {
		return this.DESC;
	}
	
	public void setAMOUNT(int AMOUNT) {
		this.AMOUNT=AMOUNT;
	}
	
	public int getAMOUNT() {
		return this.AMOUNT;
	}
	
	
	// Constructors
	
	public Object(String NAME) {
		this.NAME = NAME;
		this.DESC = "";
		this.AMOUNT = 1;
	}
	
	public Object(String NAME, String DESC) {
		this.NAME = NAME;
		this.DESC = DESC;
		this.AMOUNT = 1;
	}	
	
}
