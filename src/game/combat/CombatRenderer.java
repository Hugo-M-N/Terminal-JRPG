package game.combat;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

import game.entity.Entity;
import game.item.Item;
import game.skill.Skill;
import game.sprite.Sprite;
import game.sprite.SpriteManager;
import game.utils.WindowGraphics;

public class CombatRenderer {

    // ── Layout ───────────────────────────────────────────────────────────────
    private static final int PANEL_H      = 180;
    private static final int SPRITE_SC    = 4;    // 32 * 4 = 128 px
    private static final int GROUND_LINE  = 250;  // sky/ground split inside arena (arena-relative y)
    // ── Palette ──────────────────────────────────────────────────────────────
    private static final Color C_SKY_TOP   = new Color( 8,  12,  55);
    private static final Color C_SKY_MID   = new Color(30,  58, 140);
    private static final Color C_SKY_BOT   = new Color(60, 100, 185);
    private static final Color C_GND_TOP   = new Color(32,  58,  18);
    private static final Color C_GND_BOT   = new Color(14,  28,   7);
    private static final Color C_MTN_FAR   = new Color(20,  28,  72);
    private static final Color C_MTN_NEAR  = new Color(13,  18,  50);
    private static final Color C_SHADOW    = new Color( 0,   0,   0,  55);

    private static final Color C_PANEL     = new Color( 6,   6,  18);
    private static final Color C_BOX_BG    = new Color( 4,   4,  14, 215);
    private static final Color C_BOX_BD    = new Color(160, 175, 220, 145);
    private static final Color C_BOX_INNER = new Color(255, 255, 255,  16);
    private static final Color C_BOX_HDR   = new Color(200, 210, 255, 215);
    private static final Color C_DIVIDER   = new Color(180, 195, 255,  50);

    private static final Color C_TEXT      = new Color(218, 220, 230);
    private static final Color C_TEXT_DIM  = new Color(145, 148, 165);
    private static final Color C_SEL_BG    = new Color(255, 212,  40,  38);
    private static final Color C_SEL_BD    = new Color(255, 212,  40, 120);
    private static final Color C_SEL_FG    = new Color(255, 215,  45);
    private static final Color C_DEAD      = new Color(120, 120, 130);

    private static final Color C_HP_HI     = new Color(48,  205,  65);
    private static final Color C_HP_MID    = new Color(232, 195,  25);
    private static final Color C_HP_LO     = new Color(215,  38,  28);
    private static final Color C_MP        = new Color(55,  115, 230);
    private static final Color C_ATK       = new Color(215, 170,  22);
    private static final Color C_HPBG      = new Color(48,   7,   7);
    private static final Color C_MPBG      = new Color( 7,  16,  55);
    private static final Color C_ATKBG     = new Color(42,  38,   7);
    private static final Color C_ACTING_BD = new Color(255, 212,  40, 200);
    private static final Color C_ACTING_BG = new Color(255, 212,  40,  28);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font F_CARD_NAME = new Font("Monospaced", Font.BOLD,  12);
    private static final Font F_CARD_VAL  = new Font("Monospaced", Font.PLAIN, 10);
    private static final Font F_OPT       = new Font("Monospaced", Font.BOLD,  16);
    private static final Font F_SUB       = new Font("Monospaced", Font.PLAIN, 14);
    private static final Font F_LOG       = new Font("Monospaced", Font.PLAIN, 13);
    private static final Font F_HINT      = new Font("Monospaced", Font.PLAIN, 11);
    private static final Font F_HDR       = new Font("Monospaced", Font.BOLD,  13);

    // ── Public API ───────────────────────────────────────────────────────────

    public static void renderFrame(CombatState state, Canvas canvas) {
        BufferStrategy bs = canvas.getBufferStrategy();
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        try {
            drawBackground(g, canvas.getWidth(), canvas.getHeight());
            drawEnemies(state, canvas, g);
            drawAllies(state, canvas, g);
            drawPanel(state, canvas, g);
        } finally {
            g.dispose();
            bs.show();
        }
    }

