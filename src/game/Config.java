package game;

public class Config {
	private static int TEXT_SPEED=30;
	
	public static void setTextSpeed(int ms) {
		TEXT_SPEED=ms;
	}
	
	public static int getTextSpeed() {
		return TEXT_SPEED;
	}
}
