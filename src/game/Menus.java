package game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import game.entity.Entity;
import game.item.Item;
import game.item.Usable;
import game.skill.Skill;
import game.utils.InputHelper;

public class Menus {
	static Scanner sc = new Scanner(System.in);
	static int MainGameMenuSel = 0;
	public static String MainMenu() throws IOException {		
		String[] options = { "New Game", "Load Game", "Exit Game" };
		int sel = 0;
		
		String logo = "\033[40m\033[31m_________ _______  _______  _______ _________ _        _______  _            _________ _______  _______  _______ \r\n"
				+ "\\__   __/(  ____ \\(  ____ )(       )\\__   __/( (    /|(  ___  )( \\           \\__    _/(  ____ )(  ____ )(  ____ \\\r\n"
				+ "   ) (   | (    \\/| (    )|| () () |   ) (   |  \\  ( || (   ) || (              )  (  | (    )|| (    )|| (    \\/\r\n"
				+ "   | |   | (__    | (____)|| || || |   | |   |   \\ | || (___) || |              |  |  | (____)|| (____)|| |      \r\n"
				+ "   | |   |  __)   |     __)| |(_)| |   | |   | (\\ \\) ||  ___  || |              |  |  |     __)|  _____)| | ____ \r\n"
				+ "   | |   | (      | (\\ (   | |   | |   | |   | | \\   || (   ) || |              |  |  | (\\ (   | (      | | \\_  )\r\n"
				+ "   | |   | (____/\\| ) \\ \\__| )   ( |___) (___| )  \\  || )   ( || (____/\\     |\\_)  )  | ) \\ \\__| )      | (___) |\r\n"
				+ "   )_(   (_______/|/   \\__/|/     \\|\\_______/|/    )_)|/     \\|(_______/_____(____/   |/   \\__/|/       (_______)\r\n"
				+ "                                                                       (_____)                                   \033[0m";
		
		if(!InputHelper.checkMenuMode()) InputHelper.enableMenuMode();

		try {
			while(true) {
                InputHelper.clearScreen();
                
                System.out.println(logo);
        		System.out.println("\n_________________________");
        		System.out.println("Main Menu\n");
                for (int i = 0; i < options.length; i++) {
                	if(sel==i) {
                		System.out.print("\u001B[7m" + options[i] + "\u001B[0m\n");
                	} else System.out.println(options[i]);
                }
                System.out.println("_________________________");
                
                String key = InputHelper.READER.readBinding(InputHelper.KEYMAP);
                if (key == null) {
                    continue;
                }
                
                switch (key) {
                case "UP":
                    sel--;
                    if(sel==-1) sel=options.length-1;
                    break;
                case "DOWN":
                    sel++;
                    if(sel==options.length) sel=0;
                    break;
                case "ENTER":
                	InputHelper.clearScreen();
                    return options[sel];
                case "ESCAPE":
                	InputHelper.clearScreen();
                    return null;
                }
			}
		} finally {
			InputHelper.enableTextMode();
		}
	}
	
	public static String Menu(ArrayList<String> options) {
		return Menu((String[]) options.toArray(new String[0]));
	}
	
