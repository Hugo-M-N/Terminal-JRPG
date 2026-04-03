package game.item;

import game.entity.Entity;
import game.skill.Skill;

public class Accessory extends Equipment implements Equippable{
	private Skill skill;

	public Accessory(String ID, String NAME, String DESC,int PRICE, int HP, int MP, int STR, int DEF, int MAG, int DEX, Skill skill) {
		super(ID, NAME, DESC, PRICE, HP, MP, STR, DEF, MAG, DEX);
		this.skill=skill;
	}

	@Override
	public Item copy() {
		Accessory a = new Accessory(ID, NAME, DESC, PRICE, getBonusHP(), getBonusMP(), getBonusSTR(), getBonusDEF(), getBonusMAG(), getBonusDEX(), skill);
		a.setAMOUNT(AMOUNT);
		return a;
	}

	@Override
	public void Equip(Entity target) {
		target.setAccesory(this);
		target.addSkill(skill);
		}

	@Override
	public void Unequip(Entity target) {
		target.setAccesory(null);
		target.removeSkill(skill);
		}
	
	public Skill getSkill() {
		return skill;
	}

}
