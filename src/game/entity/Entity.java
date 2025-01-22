package game.entity;

import java.util.ArrayList;

import game.object.Object;
import game.skill.Skill;

public class Entity {

	// Variables
	String NAME;
	int LVL;
	int XP;
	int LvlXP;
	int GOLD;
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
	Skill EFFECT = new Skill("",0,null,0);
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
	
	public void addXP(int XP) {
		this.XP += XP;
		 this.LvlXP = (int) (this.LVL + 9 + (Math.pow(this.LVL, (this.LVL*0.05))));
		while(this.XP>=LvlXP) {
			this.XP-=LvlXP;
			LvlUp();
		}
	}
	
	public void LvlUp() {
		this.LVL++;
		System.out.printf("%s got lvl %d!\n",this.NAME, this.LVL);
		int points= (int) (Math.random()*3)+1;
		while(points>0) {
			int Stat = (int) (Math.random()*12);
			if(this.CLASS==EntityClass.WARRIOR) {
				if(Stat<=4) {
					this.STR++;
					System.out.printf("%s's STR is now %d.\n", this.NAME, this.STR);
				} else if(Stat<6) {
					this.MAG++;
					System.out.printf("%s's MAG is now %d.\n", this.NAME, this.MAG);
				} else if(Stat<=8) {
					this.DEF++;
					System.out.printf("%s's DEF is now %d.\n", this.NAME, this.DEF);
				} else {
					this.DEX++;
					System.out.printf("%s's DEX is now %d.\n", this.NAME, this.DEX);
				}
				
				this.MAX_HP = (int) (this.DEF * 0.25 * LVL);
				this.HP = this.MAX_HP;
				this.MAX_MP = (int) (this.MAG * 0.1 * LVL);
				this.MP = this.MAX_MP;
				
			} else if(this.CLASS==EntityClass.MAGE) {
				if(Stat<=1) {
					this.STR++;
					System.out.printf("%s's STR is now %d.\n", this.NAME, this.STR);
				} else if(Stat<7) {
					this.MAG++;
					System.out.printf("%s's MAG is now %d.\n", this.NAME, this.MAG);
				} else if(Stat<=9) {
					this.DEF++;
					System.out.printf("%s's DEF is now %d.\n", this.NAME, this.DEF);
				} else {
					this.DEX++;
					System.out.printf("%s's DEX is now %d.\n", this.NAME, this.DEX);
				}
				
				this.MAX_HP = (int) (this.DEF * 0.1 * LVL);
				this.HP = this.MAX_HP;
				this.MAX_MP = (int) (this.MAG * 0.25 * LVL);
				this.MP = this.MAX_MP;
				
			} else if(this.CLASS==EntityClass.CLERIC) {
				if(Stat<=1) {
					this.STR++;
					System.out.printf("%s's STR is now %d.\n", this.NAME, this.STR);
				} else if(Stat<6) {
					this.MAG++;
					System.out.printf("%s's MAG is now %d.\n", this.NAME, this.MAG);
				} else if(Stat<=10) {
					this.DEF++;
					System.out.printf("%s's DEF is now %d.\n", this.NAME, this.DEF);
				} else {
					this.DEX++;
					System.out.printf("%s's DEX is now %d.\n", this.NAME, this.DEX);
				}
				
				this.MAX_HP = (int) (this.DEF * 0.15 * LVL);
				this.HP = this.MAX_HP;
				this.MAX_MP = (int) (this.MAG * 0.2 * LVL);
				this.MP = this.MAX_MP;
				
			} else if(this.CLASS==EntityClass.ROGUE) {
				if(Stat<=4) {
					this.STR++;
					System.out.printf("%s's STR is now %d.\n", this.NAME, this.STR);
				} else if(Stat<6) {
					this.MAG++;
					System.out.printf("%s's MAG is now %d.\n", this.NAME, this.MAG);
				} else if(Stat<=7) {
					this.DEF++;
					System.out.printf("%s's DEF is now %d.\n", this.NAME, this.DEF);
				} else {
					this.DEX++;
					System.out.printf("%s's DEX is now %d.\n", this.NAME, this.DEX);
				}
				
				this.MAX_HP = (int) (this.DEF * 0.15 * LVL);
				this.HP = this.MAX_HP;
				this.MAX_MP = (int) (this.MAG * 0.15 * LVL);
				this.MP = this.MAX_MP;
				
			}
			if(!(this.MAX_HP>0)) {
				this.MAX_HP++;
				this.HP = this.MAX_HP;
			}
			points--;
		}
		/*
		 * To-do:
		 * -Add Skills when a certain Lvl its achieved
		 * 
		 * */
	}
	
