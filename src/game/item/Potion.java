package game.item;

import game.entity.Entity;

public class Potion extends Item implements Usable{
	PotionType TYPE;

	public PotionType getPotionType() {
		return TYPE;
	}
	
	public Potion(String ID, String NAME, String DESC, int PRICE, PotionType type) {
		super(ID, NAME, DESC, PRICE);
		TYPE=type;
	}
	
	@Override
	public Item copy() {
		Potion p = new Potion(ID, NAME, DESC, PRICE, TYPE);
		p.setAMOUNT(AMOUNT);
		return p;
	}

	public void Use(Entity target) {
		if(!(this.AMOUNT>0)) return;
		switch(TYPE) {
			case HEAL1:
				if(target.getHP()==target.getMAX_HP()) {
					System.out.println("Error 404 wound not found.");
					break;
				}
				target.setHP(target.getHP()+ (int)(target.getMAX_HP()*0.1)+1);
				if(target.getHP()>target.getMAX_HP()) target.setHP(target.getMAX_HP());
				this.remove();
				break;
			case HEAL2:
				if(target.getHP()==target.getMAX_HP()) {
					System.out.println("Error 404 wound not found.");
					break;
				}
				target.setHP(target.getHP()+ (int)(target.getMAX_HP()*0.2)+1);
				if(target.getHP()>target.getMAX_HP()) target.setHP(target.getMAX_HP());
				this.remove();
				break;
			case HEAL3:
				if(target.getHP()==target.getMAX_HP()) {
					System.out.println("Error 404 wound not found.");
					break;
				}
				target.setHP(target.getHP()+ (int)(target.getMAX_HP()*0.3)+1);
				if(target.getHP()>target.getMAX_HP()) target.setHP(target.getMAX_HP());
				this.remove();
				break;
			case ETHER1:
				if(target.getMP()==target.getMAX_MP()) {
					System.out.println("You shouldn't do that.");
					break;
				}
				target.setMP(target.getMP()+ (int)(target.getMAX_MP()*0.1)+1);
				if(target.getMP()>target.getMAX_MP()) target.setMP(target.getMAX_MP());
				this.remove();
				break;
			case ETHER2:
				if(target.getMP()==target.getMAX_MP()) {
					System.out.println("You shouldn't do that.");
					break;
				}
				target.setMP(target.getMP()+ (int)(target.getMAX_MP()*0.2)+1);
				if(target.getMP()>target.getMAX_MP()) target.setMP(target.getMAX_MP());
				this.remove();
				break;
			case ETHER3:
				if(target.getMP()==target.getMAX_MP()) {
					System.out.println("You shouldn't do that.");
					break;
				}
				target.setMP(target.getMP()+ (int)(target.getMAX_MP()*0.3)+1);
				if(target.getMP()>target.getMAX_MP()) target.setMP(target.getMAX_MP());
				this.remove();
				break;
			default:
				break;
		}
	}

}
