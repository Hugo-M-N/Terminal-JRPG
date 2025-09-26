package game;

public class Config {
	private static int TEXT_SPEED=10;
	private static int SCREEN_WIDTH=83;
	private static int SCREEN_HEIGTH=24;
	
	public static void setTextSpeed(int ms) {
		TEXT_SPEED=ms;
	}
	
	public static int getTextSpeed() {
		return TEXT_SPEED;
	}

	public static int getSCREEN_WIDTH() {
		return SCREEN_WIDTH;
	}

	public static void setSCREEN_WIDTH(int width) {
		SCREEN_WIDTH = width;
	}

	public static int getSCREEN_HEIGTH() {
		return SCREEN_HEIGTH;
	}

	public static void setSCREEN_HEIGTH(int heigth) {
		SCREEN_HEIGTH = heigth;
	}
}
