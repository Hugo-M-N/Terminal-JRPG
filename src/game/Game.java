package game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import game.combat.Combat;
import game.entity.Entity;
import game.entity.EntityClass;
import game.object.Object;
import game.object.Potion;
import game.object.PotionType;
import game.skill.DamageType;
import game.skill.Skill;
import game.utils.InputHelper;

public class Game {
	static String directoryURL = System.getProperty("user.home") + "/Terminal_JRPG/";
	static File directory = new File(directoryURL);

	static Scanner sc = new Scanner(System.in);
	static ScreenBuffer BUFFER = new ScreenBuffer();
	
	static ArrayList<String> ExploreOptions = new ArrayList<String>();
	
	static ArrayList<Boolean> SpecialEvents= new ArrayList<Boolean>();

	public static void main(String[] args) throws IOException, InterruptedException {
		
		if (!directory.exists()) {
			directory.mkdir();
		}
		boolean GAME = true;
		ArrayList<Entity> Allies = null;
		ExploreOptions.add("Go back");

		boolean invalid = true;
		while (invalid) {
			// Clear Terminal
			InputHelper.clearScreen();
			
			// Game start
			switch (Menus.MainMenu()) {
			case "New Game":
				// Create Character
				Allies = NewStart();
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

		//Special Events
		SpecialEvents.add(true); // it's the first time you explore
		SpecialEvents.add(true); // first time in the forest
		SpecialEvents.add(true); // King goblin
		SpecialEvents.add(true); // first time in the shop
		

		
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
				for(String s : stats) BUFFER.addToBuffer(s);
				break;
			case "Skills":
				BUFFER.clearBuffer();
				for(String s : Menus.SkillList(Allies.get(0))) BUFFER.addToBuffer(s);
				break;

			case "Inventory":
				BUFFER.clearBuffer();
				for(String s : Menus.ObjectList(Allies.get(0))) BUFFER.addToBuffer(s);;
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
		BUFFER.printAnimatedMessage("There`s 4 main stats, STR, MAG, DEF, DEX");
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

		return Allies;
	}

	private static void Search(ArrayList<Entity> Allies) {
		int n = (int) (Math.random() * 12);

		if (n >= 0 && n <= 1)
			System.out.println("You didn´t find anything.\n");
		else if (n > 1 && n <= 3) {
			int m = (int) (Math.random() * 10);
			System.out.println("You find " + m + " gold.\n");
			Allies.get(0).setGOLD(Allies.get(0).getGOLD() + m);
		} else if (n > 3 && n <= 8) {
			// Test 1vs1
			boolean Losed = false;
			ArrayList<Entity> Enemies = new ArrayList<Entity>();
			Enemies.add(new Entity("Test Enemy", EntityClass.WARRIOR, (int) (Math.random() * Allies.get(0).getLVL()) + 1));
			Combat comb = new Combat(Allies, Enemies, BUFFER);
			for (Entity Ally : Allies) {
				if (Ally.getHP() <= 0) {
					Losed = true;
				}
			}
			if (Losed) {
				Allies.get(0).setHP(1);
				int G = Allies.size() * 10;
				if (Allies.get(0).getGOLD() > G)
					Allies.get(0).setGOLD(Allies.get(0).getGOLD() - G);
				else
					Allies.get(0).setGOLD(0);
				System.out.println("You lost some gold while you where unconscious.");
			}

		} else {
			System.out.println("You find an item.");
			int item = (int) Math.round(Math.random() * 2 + 1);
			boolean found = false;
			switch (item) {
			case 1, 3:
				System.out.println("You found a heal potion.");
				Allies.get(0).addToInventory(new Potion("Heal potion",
						"This potion will heal you 10% of your max health.", PotionType.HEAL1));
				break;
			case 2:
				System.out.println("You found an ether");
				Allies.get(0).addToInventory(new Potion("Ether",
						"This potion will restore you 10% of your max magic points.", PotionType.ETHER1));
			}
		}

	}

	private static void Explore(ArrayList<Entity> Allies) throws InterruptedException {
		BUFFER.printAnimatedMessage("");
		
		if(SpecialEvents.get(0)) {
			BUFFER.printAnimatedMessage("You look around, you don't recognize anything. Actually, you don't know "
			+ "anything, you \"understand\" basic concepts like yourself, your name and how some things are called, "
			+ "but you can't remember more than a few minutes ago when you opened your eyes. So you don't know from "
			+ "where that knoledge came from. You start to walk and two things caught your eye, a forest and a small "
			+ "village.");
			
			ExploreOptions.add("Forest");
			ExploreOptions.add("Village");
			SpecialEvents.set(0, false);
		}
		
		switch(Menus.Menu(ExploreOptions)) {
			case "Go back":
				BUFFER.printAnimatedMessage("You sat down.");
				break;
			case "Forest":
				ArrayList<Entity> Enemies = new ArrayList<Entity>();
				boolean Losed=false;
				if(SpecialEvents.get(1)) {
					BUFFER.printAnimatedMessage("You walk into the forest, after some time you realize the forest is quiet, too quiet. "
							+ "You don't hear anything, this shouldn't be like this, right? You keep walking and some trees with "
							+ "marks start to appear, suddenly you hear a high pitched chirp and something jumps towards you.");
					
					Enemies.add(new Entity("Goblin", EntityClass.WARRIOR, 1));
					Combat comb = new Combat(Allies, Enemies, BUFFER);
					for(String s : comb.getResult()) BUFFER.addToBuffer(s);
				} else if(SpecialEvents.get(2) && Allies.get(0).getLVL()>=7){
					Enemies.add(new Entity("King Goblin", EntityClass.WARRIOR, 10));
					Combat comb = new Combat(Allies, Enemies, BUFFER);	
					for(String s : comb.getResult()) BUFFER.addToBuffer(s);
				} else {
					Enemies.add(new Entity("Goblin", EntityClass.WARRIOR, (int) (Math.random() * 3) + 1));
					Combat comb = new Combat(Allies, Enemies, BUFFER);
					for(String s : comb.getResult()) BUFFER.addToBuffer(s);
				}
				
				for (Entity Ally : Allies) {
					if (Ally.getHP() <= 0) {
						Losed = true;
					}
				}
				
				if(!Losed && SpecialEvents.get(1)) {
					SpecialEvents.set(1, false);
					SpecialEvents.set(2, true);
				} else if(!Losed && SpecialEvents.get(2) && SpecialEvents.get(1)==false) SpecialEvents.set(2, false);
				
				if (Losed) {
					Allies.get(0).setHP(1);
					int G = Allies.size() * 10;
					if (Allies.get(0).getGOLD() > G)
						Allies.get(0).setGOLD(Allies.get(0).getGOLD() - G);
					else
						Allies.get(0).setGOLD(0);
					BUFFER.printAnimatedMessage("You lost some gold while you where unconscious.");
				
			}
				
				break;
			case "Village":
				break;
			default:
				break;
		}
	}
}