	public void stats() {
		// Clear terminal
        System.out.print("\033[H\033[2J");
        System.out.flush();
		
		System.out.println("\033[40m\033[37m_________________________");
		System.out.println("Stats\n");
		System.out.println("Name: "+ this.NAME);
		System.out.println("Class: "+ this.CLASS);
		System.out.println("Lvl: "+ this.LVL+ "   " + this.XP + "/" + this.LvlXP + "XP" + "   \033[33mG:"+this.GOLD);
		System.out.print("\033[31mHP "+ this.HP+"/"+this.MAX_HP + "   \033[34mMP "+ this.MP+"/"+this.MAX_MP + "\n\033[37m");
		System.out.println("STR: "+ this.STR);
		System.out.println("MAG: "+ this.MAG);
		System.out.println("DEF: "+ this.DEF);
		System.out.println("DEX: "+ this.DEX);
		System.out.println("_________________________\033[0m");
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
	
	public int getGOLD() {
		return GOLD;
	}
	
	public void setGOLD(int GOLD) {
		this.GOLD = GOLD;
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

	public void setIsDef(boolean isDef) {
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
		this.NAME = NAME;
		this.LVL = LVL;
		this.XP=0;
		this.LvlXP = (int) (this.LVL + 9 + (Math.pow(this.LVL, (this.LVL*0.05))));
		this.CLASS = CLASS;
		this.GOLD = 0;
		
		
		int LVL_P = 12 + LVL;
		switch(CLASS) {
			
			case WARRIOR:
				// Main stats: DEF & STR
				this.STR = (int) ((4.0/12) * LVL_P);
				this.MAG = (int) ((1.0/12) * LVL_P);
				this.DEF = (int) ((5.0/12) * LVL_P);
				this.DEX = (int) ((2.0/12) * LVL_P);
				this.MAX_HP = (int) (this.DEF * 0.25 * LVL);
				this.HP = this.MAX_HP;
				this.MAX_MP = (int) (this.MAG * 0.1 * LVL);
				this.MP = this.MAX_MP;
				
				break;
			case MAGE:
				// Main stats: MAG & DEX
				this.STR = (int) ((1.0/12) * LVL_P);
				this.MAG = (int) ((6.0/12) * LVL_P);
				this.DEF = (int) ((2.0/12) * LVL_P);
				this.DEX = (int) ((3.0/12) * LVL_P);
				this.MAX_HP = (int) (this.DEF * 0.1 * LVL);
				this.HP = this.MAX_HP;
				this.MAX_MP = (int) (this.MAG * 0.25 * LVL);
				this.MP = this.MAX_MP;
				break;
			case CLERIC:
				// Main stats: MAG & DEF
				this.STR = (int) ((1.0/12) * LVL_P);
				this.MAG = (int) ((5.0/12) * LVL_P);
				this.DEF = (int) ((4.0/12) * LVL_P);
				this.DEX = (int) ((2.0/12) * LVL_P);
				this.MAX_HP = (int) (this.DEF * 0.15 * LVL);
				this.HP = this.MAX_HP;
				this.MAX_MP = (int) (this.MAG * 0.2 * LVL);
				this.MP = this.MAX_MP;
				break;
			case ROGUE:
				// Main stats: STR & DEX
				this.STR = (int) ((4.0/12) * LVL_P);
				this.MAG = (int) ((2.0/12) * LVL_P);
				this.DEF = (int) ((1.0/12) * LVL_P);
				this.DEX = (int) ((5.0/12) * LVL_P);
				this.MAX_HP = (int) (this.DEF * 0.15 * LVL);
				this.HP = this.MAX_HP;
				this.MAX_MP = (int) (this.MAG * 0.15 * LVL);
				this.MP = this.MAX_MP;
				break;
		}
		if(!(this.MAX_HP>0)) {
			this.MAX_HP++;
			this.HP = this.MAX_HP;
		}
		
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
		this.GOLD = 0;
	}

}
