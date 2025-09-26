package game.item;

import game.entity.Entity;

public interface Equippable {
	public void Equip(Entity target);
	public void Unequip(Entity target);
}
