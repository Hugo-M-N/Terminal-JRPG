package game.combat;

import java.util.ArrayList;
import java.util.Scanner;

import game.entity.Entity;
import game.object.Object;
import game.skill.DamageType;
import game.skill.Skill;

public class Combat {	
	String[] options= {"Attack","Defend","Skills","Objects","Exit"};
	boolean combat = true;
	
	Scanner sc = new Scanner(System.in);
	public Combat(ArrayList<Entity> Allies, ArrayList<Entity> Enemies) {
		int ASel = 0, ESel=0;
		
		while(combat) {
			
			// Ally turn
			for(Entity Ally : Allies) {
				if(Ally.getT_COUNT()>=100 && Ally.getHP()>0) {
					Ally.setT_COUNT(Ally.getT_COUNT()-100);
					Ally.setIsDef(false);
					while(!(ASel>=1 && ASel<=options.length)) {
					Menu(options, Allies, Enemies, Ally);
						try {
							ASel = Integer.valueOf(sc.nextLine());							
						} catch (Exception e) {
							System.out.println("Invalid selection.");
						}
					}
					
					// Clear terminal
			        System.out.print("\033[H\033[2J");
			        System.out.flush();
			        
					switch(ASel) {
						case 1:
							Attack(Ally, Enemies, false);
							break;
						case 2:
							Defend(Ally);
							break;
						case 3:
							Skills(Ally, Allies, Enemies, false);
							break;
						case 4:
							Objects(Ally, Allies,Enemies, false);
							break;
					}
					if(options[ASel-1].equals("Exit")) return;
					ASel=0;
				}
			}
		
			// Enemy turn
			for(Entity Enemy : Enemies) {
				if(Enemy.getT_COUNT()>=100 && Enemy.getHP()>0){
					Enemy.setT_COUNT(Enemy.getT_COUNT()-100);
					Enemy.setIsDef(false);
					System.out.printf(Enemy.getNAME()+"'s turn\n");
					int EOptions=3;
					if(Enemy.getSkills().size()<1) EOptions--;
					if(Enemy.getInventory().size()<1) EOptions--;
					ESel = (int) (Math.random()*EOptions)+1;
					switch(ESel) {
						case 1:
							Attack(Enemy, Allies, true);
							break;
						case 2:
							Defend(Enemy);;
							break;
						case 3:
							Skills(Enemy, Enemies, Allies, true);;
							break;
						case 4:
							Objects(Enemy, Enemies, Allies, true);
							break;
					}
					
				}
			}
			
			// Increase T_COUNT
			for(Entity Ally : Allies) {
				Ally.setT_COUNT(Ally.getT_COUNT() + Ally.getDEX());
			}
			
			for(Entity Enemy : Enemies) {
				Enemy.setT_COUNT(Enemy.getT_COUNT() + Enemy.getDEX());
			}
			
			switch(checkCombat(Allies,Enemies)) {
			
				case 1:
					winCombat(Allies,Enemies);
					combat=false;
					break;
				case 2:
					System.out.println("You Lose.");
					System.out.println("You won`t gain any XP or gold.");
					combat=false;
					break;
				default:
					break;
			}
		}
	}
	
	// Functions
	
	private static void Menu(String[] options, ArrayList<Entity> Allies, ArrayList<Entity> Enemies, Entity turn) {
		System.out.println("___________________________________");
		for(Entity Enemy : Enemies) {
			System.out.printf("%s: \033[31m%d/%d-HP\033[0m | ", Enemy.getNAME(), Enemy.getHP(), Enemy.getMAX_HP());
		}
		System.out.println("\n");
		for(Entity Ally : Allies) {
			System.out.printf("%s: \033[31m%d/%d-HP\033[0m \033[34m%d/%d-MP\033[0m\n", Ally.getNAME(), Ally.getHP(), Ally.getMAX_HP(), Ally.getMP(), Ally.getMAX_MP());
		}
		
		// Turn
		System.out.println("\n"+ turn.getNAME()+"'s turn:");
		
		// Display options
		for(int i=0; i<options.length; i++) {
			System.out.printf(" %d-%s |", (i+1), options[i]);
		}
		System.out.print("\nSelection: ");
	}
	
