package game.item;

import game.entity.Entity;
import game.entity.EntityClass;

public class Weapon extends Equipment implements Equippable{
	private EntityClass exclusiveClass;
	
	public EntityClass getExclusiveClass() {return exclusiveClass;}

	public Weapon(String ID, String NAME, String DESC,int PRICE, int HP, int MP, int STR, int DEF, int MAG, int DEX, EntityClass Class) {
		super(ID, NAME, DESC, PRICE, HP, MP, STR, DEF, MAG, DEX);
		exclusiveClass = Class;
	}

	@Override
	public Item copy() {
		Weapon w = new Weapon(ID, NAME, DESC, PRICE, getBonusHP(), getBonusMP(), getBonusSTR(), getBonusDEF(), getBonusMAG(), getBonusDEX(), exclusiveClass);
		w.setAMOUNT(AMOUNT);
		return w;
	}

	@Override
	public void Equip(Entity target) {
		boolean noRestriction = exclusiveClass == null;
		boolean classMatches  = target.getCLASS() != null
				&& target.getCLASS().getID().equals(exclusiveClass != null ? exclusiveClass.getID() : null);
		if (noRestriction || classMatches) target.setWeapon(this);
	}
	
	@Override
	public void Unequip(Entity target) {target.setWeapon(null);}

}