    /**
     * Blocking ally turn: waits for player input for each ready ally.
     * Handles Attack, Defend, Skills, Objects, Exit with full sub-menus.
     */
    public static void allyTurn(CombatState state, Canvas canvas) throws InterruptedException {
        for (Entity ally : state.allies) {
            if (ally.getT_COUNT() < 100 || ally.getHP() <= 0) continue;

            ally.setIsDef(false);
            state.currentActingAlly = ally;
            state.sel    = 0;
            state.subSel = 0;
            state.nextAction = "";

            outer:
            while (true) {

                state.currentMenu = "main";
                waitForAction(state, canvas);
                String choice = state.nextAction;
                state.nextAction = "";

                switch (choice) {

                    case "Attack" -> {
                        ArrayList<Entity> alive = aliveEnemies(state);
                        if (alive.isEmpty()) continue outer;
                        state.sel = 0;
                        state.currentMenu = "targetSel";
                        waitForAction(state, canvas);
                        if ("back".equals(state.nextAction)) { state.nextAction = ""; continue outer; }
                        state.nextAction = "";

                        Entity target = alive.get(Math.min(state.sel, alive.size() - 1));
                        int preHp = target.getHP();
                        ArrayList<Entity> tl = new ArrayList<>();
                        tl.add(target);
                        CombatLogic.attack(ally, tl, false, state);
                        state.addLog(target.getNAME() + " took " + (preHp - target.getHP()) + " damage.");
                    }

                    case "Defend" -> {
                        CombatLogic.defend(ally, state);
                        state.addLog(ally.getNAME() + " is defending.");
                        ally.setT_COUNT(0);
                        state.currentMenu = "";
                        state.currentActingAlly = null;
                        renderFrame(state, canvas);
                        return;
                    }

                    case "Skills" -> {
                        ArrayList<Skill> skills = ally.getSkills();
                        if (skills.isEmpty()) { state.addLog("No skills available."); continue outer; }
                        state.subSel = 0;
                        state.currentMenu = "skillSel";
                        waitForAction(state, canvas);
                        if ("back".equals(state.nextAction)) { state.nextAction = ""; continue outer; }
                        state.nextAction = "";

                        Skill sk = skills.get(Math.min(state.subSel, skills.size() - 1));
                        if (ally.getMP() < sk.getCOST()) {
                            state.addLog("Not enough MP for " + sk.getNAME() + "!");
                            continue outer;
                        }
                        state.selectedSkill = sk;

                        boolean dmg = "DAMAGE".equals(sk.getEFFECT());
                        ArrayList<Entity> targets = dmg ? aliveEnemies(state) : state.allies;
                        if (targets.isEmpty()) { state.selectedSkill = null; continue outer; }
                        state.sel = 0;
                        state.currentMenu = "skillTargetSel";
                        waitForAction(state, canvas);
                        if ("back".equals(state.nextAction)) {
                            state.nextAction = "";
                            state.selectedSkill = null;
                            continue outer;
                        }
                        state.nextAction = "";

                        Entity t = targets.get(Math.min(state.sel, targets.size() - 1));
                        switch (sk.getDamageType()) {
                            case STR -> sk.Use(t, ally.getEffectiveSTR());
                            case MAG -> sk.Use(t, ally.getEffectiveMAG());
                            case DEX -> sk.Use(t, ally.getEffectiveDEX());
                        }
                        ally.setMP(ally.getMP() - sk.getCOST());
                        state.addLog(ally.getNAME() + " used " + sk.getNAME() + " on " + t.getNAME() + ".");
                        state.selectedSkill = null;
                    }

                    case "Objects" -> {
                        ArrayList<Item> items = ally.getInventory();
                        if (items.isEmpty()) { state.addLog("No items available."); continue outer; }
                        state.subSel = 0;
                        state.currentMenu = "objSel";
                        waitForAction(state, canvas);
                        if ("back".equals(state.nextAction)) { state.nextAction = ""; continue outer; }
                        state.nextAction = "";

                        Item it = items.get(Math.min(state.subSel, items.size() - 1));
                        state.selectedItem = it;
                        state.sel = 0;
                        state.currentMenu = "objTargetSel";
                        waitForAction(state, canvas);
                        if ("back".equals(state.nextAction)) {
                            state.nextAction = "";
                            state.selectedItem = null;
                            continue outer;
                        }
                        state.nextAction = "";

                        Entity t = state.allies.get(Math.min(state.sel, state.allies.size() - 1));
                        it.Use(t);
                        it.remove();
                        state.addLog(ally.getNAME() + " used " + it.getNAME() + " on " + t.getNAME() + ".");
                        if (it.getAMOUNT() <= 0) items.remove(it);
                        state.selectedItem = null;
                    }

                    case "Exit" -> {
                        state.result = new String[]{"You ran away."};
                        for (Entity e : state.allies) e.setT_COUNT(0);
                        state.running = false;
                        state.currentMenu = "";
                        state.currentActingAlly = null;
                        return;
                    }
                }

                ally.setT_COUNT(0);
                state.currentMenu = "";
                state.currentActingAlly = null;
                renderFrame(state, canvas);
                Thread.sleep(800);
                return;
            }
        }
    }

