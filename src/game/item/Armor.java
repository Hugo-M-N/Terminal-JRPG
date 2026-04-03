package game.item;

import game.entity.Entity;

public class Armor extends Equipment implements Equippable{

	public Armor(String ID, String NAME, String DESC,int PRICE, int HP, int MP, int STR, int DEF, int MAG, int DEX) {
		super(ID, NAME, DESC, PRICE, HP, MP, STR, DEF, MAG, DEX);
	}

	@Override
	public Item copy() {
		Armor a = new Armor(ID, NAME, DESC, PRICE, getBonusHP(), getBonusMP(), getBonusSTR(), getBonusDEF(), getBonusMAG(), getBonusDEX());
		a.setAMOUNT(AMOUNT);
		return a;
	}

	@Override
	public void Equip(Entity target) {target.setArmor(this);}

	@Override
	public void Unequip(Entity target) {target.setArmor(null);}

}
