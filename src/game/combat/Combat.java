package game.combat;

import java.awt.Canvas;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import game.ScreenBuffer;
import game.TextMenus;
import game.entity.Entity;
import game.utils.InputHelper;

public class Combat {

    private final CombatState state;

    public String[] getResult() {
        return state.result;
    }

    // --- Terminal mode ---

    public Combat(ArrayList<Entity> allies, ArrayList<Entity> enemies, ScreenBuffer buffer) throws InterruptedException {
        state = new CombatState(allies, enemies);
        state.buffer = buffer;

        while (state.running) {
            state.buffer.clearBuffer();
            for (String s : TextMenus.combatMenu(state.allies, state.enemies)) state.buffer.updateBuffer(s);
            InputHelper.clearScreen();
            for (String s : state.buffer.getScreenBuffer()) System.out.println(s);

            CombatLogic.allyTextTurn(state);
            CombatLogic.enemyTurn(state);
            CombatLogic.tick(state);
            Thread.sleep(100);
        }
        Thread.sleep(500);
    }

    // --- GUI mode ---

    public Combat(ArrayList<Entity> allies, ArrayList<Entity> enemies, Canvas canvas) throws InterruptedException {
        state = new CombatState(allies, enemies);

        KeyAdapter listener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                switch (state.currentMenu) {

                    case "main" -> {
                        if (key == KeyEvent.VK_UP)    state.sel = (state.sel - 1 + state.options.length) % state.options.length;
                        if (key == KeyEvent.VK_DOWN)  state.sel = (state.sel + 1) % state.options.length;
                        if (key == KeyEvent.VK_ENTER) state.nextAction = state.options[state.sel];
                    }

                    case "targetSel" -> {
                        int n = aliveEnemyCount();
                        if (n == 0) return;
                        if (key == KeyEvent.VK_UP)    state.sel = (state.sel - 1 + n) % n;
                        if (key == KeyEvent.VK_DOWN)  state.sel = (state.sel + 1) % n;
                        if (key == KeyEvent.VK_ENTER) state.nextAction = "targetSel";
                        if (key == KeyEvent.VK_ESCAPE)state.nextAction = "back";
                    }

                    case "skillSel" -> {
                        int n = state.currentActingAlly != null ? state.currentActingAlly.getSkills().size() : 0;
                        if (n == 0) return;
                        if (key == KeyEvent.VK_UP)    state.subSel = (state.subSel - 1 + n) % n;
                        if (key == KeyEvent.VK_DOWN)  state.subSel = (state.subSel + 1) % n;
                        if (key == KeyEvent.VK_ENTER) state.nextAction = "skillSel";
                        if (key == KeyEvent.VK_ESCAPE)state.nextAction = "back";
                    }

                    case "skillTargetSel" -> {
                        boolean dmg = state.selectedSkill != null && "DAMAGE".equals(state.selectedSkill.getEFFECT());
                        int n = dmg ? aliveEnemyCount() : state.allies.size();
                        if (n == 0) return;
                        if (key == KeyEvent.VK_UP)    state.sel = (state.sel - 1 + n) % n;
                        if (key == KeyEvent.VK_DOWN)  state.sel = (state.sel + 1) % n;
                        if (key == KeyEvent.VK_ENTER) state.nextAction = "skillTargetSel";
                        if (key == KeyEvent.VK_ESCAPE)state.nextAction = "back";
                    }

                    case "objSel" -> {
                        int n = state.currentActingAlly != null ? state.currentActingAlly.getInventory().size() : 0;
                        if (n == 0) return;
                        if (key == KeyEvent.VK_UP)    state.subSel = (state.subSel - 1 + n) % n;
                        if (key == KeyEvent.VK_DOWN)  state.subSel = (state.subSel + 1) % n;
                        if (key == KeyEvent.VK_ENTER) state.nextAction = "objSel";
                        if (key == KeyEvent.VK_ESCAPE)state.nextAction = "back";
                    }

                    case "objTargetSel" -> {
                        int n = state.allies.size();
                        if (key == KeyEvent.VK_UP)    state.sel = (state.sel - 1 + n) % n;
                        if (key == KeyEvent.VK_DOWN)  state.sel = (state.sel + 1) % n;
                        if (key == KeyEvent.VK_ENTER) state.nextAction = "objTargetSel";
                        if (key == KeyEvent.VK_ESCAPE)state.nextAction = "back";
                    }

                    case "result" -> {
                        if (key == KeyEvent.VK_ENTER) state.waitingForConfirm = true;
                        if (key == KeyEvent.VK_UP)   state.logScroll = Math.min(state.logScroll + 1, state.log.size() - 1);
                        if (key == KeyEvent.VK_DOWN) state.logScroll = Math.max(state.logScroll - 1, 0);
                    }
                }
            }

            private int aliveEnemyCount() {
                int count = 0;
                for (Entity en : state.enemies) if (en.getHP() > 0) count++;
                return count;
            }
        };

        canvas.addKeyListener(listener);

        while (state.running) {
            CombatRenderer.allyTurn(state, canvas);
            if (!state.running) break;
            CombatLogic.enemyTurn(state);
            CombatLogic.tick(state);
            CombatRenderer.renderFrame(state, canvas);
            Thread.sleep(16);
        }

        // Show result and wait for Enter
        if (state.result != null && !state.result[0].equals("You ran away.")) {
            state.currentMenu = "result";
            state.waitingForConfirm = false;
            state.logScroll = 0;
            while (!state.waitingForConfirm) {
                CombatRenderer.renderFrame(state, canvas);
                Thread.sleep(16);
            }
        }

        canvas.removeKeyListener(listener);
        Thread.sleep(200);
    }
}
