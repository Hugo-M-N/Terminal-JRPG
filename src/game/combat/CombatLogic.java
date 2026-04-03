package game.combat;

import java.util.ArrayList;

import game.Config;
import game.quest.QuestManager;
import game.TextMenus;
import game.entity.Entity;
import game.item.Item;
import game.skill.Skill;
import game.utils.InputHelper;

public class CombatLogic {

    public static void allyTextTurn(CombatState state) {
        for (Entity ally : state.allies) {
            if (ally.getT_COUNT() >= 100 && ally.getHP() > 0) {
                ally.setIsDef(false);
                switch (TextMenus.Menu(state.options)) {
                    case "Attack"  -> attack(ally, state.enemies, false, state);
                    case "Defend"  -> defend(ally, state);
                    case "Skills"  -> useSkill(ally, state.allies, state.enemies, false, state);
                    case "Objects" -> useObject(ally, state.allies, state.enemies, false, state);
                    case "Exit"    -> {
                        state.result = new String[]{"You ran away."};
                        for (Entity e : state.allies) e.setT_COUNT(0);
                        state.running = false;
                        return;
                    }
                }
                ally.setT_COUNT(0);
            }
        }
    }

    public static void enemyTurn(CombatState state) throws InterruptedException {
        for (Entity enemy : state.enemies) {
            if (enemy.getT_COUNT() >= 100 && enemy.getHP() > 0) {
                enemy.setIsDef(false);
                if (Config.getAPP_MODE() == 0) {
                    state.buffer.addToBuffer(enemy.getNAME() + "'s turn");
                    InputHelper.clearScreen();
                    for (String s : state.buffer.getScreenBuffer()) System.out.println(s);
                }
                int eOptions = 3;
                if (enemy.getInventory().size() < 1) eOptions--;
                if (enemy.getSkills().size() < 1) eOptions--;
                int eSel = (int) (Math.random() * eOptions) + 1;
                switch (eSel) {
                    case 1 -> attack(enemy, state.allies, true, state);
                    case 2 -> {
                        defend(enemy, state);
                        if (Config.getAPP_MODE() != 0) state.addLog(enemy.getNAME() + " is defending.");
                    }
                    case 3 -> useSkill(enemy, state.enemies, state.allies, true, state);
                    case 4 -> useObject(enemy, state.enemies, state.allies, true, state);
                }
                enemy.setT_COUNT(0);
            }
        }
    }

    public static void tick(CombatState state) {
        for (Entity ally : state.allies) {
            if (ally.getHP() > 0) ally.setT_COUNT(ally.getT_COUNT() + 3 + (ally.getDEX() / 2));
            if (ally.getT_COUNT() > 100) ally.setT_COUNT(100);
        }
        for (Entity enemy : state.enemies) {
            if (enemy.getHP() > 0) enemy.setT_COUNT(enemy.getT_COUNT() + 3 + (enemy.getDEX() / 2));
            if (enemy.getT_COUNT() > 100) enemy.setT_COUNT(100);
        }
        switch (checkCombat(state)) {
            case 1 -> {
                state.running = false;
                state.result = winCombat(state);
                for (String s : state.result) if (s != null && !s.trim().isEmpty()) state.addLog(s);
                for (Entity e : state.allies) e.setT_COUNT(0);
            }
            case 2 -> {
                state.running = false;
                state.result = new String[]{"You Lose.", "You won't gain any XP or gold."};
                state.addLog("You Lose.");
                state.addLog("You won't gain any XP or gold.");
                for (Entity e : state.allies) e.setT_COUNT(0);
            }
        }
    }

    static int checkCombat(CombatState state) {
        long alliesAlive  = state.allies.stream().filter(e -> e.getHP() > 0).count();
        long enemiesAlive = state.enemies.stream().filter(e -> e.getHP() > 0).count();
        if (alliesAlive > 0 && enemiesAlive == 0) return 1;
        if (alliesAlive == 0 && enemiesAlive > 0) return 2;
        return 0;
    }

