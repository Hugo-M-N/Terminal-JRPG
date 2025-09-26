package game.item;

import java.util.ArrayList;

import game.entity.Entity;

public abstract class Item {
	
	// Variables
	String NAME;
	String DESC;
	int AMOUNT;
	int PRICE;
	
	// Functions
	public void Use(Entity target) {}
	
	public void Equip() {}
	
	public void add() {AMOUNT++;}
	
	public void add(int amount) {AMOUNT+= amount;}
	
	public void remove() {AMOUNT--;}
	
	public void remove(int amount) {AMOUNT-=amount;}
	
	// Setters & Getters
	public void setNAME(String NAME) {this.NAME = NAME;}
	public String getNAME() {return this.NAME;}
	
	public void setDESC(String DESC) {this.DESC = DESC;}
	public String getDESC() {return this.DESC;}
	
	public void setAMOUNT(int AMOUNT) {this.AMOUNT=AMOUNT;}
	public int getAMOUNT() {return this.AMOUNT;}
	
	public void setPRICE(int PRICE) {this.PRICE=PRICE;}
	public int getPRICE() {return this.PRICE;}
	
	// Constructor
	public Item(String NAME, String DESC, int PRICE) {
		this.NAME = NAME;
		this.DESC = DESC;
		this.PRICE = PRICE;
		this.AMOUNT = 1;
	}

	public static ArrayList<String> getItemOptions(Item item) {
	    ArrayList<String> options = new ArrayList<>();

	    if (item instanceof Usable) {
	    	options.add("Use");
	    }
	    
	    if (item instanceof Equippable) {
	        options.add("Equip");
	        options.add("Unequip");
	    }

	    // Siempre se puede descartar
	    options.add("Discard");

	    return options;
	}

	
}
