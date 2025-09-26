package game.entity;

import java.util.ArrayList;

import game.item.Accesory;
import game.item.Armor;
import game.item.Item;
import game.item.Weapon;
import game.skill.DamageType;
import game.skill.Skill;
import game.skill.SkillManager;
import game.utils.InputHelper;
import game.zone.Quest;

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
	Skill EFFECT = new Skill("", "", 0, 0, 0, null, "");
	int EF_COUNT;
	boolean isDef;
	ArrayList<Skill> Skills = new ArrayList<>();
	ArrayList<Item> Inventory = new ArrayList<>();
	ArrayList<Quest> Quests = new ArrayList<>();
	
	//Equipment
	Weapon weapon;
	Armor armor;
	Accesory accesory;
	
	// Functions
	
	public void addSkill(Skill SKILL) {if(!Skills.contains(SKILL)) Skills.add(SKILL);}
	public void removeSkill(Skill SKILL) { Skills.remove(SKILL);}
	
	public String addToInventory(Item OBJ) {
		for(Item item : Inventory) {
			if (item.getNAME().equals(OBJ.getNAME())) {
				item.setAMOUNT(item.getAMOUNT()+OBJ.getAMOUNT());
				return String.format("%s obtained %d %s", this.getNAME(),OBJ.getAMOUNT(), OBJ.getNAME());
			}
		}
		
		Inventory.add(OBJ);
		return String.format("%s obtained %d %s", this.getNAME(),OBJ.getAMOUNT(), OBJ.getNAME());
		
	}
	
	public String[] addXP(int XP) {
		String[] result = {""};
		this.XP += XP;
		 LvlXP = (int) (LVL + 9 + (Math.pow(LVL, (LVL*0.05))));
		while(this.XP>=LvlXP) {
			this.XP-=LvlXP;
			result = LvlUp();
		}
		
		return result;
	}
	
	public String[] LvlUp() {
		ArrayList<String> result = new ArrayList<String>();
		LVL++;
		result.add(String.format("%s got lvl %d!",NAME, LVL));
		int points= (int) (Math.random()*3)+1;
		while(points>0) {
			int Stat = (int) (Math.random()*12);
			if(CLASS==EntityClass.WARRIOR) {
				if(Stat<=4) {
					STR++;
					result.add(String.format("%s's STR is now %d.", NAME, STR));
				} else if(Stat<6) {
					MAG++;
					result.add(String.format("%s's MAG is now %d.", NAME, MAG));
				} else if(Stat<=8) {
					DEF++;
					result.add(String.format("%s's DEF is now %d.", NAME, DEF));
				} else {
					DEX++;
					result.add(String.format("%s's DEX is now %d.", NAME, DEX));
				}
				
				MAX_HP = (int) (DEF * 0.40 * LVL) * 6;
				HP = MAX_HP;
				MAX_MP = (int) (MAG * 0.1 * LVL) * 2;
				MP = MAX_MP;
				
			} else if(CLASS==EntityClass.MAGE) {
				if(Stat<=1) {
					STR++;
					result.add(String.format("%s's STR is now %d.", NAME, STR));
				} else if(Stat<7) {
					MAG++;
					result.add(String.format("%s's MAG is now %d.", NAME, MAG));
				} else if(Stat<=9) {
					DEF++;
					result.add(String.format("%s's DEF is now %d.", NAME, DEF));
				} else {
					DEX++;
					result.add(String.format("%s's DEX is now %d.", NAME, DEX));
				}
				
				MAX_HP = (int) (DEF * 0.15 * LVL) * 6;
				HP = MAX_HP;
				MAX_MP = (int) (MAG * 0.25 * LVL) * 2;
				if(MAX_MP<25) MAX_MP = 25;
				MP = MAX_MP;
				
			} else if(CLASS==EntityClass.CLERIC) {
				if(Stat<=1) {
					STR++;
					result.add(String.format("%s's STR is now %d.", NAME, STR));
				} else if(Stat<6) {
					MAG++;
					result.add(String.format("%s's MAG is now %d.", NAME, MAG));
				} else if(Stat<=10) {
					DEF++;
					result.add(String.format("%s's DEF is now %d.", NAME, DEF));
				} else {
					DEX++;
					result.add(String.format("%s's DEX is now %d.", NAME, DEX));
				}
				
				MAX_HP = (int) (DEF * 0.25 * LVL) * 6;
				HP = MAX_HP;
				MAX_MP = (int) (MAG * 0.2 * LVL) * 2;
				if(MAX_MP<20) MAX_MP = 20;
				MP = MAX_MP;
				
			} else if(CLASS==EntityClass.ROGUE) {
				if(Stat<=4) {
					STR++;
					result.add(String.format("%s's STR is now %d.", NAME, STR));
				} else if(Stat<6) {
					MAG++;
					result.add(String.format("%s's MAG is now %d.", NAME, MAG));
				} else if(Stat<=7) {
					DEF++;
					result.add(String.format("%s's DEF is now %d.", NAME, DEF));
				} else {
					DEX++;
					result.add(String.format("%s's DEX is now %d.", NAME, DEX));
				}
				
				MAX_HP = (int) (DEF * 0.20 * LVL) * 6;
				HP = MAX_HP;
				MAX_MP = (int) (MAG * 0.15 * LVL) * 2;
				MP = MAX_MP;
				
			}
			if(!(MAX_HP>0)) {
				MAX_HP++;
				HP = MAX_HP;
			}
			points--;
			
			if(MAX_HP<5) MAX_HP = 5;
			HP = MAX_HP;
		}
		/*
		 * To-do:
		 * -Add Skills when a certain Lvl its achieved
		 * 
		 * */
		// Test
		if(LVL==5) {
			Skills.add(new Skill("Test Skill", "DAMAGE", 2, 2, 0, DamageType.STR,""));
			result.add("You unlocked: Test Skill");
		}
		
		return (String[]) result.toArray(new String[0]);
	}
	
	public String[] stats() {
		String healthBar ="";
		String manaBar ="";
		
		healthBar+="\033[42m";
		for(int i=0;i<((double)HP/(double)MAX_HP)*20;i++) healthBar+= " ";
		healthBar+="\033[41m";
		for(int i=0;i<(((double)MAX_HP-(double)HP)/(double)MAX_HP)*20; i++) healthBar +=" ";
		
		if(MAX_MP>=1) {
			manaBar+="\033[44m";
			for(int i=0;i<((double)MP/(double)MAX_MP)*20;i++) manaBar+=" ";
			manaBar+="\033[47m";
			for(int i=0;i<(((double)MAX_MP-(double)MP)/(double)MAX_MP)*20; i++) manaBar+=" ";			
		} else for(int i=0;i<20; i++) System.out.print("m");
		int bonusSTR = this.getEffectiveSTR()-this.getSTR();
		int bonusMAG = this.getEffectiveMAG()-this.getMAG();
		int bonusDEF = this.getEffectiveDEF()-this.getDEF();
		int bonusDEX = this.getEffectiveDEX()-this.getDEX();
		
		String[] menu = {
				"_________________________________",
				"Stats",
				"Current Zone: ",
				"Name: "+ NAME,
				"Class: "+ CLASS,
				"Weapon: " + ((this.getWeapon()!=null) ? this.getWeapon().getNAME() : ""),
				"Armor: " + ((this.getArmor()!=null) ? this.getArmor().getNAME() : ""),
				"Accesory: " + ((this.getAccesory()!=null) ? this.getAccesory().getNAME() : ""),
				"Lvl: "+ LVL+ "   " + XP + "/" + LvlXP + "XP" + "   G:"+GOLD,
				"HP "+ HP+"/"+MAX_HP + "  " + healthBar + "\033[0m" ,
				"MP "+ MP+"/"+MAX_MP +"  " + manaBar + "\033[0m",
				"STR: "+ STR + ((bonusSTR>0) ? " +"+bonusSTR : ""),
				"MAG: "+ MAG + ((bonusMAG>0) ? " +"+bonusMAG : ""),
				"DEF: "+ DEF + ((bonusDEF>0) ? " +"+bonusDEF : ""),
				"DEX: "+ DEX + ((bonusDEX>0) ? " +"+bonusDEX : ""),
				"_________________________________"
		};
		
		return menu;
	}
	
	// Getters & Setters
	
	public String getNAME() {return NAME;}
	public void setNAME(String NAME) {this.NAME = NAME;}

	public int getLVL(){return LVL;}
	public void setLVL(int LVL) {this.LVL = LVL;}

	public int getXP() {return XP;}
	public void setXP(int XP) {this.XP = XP;}
	
	public int getGOLD() {return GOLD;}
	public void setGOLD(int GOLD) {this.GOLD = GOLD;}
	
	public EntityClass getCLASS() {return CLASS;}
	public void setCLASS(EntityClass CLASS) {this.CLASS = CLASS;}

	public int getHP() {return HP;}
	public void setHP(int HP) {	this.HP = HP;}

	public int getMAX_HP() {return MAX_HP;}
	public void setMAX_HP(int MAX_HP) {this.MAX_HP = MAX_HP;}

	public int getMP() {return MP;}
	public void setMP(int MP) {this.MP = MP;}

	public int getMAX_MP() {return MAX_MP;}
	public void setMAX_MP(int MAX_MP) {this.MAX_MP = MAX_MP;}

	public int getSTR() {return STR;}
	public void setSTR(int STR) {this.STR = STR;}

	public int getMAG() {return MAG;}
	public void setMAG(int MAG) {this.MAG = MAG;}

	public int getDEF() {return DEF;}
	public void setDEF(int DEF) {this.DEF = DEF;}

	public int getDEX() {return DEX;}
	public void setDEX(int DEX) {this.DEX = DEX;	}

	public int getT_COUNT() {return T_COUNT;}
	public void setT_COUNT(int T_COUNT) {this.T_COUNT = T_COUNT;}

	public int getACCION() {return ACCION;}
	public void setACCION(int ACCION) {this.ACCION = ACCION;}

	public Skill getEFFECT() {return EFFECT;}
	public void setEFFECT(Skill EFFECT) {this.EFFECT = EFFECT;}

	public int getEF_COUNT() {return EF_COUNT;}
	public void setEF_COUNT(int EF_COUNT) {this.EF_COUNT = EF_COUNT;}

	public boolean isDef() {return isDef;}
	public void setIsDef(boolean isDef) {this.isDef = isDef;}

	public ArrayList<Skill> getSkills() {return Skills;}
	public void setSkills(ArrayList<Skill> Skills) {this.Skills = Skills;}

	public ArrayList<Item> getInventory() {return Inventory;}
	public void setInventory(ArrayList<Item> Inventory) {this.Inventory = Inventory;}
	
	public ArrayList<Quest> getQuests() {return Quests;}
	public void setQuests(ArrayList<Quest> Quest) {this.Quests = Quest;}
	public void addQuest(Quest quest) {Quests.add(quest);}
	public void removeQuest(Quest quest) {Quests.remove(quest);}
	
	public Weapon getWeapon() { return weapon;}
	public void setWeapon(Weapon weapon) {this.weapon=weapon;}
	
	public Armor getArmor() {return armor;}
	public void setArmor(Armor armor) {this.armor=armor;}
	
	public Accesory getAccesory() {return accesory;}
	public void setAccesory(Accesory accesory) {this.accesory=accesory;}
	
	public int getEffectiveSTR() {
		int finalSTR = this.getSTR();
		if(this.getWeapon()!=null) finalSTR+= this.getWeapon().getBonusSTR();
		if(this.getArmor()!=null) finalSTR+= this.getArmor().getBonusSTR();
		if(this.getAccesory()!=null) finalSTR+= this.getAccesory().getBonusSTR();
		
		return finalSTR;
		}
	public int getEffectiveDEF() {
		int finalDEF = this.getDEF();
		if(this.getWeapon()!=null) finalDEF+= this.getWeapon().getBonusDEF();
		if(this.getArmor()!=null) finalDEF+= this.getArmor().getBonusDEF();
		if(this.getAccesory()!=null) finalDEF+= this.getAccesory().getBonusDEF();
		
		return finalDEF;
	}
	public int getEffectiveMAG() {
		int finalMAG = this.getMAG();
		if(this.getWeapon()!=null) finalMAG+= this.getWeapon().getBonusMAG();
		if(this.getArmor()!=null) finalMAG+= this.getArmor().getBonusMAG();
		if(this.getAccesory()!=null) finalMAG+= this.getAccesory().getBonusMAG();
		
		return finalMAG;
	}
	public int getEffectiveDEX() {
		int finalDEX = this.getDEX();
		if(this.getWeapon()!=null) finalDEX+= this.getWeapon().getBonusDEX();
		if(this.getArmor()!=null) finalDEX+= this.getArmor().getBonusDEX();
		if(this.getAccesory()!=null) finalDEX+= this.getAccesory().getBonusDEX();
		
		return finalDEX;
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
				this.MAX_HP = (int) (this.DEF * 0.40 * LVL) * 6;
				this.MAX_MP = (int) (this.MAG * 0.1 * LVL) * 2;
				
				break;
			case MAGE:
				// Main stats: MAG & DEX
				this.STR = (int) ((1.0/12) * LVL_P);
				this.MAG = (int) ((6.0/12) * LVL_P);
				this.DEF = (int) ((2.0/12) * LVL_P);
				this.DEX = (int) ((3.0/12) * LVL_P);
				this.MAX_HP = (int) (this.DEF * 0.15 * LVL) * 6;
				this.MAX_MP = (int) (this.MAG * 0.25 * LVL) * 2;
				if(MAX_MP<25) MAX_MP = 25;
				Skills.add(SkillManager.getSkill("SPARK"));
				break;
			case CLERIC:
				// Main stats: MAG & DEF
				this.STR = (int) ((1.0/12) * LVL_P);
				this.MAG = (int) ((5.0/12) * LVL_P);
				this.DEF = (int) ((4.0/12) * LVL_P);
				this.DEX = (int) ((2.0/12) * LVL_P);
				this.MAX_HP = (int) (this.DEF * 0.25 * LVL) * 6;
				this.MAX_MP = (int) (this.MAG * 0.2 * LVL) * 2;
				if(MAX_MP<20) MAX_MP = 20;
				Skills.add(SkillManager.getSkill("HEAL"));
				break;
			case ROGUE:
				// Main stats: STR & DEX
				this.STR = (int) ((4.0/12) * LVL_P);
				this.MAG = (int) ((2.0/12) * LVL_P);
				this.DEF = (int) ((1.0/12) * LVL_P);
				this.DEX = (int) ((5.0/12) * LVL_P);
				this.MAX_HP = (int) (this.DEF * 0.20 * LVL) * 6;
				this.MAX_MP = (int) (this.MAG * 0.15 * LVL) * 2;
				break;
		}
		
		if(this.MAX_HP<5) this.MAX_HP = 5;
		this.HP = this.MAX_HP;
		this.MP = this.MAX_MP;
	}

	public Entity(String NAME, int LVL, int MAX_HP, int MAX_MP, int STR, int MAG, int DEF, int DEX) {
		this.NAME = NAME;
		this.LVL = LVL;
		this.LvlXP = (int) (this.LVL + 9 + (Math.pow(this.LVL, (this.LVL*0.05))));
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
	
	public Entity(Entity model) {
		this.NAME = model.NAME;
		this.LVL = model.LVL;
		this.MAX_HP = model.MAX_HP;
		this.HP = MAX_HP;
		this.MAX_MP = model.MAX_MP;
		this.MP = MAX_MP;
		this.STR = model.STR;
		this.MAG = model.MAG;
		this.DEF = model.DEF;
		this.DEX = model.DEX;
		this.GOLD = 0;
	}

}
