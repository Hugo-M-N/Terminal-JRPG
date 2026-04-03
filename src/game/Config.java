package game;

public class Config {
	private static int APP_MODE = 0; // 0-Terminal / 1-Window / 2-Web
	private static double SCALE = 1;
	private static int TEXT_SPEED=10;
	private static int TERMINAL_WIDTH=83;
	private static int TERMINAL_HEIGTH=24;
	private static int WINDOW_WIDTH=1280;
	private static int WINDOW_HEIGTH=720;
	public static int getAPP_MODE() {
		return APP_MODE;
	}
	public static void setAPP_MODE(int aPP_MODE) {
		APP_MODE = aPP_MODE;
	}
	public static double getSCALE() {
		return SCALE;
	}
	public static void setSCALE(double scale) {
		SCALE=scale;
	}
	public static int getTEXT_SPEED() {
		return TEXT_SPEED;
	}
	public static void setTEXT_SPEED(int tEXT_SPEED) {
		TEXT_SPEED = tEXT_SPEED;
	}
	public static int getTERMINAL_WIDTH() {
		return TERMINAL_WIDTH;
	}
	public static void setTERMINAL_WIDTH(int tERMINAL_WIDTH) {
		TERMINAL_WIDTH = tERMINAL_WIDTH;
	}
	public static int getTERMINAL_HEIGTH() {
		return TERMINAL_HEIGTH;
	}
	public static void setTERMINAL_HEIGTH(int tERMINAL_HEIGTH) {
		TERMINAL_HEIGTH = tERMINAL_HEIGTH;
	}
	public static int getWINDOW_WIDTH() {
		return WINDOW_WIDTH;
	}
	public static void setWINDOW_WIDTH(int wINDOW_WIDTH) {
		WINDOW_WIDTH = wINDOW_WIDTH;
	}
	public static int getWINDOW_HEIGTH() {
		return WINDOW_HEIGTH;
	}
	public static void setWINDOW_HEIGTH(int wINDOW_HEIGTH) {
		WINDOW_HEIGTH = wINDOW_HEIGTH;
	}
	
}
