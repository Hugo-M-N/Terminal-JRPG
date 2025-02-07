package game;

import java.io.File;
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

public class Game {

	static Scanner sc = new Scanner(System.in);
	public static void main(String[] args) {
		boolean GAME = true;
		String[] options = {"Search","Status","Skills","Inventory","Save and exit"};
		int sel = 0;
		ArrayList<Entity> Allies=null;
		
		// Clear Terminal
        System.out.print("\033[H\033[2J");
        System.out.flush();
		
        //Game start
        Welcome();
        switch(MainMenu()) {
        	case 1:
        		// Create Character
        		Allies = NewStart();
        		break;
        	case 2:
        		// Load Game
        		Allies = LoadMenu();
        		break;
        	case 3:
        		// Exit
        		return;
        }

		
		//Test
		Allies.get(0).addSkill(new Skill("Test Skill", 1,DamageType.MAG, 1));
		
		while(GAME) {
			while(!(sel>=1 && sel<=options.length)) {
				Menu(options);
				try {
					sel = Integer.valueOf(sc.nextLine());
					
				} catch(Exception e) {
					System.out.println("Invalid selection.");
				}
			}
			
			// Clear terminal
	        System.out.print("\033[H\033[2J");
	        System.out.flush();
			
			switch(options[(sel-1)]) {
				
				case "Search":
					Search(Allies);
					break;
					
				case "Status":
					Allies.get(0).stats();
					break; 
				case "Skills":
					SkillList(Allies.get(0));
					break;
					
				case "Inventory":
					ObjectList(Allies.get(0));
					break;
					
				case "Save and exit":
					boolean saved = SaveManager.SaveGame(Allies, Allies.get(0).getNAME());
					if(saved) {
						System.out.println("Gave saved");
						GAME=false;
					} else {
						System.out.println("Some error happend.");
					}
					break;
					
				default:
					break;
			}
			
			sel=0;
			
		}

	}
	
	private static void Welcome() {
		String logo = "\033[40m\033[31m_________ _______  _______  _______ _________ _        _______  _            _________ _______  _______  _______ \r\n"
				+ "\\__   __/(  ____ \\(  ____ )(       )\\__   __/( (    /|(  ___  )( \\           \\__    _/(  ____ )(  ____ )(  ____ \\\r\n"
				+ "   ) (   | (    \\/| (    )|| () () |   ) (   |  \\  ( || (   ) || (              )  (  | (    )|| (    )|| (    \\/\r\n"
				+ "   | |   | (__    | (____)|| || || |   | |   |   \\ | || (___) || |              |  |  | (____)|| (____)|| |      \r\n"
				+ "   | |   |  __)   |     __)| |(_)| |   | |   | (\\ \\) ||  ___  || |              |  |  |     __)|  _____)| | ____ \r\n"
				+ "   | |   | (      | (\\ (   | |   | |   | |   | | \\   || (   ) || |              |  |  | (\\ (   | (      | | \\_  )\r\n"
				+ "   | |   | (____/\\| ) \\ \\__| )   ( |___) (___| )  \\  || )   ( || (____/\\     |\\_)  )  | ) \\ \\__| )      | (___) |\r\n"
				+ "   )_(   (_______/|/   \\__/|/     \\|\\_______/|/    )_)|/     \\|(_______/_____(____/   |/   \\__/|/       (_______)\r\n"
				+ "                                                                       (_____)                                   \033[0m";
		System.out.println(logo);
	}
	
	private static int MainMenu(){
		String[] options= {"New Game","Load Game","Exit Game"};
		System.out.println("_________________________");
		System.out.println("Main Menu");
		for(int i=0; i<options.length;i++) {
			System.out.println((i+1)+"- "+options[i]);
		}
		System.out.println("_________________________");
		int sel=0;
		while(!(sel>0 && sel<=options.length)) {
			System.out.print("Selection: ");
			try {
				sel = Integer.valueOf(sc.nextLine());
			} catch (Exception e) {
			}
		}
		return sel;
	}
	