	private static void DisplayEntities(ArrayList<Entity> Entities) {
		for(int i=0; i<Entities.size();i++) {
			System.out.printf("%d- %s \\033[31m%d/%d-HP\\033[0m\n",(i+1),Entities.get(i).getNAME(), Entities.get(i).getHP(), Entities.get(i).getMAX_HP());
		}
	}
	
	private static int checkCombat(ArrayList<Entity> Allies, ArrayList<Entity> Enemies) {
		int AlliesCount=0;
		int EnemiesCount=0;
		for(Entity Ally : Allies) {
			if(Ally.getHP()>0) AlliesCount++;
		}
		
		for(Entity Enemy : Enemies) {
			if(Enemy.getHP()>0) EnemiesCount++;
		}
		
		if(AlliesCount>EnemiesCount && EnemiesCount==0) {
			return 1;
		}
		
		if(EnemiesCount>AlliesCount && AlliesCount==0) {
			return 2;
		}
		
		return 0;
	}
	
	private static void winCombat(ArrayList<Entity> Allies, ArrayList<Entity> Enemies) {
		int EnemiesLvl=0;
		float XP;
		int gold=0;
		for(Entity Enemy : Enemies) {
			EnemiesLvl += Enemy.getLVL();
			gold += Enemy.getGOLD();
		}
		
		EnemiesLvl = (EnemiesLvl/Enemies.size());
		XP = (EnemiesLvl*0.3f+Enemies.size()*0.7f);
		
		System.out.println("You earn " + gold + " G.");
		System.out.println("Each Ally got "+ (int) Math.round(XP/Allies.size()) + " XP.");
		
		for(Entity Ally : Allies) {
			Ally.addXP(((int) Math.round(XP/Allies.size())));
		}
		
	}
	
	private void Attack(Entity Attaker, ArrayList<Entity> Enemies, boolean isEnemy) {
		Entity Enemy = null;
		if(!isEnemy){		
			Enemy = SelectTarget(Enemies);
		} else {
			if(Enemies.size()>1) {
				Enemy= Enemies.get((int) Math.random()*Enemies.size());
			} else {
				Enemy = Enemies.get(0);
			}
		}
		int dmg = (int)(Attaker.getSTR()*0.5 - (Enemy.getDEF()*0.2))+1;  // To do: add weapon damage and Equipment stats;
		if(Enemy.isDef()) dmg = dmg/2;
		
		Enemy.setHP(Enemy.getHP()-dmg);
		System.out.println(Enemy.getNAME()+" recieved "+ dmg+ " dmg.");	
	}
	
	private void Defend(Entity Defender) {
		Defender.setIsDef(true);
		System.out.printf("%s is defending.\n",Defender.getNAME());
	}
	
	private void Skills(Entity Caster,  ArrayList<Entity> Allies, ArrayList<Entity> Enemies, boolean isEnemy) {
		ArrayList<Skill> Skills = Caster.getSkills();
		if(!isEnemy) {
			System.out.println("Select a skill\n");
			int SSel=-1;
			while (!(SSel>-1 && SSel<Skills.size())) {
				for (int i=0; i<Skills.size(); i++) {
					System.out.printf("%d- %s %d-MP\n", (i+1), Skills.get(i).getNAME(), Skills.get(i).getCOST());
				}
				System.out.print("Selection: ");
				try {
					SSel = Integer.valueOf(sc.nextLine());
					SSel--;
				} catch(Exception e) {
					SSel=-1;
					System.out.println("Invalid selection.");
				}	
			}
			Skill skill = Skills.get(SSel);
			Entity Enemy = SelectTarget(Enemies);
			if(Caster.getMP()>=skill.getCOST()) {
				Caster.setMP(Caster.getMP()-skill.getCOST());
				System.out.printf("%s used %s on %s\n", Caster.getNAME(), skill.getNAME(), Enemy.getNAME());
				switch(skill.getDamageType()) {
				case DamageType.STR:
					skill.Use(Enemy, Caster.getSTR());
					break;
				case DamageType.MAG:
					skill.Use(Enemy, Caster.getMAG());
					break;
				case DamageType.DEX:
					skill.Use(Enemy, Caster.getDEX());
					break;				
				} 
			} else {
				System.out.println("You don't have enought MP.");
			}
		} else {
			// To-do: implement when enemy use a skill
		}
		
		
	}
	
