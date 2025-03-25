package game.object;

import game.entity.Entity;

public class Potion extends Object{
	PotionType TYPE;

	public PotionType getPotionType() {
		return TYPE;
	}
	
	public Potion(String NAME, String DESC, PotionType type) {
		super(NAME, DESC);
		TYPE=type;
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