	public static String Menu(String[] options) {
		int sel=0;
		
		if(!InputHelper.checkMenuMode()) InputHelper.enableMenuMode();		
		
		try {
			while(true) {
				//InputHelper.clearScreen();
				System.out.print("\033[24;1H");
				
				String window="";
				String insert = "";
				String tmp ="";
				window+="╠" + "═".repeat(Config.getSCREEN_WIDTH()) + "╣\n";
				
				window+="║ ";
				for(int i=0; i<options.length;i++) {
					tmp+=("  ╔"+"═".repeat(options[i].length()+2)+"╗  ");
				}
				while(tmp.length()<Config.getSCREEN_WIDTH()-1) {
					tmp += " ";
				}
				window+= tmp;
				window+="║\n";
				window+="║ ";
				tmp="";
				for(int i=0; i<options.length;i++) {
					if(sel==i) {
						insert+=("  ║\u001B[7m " + options[i] + " \u001B[0m║  ");
					} else insert+=("  ║ "+options[i]+" ║  ");
				}
				tmp = insert.replace("\u001B[7m", "").replace("\u001B[0m", "");
				while(tmp.length()<Config.getSCREEN_WIDTH()-1) {
					tmp += " ";
					insert+= " ";
				}
				window+= insert;
				window+="║\n";
				window+="║ ";
				tmp = "";
				for(int i=0; i<options.length;i++) {
					tmp+="  ╚" + "═".repeat(options[i].length()+2) + "╝  ";
				}
				while(tmp.length()<Config.getSCREEN_WIDTH()-1) {
					tmp += " ";
				}
				window+= tmp;
				window+="║\n";
				
				window+= "╚" + "═".repeat(Config.getSCREEN_WIDTH()) + "╝\n";
				
				System.out.println(window);
				
				String key = InputHelper.READER.readBinding(InputHelper.KEYMAP);
				switch(key) {
				case "RIGHT":
					sel++;
					if(sel==options.length) sel=0;
					break;
				case "LEFT":
					sel--;
					if(sel==-1) sel=options.length-1;
					break;
				case "ENTER":
					return options[sel];
				case "ESCAPE":
					return "Menu";
				}		
			}
		} finally {
			InputHelper.enableTextMode();
		}
		
	}
	
	public static String mainGameMenu() {
		String[] options = { "Explore", "Status", "Skills", "Inventory", "Menu" };
		
		if(!InputHelper.checkMenuMode()) InputHelper.enableMenuMode();
		
		
		try {
			while(true) {
				//InputHelper.clearScreen();
				System.out.print("\033[24;1H");
				
				String window="";
				window+="╠" + "═".repeat(Config.getSCREEN_WIDTH()) + "╣\n";

				window+="║ ";
				for(int i=0; i<options.length;i++) {
					window+=("  ╔"+"═".repeat(options[i].length()+2)+"╗  ");
				}
				window+="          ║\n";
				window+="║ ";
				for(int i=0; i<options.length;i++) {
					if(MainGameMenuSel==i) {
						window+=("  ║\u001B[7m " + options[i] + " \u001B[0m║  ");
					} else window+=("  ║ "+options[i]+" ║  ");
				}
				window+="          ║\n";
				window+="║ ";
				for(int i=0; i<options.length;i++) {
					window+="  ╚" + "═".repeat(options[i].length()+2) + "╝  ";
				}
				window+="          ║\n";
				
				window+= "╚" + "═".repeat(Config.getSCREEN_WIDTH()) + "╝\n";
				
				System.out.println(window);
				
				String key = InputHelper.READER.readBinding(InputHelper.KEYMAP);
				switch(key) {
					case "RIGHT":
						MainGameMenuSel++;
						if(MainGameMenuSel==options.length) MainGameMenuSel=0;
						break;
					case "LEFT":
						MainGameMenuSel--;
						if(MainGameMenuSel==-1) MainGameMenuSel=options.length-1;
						break;
					case "ENTER":
						return options[MainGameMenuSel];
					case "ESCAPE":
						return "Menu";
				}		
			}
		} finally {
			InputHelper.enableTextMode();
		}
	}
	
