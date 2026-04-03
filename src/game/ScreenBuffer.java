package game;

import java.util.ArrayList;

import game.utils.InputHelper;

public class ScreenBuffer {
	private static String[] BUFFER= new String[Config.getTERMINAL_HEIGTH()];
	private static String[] TMP_BUFFER = new String[Config.getTERMINAL_HEIGTH()];
	private static String[] ANSI = {"\033[0m","\033[30m","\033[31m","\033[32m","\033[33m","\033[34m",
			"\033[35m","\033[36m","\033[37m","\033[40m","\033[41m","\033[42m","\033[43m","\033[44m"
			,"\033[45m","\033[46m","\033[47m","\033[103m"};
	 
	public static String[] formatText(String input, int formatWidth) {
		ArrayList<String> parts = new ArrayList<String>();
		if(input.length()>formatWidth-1) {
			String[] words = input.split(" ");
			String tmp="";
			for(String w : words) {
				if((tmp+w).length()+1 < formatWidth) {
					tmp += w + " ";
				} else if ((tmp+w+1).length() > formatWidth-1) {
					parts.add(tmp);
					tmp = w + " ";
				}
			}
			
			if(!tmp.isEmpty()) parts.add(tmp);
		} else return new String[] {input};
		
		
			
		String [] result = new String[parts.size()];
		for(int i=0; i<parts.size();i++) result[i]=parts.get(i);
		return result;
	}
	
	private int calculateRow() {
		int row=Config.getTERMINAL_HEIGTH()-2;
		while(BUFFER[row].equals("║" + " ".repeat(Config.getTERMINAL_WIDTH()) + "║")) {
			if(row==1) break;
			else row--;
		}
		
		if(!BUFFER[row].equals("║" + " ".repeat(Config.getTERMINAL_WIDTH()) + "║") && row!=Config.getTERMINAL_HEIGTH()-2) row++;
		return row;
	}
	
	public void updateBuffer(String input) {
		String tmp = input;
		for(String s : ANSI) tmp = tmp.replace(s, "");
		
		int row= calculateRow();
		
		if(row==0) row=1;
		else if(row==Config.getTERMINAL_HEIGTH()-2) {
			BUFFER[row]="║" + input +" ".repeat(Config.getTERMINAL_WIDTH()-tmp.length()) + "║";
			for(int i=1;i<Config.getTERMINAL_HEIGTH()-2;i++) BUFFER[i]=BUFFER[i+1];
		} else BUFFER[row]="║" + input +" ".repeat(Config.getTERMINAL_WIDTH()-tmp.length()) + "║";
	}
	
	public void addToBuffer(String input) {
		String[] formated = formatText(input, Config.getTERMINAL_WIDTH());
		for(String s : formated) updateBuffer(s);
			
	}
	
	
	public void printAnimatedMessage(String message) throws InterruptedException {
	    InputHelper.enableMenuMode();

	    String[] formattedLines = formatText(message, Config.getTERMINAL_WIDTH());

	    TMP_BUFFER = getScreenBuffer();
	    
	    for (String line : formattedLines) {
	        
	        System.out.print("\033[1;1H");

	        for (int i = 0; i < TMP_BUFFER.length; i++) {
	            if ((i == calculateRow()) && (i<Config.getTERMINAL_HEIGTH())) {
	                System.out.print("║");
	                for (int j = 0; j < Config.getTERMINAL_WIDTH(); j++) {
	                    if (j < line.length()) {
	                    	// Prints
	                        System.out.print(line.charAt(j));
	                        Thread.sleep(Config.getTEXT_SPEED());
	                    } else {
	                    	// Fill with empty space until border
	                        System.out.print(" ");
	                    }
	                }
	                System.out.println("║");
	            } else {
	                System.out.println(TMP_BUFFER[i]);
	            }
	        }

	        updateBuffer(line);
	    }

	    Thread.sleep(Config.getTEXT_SPEED());
	    InputHelper.enableTextMode();
	}

	
	
	public static String[] getScreenBuffer() {
		return BUFFER;
	}
	
	public void clearBuffer() {
		if(BUFFER.length!=Config.getTERMINAL_HEIGTH()) BUFFER = new String[Config.getTERMINAL_HEIGTH()];
		BUFFER[0]="╔" + "═".repeat(Config.getTERMINAL_WIDTH()) + "╗";
		for(int i=1;i<Config.getTERMINAL_HEIGTH()-1;i++) {
			BUFFER[i]="║" + " ".repeat(Config.getTERMINAL_WIDTH()) + "║";
		}
		BUFFER[Config.getTERMINAL_HEIGTH()-1]="╚" + "═".repeat(Config.getTERMINAL_WIDTH()) + "╝";
		TMP_BUFFER=BUFFER;
	}
	
	public ScreenBuffer() {
		if(BUFFER.length!=Config.getTERMINAL_HEIGTH()) BUFFER = new String[Config.getTERMINAL_HEIGTH()];
		BUFFER[0]="╔" + "═".repeat(Config.getTERMINAL_WIDTH()) + "╗";
		for(int i=1;i<Config.getTERMINAL_HEIGTH()-1;i++) {
			BUFFER[i]="║" + " ".repeat(Config.getTERMINAL_WIDTH()) + "║";
		}
		BUFFER[Config.getTERMINAL_HEIGTH()-1]="╚" + "═".repeat(Config.getTERMINAL_WIDTH()) + "╝";
		TMP_BUFFER=BUFFER;
	}
}
