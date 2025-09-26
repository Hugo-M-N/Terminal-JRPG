package game.zone;

import java.util.ArrayList;

import game.Event;
import game.ScreenBuffer;
import game.combat.Combat;
import game.entity.EnemyManager;
import game.entity.Entity;
import game.entity.EntityClass;
import game.item.ItemManager;
import game.item.Potion;
import game.item.PotionType;

public enum ZoneZZ {	
	START("???"){

		@Override
		public String[] Enter(ArrayList<Entity> Allies, ScreenBuffer BUFFER) {
			if(Event.First_Explore.getStatus()) {
				try {
					BUFFER.printAnimatedMessage("");
					BUFFER.printAnimatedMessage("You look around, you don't recognize anything. Actually, you don't know "
					+ "anything, you \"understand\" basic concepts like yourself, your name and how some things are called, "
					+ "but you can't remember more than a few minutes ago when you opened your eyes. So you don't know from "
					+ "where that knoledge came from. You start to walk and two things caught your eye, a forest and a small "
					+ "village.");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Event.First_Explore.setStatus(false);
			}
			return null;
			
		}

		@Override
		public String[] getConns() {
			return new String[] {"Go back", "Forest", "Village"};
		}
		
	},
	FOREST("Forest"){

		@Override
		public String[] Enter(ArrayList<Entity> Allies, ScreenBuffer BUFFER) throws InterruptedException {
			ArrayList<Entity> Enemies = new ArrayList<Entity>();

			if(Event.King_Goblin.getStatus() && Allies.get(0).getLVL()>=7){
				Enemies.add(new Entity("King Goblin", EntityClass.WARRIOR, 10));
				Combat comb = new Combat(Allies, Enemies, BUFFER);	
				return comb.getResult();
			} else if(Event.First_Forest.getStatus()) {
				BUFFER.printAnimatedMessage("");
				BUFFER.printAnimatedMessage("You walk into the forest, after some time you realize the forest is quiet, too quiet. "
						+ "You don't hear anything, this shouldn't be like this, right? You keep walking and some trees with "
						+ "marks start to appear, suddenly you hear a high pitched chirp and something jumps towards you.");
				
				Enemies.add(EnemyManager.getEnemy("GOBLIN"));
				Enemies.get(0).setGOLD(10);
				Enemies.get(0).addToInventory(new Potion("Heal", "Testing healing", 20, PotionType.HEAL3));
				Enemies.get(0).addToInventory(new Potion("Magic", "Testing MP", 20, PotionType.ETHER1));
				Combat comb = new Combat(Allies, Enemies, BUFFER);
				return comb.getResult();
				
			} else {
				Enemies.add(EnemyManager.getEnemy("GOBLIN"));
				Enemies.get(0).setGOLD(5+(int)(Math.random()*5));
				//Item test
				int amount = (int) (Math.random()*3);
				Potion p = new Potion("Heal", "Testing healing", 20, PotionType.HEAL3);
				if(amount!=0) {
					p.setAMOUNT(amount);
					Enemies.get(0).addToInventory(p);
				}
				amount = (int) (Math.random()*3);
				p = new Potion("Magic", "Testing MP", 20, PotionType.ETHER1);
				if(amount!=0) {
					p.setAMOUNT(amount);
					Enemies.get(0).addToInventory(p);
				}
				
				Combat comb = new Combat(Allies, Enemies, BUFFER);
				return comb.getResult();				
			}
			
		}

		@Override
		public String[] getConns() {
			return new String[] {"Go back", "Forest", "Village"};
		}


	},
	VILLAGE("Village"){

		@Override
		public String[] Enter(ArrayList<Entity> Allies, ScreenBuffer BUFFER) throws InterruptedException {
			if(Event.First_Village.getStatus()) {
				BUFFER.printAnimatedMessage("");
				BUFFER.printAnimatedMessage("It's a small village, you quickly see a house with a smoke trail.");
				BUFFER.printAnimatedMessage("As you get closer you realize most houses looks like haven't been anybody in here in a lot of time.");
				BUFFER.printAnimatedMessage("You get in front of the building, there's a sign with a bottle on it aside the door.");
				Event.First_Village.setStatus(false);
			} else {
				switch(Allies.get(0).getCLASS()) {
					case WARRIOR:
						Allies.get(0).setWeapon(ItemManager.getWeapon("RUSTY_SWORD"));
						break;
					case MAGE:
						Allies.get(0).setWeapon(ItemManager.getWeapon("OLD_SPELLBOOK"));
						break;
					case CLERIC:
						Allies.get(0).setWeapon(ItemManager.getWeapon("WOODEN_STAFF"));
						break;
					case ROGUE:
						Allies.get(0).setWeapon(ItemManager.getWeapon("BLUNT_DAGGER"));
						break;
					default:
						break;
				}
			}
			
			return null;
			
		}

		@Override
		public String[] getConns() {
			return new String[] {"Go back", "Forest", "Village"};
		}
		
	};
	
	private String Name;
	
	ZoneZZ(String name){
		this.Name = name;
	}
	
	public abstract String[] Enter(ArrayList<Entity> Allies, ScreenBuffer BUFFER) throws InterruptedException;
	
	public abstract String[] getConns();

	public String getName() {
		return Name;
	}

}
