package game;

import java.util.ArrayList;
import java.util.Scanner;

import game.combat.Combat;
import game.entity.EnemyManager;
import game.entity.Entity;
import game.entity.EntityClass;
import game.skill.SkillManager;
import game.utils.InputHelper;
import game.zone.ZoneManager;

public class Tests {
	static Scanner sc = new Scanner(System.in);
	public static void main(String[] args) throws InterruptedException {
		String[][] Map = new String[15][30];
		for(int y=0; y<Map.length; y++){
			for(int x=0; x<Map[y].length; x++) {
				 Map[y][x] = "\033[92m\033[42mw";
			}
		}
		ScreenBuffer BUFFER = new ScreenBuffer();		
		
		SkillManager.loadSkills();
		EnemyManager.loadEnemies();
		ZoneManager.loadZones();
		ZoneManager.setCurrentZone("TEST");
		
		Entity Player = new Entity("Player", EntityClass.WARRIOR, 1);
		Entity test = EnemyManager.getEnemy("GOBLIN_SHAMAN");
		ArrayList<Entity> Allies = new ArrayList<Entity>();
		Allies.add(Player);
		Allies.add(test);
		Allies.get(0).addXP(30);
		
		int Px=15, Py=7;
		int Ex=25, Ey=5;
		int Hx=15, Hy=4;
		
		try {
			while(true) {
				InputHelper.clearScreen();
				for(int y=0; y<Map.length; y++){
					for(int x=0; x<Map[y].length; x++) {
						if((Px==x) && (Py==y)) System.out.print("\033[35m\033[104m@\033[0m");
						else if((Ex==x) && (Ey==y)) System.out.print("\033[31m#");
						else if((Hx==x) && (Hy==y)) System.out.print("\033[96m\033[46m+\033[0m");
						else System.out.print(Map[y][x]);
					}
					System.out.println();
				}
				
				System.out.println("\033[0mPx:"+ Px + " Py:"+ Py);
				                
				String key = InputHelper.READER.readBinding(InputHelper.KEYMAP);
				if (key == null) {
					continue;
				}
	            
				int tmpX = Px, tmpY = Py;
				
				switch (key) {
				case "UP":
					Py--;
					break;
				case "DOWN":
					Py++;
					break;
				case "RIGHT":
					Px++;
					break;
				case "LEFT":
					Px--;
					break;
				case "ESCAPE":
					String sel;
					do {
						sel = Menus.mainGameMenu();
						switch(sel) {
						case "Status":
							for(String s : Allies.get(0).stats()) BUFFER.addToBuffer(s);
							InputHelper.clearScreen();
							for(String s : BUFFER.getScreenBuffer()) System.out.println(s);
							break;
						}
					} while(!sel.equals("Explore"));
					break;
				}
				
				if((Px==Ex) && (Py==Ey)) {
					ArrayList<Entity> Enemies = new ArrayList<Entity>();
					String[] fights = ZoneManager.getCurrentZone().getFights();
					int random = (int) (Math.random() * fights.length-1) + 1;
					Enemies.add(EnemyManager.getEnemy(fights[random]));
					if(Allies.get(0).getLVL()>=3) {
						random = (int) (int) (Math.random() * fights.length-1) + 1;
						Enemies.add(EnemyManager.getEnemy(fights[random]));
					}
					if(Allies.get(0).getLVL()>=6) {
						random = (int) (int) (Math.random() * fights.length-1) + 1;
						Enemies.add(EnemyManager.getEnemy(fights[random]));
					}
					if(Allies.get(0).getLVL()>=7) {
						Enemies.clear();
						Enemies.add(EnemyManager.getEnemy("KING_GOBLIN"));
					}
					Combat comb = new Combat(Allies, Enemies, BUFFER);
					Px=tmpX;
					Py=tmpY;
				}
				
				if((Px==Hx) && (Py==Hy)) {
					for(Entity e : Allies) {
						e.setHP(e.getMAX_HP());
						e.setMP(e.getMAX_MP());
					}
					Px=tmpX;
					Py=tmpY;
				}
				
				Thread.sleep(16);
			}
		} finally {
			InputHelper.enableTextMode();
		}
	}
}		