	public static String SaveLoadExitMenu() {
		String[] options = {"Return","Options","Save game", "Load game", "Exit game"};
		int sel=0;
		
		if(!InputHelper.checkMenuMode()) InputHelper.enableMenuMode();
		
		try {
			while(true) {
				InputHelper.clearScreen();
				
				System.out.println();
				System.out.println();
				System.out.println();
				System.out.println();
				System.out.println(" ".repeat(12) + "╔═════════════════════╗");
				System.out.println(" ".repeat(12) + "║Menu                 ║");
				System.out.println(" ".repeat(12) + "║---------------------║");
				for(int i=0; i<options.length;i++) {
                	if(sel==i) {
                		System.out.print(" ".repeat(12) + "║\u001B[7m" + options[i]+ " ".repeat(21-options[i].length()) + "\u001B[0m║\n");
                	} else System.out.println(" ".repeat(12) + "║"+options[i]+ " ".repeat(21-options[i].length())+ "║");
				}
				System.out.println(" ".repeat(12) + "╚═════════════════════╝");
				
				String key = InputHelper.READER.readBinding(InputHelper.KEYMAP);
                switch (key) {
                case "UP":
                    sel--;
                    if(sel==-1) sel=options.length-1;
                    break;
                case "DOWN":
                    sel++;
                    if(sel==options.length) sel=0;
                    break;
                case "ENTER":
                	InputHelper.clearScreen();
                    return options[sel];
                }
				
			}
		} finally {
			InputHelper.enableTextMode();
		}		
	}
	
	public static int confirmExit() {
		String[] options = {"No", "Yes"};
		int sel=0;
		
		if(!InputHelper.checkMenuMode()) InputHelper.enableMenuMode();
		
		try {
			while(true) {
				InputHelper.clearScreen();
				System.out.println("\n\n\n\n");
				System.out.println("          ╔═══════════════════════════════════╗");
				System.out.println("          ║ Any unsaved progress will be lost ║");
				System.out.println("          ║ are you sure you want to exit?    ║");
				System.out.println("          ║                                   ║");
				System.out.println("          ║                                   ║");
				System.out.print("          ║            ");
				for(int i=0; i<options.length;i++) {
                	if(sel==i) {
                		System.out.print("\u001B[7m" + options[i] + "\u001B[0m");
                	} else System.out.print(options[i]);
                	System.out.print("     ");
				}
				System.out.println("        ║");
				System.out.println("          ║                                   ║");
				System.out.println("          ╚═══════════════════════════════════╝");
				
				String key = InputHelper.READER.readBinding(InputHelper.KEYMAP);
                switch (key) {
                case "LEFT":
                    sel--;
                    if(sel==-1) sel=options.length-1;
                    break;
                case "RIGHT":
                    sel++;
                    if(sel==options.length) sel=0;
                    break;
                case "ENTER":
                	InputHelper.clearScreen();
                    return sel;
                }
				
			}
		} finally {
			InputHelper.enableTextMode();
		}	
	}
	
	public static ArrayList<Entity> LoadMenu() throws IOException {		
		ArrayList<Entity> Allies = new ArrayList<Entity>();
		File directory = new File(SaveManager.directoryURL);
		SaveManager.CheckSaveDirectory();
		File[] saves = directory.listFiles();
		ArrayList<String> options = new ArrayList<String>();
		options.add("Go back");
		
		for(int i=0; i< saves.length; i++) {
			options.add(saves[i].getName());
		}
		
		int sel = 0;
        
		if(!InputHelper.checkMenuMode()) InputHelper.enableMenuMode();
       
		try {
			while(true) {
				InputHelper.clearScreen();
				
				System.out.println("_________________________");
				System.out.println("Load Menu\n\n");
				
				if (saves != null) {
					for (int i = 0; i < options.size(); i++) {
						if(sel==i) {
							System.out.print("\u001B[7m" + options.get(i) + "\u001B[0m\n");
						} else System.out.println(options.get(i));
					}
					
				} else {
					System.out.println("There's no saves yet.");
					System.out.println("_________________________");
					return null;
				}
				
				System.out.println("_________________________");
				
				String key = InputHelper.READER.readBinding(InputHelper.KEYMAP);
				if(key == null) {
					continue;
				}
                
                switch (key) {
                case "UP":
                    sel--;
                    if(sel==-1) sel=options.size()-1;
                    break;
                case "DOWN":
                    sel++;
                    if(sel==options.size()) sel=0;
                    break;
                case "ENTER":
                	InputHelper.clearScreen();
                	if(sel>0) return SaveManager.LoadGame(saves[(sel-1)].getName());
                	else return null;
                    
                case "ESCAPE":
                	InputHelper.clearScreen();
                    return null;
                default:
                	  break;
                }
				
			}			
		} finally {
			InputHelper.enableTextMode();
		}
	}
	
