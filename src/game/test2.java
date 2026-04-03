package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Map;

import game.entity.ClassManager;
import game.entity.EntityClass;
import game.skill.Skill;
import game.skill.SkillManager;
import game.sprite.SpriteManager;
import game.utils.WindowGraphics;

public class test2 {

    // ---------- Config ----------
    private static final int W   = 1280;
    private static final int H   = 720;
    private static final int FPS = 60;

    private static JFrame  window;
    private static Canvas  canvas;
    private static boolean running = true;

    // ---------- Navigation ----------
    enum Nav { UP, DOWN, LEFT, RIGHT, CONFIRM, BACK }

    // ---------- Menus ----------
    private static VerticalMenu          mainMenu;
    private static OptionsMenu           optionsMenu;
    private static CharacterCreationMenu charCreationMenu;
    private static IMenu                 activeMenu;

    public static void main(String[] args) {
        SkillManager.loadSkills();
        ClassManager.LoadClasses();
        SpriteManager.loadPeople();

        initWindow();
        canvas.createBufferStrategy(3);

        mainMenu    = new VerticalMenu("MAIN MENU", new String[]{"New Game", "Load Game", "Options", "Exit Game"});
        optionsMenu = new OptionsMenu();
        activeMenu  = mainMenu;

        gameLoop();
        System.exit(0);
    }