	private void Objects(Entity Caster,ArrayList<Entity> Allies,ArrayList<Entity> Enemies,boolean isEnemy) {
		Entity Enemy;
		ArrayList<Object> objects;
		int OSel=-1;
		
		if(!isEnemy){
			objects = Allies.get(0).getInventory();
			if(objects.size()>=1) {
				System.out.println("_______________________");
				System.out.println("Inventory\n");
				System.out.printf("  %15s  %2s  %s\n","Name","nº", "Description");
				for(int i=0; i<objects.size();i++) {
					System.out.printf("%d- %15s  %2s  %s\n",(i+1),objects.get(i).getNAME(),objects.get(i).getAMOUNT(),objects.get(i).getDESC());
				}
				System.out.println("_______________________");
				while(!(OSel>=0 && OSel<objects.size())) {
					System.out.print("Selection: ");
					try {
						OSel = Integer.valueOf(sc.nextLine());
						OSel--;
					} catch(Exception e) {
						OSel=-1;
						System.out.println("Invalid selection.");
					}
				}
			} else {
				System.out.println("You don't have anything yet...");
				return;
			}
			
			Enemy = SelectTarget(Allies,Enemies);
			
			objects.get(OSel).Use(Enemy);
			System.out.println();
		} else {
			if(Enemies.size()>1) {
				Enemy = Enemies.get((int) Math.random()*Enemies.size());
			} else {
				Enemy = Enemies.get(0);
			}
		}
		
	}
	
	private Entity SelectTarget(ArrayList<Entity> Enemies) {
		Entity Enemy;
		if(Enemies.size()>1) {
			System.out.println("Targets:\n");
			DisplayEntities(Enemies);
			int sel=0;
			while(!(sel>=1 && sel<=Enemies.size())) {
				System.out.print("Select Enemy: ");
				try {
					sel = Integer.valueOf(sc.nextLine());								
				} catch (Exception e) {
					System.out.println("Invalid target.");
				}
				
			}
			Enemy = Enemies.get((sel-1));			
		} else {
			Enemy = Enemies.get(0);
		}
		
		return Enemy;
	}
	
	private Entity SelectTarget(ArrayList<Entity> Allies, ArrayList<Entity> Enemies) {
		Entity target;
		System.out.println("Targets:\n");
		int i;
		for(i=0;i<Allies.size();i++) {
			Entity Ally = Allies.get(i);
			System.out.printf("%d- %s %d/%d-HP  %d/%d-MP\n",(i+1),Ally.getNAME(),Ally.getHP(),Ally.getMAX_HP(), Ally.getMP(),Ally.getMAX_MP());
		}
		for(i--; (i-Allies.size()+1)<Enemies.size();i++) {
			System.out.printf("%d- %s\n", (i+Allies.size()+1), Enemies.get(i).getNAME());
		}
		int TSel=-1;
		while(!(TSel>=0 && TSel<(Allies.size()+Enemies.size()))) {
			System.out.print("Select target: ");
			try {
				TSel = Integer.valueOf(sc.nextLine());
				TSel--;
			} catch (Exception e) {
				TSel=-1;
				System.out.println("Invalid target.");
			}
		}
		
		if(TSel<Allies.size()) {
			target = Allies.get(TSel);
		} else {
			TSel = TSel-Allies.size();
			target = Enemies.get(TSel);
		}
		
		return target;
	}
	
}
