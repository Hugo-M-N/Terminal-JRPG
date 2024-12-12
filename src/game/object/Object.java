package game.object;

public class Object {
	
	// Variables
	String NAME;
	String DESC;
	int AMOUNT;
	
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
