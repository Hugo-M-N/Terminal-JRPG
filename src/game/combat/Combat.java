package game.combat;

import java.util.ArrayList;
import java.util.Scanner;

import game.Menus;
import game.ScreenBuffer;
import game.entity.Entity;
import game.item.Item;
import game.skill.DamageType;
import game.skill.Skill;
import game.utils.InputHelper;

public class Combat {	
	String[] options= {"Attack","Defend","Skills","Objects","Exit"};
	boolean combat = true;
	String[] Result = null;
	static ScreenBuffer CBUFFER;
	
	public String[] getResult() {
		return Result;
	}
	
	Scanner sc = new Scanner(System.in);
	public Combat(ArrayList<Entity> Allies, ArrayList<Entity> Enemies, ScreenBuffer BUFFER) throws InterruptedException {
		int ESel=0;
		String[] result = null;
		CBUFFER = BUFFER;
		
		while(combat) {
			CBUFFER.clearBuffer();
			for(String s : Menus.combatMenu(Allies, Enemies)) CBUFFER.updateBuffer(s);
			
			//Print Screen
			InputHelper.clearScreen();
			for(String s : CBUFFER.getScreenBuffer()) System.out.println(s);
			
			// Ally turn
			for(Entity Ally : Allies) {
				if(Ally.getT_COUNT()>=100 && Ally.getHP()>0) {
					Ally.setIsDef(false);
			        
					switch(Menus.Menu(options)) {
						case "Attack":
							Attack(Ally, Enemies, false);
							break;
						case "Defend":
							Defend(Ally);
							break;
						case "Skills":
							Skills(Ally, Allies, Enemies, false);
							break;
						case "Objects":
							Objects(Ally, Allies,Enemies, false);
							break;
						case "Exit":
							Result = new String[] {"You ran away."};
							for(Entity e : Allies) e.setT_COUNT(0);
							return;
					}
					
					Ally.setT_COUNT(0);
				}

			}
			
			// Enemy turn
			for(Entity Enemy : Enemies) {
				if(Enemy.getT_COUNT()>=100 && Enemy.getHP()>0){
					Enemy.setIsDef(false);
					CBUFFER.addToBuffer(Enemy.getNAME()+"'s turn");
					InputHelper.clearScreen();
					for(String s : CBUFFER.getScreenBuffer()) System.out.println(s);
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
					
					Enemy.setT_COUNT(0);
				}
			}
			
			// Increase T_COUNT
			for(Entity Ally : Allies) {
				if(Ally.getHP()>0) Ally.setT_COUNT(Ally.getT_COUNT() + 3 + (Ally.getDEX()/2));
				if(Ally.getT_COUNT()>100) Ally.setT_COUNT(100);
			}
			
			for(Entity Enemy : Enemies) {
				if(Enemy.getHP()>0) Enemy.setT_COUNT(Enemy.getT_COUNT() + 3 + (Enemy.getDEX()/2));
				if(Enemy.getT_COUNT()>100) Enemy.setT_COUNT(100);
			}
			
			switch(checkCombat(Allies,Enemies)) {
			
				case 1:
					combat=false;
					result = winCombat(Allies,Enemies);
					for(Entity e : Allies) e.setT_COUNT(0);
					break;
				case 2:
					combat=false;
					result = new String[] {"You Lose.", "You won`t gain any XP or gold."};
					for(Entity e : Allies) e.setT_COUNT(0);
					break;
				default:
					break;
			}
			Thread.sleep(100);
		}
		Thread.sleep(500);
		Result = result;
	}
	
	// Functions
		
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
	
