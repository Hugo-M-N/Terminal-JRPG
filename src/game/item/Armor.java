package game.item;

import game.entity.Entity;

public class Armor extends Equipment implements Equippable{

	public Armor(String NAME, String DESC,int PRICE, int HP, int MP, int STR, int DEF, int MAG, int DEX) {
		super(NAME, DESC, PRICE, HP, MP, STR, DEF, MAG, DEX);
	}

	@Override
	public void Equip(Entity target) {target.setArmor(this);}

	@Override
	public void Unequip(Entity target) {target.setArmor(null);}

}