	public static ArrayList<String> SkillList(Entity entity) {
		ArrayList<String> output = new ArrayList<String>();
		ArrayList<Skill> Skills = entity.getSkills();
		output.add("Skills");
		output.add("_______________________");
		for (Skill s : Skills) {
			output.add(s.getNAME()+" - "+s.getCOST()+"MP");
		}
		
		return output;
	}
	
	public static void Inventory(ArrayList<Entity> Allies) {
	    String[] borders = {"╔","╦","═","╗","║","╠","╣","╚","╩","╝","╬"};
	    ArrayList<Item> Items = Allies.get(0).getInventory();
	    ArrayList<String> result = new ArrayList<>();
	    String tmp = "";
	    int itemWidth = (2 * Config.getSCREEN_WIDTH() / 3) - 1;
	    int infoWidth = (Config.getSCREEN_WIDTH() / 3) - 2;
	    int itemSel = 0, infoSel = 0;
	    boolean menu = true, subMenu = false;

	    if (!InputHelper.checkMenuMode()) InputHelper.enableMenuMode();

	    try {
	        while (menu) {
	            result.clear();
	            InputHelper.clearScreen();

	            String[] descLines = new String[0];
	            if (Items.size() > itemSel) {
	                descLines = ScreenBuffer.formatText(Items.get(itemSel).getDESC(), infoWidth);
	            }

	            ArrayList<String> options = new ArrayList<>();
	            if (Items.size() > itemSel) {
	                options = Item.getItemOptions(Items.get(itemSel));
	            }
	            result.add(borders[0] + borders[2].repeat(itemWidth) + borders[1] + borders[2].repeat(infoWidth) + borders[3]);
	            result.add(borders[4] + "Inventory" + " ".repeat(itemWidth - "Inventory".length()) + borders[4] + "Item info" + " ".repeat(infoWidth - "Item info".length()) + borders[4]);
	            result.add(borders[5] + borders[2].repeat(itemWidth) + borders[10] + borders[2].repeat(infoWidth) + borders[6]);
	            
	            for (int heigth = 0; heigth <= Config.getSCREEN_HEIGTH(); heigth++) {
	                tmp = "";
	                if (heigth == Config.getSCREEN_HEIGTH() - 5) {
	                    tmp += borders[4] + " ".repeat(itemWidth) + borders[5] + borders[2].repeat(infoWidth) + borders[6];
	                } else if (heigth >= Config.getSCREEN_HEIGTH() - 4 && heigth <= Config.getSCREEN_HEIGTH() - 1) {
	                    tmp += borders[4] + " ".repeat(itemWidth) + borders[4];
	                    int optionIndex = heigth - (Config.getSCREEN_HEIGTH() - 4);

	                    if (optionIndex < options.size()) {
	                        String optionLine = options.get(optionIndex);
	                        if (subMenu && infoSel == optionIndex) {
	                            optionLine = "-> " + optionLine;
	                        }
	                        tmp += optionLine + " ".repeat(infoWidth - optionLine.length());
	                    } else {
	                        tmp += " ".repeat(infoWidth);
	                    }

	                    tmp += borders[4];
	                } else if (heigth == Config.getSCREEN_HEIGTH()) {
	                    tmp += borders[7] + borders[2].repeat(itemWidth) + borders[8] + borders[2].repeat(infoWidth) + borders[9];
	                } else {
	                    // Parte del listado de objetos
	                    tmp += borders[4];
	                    if (Items.size() > heigth) {
	                        String itemName = Items.get(heigth).getNAME();
	                        if (heigth == itemSel) itemName = "-> " + itemName;
	                        tmp += itemName + " -X"+ Items.get(heigth).getAMOUNT() + " ".repeat(itemWidth - (itemName + " -X"+ Items.get(heigth).getAMOUNT()).length());
	                    } else {
	                        tmp += " ".repeat(itemWidth);
	                    }
	                    tmp += borders[4];

	                    // Parte del texto de descripción
	                    int descLineIdx = heigth;
	                    if (descLineIdx < descLines.length) {
	                        tmp += descLines[descLineIdx];
	                        tmp += " ".repeat(infoWidth - descLines[descLineIdx].length());
	                    } else {
	                        tmp += " ".repeat(infoWidth);
	                    }

	                    tmp += borders[4];
	                }
	                result.add(tmp);
	            }

	            for (String s : result) System.out.println(s);

	            String key = InputHelper.READER.readBinding(InputHelper.KEYMAP);
	            if (key == null) continue;

	            if (!subMenu) {
	                switch (key) {
	                    case "UP":
	                        itemSel = (itemSel - 1 + Items.size()) % Items.size();
	                        break;
	                    case "DOWN":
	                        itemSel = (itemSel + 1) % Items.size();
	                        break;
	                    case "ENTER":
	                        subMenu = true;
	                        break;
	                    case "ESCAPE":
	                        menu = false;
	                        break;
	                    default:
	                        break;
	                }
	            } else {
	                switch (key) {
	                    case "UP":
	                        infoSel = (infoSel - 1 + options.size()) % options.size();
	                        break;
	                    case "DOWN":
	                        infoSel = (infoSel + 1) % options.size();
	                        break;
	                    case "ENTER":
	                        // To-do: Lógica de opciones
	                    	if(Items.get(itemSel) instanceof Usable) {
	                    		Entity target = Menus.SelectTarget(Allies);
	                    		Items.get(itemSel).Use(target);
	                    	}
	                        break;
	                    case "ESCAPE":
	                        subMenu = false;
	                        break;
	                    default:
	                        break;
	                }
	            }
	        }
	    } finally {
	        InputHelper.enableTextMode();
	    }
	}

	
	public static Entity SelectTarget(ArrayList<Entity> Enemies) {
		Entity Enemy;
		if(Enemies.size()>1) {
			InputHelper.clearScreen();
			int sel = 0;
			
			if(!InputHelper.checkMenuMode()) InputHelper.enableMenuMode();
			
			try {
				while(true) {
					InputHelper.clearScreen();
					
					System.out.println("Select target");
					System.out.println("---------------------");
					for(int i=0; i<Enemies.size();i++) {
	                	if(sel==i) System.out.print("\u001B[7m" + Enemies.get(i).getNAME()+ " - "+ Enemies.get(i).getHP()+ "/"+ Enemies.get(i).getMAX_HP() + "\u001B[0m\n");
	                	else System.out.println(Enemies.get(i).getNAME()+ " - "+ Enemies.get(i).getHP()+ "/"+ Enemies.get(i).getMAX_HP());
					}				
					String key = InputHelper.READER.readBinding(InputHelper.KEYMAP);
	                switch (key) {
	                case "UP":
	                    sel--;
	                    if(sel==-1) sel=Enemies.size()-1;
	                    break;
	                case "DOWN":
	                    sel++;
	                    if(sel==Enemies.size()) sel=0;
	                    break;
	                case "ENTER":
	                	InputHelper.clearScreen();
	                    return Enemies.get(sel);
	                }
					
				}
			} finally {
				InputHelper.enableTextMode();
			}		
			
		} else {
			Enemy = Enemies.get(0);
		}
		
		return Enemy;
	}
	