	private static String[] winCombat(ArrayList<Entity> Allies, ArrayList<Entity> Enemies) {
		int EnemiesLvl=0;
		float XP;
		int gold=0;
		for(Entity Enemy : Enemies) {
			EnemiesLvl += Enemy.getLVL();
			gold += Enemy.getGOLD();
		}
		
		EnemiesLvl = (EnemiesLvl/Enemies.size());
		XP = (float)(Math.random()*2.5*EnemiesLvl*0.3+Enemies.size()*0.7);
		
		Allies.get(0).setGOLD(Allies.get(0).getGOLD() + gold);
		String g = "You earn " + gold + " G.";
		String xp = "Each Ally got "+ (int) Math.round(XP/Allies.size()) + " XP.";
		ArrayList<String> result = new ArrayList<String>();
		result.add(g);
		result.add(xp);
		
		for(Entity Ally : Allies) {
			String[] info = Ally.addXP(((int) Math.round(XP/Allies.size())));
			for(String s : info) result.add(s);
		}
		// Item test
		for(Entity Enemy : Enemies) {
			if(Enemy.getInventory().size()>=1) {
				for(Item item : Enemy.getInventory()) {
					result.add(Allies.get(0).addToInventory(item));
				}				
			}
		}
		
		return result.toArray(new String[0]);
		
	}
	
	private void Attack(Entity Attaker, ArrayList<Entity> Enemies, boolean isEnemy) {
		Entity Enemy = null;
		if(!isEnemy){		
			Enemy = Menus.SelectTarget(Enemies);
		} else {
			if(Enemies.size()>1) {
				Enemy= Enemies.get((int) (Math.random()*Enemies.size()));
			} else {
				Enemy = Enemies.get(0);
			}
		}
		
		
		int dmg = (int)(Attaker.getEffectiveSTR()*0.75 - (Enemy.getEffectiveDEF()*0.3))+2;
		if(Enemy.isDef()) dmg = dmg/2;
		
		Enemy.setHP(Math.max(0, (Enemy.getHP()-dmg)));
		CBUFFER.addToBuffer(Enemy.getNAME()+" recieved "+ dmg+ " dmg.");	
		InputHelper.clearScreen();
		for(String s : CBUFFER.getScreenBuffer()) System.out.println(s);
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void Defend(Entity Defender) {
		Defender.setIsDef(true);
		CBUFFER.addToBuffer(String.format("%s is defending.",Defender.getNAME()));
		InputHelper.clearScreen();
		for(String s : CBUFFER.getScreenBuffer()) System.out.println(s);
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void Skills(Entity Caster,  ArrayList<Entity> Allies, ArrayList<Entity> Enemies, boolean isEnemy) {		
		if(!isEnemy) {
			Skill selected = Menus.SkillMenu(Caster.getSkills());
			if(selected!=null && Caster.getMP()>=selected.getCOST()){
				Entity target = (selected.getSTR()>0) ? Menus.SelectTarget(Enemies) : Menus.SelectTarget(Allies, Enemies);
				switch(selected.getDamageType()) {
					case STR:
						selected.Use(target, Caster.getEffectiveSTR());
						break;
					case MAG:
						selected.Use(target, Caster.getEffectiveMAG());
						break;
					case DEX:
						selected.Use(target, Caster.getEffectiveDEX());
						break;
				}
				Caster.setMP(Caster.getMP()-selected.getCOST());
				CBUFFER.addToBuffer(Caster.getNAME() + " used " + selected.getNAME() + " on " + target.getNAME());
			}
		} else {
			// To-do: implement when enemy use a skill
		}
		
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	private void Objects(Entity Caster,ArrayList<Entity> Allies,ArrayList<Entity> Enemies,boolean isEnemy) {
		Entity Enemy;
		ArrayList<Item> objects;
		
		if(!isEnemy){
			objects = Allies.get(0).getInventory();
			if(objects.size()>=1) {
				Item selected = Menus.ObjectMenu(Allies.get(0).getInventory());
				if(selected!=null && selected.getAMOUNT()>=1) {
					Entity target = Menus.SelectTarget(Allies, Enemies);
					selected.Use(target);
					selected.setAMOUNT(selected.getAMOUNT()-1);
					CBUFFER.addToBuffer(Caster.getNAME() + " used " + selected.getNAME() + " on " + target.getNAME());
				}
			} else {
				CBUFFER.addToBuffer("You don't have anything yet...");
				InputHelper.clearScreen();
				for(String s : CBUFFER.getScreenBuffer()) System.out.println(s);
				return;
			}
		} else {
			if(Enemies.size()>1) {
				Enemy = Enemies.get((int)( Math.random()*Enemies.size()));
			} else {
				Enemy = Enemies.get(0);
			}
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	
}
