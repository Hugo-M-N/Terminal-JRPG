package game.entity;

import java.util.ArrayList;

import game.EntityClass;
import game.object.Object;
import game.skill.Skill;

public class Entity {

	// Variables
	String NAME;
	int LVL;
	int XP;
	EntityClass CLASS;
	int HP;
	int MAX_HP;
	int MP;
	int MAX_MP;
	int STR;
	int MAG;
	int DEF;
	int DEX;
	int T_COUNT;
	int ACCION;
	Skill EFFECT = new Skill("",0,0);
	int EF_COUNT;
	boolean isDef;
	ArrayList<Skill> Skills = new ArrayList<>();
	ArrayList<Object> Inventory = new ArrayList<>();
	
	// Functions
	
	public void addSkill(Skill SKILL) {
		this.Skills.add(SKILL);
	}
	
	public void addToInventory(Object OBJ) {
		for(Object item : Inventory) {
			if (item.getNAME().equals(OBJ.getNAME())) {
				item.setAMOUNT(item.getAMOUNT()+1);
				return;
			}
		}
		
		Inventory.add(OBJ);
		
	}
	
	public void stats() {
		// Clear terminal
        System.out.print("\033[H\033[2J");
        System.out.flush();
		
		System.out.println("____________________");
		System.out.println("Stats\n");
		System.out.println("Name: "+ this.NAME);
		System.out.println("Lvl: "+ this.LVL);
		System.out.println("HP "+ this.HP+"/"+this.MAX_HP);
		System.out.println("MP "+ this.MP+"/"+this.MAX_MP);
		System.out.println("STR: "+ this.STR);
		System.out.println("MAG: "+ this.MAG);
		System.out.println("DEF: "+ this.DEF);
		System.out.println("DEX: "+ this.DEX);
		System.out.println("____________________");
	}
	
	// Getters & Setters
	
	public String getNAME() {
		return NAME;
	}

	public void setNAME(String NAME) {
		this.NAME = NAME;
	}

	public int getLVL(){
		return LVL;
	}

	public void setLVL(int LVL) {
		this.LVL = LVL;
	}

	public int getXP() {
		return XP;
	}

	public void setXP(int XP) {
		this.XP = XP;
	}

	public EntityClass getCLASS() {
		return CLASS;
	}

	public void setCLASS(EntityClass CLASS) {
		this.CLASS = CLASS;
	}

	public int getHP() {
		return HP;
	}

	public void setHP(int HP) {
		this.HP = HP;
	}

	public int getMAX_HP() {
		return MAX_HP;
	}

	public void setMAX_HP(int MAX_HP) {
		this.MAX_HP = MAX_HP;
	}

	public int getMP() {
		return MP;
	}

	public void setMP(int MP) {
		this.MP = MP;
	}

	public int getMAX_MP() {
		return MAX_MP;
	}

	public void setMAX_MP(int MAX_MP) {
		this.MAX_MP = MAX_MP;
	}

	public int getSTR() {
		return STR;
	}

	public void setSTR(int STR) {
		this.STR = STR;
	}

	public int getMAG() {
		return MAG;
	}

	public void setMAG(int MAG) {
		this.MAG = MAG;
	}

	public int getDEF() {
		return DEF;
	}

	public void setDEF(int DEF) {
		this.DEF = DEF;
	}

	public int getDEX() {
		return DEX;
	}

	public void setDEX(int DEX) {
		this.DEX = DEX;
	}

	public int getT_COUNT() {
		return T_COUNT;
	}

	public void setT_COUNT(int T_COUNT) {
		this.T_COUNT = T_COUNT;
	}

	public int getACCION() {
		return ACCION;
	}

	public void setACCION(int ACCION) {
		this.ACCION = ACCION;
	}

	public Skill getEFFECT() {
		return EFFECT;
	}

	public void setEFFECT(Skill EFFECT) {
		this.EFFECT = EFFECT;
	}

	public int getEF_COUNT() {
		return EF_COUNT;
	}

	public void setEF_COUNT(int EF_COUNT) {
		this.EF_COUNT = EF_COUNT;
	}

	public boolean isDef() {
		return isDef;
	}

	public void setDef(boolean isDef) {
		this.isDef = isDef;
	}

	public ArrayList<Skill> getSkills() {
		return Skills;
	}

	public void setSkills(ArrayList<Skill> Skills) {
		this.Skills = Skills;
	}

	public ArrayList<Object> getInventory() {
		return Inventory;
	}

	public void setInventory(ArrayList<Object> Inventory) {
		this.Inventory = Inventory;
	}
	
	// Constructors
	
	public Entity(String NAME, EntityClass CLASS, int LVL) {
		
	}

	public Entity(String NAME, int LVL, int MAX_HP, int MAX_MP, int STR, int MAG, int DEF, int DEX) {
		this.NAME = NAME;
		this.LVL = LVL;
		this.MAX_HP = MAX_HP;
		this.HP = MAX_HP;
		this.MAX_MP = MAX_MP;
		this.MP = MAX_MP;
		this.STR = STR;
		this.MAG = MAG;
		this.DEF = DEF;
		this.DEX = DEX;
	}

}