	public static Entity SelectTarget(ArrayList<Entity> Allies, ArrayList<Entity> Enemies) {
        InputHelper.clearScreen();
        int sel = 0;

        if (!InputHelper.checkMenuMode()) InputHelper.enableMenuMode();

        try {
            while (true) {
                InputHelper.clearScreen();
                System.out.println("Select target");
                System.out.println("---------------------");
                System.out.println("Allies:");
                
                for (int i = 0; i < Allies.size(); i++) {
                	if(sel==i) System.out.print("\u001B[7m" + Allies.get(i).getNAME()+ " - "+ Allies.get(i).getHP()+ "/"+ Allies.get(i).getMAX_HP() + "\u001B[0m\n");
                	else System.out.println(Allies.get(i).getNAME()+ " - "+ Allies.get(i).getHP()+ "/"+ Allies.get(i).getMAX_HP());  
                }
                
                System.out.println("=====================");
                System.out.println("Enemies");
                
                for (int i = 0; i < Enemies.size(); i++) {
                	if(sel==i+Allies.size()) System.out.print("\u001B[7m" + Enemies.get(i).getNAME()+ " - "+ Enemies.get(i).getHP()+ "/"+ Enemies.get(i).getMAX_HP() + "\u001B[0m\n");
                	else System.out.println(Enemies.get(i).getNAME()+ " - "+ Enemies.get(i).getHP()+ "/"+ Enemies.get(i).getMAX_HP());  
                }

                String key = InputHelper.READER.readBinding(InputHelper.KEYMAP);
                switch (key) {
                    case "UP":
                        sel--;
                        if (sel < 0) sel = Allies.size()+Enemies.size() - 1;
                        break;
                    case "DOWN":
                        sel++;
                        if (sel >= Allies.size()+Enemies.size()) sel = 0;
                        break;
                    case "ENTER":
                        InputHelper.clearScreen();
                        if(sel<Allies.size()) return Allies.get(sel);
                        else return Enemies.get(sel-Allies.size());
                }
            }
        } finally {
            InputHelper.enableTextMode();
        }
	}

	
	public static String[] combatMenu(ArrayList<Entity> Allies, ArrayList<Entity> Enemies) {
		ArrayList<String> output = new ArrayList<String>();
		int barWidth = 16;
		
		output.add("\033[0m");
		for(Entity Enemy : Enemies) {
			String tmp = "|";
			for(int i=0; i < ((double)Enemy.getT_COUNT() / 100.0) * 10; i++) tmp += "\033[103m ";
			for(int i=0; i < Math.floor(10 - (((double)Enemy.getT_COUNT() / 100.0) * 10) ); i++) tmp += "\033[47m ";
			tmp+= "\033[0m| ";
			tmp += String.format("%12s: \033[31m%d/%d-HP\033[0m", Enemy.getNAME(), Enemy.getHP(), Enemy.getMAX_HP());
			for(int i=0;i<((double)Enemy.getHP()/(double)Enemy.getMAX_HP())*barWidth;i++) tmp += "\033[42m ";
			for(int i=0;i< Math.floor((((double)Enemy.getMAX_HP()-(double)Enemy.getHP())/(double)Enemy.getMAX_HP())*barWidth); i++) tmp +="\033[41m ";
			tmp += "\033[40m"+"\033[34m"+"  MP "+ Enemy.getMP()+"/"+Enemy.getMAX_MP() +"\033[37m";
			if(Enemy.getMAX_MP()>=1) {
				for(int i=0;i<((double)Enemy.getMP()/(double)Enemy.getMAX_MP())*barWidth;i++) tmp +="\033[44m ";
				for(int i=0;i< Math.floor((((double)Enemy.getMAX_MP()-(double)Enemy.getMP())/(double)Enemy.getMAX_MP())*barWidth); i++) tmp += "\033[47m ";				
			} else for(int i=0;i<barWidth; i++) tmp += "\033[47m ";
			tmp += "\033[0m";
			output.add(tmp);
		}
		output.add("\033[0m");
		for(Entity Ally : Allies) {
			String tmp = "|";
			for(int i=0; i < ((double)Ally.getT_COUNT() / 100.0) * 10; i++) tmp += "\033[103m ";
			for(int i=0; i < Math.floor(10 - (((double)Ally.getT_COUNT() / 100.0) * 10) ); i++) tmp += "\033[47m ";
			tmp+= "\033[0m| ";
			tmp += String.format("%12s: \033[31m%d/%d-HP\033[0m", Ally.getNAME(), Ally.getHP(), Ally.getMAX_HP());
			for(int i=0;i<((double)Ally.getHP()/(double)Ally.getMAX_HP())*barWidth;i++) tmp += "\033[42m ";
			for(int i=0;i<(((double)Ally.getMAX_HP()-(double)Ally.getHP())/(double)Ally.getMAX_HP())*barWidth; i++) tmp += "\033[41m ";
			tmp += "\033[40m"+"\033[34m"+"  MP "+ Ally.getMP()+"/"+Ally.getMAX_MP() +"\033[37m";
			if(Ally.getMAX_MP()>=1) {
				for(int i=0;i<((double)Ally.getMP()/(double)Ally.getMAX_MP())*barWidth;i++) tmp += "\033[44m ";
				for(int i=0;i<(((double)Ally.getMAX_MP()-(double)Ally.getMP())/(double)Ally.getMAX_MP())*barWidth; i++) tmp += "\033[47m ";				
			} else for(int i=0;i<barWidth; i++) tmp += "\033[47m ";
			tmp += "\033[0m";
			output.add(tmp);
		}
		output.add("\033[0m");
				
		return (String[]) output.toArray(new String[0]);
	}

