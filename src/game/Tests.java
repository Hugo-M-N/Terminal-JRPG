package game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import game.combat.Combat;
import game.entity.ClassManager;
import game.entity.EnemyManager;
import game.entity.Entity;
import game.map.Map;
import game.map.Tileset;
import game.npc.NpcManager;
import game.skill.SkillManager;
import game.utils.InputHelper;
import game.zone.ZoneManager;

public class Tests {
	static Scanner sc = new Scanner(System.in);
	public static void main(String[] args) throws InterruptedException {
		ScreenBuffer BUFFER = new ScreenBuffer();		
		
		try {
			Tileset.loadTileSet();
		} catch (IOException e) {
			e.printStackTrace();
		}
		SkillManager.loadSkills();
		ClassManager.LoadClasses();		
		EnemyManager.loadEnemies();
		NpcManager.loadNpcFiles();
		ZoneManager.loadZones();
		ZoneManager.setCurrentZone("TEST");
		Map map = ZoneManager.getCurrentZone().getMap();
		
		Entity Player = new Entity("Player", ClassManager.getClasses().get("CLERIC"), 1);
		ArrayList<Entity> Allies = new ArrayList<Entity>();
		Allies.add(Player);
		
//		ArrayList<Item> items = new ArrayList<Item>() ;
//		items.add(new Potion("Heal", "Heals 10% of your HP", 15, PotionType.HEAL1));
//		items.add(new Potion("Ether", "Restores 10% of your MP", 15, PotionType.ETHER1));
//		
//		Menus.shopMenu(items, Allies);
//		
		int Px=10, Py=7;
		int Ex=15, Ey=5;
		int Hx=15, Hy=4;
		
		try {
			InputHelper.enableMenuMode();
			while(true) {
				InputHelper.clearScreen();
				for(int y=0; y<(map.getHeight()); y++){
					for(int x=0; x<(map.getWidth()); x++) {
						if((Px==x) && (Py==y)) System.out.print("\033[35m\033[104m@\033[0m");
						else if((Ex==x) && (Ey==y)) System.out.print("\033[31m#\033[0m");
						else if((Hx==x) && (Hy==y)) System.out.print("\033[96m\033[46m+\033[0m");
						else System.out.print(map.getMapTile(x, y));
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
						sel = TextMenus.mainGameMenu();
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
					new Combat(Allies, Enemies, BUFFER);
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