    // ── Background ───────────────────────────────────────────────────────────

    private static void drawBackground(Graphics2D g, int W, int H) {
        int arenaH = H - PANEL_H;
        int skyH   = GROUND_LINE;
        int gndH   = arenaH - skyH;

        // Sky: three-stop gradient
        int half = skyH / 2;
        for (int i = 0; i < 10; i++) {
            float t = (float) i / 10;
            g.setColor(blend(C_SKY_TOP, C_SKY_MID, t));
            g.fillRect(0, half * i / 10, W, half / 10 + 1);
        }
        for (int i = 0; i < 10; i++) {
            float t = (float) i / 10;
            g.setColor(blend(C_SKY_MID, C_SKY_BOT, t));
            g.fillRect(0, half + half * i / 10, W, half / 10 + 1);
        }

        // Mountain silhouettes (two layers, far → near)
        g.setColor(C_MTN_FAR);
        int[] xf = {0, 90, 190, 340, 500, 650, 790, 930, 1080, 1200, W};
        int[] yf = {skyH, skyH-58, skyH-28, skyH-80, skyH-42,
                    skyH-68, skyH-18, skyH-62, skyH-33, skyH-52, skyH};
        g.fillPolygon(xf, yf, xf.length);

        g.setColor(C_MTN_NEAR);
        int[] xn = {0, 65, 155, 280, 420, 555, 680, 820, 960, 1110, 1220, W};
        int[] yn = {skyH, skyH-30, skyH-14, skyH-46, skyH-20,
                    skyH-38, skyH-10, skyH-34, skyH-17, skyH-28, skyH-11, skyH};
        g.fillPolygon(xn, yn, xn.length);

        // Ground gradient (extended to full screen height so panel area matches)
        int fullGndH = H - skyH;
        for (int i = 0; i < 7; i++) {
            float t = (float) i / 7;
            g.setColor(blend(C_GND_TOP, C_GND_BOT, t));
            int y1 = skyH + fullGndH * i / 7;
            g.fillRect(0, y1, W, fullGndH / 7 + 1);
        }

        // Horizon glow line
        g.setColor(new Color(100, 155, 65, 90));
        g.fillRect(0, skyH, W, 2);

        // Subtle ground perspective lines
        g.setColor(new Color(0, 0, 0, 14));
        for (int i = 1; i <= 5; i++) {
            int gy     = skyH + gndH * i / 6;
            int shrink = (W / 2) * (6 - i) / 6;
            g.drawLine(shrink, gy, W - shrink, gy);
        }
    }

    // ── Entities ─────────────────────────────────────────────────────────────

    private static void drawAllies(CombatState state, Canvas canvas, Graphics2D g) {
        int W = canvas.getWidth();
        int H = canvas.getHeight();
        int n = state.allies.size();
        int sprW = 32 * SPRITE_SC;
        int startPosX = (2*W)/7;
        int startPosY = H/2;
        
        boolean showAllyArrow = "objTargetSel".equals(state.currentMenu)
            || ("skillTargetSel".equals(state.currentMenu)
                && state.selectedSkill != null
                && !"DAMAGE".equals(state.selectedSkill.getEFFECT()));

        for (int i = 0; i < n; i++) {
            Entity ally = state.allies.get(i);
            Sprite sp   = SpriteManager.getPerson(ally.getSpriteIdx());
            int tmpX = startPosX - (100 * i);
            int tmpY = startPosY + (80 * i);

            if (ally.getT_COUNT() >= 100 && ally.getHP() > 0) tmpX += 16;

            boolean dead   = ally.getHP() <= 0;
            boolean acting = ally == state.currentActingAlly;
            drawEntity(g, sp, ally, tmpX, tmpY, true, dead, true, acting);

            if (!dead && showAllyArrow && i == state.sel) {
                drawArrow(g,  tmpX, tmpY - sprW - 14);
            }
        }
    }

