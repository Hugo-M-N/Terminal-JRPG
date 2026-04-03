package game.combat;

import java.util.ArrayList;

import game.ScreenBuffer;
import game.entity.Entity;
import game.item.Item;
import game.skill.Skill;

public class CombatState {
    public final String[] options = {"Attack", "Defend", "Skills", "Objects", "Exit"};
    public ArrayList<Entity> allies;
    public ArrayList<Entity> enemies;
    public boolean running = true;
    public boolean waitingForConfirm = false;
    public int logScroll = 0;
    public String[] result = null;
    public int sel = 0;
    public int subSel = 0;
    public String nextAction = "";
    public String currentMenu = "";
    public ScreenBuffer buffer;

    // GUI extras
    public ArrayList<String> log = new ArrayList<>();
    public Skill selectedSkill = null;
    public Item selectedItem = null;
    public Entity currentActingAlly = null;

    public CombatState(ArrayList<Entity> allies, ArrayList<Entity> enemies) {
        this.allies = allies;
        this.enemies = enemies;
    }

    public void addLog(String message) {
        log.add(message);
        if (log.size() > 20) log.remove(0);
    }
}