	private static ArrayList<Entity> NewStart() {
		// clear terminal
        System.out.print("\033[H\033[2J");
        System.out.flush();
        
		System.out.println("Welcome to Terminal_JRPG.");
		System.out.println("This game is a turn based game inspired by the classic JRPG's.");
		System.out.println("Hope you have fun!\n");
		System.out.println("Let's create your character.");
		System.out.println("What's your name? (Max 50 char)");
		String Name="";
		while(!(Name.length()>0 && Name.length()<=50)) {
			System.out.print("Name: ");
				Name = sc.nextLine();
			if(!(Name.length()>0 && Name.length()<=50)) {
				System.out.println("Enter a valid name.");
			}
		}
		// clear terminal
        System.out.print("\033[H\033[2J");
        System.out.flush();
		
		System.out.println("There`s 4 main stats, STR, MAG, DEF, DEX");
		System.out.println("Strength (STR): Fisical damage.");
		System.out.println("Magic (MAG): Magical damage and magic points (MP).");
		System.out.println("Defense (DEF): Damage reduction and max health points (HP)");
		System.out.println("Dexterity (DEX): Speed and critic attacks");
		System.out.println("Choose your class:");
		int CSEL=-1;
		String[] CLASSES = {"Warrior +(DEF,STR) -(MAG,DEX)", "Mage +(MAG) -(STR,DEF)", "Cleric +(MAG,DEF) -(STR,DEX)", "Rogue +(DEX, STR) -(DEF, MAG)"};
		EntityClass PClass = null;
		while(!(CSEL>0 && CSEL<=4)) {			
			System.out.println("CLASSES");
			for(int i=0; i<CLASSES.length;i++) {
				System.out.println((i+1)+"-"+CLASSES[i]);
			}
			while(!(CSEL>=1 && CSEL<=4)) {
				System.out.print("SEL: ");
				try {					
					CSEL = Integer.valueOf(sc.nextLine());
				} catch (Exception e) {
					System.out.println("Invalid selection.");
				}
			}
			
			switch(CSEL) {
			
				case 1:
					PClass = EntityClass.WARRIOR;
					break;
				case 2:
					PClass = EntityClass.MAGE;
					break;
				case 3:
					PClass = EntityClass.CLERIC;
					break;
				case 4:
					PClass = EntityClass.ROGUE;
					break;
					
				default:
					System.out.println("Invalid selection, try again.");
			}
		}
		Entity Player = new Entity(Name, PClass, 10);
		
		ArrayList<Entity> Allies = new ArrayList<Entity>();
		Allies.add(Player);
		
		return Allies;
	}
	
	private static ArrayList<Entity> LoadMenu(){
		ArrayList<Entity> Allies = new ArrayList<Entity>();
		File directory = new File("saves/");
		File[] saves = directory.listFiles();
		int sel=0;
		while(!(sel>0 && sel<=saves.length)) {
			System.out.println("_________________________");
			System.out.println("Load Menu\n");
			for(int i=0; i<saves.length;i++) {
				String name = (saves[i].getName());
				System.out.println((i+1)+"- " + name);
			}
			System.out.println("_________________________");
			System.out.print("Selection: ");
			try {
				sel = Integer.valueOf(sc.nextLine());		
				Allies = SaveManager.LoadGame(saves[(sel-1)].getName());
			} catch (Exception e) {
			}
		}
		
		return Allies;
	}
	
	private static void Menu(String[] options) {
		for(int i=0; i<options.length; i++) {
			System.out.printf(" %d-%s |", (i+1), options[i]);
		}
		System.out.print("\nSelection: ");
	}
	
	private static void Search(ArrayList<Entity> Allies) {
		int n = (int) (Math.random()*12);
		
		if(n>=0 && n<=1) System.out.println("You didn´t find anything.\n");
		else if(n>1 && n<=3) {
			int m = (int) (Math.random()*10);
			System.out.println("You find "+ m +" gold.\n");
			Allies.get(0).setGOLD(Allies.get(0).getGOLD()+m);
		} else if(n>3 && n<=8) {
			// Test 1vs1
			boolean Losed = false;
			ArrayList<Entity> Enemies = new ArrayList<Entity>();
			Enemies.add(new Entity("Test Enemy",EntityClass.WARRIOR,(int)(Math.random()*Allies.get(0).getLVL())+1));
			Combat comb = new Combat(Allies, Enemies);
			for(Entity Ally : Allies) {
				if(Ally.getHP()<=0) {
					Losed=true;
				}
			}
			if(Losed) {
				Allies.get(0).setHP(1);
				int G = Allies.size()*10;
				if(Allies.get(0).getGOLD()>G) Allies.get(0).setGOLD(Allies.get(0).getGOLD()-G);
				else Allies.get(0).setGOLD(0);
				System.out.println("You lost some gold while you where unconscious.");
			}
			
		} else {
			System.out.println("You find an item.");
			int item =(int) Math.round(Math.random()*2+1);
			boolean found=false;
			switch(item) {
				case 1,3:
					System.out.println("You found a heal potion.");
					Allies.get(0).addToInventory(new Potion("Heal potion", "This potion will heal you 10% of your max health.", PotionType.HEAL1));
					break;
				case 2:
					System.out.println("You found an ether");
					Allies.get(0).addToInventory(new Potion("Ether", "This potion will restore you 10% of your max magic points.", PotionType.ETHER1));
			}
		}
		
	}
	
	private static void SkillList(Entity entity) {
		ArrayList<Skill> Skills = entity.getSkills();
		System.out.println("_______________________");
		System.out.println("Skills\n");
		for(int i=0; i<Skills.size(); i++) {
			System.out.println((i+1)+ "- "+ Skills.get(i).getNAME());
		}
		System.out.println("_______________________");
		
	}
	
	private static void ObjectList(Entity entity) {
		ArrayList<Object> Objects = entity.getInventory();
		if(Objects.size()>=1) {
			System.out.println("_______________________");
			System.out.println("Inventory\n");
			System.out.printf("  %15s  %2s  %s\n","Name","nº", "Description");
			for(Object obj : Objects) {
				System.out.printf("- %15s  %2s  %s\n",obj.getNAME(),obj.getAMOUNT(),obj.getDESC());
			}
			System.out.println("_______________________");
		} else System.out.println("You don't have anything yet...");
	}

}
