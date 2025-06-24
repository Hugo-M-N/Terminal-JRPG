package game.combat;

import java.util.ArrayList;
import java.util.Scanner;

import game.Menus;
import game.ScreenBuffer;
import game.entity.Entity;
import game.object.Object;
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
	public Combat(ArrayList<Entity> Allies, ArrayList<Entity> Enemies, ScreenBuffer BUFFER) {
		int ASel = 0, ESel=0;
		String[] result = null;
		CBUFFER = BUFFER;
		
		while(combat) {
			InputHelper.clearScreen();
			for(String s : CBUFFER.getScreenBuffer()) System.out.println(s);

			// Ally turn
			for(Entity Ally : Allies) {
				if(Ally.getT_COUNT()>=100 && Ally.getHP()>0) {
					Ally.setT_COUNT(Ally.getT_COUNT()-100);
					Ally.setIsDef(false);
					for(String s : Menus.combatMenu(Allies, Enemies, Ally)) CBUFFER.updateBuffer(s);
					
					//Print Screen
					InputHelper.clearScreen();
					for(String s : CBUFFER.getScreenBuffer()) System.out.println(s);
			        
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
							return;
					}
					
				}
			}
		
			// Enemy turn
			for(Entity Enemy : Enemies) {
				if(Enemy.getT_COUNT()>=100 && Enemy.getHP()>0){
					Enemy.setT_COUNT(Enemy.getT_COUNT()-100);
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
					combat=false;
					result = winCombat(Allies,Enemies);
					break;
				case 2:
					combat=false;
					result = new String[] {"You Lose.", "You won`t gain any XP or gold."};
					break;
				default:
					break;
			}
		}
		Result = result;
	}
	
	// Functions
	
	private static void DisplayEntities(ArrayList<Entity> Entities) {
		for(int i=0; i<Entities.size();i++) {
			CBUFFER.addToBuffer(String.format("%d- %s \\033[31m%d/%d-HP\\033[0m",(i+1),Entities.get(i).getNAME(), Entities.get(i).getHP(), Entities.get(i).getMAX_HP()));
		}
		InputHelper.clearScreen();
		for(String s : CBUFFER.getScreenBuffer()) System.out.println(s);
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
	
	private static String[] winCombat(ArrayList<Entity> Allies, ArrayList<Entity> Enemies) {
		int EnemiesLvl=0;
		float XP;
		int gold=0;
		for(Entity Enemy : Enemies) {
			EnemiesLvl += Enemy.getLVL();
			gold += Enemy.getGOLD();
		}
		
		EnemiesLvl = (EnemiesLvl/Enemies.size());
		XP = (EnemiesLvl*0.3f+Enemies.size()*0.7f);
		
		String g = "You earn " + gold + " G.";
		String xp = "Each Ally got "+ (int) Math.round(XP/Allies.size()) + " XP.";
		ArrayList<String> result = new ArrayList<String>();
		result.add(g);
		result.add(xp);
		
		for(Entity Ally : Allies) {
			String[] info = Ally.addXP(((int) Math.round(XP/Allies.size())));
			for(String s : info) result.add(s);
		}
		
		
		return result.toArray(new String[0]);
		
	}
	
	private void Attack(Entity Attaker, ArrayList<Entity> Enemies, boolean isEnemy) {
		Entity Enemy = null;
		if(!isEnemy){		
			Enemy = Menus.SelectTarget(Enemies);
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
		CBUFFER.addToBuffer(Enemy.getNAME()+" recieved "+ dmg+ " dmg.");	
		InputHelper.clearScreen();
		for(String s : CBUFFER.getScreenBuffer()) System.out.println(s);
	}
	
	private void Defend(Entity Defender) {
		Defender.setIsDef(true);
		CBUFFER.addToBuffer(String.format("%s is defending.",Defender.getNAME()));
		InputHelper.clearScreen();
		for(String s : CBUFFER.getScreenBuffer()) System.out.println(s);
	}
	
	private void Skills(Entity Caster,  ArrayList<Entity> Allies, ArrayList<Entity> Enemies, boolean isEnemy) {
		if(!isEnemy) {
			Skill selected = Menus.SkillMenu(Caster.getSkills());
			if(selected!=null && Caster.getMP()>=selected.getCOST()){
				Entity target = (selected.getDMG()>0) ? Menus.SelectTarget(Enemies) : Menus.SelectTarget(Allies, Enemies);
				switch(selected.getDamageType()) {
					case STR:
						selected.Use(target, Caster.getSTR());
						break;
					case MAG:
						selected.Use(target, Caster.getMAG());
						break;
					case DEX:
						selected.Use(target, Caster.getDEX());
						break;
				}
				Caster.setMP(Caster.getMP()-selected.getCOST());
				CBUFFER.addToBuffer(Caster.getNAME() + " used " + selected.getNAME() + " on " + target.getNAME());
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
				Object selected = Menus.ObjectMenu(Allies.get(0).getInventory());
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
			
			Enemy = Menus.SelectTarget(Allies,Enemies);
			
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

	
}
