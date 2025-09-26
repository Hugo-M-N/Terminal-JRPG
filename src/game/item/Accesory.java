package game.item;

import game.entity.Entity;
import game.skill.Skill;

public class Accesory extends Equipment implements Equippable{
	private Skill skill;

	public Accesory(String NAME, String DESC,int PRICE, int HP, int MP, int STR, int DEF, int MAG, int DEX, Skill skill) {
		super(NAME, DESC, PRICE, HP, MP, STR, DEF, MAG, DEX);
		this.skill=skill;
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

}