    private static void initWindow() {
        window = new JFrame("Terminal JRPG");
        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(W, H));
        canvas.setBackground(Color.BLACK);

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.add(canvas);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        canvas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (activeMenu == null) return;
                Nav nav = navFromKey(e.getKeyCode());
                if (nav != null) activeMenu.onNav(nav);
                else             activeMenu.onKey(e);
            }
        });
        canvas.requestFocus();
    }

    private static Nav navFromKey(int keyCode) {
        return switch (keyCode) {
            case KeyEvent.VK_UP     -> Nav.UP;
            case KeyEvent.VK_DOWN   -> Nav.DOWN;
            case KeyEvent.VK_LEFT   -> Nav.LEFT;
            case KeyEvent.VK_RIGHT  -> Nav.RIGHT;
            case KeyEvent.VK_ENTER  -> Nav.CONFIRM;
            case KeyEvent.VK_ESCAPE -> Nav.BACK;
            default -> null;
        };
    }

    private static void gameLoop() {
        final long frameNs = 1_000_000_000L / FPS;
        while (running) {
            long start   = System.nanoTime();
            render();
            long sleepNs = frameNs - (System.nanoTime() - start);
            if (sleepNs > 0) {
                try { Thread.sleep(sleepNs / 1_000_000L, (int)(sleepNs % 1_000_000L)); }
                catch (InterruptedException ignored) {}
            }
        }
    }

    private static void render() {
        BufferStrategy bs = canvas.getBufferStrategy();
        if (bs == null) { canvas.createBufferStrategy(3); return; }
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        try {
            g.setColor(new Color(20, 18, 40));
            g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            activeMenu.render(g, canvas.getWidth(), canvas.getHeight());
        } finally {
            g.dispose();
        }
        bs.show();
        Toolkit.getDefaultToolkit().sync();
    }

    // ==========================================================================
    //  IMenu
    // ==========================================================================

    interface IMenu {
        void onNav(Nav nav);
        void render(Graphics2D g, int screenW, int screenH);
        default void onKey(KeyEvent e) {}
    }

    // ==========================================================================
    //  VerticalMenu
    // ==========================================================================

    static class VerticalMenu implements IMenu {
        private final String   title;
        private final String[] items;
        private int sel = 0;

        VerticalMenu(String title, String[] items) {
            this.title = title;
            this.items = items;
        }

        @Override
        public void onNav(Nav nav) {
            switch (nav) {
                case UP   -> sel = (sel - 1 + items.length) % items.length;
                case DOWN -> sel = (sel + 1) % items.length;
                case CONFIRM -> {
                    switch (items[sel]) {
                        case "New Game"  -> { charCreationMenu = new CharacterCreationMenu(); activeMenu = charCreationMenu; }
                        case "Options"   -> activeMenu = optionsMenu;
                        case "Exit Game" -> running = false;
                        default          -> System.out.println("Selected: " + items[sel]);
                    }
                }
                default -> {}
            }
        }

        @Override
        public void render(Graphics2D g, int screenW, int screenH) {
            int boxW = screenW / 3;
            int boxH = screenH / 2;
            int x    = (screenW - boxW) / 2;
            int y    = (screenH - boxH) / 2;

            g.setColor(new Color(0, 0, 0, 200));
            g.fillRoundRect(x, y, boxW, boxH, 16, 16);
            g.setColor(Color.WHITE);
            g.drawRoundRect(x, y, boxW, boxH, 16, 16);

            g.setFont(new Font("Monospaced", Font.BOLD, 18));
            g.setColor(Color.WHITE);
            g.drawString(title, x + 20, y + 34);

            g.setFont(new Font("Monospaced", Font.PLAIN, 16));
            int lineY = y + 70;
            for (int i = 0; i < items.length; i++) {
                g.setColor(i == sel ? Color.WHITE : new Color(180, 180, 180));
                if (i == sel) g.drawString("▶", x + 20, lineY);
                g.drawString(items[i], x + 50, lineY);
                lineY += 30;
            }

            g.setFont(new Font("Monospaced", Font.PLAIN, 12));
            g.setColor(new Color(160, 160, 160, 160));
            g.drawString("↑↓ move   ENTER select", x + 20, y + boxH - 16);
        }
    }

    // ==========================================================================
    //  CharacterCreationMenu — columna única centrada
    // ==========================================================================

    static class CharacterCreationMenu implements IMenu {

        enum FocusRow { CLASS, SPRITE, NAME, CONFIRM }

        private FocusRow focus    = FocusRow.CLASS;
        private int      classSel = 0;
        private int      spriteSel = 0;
        private String   name     = "";

        private static final int   MAX_NAME = 16;
        private static final int   STAT_MAX = 8;
        private static final Color COL_STR  = new Color(220, 80,  60);
        private static final Color COL_MAG  = new Color(80,  120, 220);
        private static final Color COL_DEF  = new Color(140, 140, 160);
        private static final Color COL_DEX  = new Color(60,  180, 100);

        private final ArrayList<EntityClass> classes;
        private final int                    spriteCount;

        CharacterCreationMenu() {
            classes     = new ArrayList<>(ClassManager.getClasses().values());
            spriteCount = (SpriteManager.getPeople() != null) ? SpriteManager.getPeople().length : 1;
        }

        private int[] currentSprites() {
            int[] s = classes.get(classSel).getSprites();
            if (s == null || s.length == 0) {
                s = new int[spriteCount];
                for (int i = 0; i < spriteCount; i++) s[i] = i;
            }
            return s;
        }

        @Override
        public void onNav(Nav nav) {
            int[] sprites = currentSprites();
            switch (nav) {
                case UP   -> focus = prevRow(focus);
                case DOWN -> focus = nextRow(focus);
                case LEFT -> {
                    if      (focus == FocusRow.CLASS)  { classSel  = (classSel  - 1 + classes.size()) % classes.size(); spriteSel = 0; }
                    else if (focus == FocusRow.SPRITE) { spriteSel = (spriteSel - 1 + sprites.length) % sprites.length; }
                }
                case RIGHT -> {
                    if      (focus == FocusRow.CLASS)  { classSel  = (classSel  + 1) % classes.size(); spriteSel = 0; }
                    else if (focus == FocusRow.SPRITE) { spriteSel = (spriteSel + 1) % sprites.length; }
                }
                case CONFIRM -> {
                    if (focus == FocusRow.CONFIRM && !name.isBlank()) confirm();
                    else focus = nextRow(focus);
                }
                case BACK -> {
                    if (focus == FocusRow.CLASS) activeMenu = mainMenu;
                    else focus = prevRow(focus);
                }
            }
        }

        @Override
        public void onKey(KeyEvent e) {
            if (focus != FocusRow.NAME) return;
            int code = e.getKeyCode();
            if (code == KeyEvent.VK_BACK_SPACE) {
                if (!name.isEmpty()) name = name.substring(0, name.length() - 1);
            } else {
                char c = e.getKeyChar();
                if ((Character.isLetterOrDigit(c) || c == ' ' || c == '_' || c == '-')
                        && name.length() < MAX_NAME) {
                    name += c;
                }
            }
        }

        private FocusRow nextRow(FocusRow f) {
            return switch (f) {
                case CLASS   -> FocusRow.SPRITE;
                case SPRITE  -> FocusRow.NAME;
                case NAME    -> FocusRow.CONFIRM;
                case CONFIRM -> FocusRow.CONFIRM;
            };
        }

        private FocusRow prevRow(FocusRow f) {
            return switch (f) {
                case CLASS   -> FocusRow.CLASS;
                case SPRITE  -> FocusRow.CLASS;
                case NAME    -> FocusRow.SPRITE;
                case CONFIRM -> FocusRow.NAME;
            };
        }

        private void confirm() {
            EntityClass chosen    = classes.get(classSel);
            int         spriteIdx = currentSprites()[spriteSel];
            System.out.printf("New Game -> Name: \"%s\" | Class: %s | Sprite: %d%n",
                    name.trim(), chosen.getNAME(), spriteIdx);
            // TODO: pasar a GameStateManager.NEW_GAME
            activeMenu = mainMenu;
        }

        // -----------------------------------------------------------------------
        //  Render
        // -----------------------------------------------------------------------

        @Override
        public void render(Graphics2D g, int screenW, int screenH) {
            g.setColor(new Color(15, 12, 30));
            g.fillRect(0, 0, screenW, screenH);

            Font headF  = new Font("Monospaced", Font.BOLD,  17);
            Font textF  = new Font("Monospaced", Font.PLAIN, 15);
            Font smallF = new Font("Monospaced", Font.PLAIN, 12);

            // Panel centrado
            int panW = 520;
            int panH = screenH - 60;
            int panX = (screenW - panW) / 2;
            int panY = 30;

            g.setColor(new Color(22, 20, 48, 235));
            g.fillRoundRect(panX, panY, panW, panH, 20, 20);
            g.setColor(new Color(255, 255, 255, 45));
            g.drawRoundRect(panX, panY, panW, panH, 20, 20);

            int pad = 26;
            int cx  = panX + panW / 2;
            int y   = panY + pad;

            // ----- TÍTULO -----
            g.setFont(new Font("Monospaced", Font.BOLD, 20));
            g.setColor(Color.WHITE);
            drawCentered(g, "CHARACTER CREATION", cx, y + 18);
            y += 36;
            drawSep(g, panX + pad, y, panW - pad * 2);
            y += 18;

            // ----- CLASE  (< WARRIOR >) -----
            EntityClass ec          = classes.get(classSel);
            boolean     classFocused = (focus == FocusRow.CLASS);

            if (classFocused) drawFocusHighlight(g, panX + pad, y - 4, panW - pad * 2, 30);

            g.setFont(new Font("Monospaced", Font.BOLD, 19));
            FontMetrics fm   = g.getFontMetrics();
            String      cName = ec.getNAME().toUpperCase();
            String      row   = "◀  " + cName + "  ▶";
            g.setColor(classFocused ? new Color(200, 180, 255) : new Color(200, 200, 215));
            drawCentered(g, row, cx, y + 20);
            y += 38;

            // ----- SPRITE -----
            boolean spriteFocused = (focus == FocusRow.SPRITE);
            int[]   validSprites  = currentSprites();
            int     sprIdx        = validSprites[Math.min(spriteSel, validSprites.length - 1)];
            int     scale         = 5;
            int     sprPx         = 32 * scale;   // 160 px
            int     sprX          = cx - sprPx / 2;

            // Caja del sprite
            Color boxBorder = spriteFocused ? new Color(200, 180, 255, 120) : new Color(255, 255, 255, 25);
            Color boxFill   = spriteFocused ? new Color(80, 60, 160, 50)    : new Color(0, 0, 0, 50);
            g.setColor(boxFill);
            g.fillRoundRect(sprX - 10, y, sprPx + 20, sprPx + 10, 12, 12);
            g.setColor(boxBorder);
            g.drawRoundRect(sprX - 10, y, sprPx + 20, sprPx + 10, 12, 12);

            if (SpriteManager.getPeople() != null && sprIdx < SpriteManager.getPeople().length) {
                WindowGraphics.drawSprite(SpriteManager.getPerson(sprIdx), scale, sprX, y + 5, g, true, false);
            }
            y += sprPx + 14;

            // Label < N / total > debajo del sprite
            g.setFont(textF);
            String sprLabel = "◀  " + (spriteSel + 1) + " / " + validSprites.length + "  ▶";
            g.setColor(spriteFocused ? new Color(200, 180, 255) : new Color(150, 150, 170));
            drawCentered(g, sprLabel, cx, y);
            y += 22;

            drawSep(g, panX + pad, y, panW - pad * 2);
            y += 16;

            // ----- STATS -----
            int barX   = panX + pad + 52;
            int barW   = panW - pad * 2 - 70;
            int barH   = 14;
            int rowGap = 26;

            drawStatBar(g, headF, smallF, barX, y,             barW, barH, "STR", ec.getSTR(), STAT_MAX, COL_STR);
            drawStatBar(g, headF, smallF, barX, y + rowGap,    barW, barH, "MAG", ec.getMAG(), STAT_MAX, COL_MAG);
            drawStatBar(g, headF, smallF, barX, y + rowGap * 2,barW, barH, "DEF", ec.getDEF(), STAT_MAX, COL_DEF);
            drawStatBar(g, headF, smallF, barX, y + rowGap * 3,barW, barH, "DEX", ec.getDEX(), STAT_MAX, COL_DEX);
            y += rowGap * 4 + 6;

            drawSep(g, panX + pad, y, panW - pad * 2);
            y += 14;

            // ----- STARTING SKILL (lvl 1) -----
            g.setFont(headF);
            g.setColor(new Color(190, 170, 255));
            g.drawString("Starting Skill", panX + pad, y + 14);
            y += 20;

            g.setFont(textF);
            boolean found = false;
            for (var entry : ec.getSkills().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey()).toList()) {
                if (entry.getKey() != 1 || entry.getValue() == null) continue;
                found = true;
                g.setColor(new Color(170, 210, 255, 230));
                g.drawString("•  " + entry.getValue().getNAME(), panX + pad + 8, y + 14);
                y += 18;
            }
            if (!found) {
                g.setColor(new Color(130, 130, 145, 160));
                g.drawString("   (none)", panX + pad + 8, y + 14);
                y += 18;
            }
            y += 8;

            drawSep(g, panX + pad, y, panW - pad * 2);
            y += 14;

            // ----- NOMBRE -----
            boolean nameFocused = (focus == FocusRow.NAME);

            if (nameFocused) drawFocusHighlight(g, panX + pad, y - 2, panW - pad * 2, 32);

            g.setFont(headF);
            g.setColor(nameFocused ? new Color(200, 180, 255) : new Color(200, 200, 215));
            g.drawString("Name", panX + pad, y + 20);

            int fieldX = panX + pad + 88;
            int fieldW = panW - pad * 2 - 92;
            int fieldH = 28;

            g.setColor(nameFocused ? new Color(70, 55, 140, 100) : new Color(0, 0, 0, 60));
            g.fillRoundRect(fieldX, y, fieldW, fieldH, 8, 8);
            g.setColor(nameFocused ? new Color(200, 180, 255, 200) : new Color(255, 255, 255, 50));
            g.drawRoundRect(fieldX, y, fieldW, fieldH, 8, 8);

            g.setFont(textF);
            g.setColor(Color.WHITE);
            g.drawString(name + (nameFocused ? "|" : ""), fieldX + 8, y + 19);
            y += fieldH + 14;

            // ----- BOTÓN CONFIRM -----
            boolean confirmFocused = (focus == FocusRow.CONFIRM);
            boolean canConfirm     = !name.isBlank();

            int btnW = 210;
            int btnH = 34;
            int btnX = cx - btnW / 2;

            if (confirmFocused && canConfirm) {
                g.setColor(new Color(110, 85, 215));
                g.fillRoundRect(btnX, y, btnW, btnH, 12, 12);
                g.setColor(new Color(210, 190, 255));
                g.drawRoundRect(btnX, y, btnW, btnH, 12, 12);
            } else {
                g.setColor(new Color(38, 33, 65));
                g.fillRoundRect(btnX, y, btnW, btnH, 12, 12);
                g.setColor(confirmFocused ? new Color(160, 150, 190) : new Color(255, 255, 255, 35));
                g.drawRoundRect(btnX, y, btnW, btnH, 12, 12);
            }

            g.setFont(headF);
            g.setColor(canConfirm ? Color.WHITE : new Color(120, 120, 135));
            String btnTxt = confirmFocused ? "▶  CONFIRM  ◀" : "   CONFIRM";
            drawCentered(g, btnTxt, cx, y + 22);

            // ----- HINT -----
            g.setFont(smallF);
            g.setColor(new Color(140, 140, 170, 150));
            String hint = switch (focus) {
                case CLASS   -> "←→ change class   ↑↓ navigate   ESC back";
                case SPRITE  -> "←→ change sprite   ↑↓ navigate";
                case NAME    -> "type name   BACKSPACE delete   ↑↓ navigate";
                case CONFIRM -> canConfirm ? "ENTER confirm   ↑↓ navigate"
                                           : "enter a name first";
            };
            fm = g.getFontMetrics();
            g.drawString(hint, cx - fm.stringWidth(hint) / 2, panY + panH - 10);
        }

        // --- helpers ---

        private static void drawCentered(Graphics2D g, String txt, int cx, int y) {
            FontMetrics fm = g.getFontMetrics();
            g.drawString(txt, cx - fm.stringWidth(txt) / 2, y);
        }

        private static void drawSep(Graphics2D g, int x, int y, int w) {
            g.setColor(new Color(255, 255, 255, 30));
            g.drawLine(x, y, x + w, y);
        }

        private static void drawFocusHighlight(Graphics2D g, int x, int y, int w, int h) {
            g.setColor(new Color(255, 255, 255, 18));
            g.fillRoundRect(x, y, w, h, 8, 8);
        }

        private static void drawStatBar(Graphics2D g, Font labelF, Font valF,
                                        int x, int y, int maxW, int h,
                                        String label, int value, int max, Color fill) {
            g.setFont(labelF);
            g.setColor(fill.brighter());
            g.drawString(label, x - 52, y + h - 1);

            g.setColor(new Color(255, 255, 255, 18));
            g.fillRoundRect(x, y, maxW, h, h, h);

            int fw = (int)((double) value / max * maxW);
            g.setColor(fill);
            g.fillRoundRect(x, y, fw, h, h, h);

            g.setColor(new Color(255, 255, 255, 40));
            g.fillRoundRect(x, y, fw, h / 2, h, h);

            g.setFont(valF);
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(value), x + fw + 6, y + h - 1);
        }
    }

    // ==========================================================================
    //  OptionsMenu
    // ==========================================================================

    static class OptionsMenu implements IMenu {

        private static final int ROW_MUSIC      = 0;
        private static final int ROW_TEXT_SPEED = 1;
        private static final int ROW_BACK       = 2;
        private static final int ROWS           = 3;

        private int     selRow       = 0;
        private boolean editing      = false;
        private int     musicIdx     = 0;
        private int     textSpeedIdx = 1;
        private int     tmpMusicIdx;
        private int     tmpTextSpeedIdx;

        private final Option music     = new Option("Music",      new String[]{"ON", "OFF"});
        private final Option textSpeed = new Option("Text Speed", new String[]{"SLOW", "MED", "FAST"});

        @Override
        public void onNav(Nav nav) {
            if (!editing) {
                switch (nav) {
                    case UP      -> selRow = (selRow - 1 + ROWS) % ROWS;
                    case DOWN    -> selRow = (selRow + 1) % ROWS;
                    case CONFIRM -> {
                        if (selRow == ROW_BACK) { activeMenu = mainMenu; return; }
                        editing = true; tmpMusicIdx = musicIdx; tmpTextSpeedIdx = textSpeedIdx;
                    }
                    case BACK    -> activeMenu = mainMenu;
                    default      -> {}
                }
                return;
            }
            switch (nav) {
                case LEFT    -> shiftTemp(-1);
                case RIGHT   -> shiftTemp(+1);
                case CONFIRM -> { musicIdx = tmpMusicIdx; textSpeedIdx = tmpTextSpeedIdx; editing = false; }
                case BACK    -> editing = false;
                default      -> {}
            }
        }

        private void shiftTemp(int d) {
            if      (selRow == ROW_MUSIC)      tmpMusicIdx     = wrap(tmpMusicIdx     + d, music.values.length);
            else if (selRow == ROW_TEXT_SPEED) tmpTextSpeedIdx = wrap(tmpTextSpeedIdx + d, textSpeed.values.length);
        }

        private int wrap(int v, int len) { int r = v % len; return r < 0 ? r + len : r; }

        @Override
        public void render(Graphics2D g, int screenW, int screenH) {
            int boxW = screenW / 2, boxH = screenH / 2;
            int x = (screenW - boxW) / 2, y = (screenH - boxH) / 2;

            g.setColor(new Color(0, 0, 0, 200)); g.fillRoundRect(x, y, boxW, boxH, 16, 16);
            g.setColor(Color.WHITE);              g.drawRoundRect(x, y, boxW, boxH, 16, 16);

            g.setFont(new Font("Monospaced", Font.BOLD, 18));
            g.setColor(Color.WHITE);
            g.drawString("OPTIONS", x + 20, y + 32);

            int lX = x + 50, vX = x + 240, lY = y + 80;
            drawChoiceRow(g, ROW_MUSIC,      music.label,     music.values,     editing ? tmpMusicIdx     : musicIdx,     lX, vX, lY); lY += 34;
            drawChoiceRow(g, ROW_TEXT_SPEED, textSpeed.label, textSpeed.values, editing ? tmpTextSpeedIdx : textSpeedIdx, lX, vX, lY); lY += 34;

            g.setFont(new Font("Monospaced", Font.PLAIN, 15));
            g.setColor(Color.WHITE);
            if (ROW_BACK == selRow) g.drawString("▶", lX - 30, lY);
            g.drawString("Back", lX, lY);

            g.setFont(new Font("Monospaced", Font.PLAIN, 12));
            g.setColor(new Color(160, 160, 160, 160));
            g.drawString(!editing ? "↑↓ move   ENTER edit   ESC back"
                                  : "←→ change   ENTER confirm   ESC cancel",
                    x + 20, y + boxH - 16);
        }

        private void drawChoiceRow(Graphics2D g, int row, String label, String[] choices,
                                   int chosenIdx, int lX, int vX, int lY) {
            boolean rowSel = (row == selRow);
            g.setFont(new Font("Monospaced", Font.PLAIN, 15));
            g.setColor(Color.WHITE);
            if (rowSel) g.drawString("▶", lX - 30, lY);
            g.drawString(label, lX, lY);

            FontMetrics fm = g.getFontMetrics();
            int cx = vX;
            for (int i = 0; i < choices.length; i++) {
                String txt = choices[i];
                boolean chosen = (i == chosenIdx);
                int tw = fm.stringWidth(txt), th = fm.getAscent(), px = 8, py = 4;
                if (chosen) {
                    if (editing && rowSel) {
                        g.setColor(Color.WHITE);
                        g.fillRoundRect(cx - px, lY - th, tw + px * 2, th + py * 2, 8, 8);
                        g.setColor(Color.BLACK); g.drawString(txt, cx, lY); g.setColor(Color.WHITE);
                    } else {
                        g.drawRoundRect(cx - px, lY - th, tw + px * 2, th + py * 2, 8, 8);
                        g.drawString(txt, cx, lY);
                    }
                } else { g.setColor(new Color(180, 180, 180)); g.drawString(txt, cx, lY); g.setColor(Color.WHITE); }
                cx += tw + 28;
            }
        }

        static class Option {
            final String label; final String[] values;
            Option(String l, String[] v) { label = l; values = v; }
        }
    }
}