	public static Skill SkillMenu(ArrayList<Skill> skills) {
		int sel = 0;
		
		if(!InputHelper.checkMenuMode()) InputHelper.enableMenuMode();
		
		try {
			while(true) {
				InputHelper.clearScreen();
				
				System.out.println("Skill List");
				System.out.println("---------------------");
				for(int i=0; i<skills.size();i++) {
                	if(sel==i) {
                		System.out.print("\u001B[7m" + skills.get(i).getNAME()+ " - "+ skills.get(i).getCOST()+ "MP" + "\u001B[0m\n");
                	} else System.out.println(skills.get(i).getNAME()+ " - "+ skills.get(i).getCOST()+ "MP");
				}				
				String key = InputHelper.READER.readBinding(InputHelper.KEYMAP);
                switch (key) {
                case "UP":
                    sel--;
                    if(sel==-1) sel=skills.size()-1;
                    break;
                case "DOWN":
                    sel++;
                    if(sel==skills.size()) sel=0;
                    break;
                case "ENTER":
                	InputHelper.clearScreen();
                    return skills.get(sel);
                }
				
			}
		} finally {
			InputHelper.enableTextMode();
		}		
	}

	public static Item ObjectMenu(ArrayList<Item> inventory) {
		int sel = 0;
		
		if(!InputHelper.checkMenuMode()) InputHelper.enableMenuMode();
		
		try {
			while(true) {
				InputHelper.clearScreen();
				
				System.out.println("Object List");
				System.out.println("---------------------");
				for(int i=0; i<inventory.size();i++) {
                	if(sel==i) {
                		System.out.print("\u001B[7m" + inventory.get(i).getNAME()+ " - X"+ inventory.get(i).getAMOUNT()+ "\u001B[0m\n");
                	} else System.out.println(inventory.get(i).getNAME()+ " - X"+ inventory.get(i).getAMOUNT());
				}				
				String key = InputHelper.READER.readBinding(InputHelper.KEYMAP);
                switch (key) {
                case "UP":
                    sel--;
                    if(sel==-1) sel=inventory.size()-1;
                    break;
                case "DOWN":
                    sel++;
                    if(sel==inventory.size()) sel=0;
                    break;
                case "ENTER":
                	InputHelper.clearScreen();
                    return inventory.get(sel);
                }
				
			}
		} finally {
			InputHelper.enableTextMode();
		}
	}
}
