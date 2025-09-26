package game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import game.combat.Combat;
import game.entity.EnemyManager;
import game.entity.Entity;
import game.entity.EntityClass;
import game.item.Item;
import game.item.ItemManager;
import game.item.Potion;
import game.item.PotionType;
import game.skill.DamageType;
import game.skill.Skill;
import game.skill.SkillManager;
import game.utils.InputHelper;
import game.zone.ZoneZZ;
import game.zone.ZoneManager;

public class Game {
	static String directoryURL = System.getProperty("user.home") + "/Terminal_JRPG/";
	static File directory = new File(directoryURL);

	static Scanner sc = new Scanner(System.in);
	static ScreenBuffer BUFFER = new ScreenBuffer();
	
	static ZoneZZ currentZone;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		if (!directory.exists()) {
			directory.mkdir();
		}
		boolean GAME = true;
		
		ArrayList<Entity> Allies = null;
		boolean invalid = true;
		
		SkillManager.loadSkills();
		ItemManager.loadItemFiles();
		EnemyManager.loadEnemies();
		ZoneManager.loadZones();
						
		while (invalid) {
			// Clear Terminal
			InputHelper.clearScreen();
			
			// Game start
			switch (Menus.MainMenu()) {
			case "New Game":
				// Create Character
				Allies = NewStart();
				ZoneManager.setCurrentZone("TEST");
				currentZone.Enter(Allies, BUFFER);
				
				invalid = false;
				break;
			case "Load Game":
				// Load Game
				Allies = Menus.LoadMenu();
				if (Allies != null) {
					invalid = false;
				}
				break;
			case "Exit Game":
				// Exit
				if(Menus.confirmExit()==1) return;
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

			
			String option = Menus.mainGameMenu();
			
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
				for(String s : Menus.SkillList(Allies.get(0))) BUFFER.addToBuffer(s);
				break;

			case "Inventory":
				BUFFER.clearBuffer();
				Menus.Inventory(Allies);
				break;

			case "Menu":
				BUFFER.clearBuffer();
				switch (Menus.SaveLoadExitMenu()) {
				case "Options":
					break;
				
				case "Save game":
					boolean saved = SaveManager.SaveGame(Allies, Allies.get(0).getNAME());
					if (saved) BUFFER.printAnimatedMessage("Game saved");	
					else BUFFER.printAnimatedMessage("Some error happend.");
					
					break;
				case "Load game":
					ArrayList<Entity> Load = Menus.LoadMenu();
					if(Load!=null) {
						Allies = Load;
						BUFFER.clearBuffer();
					}
					break;
				case "Exit game":
					
					if(Menus.confirmExit()==1) GAME = false;
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
		
		int CSEL = -1;
		String[] CLASSES = { "Warrior +(DEF,STR) -(MAG,DEX)", "Mage +(MAG) -(STR,DEF)", "Cleric +(MAG,DEF) -(STR,DEX)",
				"Rogue +(DEX, STR) -(DEF, MAG)" };
		EntityClass PClass = null;
		while (!(CSEL > 0 && CSEL <= 4)) {
			BUFFER.printAnimatedMessage("CLASSES");
			for (int i = 0; i < CLASSES.length; i++) {
				BUFFER.printAnimatedMessage((i + 1) + "-" + CLASSES[i]);
			}
			
			InputHelper.enableTextMode();
			
			while (!(CSEL >= 1 && CSEL <= 4)) {
				System.out.print("\033[23;2HSEL: ");
				try {
					CSEL = Integer.valueOf(sc.nextLine());
				} catch (Exception e) {
					BUFFER.printAnimatedMessage("Invalid selection.");
				}
			}

			switch (CSEL) {

			case 1:
				PClass = EntityClass.WARRIOR;
				BUFFER.printAnimatedMessage("Class selected: Warrior");
				break;
			case 2:
				PClass = EntityClass.MAGE;
				BUFFER.printAnimatedMessage("Class selected: Mage");
				break;
			case 3:
				PClass = EntityClass.CLERIC;
				BUFFER.printAnimatedMessage("Class selected: Cleric");
				break;
			case 4:
				PClass = EntityClass.ROGUE;
				BUFFER.printAnimatedMessage("Class selected: Rogue");
				break;

			default:
				BUFFER.printAnimatedMessage("Invalid selection, try again.");
			}
		}
		Entity Player = new Entity(Name, PClass, 1);

		ArrayList<Entity> Allies = new ArrayList<Entity>();
		Allies.add(Player);
		currentZone = ZoneZZ.START;

		return Allies;
	}


	private static void Explore(ArrayList<Entity> Allies) throws InterruptedException {
		String[] combResult;
		boolean Losed=false;
		
		if(currentZone==ZoneZZ.START && Event.First_Explore.getStatus()) currentZone.Enter(Allies, BUFFER);
		
		switch(Menus.Menu(currentZone.getConns())) {
			case "Go back":
				BUFFER.printAnimatedMessage("");
				BUFFER.printAnimatedMessage("You sat down.");
				break;
			case "Forest":
				if(currentZone!=ZoneZZ.FOREST) currentZone = ZoneZZ.FOREST;
				
				combResult = ZoneZZ.FOREST.Enter(Allies, BUFFER);
				
				for(String s : combResult) BUFFER.addToBuffer(s);
				
				if(combResult[0].equalsIgnoreCase("You ran away.")) return;
				
				if(combResult[0].equalsIgnoreCase("You Lose.")) Losed = true;
				
				if(!Losed && Event.First_Forest.getStatus()) {
					BUFFER.printAnimatedMessage("");
					BUFFER.printAnimatedMessage("\033[0m");
					BUFFER.printAnimatedMessage("You see the gold.Maybe I should get some gear before explore the forest, I think I saw a village.");
					Event.First_Forest.setStatus(false);
					Event.King_Goblin.setStatus(true);
				} else if(!Losed && Event.King_Goblin.getStatus()) Event.King_Goblin.setStatus(false);
				
				if (Losed) {
					Allies.get(0).setHP(1);
					int G = Allies.size() * 10;
					if (Allies.get(0).getGOLD() > G)
						Allies.get(0).setGOLD(Allies.get(0).getGOLD() - G);
					else
						Allies.get(0).setGOLD(0);
					BUFFER.printAnimatedMessage("");
					BUFFER.printAnimatedMessage("You lost some gold while you where unconscious.");
				
			}

				break;
			case "Village":
				if(currentZone!=ZoneZZ.VILLAGE) currentZone = ZoneZZ.VILLAGE;
				
				ZoneZZ.VILLAGE.Enter(Allies, BUFFER);
				
				break;
			default:
				break;
		}
	}
	
	public static ZoneZZ getCurrentZone() {
		return currentZone;
		
	}
	
	public static void setCurrentZone(ZoneZZ zone) {
		currentZone = zone;
	}
}
