package game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import game.combat.Combat;
import game.entity.ClassManager;
import game.entity.EnemyManager;
import game.entity.Entity;
import game.entity.EntityClass;
import game.item.ItemManager;
import game.map.Map;
import game.map.Tileset;
import game.skill.SkillManager;
import game.utils.InputHelper;
import game.zone.Zone;
import game.zone.ZoneManager;

public class TextGame implements Runnable{

	static Scanner sc = new Scanner(System.in);
	static ScreenBuffer BUFFER = new ScreenBuffer();
	
	static Zone currentZone;
	static Map map;
	
	@Override
	public void run()  {		
		try {
			loadManagers();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			mainLoop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loadManagers() throws IOException {
		Tileset.loadTileSet();
		SkillManager.loadSkills();
		ClassManager.LoadClasses();
		ItemManager.loadItemFiles();
		EnemyManager.loadEnemies();
		ZoneManager.loadZones();
	}
	
	public static void mainLoop() throws IOException, InterruptedException {
		
		boolean GAME = true;
		
		ArrayList<Entity> Allies = null;
		boolean invalid = true;
		
						
		while (invalid) {
			// Clear Terminal
			InputHelper.clearScreen();
			
			// Game start
			switch (TextMenus.MainMenu()) {
			case "New Game":
				// Create Character
				Allies = NewStart();
				ZoneManager.setCurrentZone("TEST");
				currentZone = ZoneManager.getCurrentZone();
				map = ZoneManager.getCurrentZone().getMap();

				
				invalid = false;
				break;
			case "Load Game":
				// Load Game
				Allies = TextMenus.LoadMenu();
				if (Allies != null) invalid = false;
				map = ZoneManager.getCurrentZone().getMap();
				break;
			case "Exit Game":
				// Exit
				if(TextMenus.confirmExit()==1) return;
				else invalid = true;
				break;
				
			default:
				// Exit
				invalid = false;
				return;
			}
		}
				
		while (GAME) {
			InputHelper.clearScreen();
			
			for(String s : BUFFER.getScreenBuffer()) System.out.println(s);

			
			String option = TextMenus.mainGameMenu();
			
			switch (option) {

			case "Explore":
				BUFFER.clearBuffer();
				Explore(Allies);
				break;

			case "Status":
				BUFFER.clearBuffer();
				String[] stats = Allies.get(0).stats();
				stats[2]+= currentZone.getName();
				for(String s : stats) BUFFER.addToBuffer(s);
				break;
			case "Skills":
				BUFFER.clearBuffer();
				for(String s : TextMenus.SkillList(Allies.get(0))) BUFFER.addToBuffer(s);
				break;

			case "Inventory":
				BUFFER.clearBuffer();
				TextMenus.Inventory(Allies);
				break;

			case "Menu":
				BUFFER.clearBuffer();
				switch (TextMenus.SaveLoadExitMenu()) {
				case "Options":
					TextMenus.OptionsMenu();
					break;
				
				case "Save game":
					boolean saved = SaveManager.SaveGame(Allies, Allies.get(0).getNAME());
					if (saved) BUFFER.printAnimatedMessage("Game saved");	
					else BUFFER.printAnimatedMessage("Some error happend.");
					
					break;
				case "Load game":
					ArrayList<Entity> Load = TextMenus.LoadMenu();
					if(Load!=null) {
						Allies = Load;
						BUFFER.clearBuffer();
					}
					break;
				case "Exit game":
					
					if(TextMenus.confirmExit()==1) GAME = false;
					break;
					
				default:
					break;
				}
				break;

			default:
				break;
			}
			
		}
	}

	private static ArrayList<Entity> NewStart() throws InterruptedException {
		InputHelper.clearScreen();

		BUFFER.clearBuffer();
		for(String s : BUFFER.getScreenBuffer()) System.out.println(s);
		BUFFER.printAnimatedMessage("");
		BUFFER.printAnimatedMessage("Welcome to Terminal_JRPG.");
		BUFFER.printAnimatedMessage("This game is a turn based game inspired by the classic JRPG's.");
		BUFFER.printAnimatedMessage("Hope you have fun!");
		BUFFER.printAnimatedMessage("Let's create your character.");
		BUFFER.printAnimatedMessage("What's your name? (Max 50 char)");
				
		InputHelper.enableTextMode();
		
		String Name = "";
		while (!(Name.length() > 0 && Name.length() <= 50)) {
			System.out.print("\033[23;2HName: ");
			Name = sc.nextLine();
			if (!(Name.length() > 0 && Name.length() <= 50)) {
				BUFFER.printAnimatedMessage("Enter a valid name.");
			}
		}
		BUFFER.addToBuffer("Name: " + Name);
		
		InputHelper.clearScreen();
		
		BUFFER.printAnimatedMessage("\033[0m");
		BUFFER.printAnimatedMessage("There's 4 main stats, STR, MAG, DEF, DEX");
		BUFFER.printAnimatedMessage("Strength (STR): Fisical damage.");
		BUFFER.printAnimatedMessage("Magic (MAG): Magical damage and magic points (MP).");
		BUFFER.printAnimatedMessage("Defense (DEF): Damage reduction and max health points (HP)");
		BUFFER.printAnimatedMessage("Dexterity (DEX): Speed and critic attacks");
		BUFFER.printAnimatedMessage("Choose your class:");
		
		String[] CLASSES = { "Warrior +(DEF,STR) -(MAG,DEX)", "Mage +(MAG) -(STR,DEF)", "Cleric +(MAG,DEF) -(STR,DEX)",
				"Rogue +(DEX, STR) -(DEF, MAG)" };
		EntityClass PClass = null;
		BUFFER.printAnimatedMessage("CLASSES");
		for (int i = 0; i < CLASSES.length; i++) {
			BUFFER.printAnimatedMessage((i + 1) + "-" + CLASSES[i]);
		}
		
		BUFFER.addToBuffer("Select a class:");
		
		InputHelper.enableTextMode();
		
		ArrayList<String> options = new ArrayList<String>();
		
		ClassManager.getClasses().forEach((k,v) -> {
			options.add(v.getNAME());
		});
		
		PClass = ClassManager.getClasses().get(TextMenus.Menu(options).toUpperCase());

			
		
		Entity Player = new Entity(Name, PClass, 1);

		ArrayList<Entity> Allies = new ArrayList<Entity>();
		Allies.add(Player);
		ZoneManager.setCurrentZone("TEST");
		currentZone = ZoneManager.getCurrentZone();
		map = ZoneManager.getCurrentZone().getMap();


		return Allies;
	}


	private static void Explore(ArrayList<Entity> Allies) {				
		InputHelper.clearScreen();
		
		// Draw Map - Testing
		for(int[] row : currentZone.getMap().getMapTiles()) {
			for(int i : row) System.out.print(Tileset.getTextTile(i));
			System.out.println();
		}
		
		try {
			Thread.sleep(500);
			
			ArrayList<Entity> enemies = new ArrayList<Entity>();
			enemies.add(EnemyManager.getEnemy("GOBLIN"));
		
			new Combat(Allies, enemies, BUFFER);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static Zone getCurrentZone() {
		return currentZone;
		
	}
	
	public static void setCurrentZone(Zone zone) {
		currentZone = zone;
	}
}