    private static void drawEnemies(CombatState state, Canvas canvas, Graphics2D g) {
        int W = canvas.getWidth();
        int H = canvas.getHeight();
        int n = state.enemies.size();
        int sprW = 32 * SPRITE_SC;
        int startPosX = (5*W)/7;
        int startPosY = H/2;

        ArrayList<Entity> alive = aliveEnemies(state);
        boolean showEnemyArrow = "targetSel".equals(state.currentMenu)
            || ("skillTargetSel".equals(state.currentMenu)
                && state.selectedSkill != null
                && "DAMAGE".equals(state.selectedSkill.getEFFECT()));
        for (int i = 0; i < n; i++) {
            Entity enemy = state.enemies.get(i);
            Sprite sp    = SpriteManager.getMonster(i);
            int tmpX = startPosX + (100 * i);
            int tmpY = startPosY + (80 * i);

            if (enemy.getT_COUNT() >= 100 && enemy.getHP() > 0) tmpX -= 18;

            boolean dead = enemy.getHP() <= 0;
            drawEntity(g, sp, enemy, tmpX, tmpY, false, dead, false, false);

            int aliveIdx = alive.indexOf(enemy);
            if (!dead && showEnemyArrow && aliveIdx == state.sel) {
                drawArrow(g, tmpX, tmpY - sprW - 14);
            }
        }
    }

