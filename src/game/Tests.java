package game;

import java.util.ArrayList;
import java.util.Scanner;

import game.utils.InputHelper;

public class Tests {
	static Scanner sc = new Scanner(System.in);
	public static void main(String[] args) throws InterruptedException {
//		String message = "Test de impresión de texto animado\n"
//				+ "con varias lineas para probar tipos de texto.\n";
//		for(char c : message.toCharArray()) {
//			System.out.print(c);
//			Thread.sleep(75);
//		}
		
//		String testTamaño = "\033[41m  \033[0m";
//		for(int y=0; y<25;y++) {
//			for(int x=0; x<50; x++) {
//				System.out.print(testTamaño);
//			}
//			System.out.println();
//		}
		
//		InputHelper.enableMenuMode();
//		while(true) {
//			InputHelper.clearScreen();
//			
//			String ventana="";
//			ventana+= "╔" + "═".repeat(77) + "╗\n";
//			ventana+=("║" + " ".repeat(77) + "║\n").repeat(18);
//			ventana+= "╠" + "═".repeat(77) + "╣\n";
//			ventana+="║ " +(" ╔"+"═".repeat(11)+"╗ ").repeat(5)+" ║\n";
//			ventana+="║ " +(" ║ "+"Inventory"+" ║ ").repeat(5)+" ║\n";
//			ventana+="║ " +(" ╚" + "═".repeat(11) + "╝ ").repeat(5)+" ║\n";
//			ventana+= "╚" + "═".repeat(77) + "╝\n";
//			
//			System.out.println(ventana);
//			
//			String message ="Texto de prueba";
//			System.out.print("\033[2;3H");
//			InputHelper.printMessage(message);
//			System.out.print("\033[3;3H");
//			InputHelper.printMessage(message);
//			System.out.print("\033[4;3H");
//			InputHelper.printMessage(message);
//			
//		}
		
		ScreenBuffer buffer = new ScreenBuffer();
		
		String[] buffer2 = buffer.getScreenBuffer();
		char[] ignore = {'╔','═','╗','║','╚','╝',' '};
		int maxLine = 1;
		int i=1;
		while(true) {
			InputHelper.clearScreen();
			InputHelper.enableMenuMode();
			for(int j=0; j<buffer2.length;j++) {
				if(j==buffer2.length-2) System.out.println("║" + " ".repeat(buffer.getScreenWidth()) + "║");
				else System.out.println(buffer2[j]);
			}
			
			buffer.addToBuffer("Texto de prueba " + i + " maxLine: "+ maxLine);
			buffer2 = buffer.getScreenBuffer();
			
			i++;
			System.out.print("\033[1;1H");
			for(int j=0;j<buffer.getScreenBuffer().length;j++){
				for(char c : buffer.getScreenBuffer()[j].toCharArray()) {
					System.out.print(c);
					boolean ig = false;
					for(char x : ignore) {
						if(c==x) ig = true;
					}
					if(!ig && j==maxLine) Thread.sleep(25);
				}
				System.out.println();
			}			
			maxLine++;
			if(maxLine==buffer.getScreenHeigth()-1) maxLine--;
			Thread.sleep(25);
		}
		
//		ScreenBuffer buffer = new ScreenBuffer();
//		buffer.printAnimatedMessage("You look around, you don't recognize anything. Actually, you don't know "
//				+ "anything, you ''understand'' basic concepts like yourself, your name and how some things are called, "
//				+ "but you can't remember more than a few minutes ago when you opened your eyes. So you don't know from "
//				+ "where that knoledge came from. You start to walk and two things caught your eye, a forest and a small "
//				+ "village.");
//
//		String a = "asdsafd ascx 13e3e 09ehrfnuej 1ikh31o2 j1brfi jbkjbjk b j1 b jbjbnjkm";
//		String[] parts = a.split(" ");
//		String tmp="";
//		System.out.print("ancho: ");
//		int MaxWidth = sc.nextInt();
//		for(String s : parts) {
//			if((tmp+s).length() < MaxWidth+1) {
//				tmp += s + " ";
//			} else if ((tmp+s).length() > MaxWidth) {
//				System.out.println(tmp);
//				tmp = s + " ";
//			}
//		}
//		if(!tmp.isEmpty()) System.out.println(tmp);
	}

}