    static String[] winCombat(CombatState state) {
        int enemiesLvl = 0;
        int gold = 0;
        for (Entity enemy : state.enemies) {
            enemiesLvl += enemy.getLVL();
            gold += enemy.getGOLD();
        }
        enemiesLvl /= state.enemies.size();
        float xp = (float) (Math.random() * 2.5 * enemiesLvl * 0.3 + state.enemies.size() * 0.7);

        state.allies.get(0).setGOLD(state.allies.get(0).getGOLD() + gold);
        for (Entity enemy : state.enemies) QuestManager.notifyKill(enemy.getNAME().toUpperCase());
        ArrayList<String> result = new ArrayList<>();
        result.add("You earn " + gold + " G.");
        result.add("Each Ally got " + (int) Math.round(xp / state.allies.size()) + " XP.");
        for (Entity ally : state.allies) {
            for (java.util.List<String> lvl : ally.addXP((int) Math.round(xp / state.allies.size())))
                for (String s : lvl) result.add(s);
        }
        for (Entity enemy : state.enemies) {
            for (Item item : enemy.getInventory()) result.add(state.allies.get(0).addToInventory(item.copy()));
        }
        return result.toArray(new String[0]);
    }

    public static void attack(Entity attacker, ArrayList<Entity> targets, boolean isEnemy, CombatState state) {
        Entity target;
        if (!isEnemy) {
            target = TextMenus.SelectTarget(targets);
        } else {
            target = targets.size() > 1 ? targets.get((int) (Math.random() * targets.size())) : targets.get(0);
        }
        int dmg = (int) (attacker.getEffectiveSTR() * 0.75 - (target.getEffectiveDEF() * 0.3)) + 2;
        if (target.isDef()) dmg /= 2;
        target.setHP(Math.max(0, target.getHP() - dmg));
        if (Config.getAPP_MODE() == 0 && state.buffer != null) {
            state.buffer.addToBuffer(target.getNAME() + " received " + dmg + " dmg.");
            InputHelper.clearScreen();
            for (String s : state.buffer.getScreenBuffer()) System.out.println(s);
        }
        if (isEnemy && Config.getAPP_MODE() != 0) {
            state.addLog(attacker.getNAME() + " attacked " + target.getNAME() + " for " + dmg + " dmg.");
        }
    }

    public static void defend(Entity defender, CombatState state) {
        defender.setIsDef(true);
        if (Config.getAPP_MODE() == 0 && state.buffer != null) {
            state.buffer.addToBuffer(String.format("%s is defending.", defender.getNAME()));
            InputHelper.clearScreen();
            for (String s : state.buffer.getScreenBuffer()) System.out.println(s);
        }
        try { Thread.sleep(1500); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    public static void useSkill(Entity caster, ArrayList<Entity> allies, ArrayList<Entity> enemies, boolean isEnemy, CombatState state) {
        if (!isEnemy && Config.getAPP_MODE() == 0) {
            Skill selected = TextMenus.SkillMenu(caster.getSkills());
            if (selected != null && caster.getMP() >= selected.getCOST()) {
                Entity target = selected.getEFFECT().equals("DAMAGE")
                        ? TextMenus.SelectTarget(enemies)
                        : TextMenus.SelectTarget(allies, enemies);
                switch (selected.getDamageType()) {
                    case STR -> selected.Use(target, caster.getEffectiveSTR());
                    case MAG -> selected.Use(target, caster.getEffectiveMAG());
                    case DEX -> selected.Use(target, caster.getEffectiveDEX());
                }
                caster.setMP(caster.getMP() - selected.getCOST());
                if (state.buffer != null)
                    state.buffer.addToBuffer(caster.getNAME() + " used " + selected.getNAME() + " on " + target.getNAME());
            }
        }
        // TODO: enemy skill AI
        try { Thread.sleep(1500); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    public static void useObject(Entity caster, ArrayList<Entity> allies, ArrayList<Entity> enemies, boolean isEnemy, CombatState state) {
        if (!isEnemy && Config.getAPP_MODE() == 0) {
            ArrayList<Item> objects = allies.get(0).getInventory();
            if (objects.size() >= 1) {
                Item selected = TextMenus.ObjectMenu(allies.get(0).getInventory());
                if (selected != null && selected.getAMOUNT() >= 1) {
                    Entity target = TextMenus.SelectTarget(allies, enemies);
                    selected.Use(target);
                    selected.setAMOUNT(selected.getAMOUNT() - 1);
                    if (state.buffer != null)
                        state.buffer.addToBuffer(caster.getNAME() + " used " + selected.getNAME() + " on " + target.getNAME());
                }
            } else if (state.buffer != null) {
                state.buffer.addToBuffer("You don't have anything yet...");
                InputHelper.clearScreen();
                for (String s : state.buffer.getScreenBuffer()) System.out.println(s);
                return;
            }
        }
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
    }
}
