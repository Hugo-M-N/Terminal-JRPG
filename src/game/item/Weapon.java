package game.item;

import game.entity.Entity;
import game.entity.EntityClass;

public class Weapon extends Equipment implements Equippable{
	private EntityClass exclusiveClass;
	
	public EntityClass getExclusiveClass() {return exclusiveClass;}

	public Weapon(String NAME, String DESC,int PRICE, int HP, int MP, int STR, int DEF, int MAG, int DEX, EntityClass Class) {
		super(NAME, DESC, PRICE, HP, MP, STR, DEF, MAG, DEX);
		exclusiveClass = Class;
	}

	@Override
	public void Equip(Entity target) {if(target.getCLASS()==this.getExclusiveClass()) target.setWeapon(this);}
	
	@Override
	public void Unequip(Entity target) {target.setWeapon(null);}

}
