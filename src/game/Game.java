package game;

import java.util.ArrayList;
import java.util.Scanner;

import game.entity.Entity;
import game.object.Object;
import game.skill.Skill;

public class Game {

	static Scanner sc = new Scanner(System.in);
	public static void main(String[] args) {
		boolean GAME = true;
		String[] options = {"Search","Status","Skills","Inventory","Exit"};
		int sel = 0;
		
		//Test
		Entity TestP = new Entity("TEST", 1, 10, 5, 5, 5, 5, 5);
		TestP.addSkill(new Skill("Test Skill", 1, 1));
		TestP.addToInventory(new Object("Test Object"));
		TestP.addToInventory(new Object("Test Object 2", "Description test"));
		
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
					Search(TestP);
					break;
					
				case "Status":
					TestP.stats();
					break; 
				case "Skills":
					SkillList(TestP);
					break;
					
				case "Inventory":
					ObjectList(TestP);
					break;
					
				case "Exit":
					GAME=false;
					break;
					
				default:
					break;
			}
			
			sel=0;
			
		}

	}
	
	private static void Menu(String[] options) {
		for(int i=0; i<options.length; i++) {
			System.out.printf(" %d-%s |", (i+1), options[i]);
		}
		System.out.print("\nSelection: ");
	}
	
	private static void Search(Entity entity) {
		int n = (int) (Math.random()*10);
		
		if(n>=0 && n<=1) System.out.println("You didn´t find anything.\n");
		else if(n>1 && n<=3) {
			int m = (int) (Math.random()*10);
			System.out.println("You find "+ m +" gold.\n");
		} else if(n>3 && n<=6) {
			System.out.println("combate");
		} else {
			
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
		System.out.println("_______________________");
		System.out.println("Inventory\n");
		System.out.printf("  %15s  %2s  %s\n","Name","nº", "Description");
		for(Object obj : Objects) {
			System.out.printf("- %15s  %2s  %s\n",obj.getNAME(),obj.getAMOUNT(),obj.getDESC());
		}
		System.out.println("_______________________");
	}

}