    /**
     * Draws a single entity: ground shadow → sprite → status card below.
     * footX/footY are the horizontal center and ground contact point.
     */
    private static void drawEntity(Graphics2D g, Sprite sp, Entity e,
                                   int footX, int footY,
                                   boolean flipX, boolean dead,
                                   boolean showMp, boolean isActing) {
        int sprW = sp.getWidth()  * SPRITE_SC;
        int sprH = sp.getHeight() * SPRITE_SC;
        int sprX = footX - sprW / 2;
        int sprY = footY - sprH;

        // Ground shadow
        int shadowRx = sprW * 2 / 5;
        int shadowRy = 7;
        g.setColor(C_SHADOW);
        g.fillOval(footX - shadowRx, footY - (shadowRy * 3 / 2), shadowRx * 2, shadowRy * 2);

        // Sprite (faded if dead)
        Composite savedComp = g.getComposite();
        if (dead) g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.28f));
        WindowGraphics.drawSprite(sp, SPRITE_SC, sprX, sprY, g, flipX, false);
        if (dead) g.setComposite(savedComp);

        // ── Status card ──────────────────────────────────────
        int cardW = 128;
        int cardH = showMp ? 70 : 58;
        int cardX = footX - cardW / 2;
        int cardY = footY - sprH - 100;

        // Card body
        g.setColor(isActing ? C_ACTING_BG : C_BOX_BG);
        g.fillRoundRect(cardX, cardY, cardW, cardH, 12, 12);

        // Outer border (gold for acting, normal for others)
        g.setColor(isActing ? C_ACTING_BD : C_BOX_BD);
        g.drawRoundRect(cardX, cardY, cardW, cardH, 12, 12);

        // Inner glow line
        g.setColor(C_BOX_INNER);
        g.drawRoundRect(cardX + 2, cardY + 2, cardW - 4, cardH - 4, 10, 10);

        // Left accent strip (colored by HP)
        Color accent = dead ? C_DEAD : hpColor(e);
        g.setColor(accent);
        g.fillRoundRect(cardX + 1, cardY + 9, 4, cardH - 18, 4, 4);

        // Name (with acting indicator)
        g.setFont(F_CARD_NAME);
        g.setColor(isActing ? C_SEL_FG : (dead ? C_DEAD : C_TEXT));
        String nameStr = isActing ? "▶ " : "  ";
        String name = e.getNAME().length() > 11 ? e.getNAME().substring(0, 11) : e.getNAME();
        g.drawString(nameStr + name, cardX + 8, cardY + 14);

        int barX = cardX + 10, barW = cardW - 20, barH = 7;

        // HP bar + value
        drawBar(g, barX, cardY + 19, barW, barH, e.getHP(), e.getMAX_HP(), C_HPBG, hpColor(e));
        g.setFont(F_CARD_VAL);
        g.setColor(C_TEXT_DIM);
        String hpStr = e.getHP() + "/" + e.getMAX_HP();
        g.drawString(hpStr, cardX + cardW - 4 - g.getFontMetrics().stringWidth(hpStr), cardY + 18);

        // MP bar + value (allies only)
        if (showMp) {
            drawBar(g, barX, cardY + 31, barW, barH, e.getMP(), e.getMAX_MP(), C_MPBG, C_MP);
            if (e.getMAX_MP() > 0) {
                String mpStr = e.getMP() + "/" + e.getMAX_MP();
                g.setColor(C_TEXT_DIM);
                g.drawString(mpStr, cardX + cardW - 4 - g.getFontMetrics().stringWidth(mpStr), cardY + 30);
            }
        }

        // ATB bar + READY label
        int atkBarY = showMp ? cardY + 46 : cardY + 34;
        boolean ready = e.getT_COUNT() >= 100 && !dead;
        drawBar(g, barX, atkBarY, barW, 5, (int) e.getT_COUNT(), 100, C_ATKBG, ready ? C_SEL_FG : C_ATK);
        if (ready) {
            g.setFont(F_CARD_VAL);
            g.setColor(C_SEL_FG);
            g.drawString("READY", barX, atkBarY - 1);
        }
    }

    // ── Bottom Panel ─────────────────────────────────────────────────────────

    private static void drawPanel(CombatState state, Canvas canvas, Graphics2D g) {
        int W = canvas.getWidth(), H = canvas.getHeight();
        int panelW = W * 3 / 5;
        int panelX = (W - panelW) / 2;
        int panelY = H - PANEL_H;
        int pad    = 12;

        // Panel base
        g.setColor(C_PANEL);
        g.fillRoundRect(panelX, panelY, panelW, PANEL_H, 14, 14);

        // Top accent strip
        g.setColor(new Color(80, 95, 175, 130));
        g.fillRoundRect(panelX, panelY, panelW, 8, 14, 14);
        g.setColor(new Color(200, 215, 255, 55));
        g.fillRect(panelX + 8, panelY + 5, panelW - 16, 1);

        // Border
        g.setColor(new Color(180, 195, 255, 70));
        g.drawRoundRect(panelX, panelY, panelW, PANEL_H, 14, 14);

        int logW   = panelW * 51 / 100;
        int actX   = panelX + logW + pad;
        int actW   = panelW - logW - pad * 2;
        int innerH = PANEL_H - pad * 2;

        // Log box
        drawBox(g, panelX + pad, panelY + pad, logW - pad, innerH, "BATTLE LOG");
        drawLog(state, g, panelX + pad + 14, panelY + pad + 32, logW - pad - 28, innerH - 38);

        // Action box
        drawBox(g, actX, panelY + pad, actW, innerH, actionTitle(state));
        drawActions(state, g, actX + 14, panelY + pad + 32, actW - 28, innerH - 38);
    }

    private static String actionTitle(CombatState state) {
        return switch (state.currentMenu) {
            case "main"           -> "ACTION";
            case "targetSel"      -> "ATTACK  —  Select Target";
            case "skillSel"       -> "SKILLS";
            case "skillTargetSel" -> "SKILL  —  Select Target";
            case "objSel"         -> "ITEMS";
            case "objTargetSel"   -> "ITEM  —  Select Target";
            case "result"         -> "RESULTS";
            default               -> "ACTION";
        };
    }

    private static void drawLog(CombatState state, Graphics2D g,
                                int x, int y, int w, int h) {
        g.setFont(F_LOG);
        int lineH   = g.getFontMetrics().getHeight() + 3;
        int maxRows = Math.max(1, h / lineH);
        int total   = state.log.size();

        // scroll: 0 = bottom (latest), positive = scroll up
        int scroll  = "result".equals(state.currentMenu) ? state.logScroll : 0;
        int end     = Math.max(0, total - scroll);
        int start   = Math.max(0, end - maxRows);

        for (int i = start; i < end; i++) {
            int   idx   = i - start;
            float frac  = (float)(idx + 1) / Math.max(1, end - start);
            int   alpha = "result".equals(state.currentMenu) ? 220 : (int)(80 + 155 * frac);
            g.setColor(new Color(C_TEXT.getRed(), C_TEXT.getGreen(), C_TEXT.getBlue(), alpha));
            g.drawString("· " + state.log.get(i), x, y + idx * lineH);
        }

        // Scroll indicators in result mode
        if ("result".equals(state.currentMenu)) {
            g.setFont(F_HINT);
            g.setColor(C_TEXT_DIM);
            if (scroll > 0)           g.drawString("▲", x + w - 14, y);
            if (end < total)          g.drawString("▼", x + w - 14, y + (maxRows - 1) * lineH);
        }
    }

    private static void drawActions(CombatState state, Graphics2D g,
                                    int x, int y, int w, int h) {
        int lh = 24;

        switch (state.currentMenu) {

            case "main" -> {
                g.setFont(F_OPT);
                for (int i = 0; i < state.options.length; i++) {
                    boolean sel = i == state.sel;
                    if (sel) {
                        g.setColor(C_SEL_BG);
                        g.fillRoundRect(x - 10, y + i * lh - 17, w + 10, lh, 8, 8);
                        g.setColor(C_SEL_BD);
                        g.drawRoundRect(x - 10, y + i * lh - 17, w + 10, lh, 8, 8);
                        g.setColor(C_SEL_FG);
                        g.drawString("▶  " + state.options[i], x, y + i * lh);
                    } else {
                        g.setColor(C_TEXT);
                        g.drawString("   " + state.options[i], x, y + i * lh);
                    }
                }
            }

            case "targetSel" -> {
                ArrayList<Entity> alive = aliveEnemies(state);
                g.setFont(F_SUB);
                for (int i = 0; i < alive.size(); i++) {
                    Entity  e   = alive.get(i);
                    boolean sel = i == state.sel;
                    drawSelectableRow(g, x, y + i * lh, w, lh,
                        String.format("%s   HP %d/%d", e.getNAME(), e.getHP(), e.getMAX_HP()), sel);
                }
                hint(g, x, y + h);
            }

            case "skillSel" -> {
                ArrayList<Skill> skills = state.currentActingAlly != null
                    ? state.currentActingAlly.getSkills() : new ArrayList<>();
                if (skills.isEmpty()) {
                    g.setFont(F_SUB);
                    g.setColor(C_TEXT_DIM);
                    g.drawString("(no skills)", x, y);
                } else {
                    g.setFont(F_SUB);
                    for (int i = 0; i < skills.size(); i++) {
                        Skill   sk  = skills.get(i);
                        boolean sel = i == state.subSel;
                        drawSelectableRow(g, x, y + i * lh, w, lh,
                            String.format("%-14s  MP %d", sk.getNAME(), sk.getCOST()), sel);
                    }
                }
                hint(g, x, y + h);
            }

            case "skillTargetSel" -> {
                boolean dmg = state.selectedSkill != null && "DAMAGE".equals(state.selectedSkill.getEFFECT());
                ArrayList<Entity> targets = dmg ? aliveEnemies(state) : state.allies;
                g.setFont(F_SUB);
                for (int i = 0; i < targets.size(); i++) {
                    Entity  e   = targets.get(i);
                    boolean sel = i == state.sel;
                    drawSelectableRow(g, x, y + i * lh, w, lh,
                        String.format("%s   HP %d/%d", e.getNAME(), e.getHP(), e.getMAX_HP()), sel);
                }
                hint(g, x, y + h);
            }

            case "objSel" -> {
                ArrayList<Item> items = state.currentActingAlly != null
                    ? state.currentActingAlly.getInventory() : new ArrayList<>();
                if (items.isEmpty()) {
                    g.setFont(F_SUB);
                    g.setColor(C_TEXT_DIM);
                    g.drawString("(no items)", x, y);
                } else {
                    g.setFont(F_SUB);
                    for (int i = 0; i < items.size(); i++) {
                        Item    it  = items.get(i);
                        boolean sel = i == state.subSel;
                        drawSelectableRow(g, x, y + i * lh, w, lh,
                            String.format("%-15s  x%d", it.getNAME(), it.getAMOUNT()), sel);
                    }
                }
                hint(g, x, y + h);
            }

            case "objTargetSel" -> {
                g.setFont(F_SUB);
                for (int i = 0; i < state.allies.size(); i++) {
                    Entity  e   = state.allies.get(i);
                    boolean sel = i == state.sel;
                    drawSelectableRow(g, x, y + i * lh, w, lh,
                        String.format("%s   HP %d/%d", e.getNAME(), e.getHP(), e.getMAX_HP()), sel);
                }
                hint(g, x, y + h);
            }

            case "result" -> {
                g.setFont(F_SUB);
                g.setColor(C_TEXT_DIM);
                g.drawString("Press ENTER to continue", x, y + h / 2);
            }

            default -> {
                g.setFont(F_SUB);
                g.setColor(new Color(C_TEXT_DIM.getRed(), C_TEXT_DIM.getGreen(), C_TEXT_DIM.getBlue(), 80));
                g.drawString("Waiting...", x, y);
            }
        }
    }

    // ── Drawing helpers ───────────────────────────────────────────────────────

    private static void drawSelectableRow(Graphics2D g, int x, int y, int w, int lh,
                                          String text, boolean selected) {
        if (selected) {
            g.setColor(C_SEL_BG);
            g.fillRoundRect(x - 10, y - lh + 5, w + 10, lh, 8, 8);
            g.setColor(C_SEL_BD);
            g.drawRoundRect(x - 10, y - lh + 5, w + 10, lh, 8, 8);
            g.setColor(C_SEL_FG);
            g.drawString("▶  " + text, x, y);
        } else {
            g.setColor(C_TEXT);
            g.drawString("   " + text, x, y);
        }
    }

    private static void drawArrow(Graphics2D g, int cx, int tipY) {
        // Outer glow
        int[] gxs = {cx - 13, cx + 13, cx};
        int[] gys = {tipY - 2, tipY - 2, tipY + 18};
        g.setColor(new Color(255, 215, 45, 55));
        g.fillPolygon(gxs, gys, 3);

        // Main arrow
        int[] xs = {cx - 10, cx + 10, cx};
        int[] ys = {tipY, tipY, tipY + 15};
        g.setColor(new Color(255, 215, 45, 220));
        g.fillPolygon(xs, ys, 3);
        g.setColor(new Color(255, 255, 255, 160));
        g.drawPolygon(xs, ys, 3);
    }

    private static void drawBox(Graphics2D g, int x, int y, int w, int h, String title) {
        // Fill
        g.setColor(C_BOX_BG);
        g.fillRoundRect(x, y, w, h, 16, 16);
        // Outer border
        g.setColor(C_BOX_BD);
        g.drawRoundRect(x, y, w, h, 16, 16);
        // Inner glow
        g.setColor(C_BOX_INNER);
        g.drawRoundRect(x + 2, y + 2, w - 4, h - 4, 14, 14);

        if (title != null && !title.isEmpty()) {
            g.setFont(F_HDR);
            g.setColor(C_BOX_HDR);
            g.drawString(title, x + 14, y + 18);
            g.setColor(C_DIVIDER);
            g.drawLine(x + 12, y + 24, x + w - 12, y + 24);
        }
    }

    private static void drawBar(Graphics2D g, int x, int y, int w, int h,
                                int val, int max, Color bg, Color fill) {
        g.setColor(bg);
        g.fillRoundRect(x, y, w, h, h, h);
        if (max > 0) {
            double pct = Math.max(0.0, Math.min(1.0, (double) val / max));
            int fw = (int) Math.round(w * pct);
            if (fw > 0) {
                g.setColor(fill);
                g.fillRoundRect(x, y, fw, h, h, h);
                // Subtle top shine
                g.setColor(new Color(255, 255, 255, 40));
                g.fillRoundRect(x, y, fw, h / 2 + 1, h, h);
            }
        }
        g.setColor(new Color(0, 0, 0, 100));
        g.drawRoundRect(x, y, w, h, h, h);
    }

    private static void hint(Graphics2D g, int x, int y) {
        g.setFont(F_HINT);
        g.setColor(new Color(150, 155, 175, 120));
        g.drawString("[ESC] cancel   [↑↓] navigate   [ENTER] confirm", x, y);
    }

    private static void waitForAction(CombatState state, Canvas canvas) throws InterruptedException {
        while (state.nextAction.isEmpty()) {
            renderFrame(state, canvas);
            Thread.sleep(16);
        }
    }

    private static Color hpColor(Entity e) {
        double pct = e.getMAX_HP() > 0 ? (double) e.getHP() / e.getMAX_HP() : 0;
        if (pct > 0.60) return C_HP_HI;
        if (pct > 0.30) return C_HP_MID;
        return C_HP_LO;
    }

    private static Color blend(Color a, Color b, float t) {
        return new Color(
            (int)(a.getRed()   + (b.getRed()   - a.getRed())   * t),
            (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
            (int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t)
        );
    }

    private static ArrayList<Entity> aliveEnemies(CombatState state) {
        ArrayList<Entity> list = new ArrayList<>();
        for (Entity e : state.enemies) if (e.getHP() > 0) list.add(e);
        return list;
    }
}
