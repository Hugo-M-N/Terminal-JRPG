package game.entity;

import java.util.HashMap;
import java.util.List;

import game.skill.Skill;

public class EntityClass {
	private String ID;
	private String NAME;
	private int STR;	
	private int MAG;
	private int DEF;
	private int DEX;
	private double HealthFactor;
	private double MagicFactor;
	private HashMap<Integer,Skill> Skills;
	private int[] sprites = new int[0];

	public EntityClass(String id, String name, int str, int mag, int def, int dex, double healthFactor, double magicFactor, HashMap<Integer,Skill> skills) {
		ID=id;
		NAME=name;
		STR=str;
		MAG=mag;
		DEF=def;
		DEX=dex;
		HealthFactor=healthFactor;
		MagicFactor=magicFactor;
		Skills=skills;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getNAME() {
		return NAME;
	}

	public void setNAME(String nAME) {
		NAME = nAME;
	}

	public int getSTR() {
		return STR;
	}

	public void setSTR(int sTR) {
		STR = sTR;
	}

	public int getMAG() {
		return MAG;
	}

	public void setMAG(int mAG) {
		MAG = mAG;
	}

	public int getDEF() {
		return DEF;
	}

	public void setDEF(int dEF) {
		DEF = dEF;
	}

	public int getDEX() {
		return DEX;
	}

	public void setDEX(int dEX) {
		DEX = dEX;
	}

	public double getHealthFactor() {
		return HealthFactor;
	}

	public void setHealthFactor(double healthFactor) {
		HealthFactor = healthFactor;
	}

	public double getMagicFactor() {
		return MagicFactor;
	}

	public void setMagicFactor(double magicFactor) {
		MagicFactor = magicFactor;
	}

	public HashMap<Integer,Skill> getSkills() {
		return Skills;
	}

	public void setSkills(HashMap<Integer,Skill> skills) {
		Skills = skills;
	}

	public int[] getSprites() { return sprites; }
	public void setSprites(int[] sprites) { this.sprites = sprites; }
}
