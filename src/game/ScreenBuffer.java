package game;

import java.util.ArrayList;

import game.utils.InputHelper;

public class ScreenBuffer {
	private static int LAST_LINE=1;
	private static int SCREEN_WIDTH=78;
	private static int SCREEN_HEIGTH=24;
	private static String[] BUFFER= new String[SCREEN_HEIGTH];
	private static String[] TMP_BUFFER = new String[SCREEN_HEIGTH];
	private static char[] IGNORED = {'╔','═','╗','║','╚','╝',' '};
	private static String[] ANSI = {"\033[0m","\033[30m","\033[31m","\033[32m","\033[33m","\033[34m",
			"\033[35m","\033[36m","\033[37m","\033[40m","\033[41m","\033[42m","\033[43m","\033[44m"
			,"\033[45m","\033[46m","\033[47m"};
	 
	private String[] formatText(String input) {
		ArrayList<String> parts = new ArrayList<String>();
		if(input.length()>SCREEN_WIDTH-1) {
			String[] words = input.split(" ");
			String tmp="";
			for(String w : words) {
				if((tmp+w).length() < SCREEN_WIDTH) {
					tmp += w + " ";
				} else if ((tmp+w).length() > SCREEN_WIDTH-1) {
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
	
	public void addToBuffer(String input) {
		String[] formated = formatText(input);
		for(String s : formated) updateBuffer(s);
		LAST_LINE++;
		if(LAST_LINE==getScreenHeigth()-1) LAST_LINE--;
			
	}
	
	public void updateBuffer(String input) {
		int row=SCREEN_HEIGTH-2;
		while(BUFFER[row].equals("║" + " ".repeat(SCREEN_WIDTH) + "║")) {
			if(row==1) break;
			else row--;
		}
		
		if(!BUFFER[row].equals("║" + " ".repeat(SCREEN_WIDTH) + "║") && row!=SCREEN_HEIGTH-2) row++;
		
		String tmp = input;
		for(String s : ANSI) tmp = tmp.replace(s, "");
		
		if(row==0) row=1;
		else if(row==SCREEN_HEIGTH-2) {
			for(int i=1;i<SCREEN_HEIGTH-1;i++) BUFFER[i]=BUFFER[i+1];
			BUFFER[row]="║" + input +" ".repeat(SCREEN_WIDTH-tmp.length()) + "║";
		} else BUFFER[row]="║" + input +" ".repeat(SCREEN_WIDTH-tmp.length()) + "║";
	}
	
	public void printAnimatedMessage(String message) throws InterruptedException {
		int spd = Config.getTextSpeed();
	    InputHelper.enableMenuMode();

	    String[] formattedLines = formatText(message);

	    for (String line : formattedLines) {
	        updateBuffer(line);
	        TMP_BUFFER = getScreenBuffer();

	        System.out.print("\033[1;1H");

	        for (int i = 0; i < TMP_BUFFER.length; i++) {
	            if (i == LAST_LINE - 1) {
	                System.out.print("║");
	                for (int j = 0; j < getScreenWidth(); j++) {
	                    if (j < line.length()) {
	                        System.out.print(line.charAt(j));
	                        Thread.sleep(spd);
	                    } else {
	                        System.out.print(" ");
	                    }
	                }
	                System.out.println("║");
	            } else {
	                System.out.println(TMP_BUFFER[i]);
	            }
	        }

	        LAST_LINE++;
	        if (LAST_LINE == getScreenHeigth() - 1) LAST_LINE--;
	    }

	    Thread.sleep(50);
	    InputHelper.enableTextMode();
	}

	
	
	public String[] getScreenBuffer() {
		return BUFFER;
	}
	
	public void SetScreenWidth(int width) {
		SCREEN_WIDTH=width;
	}
	
	public int getScreenWidth() {
		return SCREEN_WIDTH;
	}
	
	public void SetScreenHeigth(int heigth) {
		SCREEN_HEIGTH=heigth;
	}
	
	public int getScreenHeigth() {
		return SCREEN_HEIGTH;
	}
	
	public void clearBuffer() {
		BUFFER[0]="╔" + "═".repeat(SCREEN_WIDTH) + "╗";
		for(int i=1;i<SCREEN_HEIGTH-1;i++) {
			BUFFER[i]="║" + " ".repeat(SCREEN_WIDTH) + "║";
		}
		BUFFER[SCREEN_HEIGTH-1]="╚" + "═".repeat(SCREEN_WIDTH) + "╝";
		TMP_BUFFER=BUFFER;
		LAST_LINE=1;
	}
	
	public ScreenBuffer() {
		BUFFER[0]="╔" + "═".repeat(SCREEN_WIDTH) + "╗";
		for(int i=1;i<SCREEN_HEIGTH-1;i++) {
			BUFFER[i]="║" + " ".repeat(SCREEN_WIDTH) + "║";
		}
		BUFFER[SCREEN_HEIGTH-1]="╚" + "═".repeat(SCREEN_WIDTH) + "╝";
		TMP_BUFFER=BUFFER;
		LAST_LINE=1;
	}
}
