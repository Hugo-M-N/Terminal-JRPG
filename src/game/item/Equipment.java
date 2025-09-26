package game.item;

public class Equipment extends Item {
	
	private int bonusHP, bonusMP, bonusSTR, bonusDEF, bonusMAG, bonusDEX;

	public  int getBonusHP() {return bonusHP;}
	public  int getBonusMP() {return bonusMP;}
	public  int getBonusSTR() {return bonusSTR;}
	public  int getBonusDEF() {return bonusDEF;}
	public  int getBonusMAG() {return bonusMAG;}
	public  int getBonusDEX() {return bonusDEX;}
	
	// Constructor
	public Equipment(String NAME, String DESC,int PRICE,int HP, int MP, int STR, int DEF, int MAG, int DEX) {
		super(NAME, DESC, PRICE);
		bonusHP = HP;
		bonusMP = MP;
		bonusSTR = STR;
		bonusDEF = DEF;
		bonusMAG = MAG;
		bonusDEX = DEX;
	}
}