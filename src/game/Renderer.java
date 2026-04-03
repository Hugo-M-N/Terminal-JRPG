package game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import game.Menus.MainMenu;
import game.entity.Entity;
import game.map.Map;
import game.map.Tileset;
import game.sprite.Sprite;
import game.sprite.SpriteManager;
import game.utils.WindowGraphics;
import game.zone.Zone;

public class Renderer {

    static final int TILE = 32;

    private static final Font FONT_SMALL  = new Font("Monospaced", Font.PLAIN, 12);
    private static final Font FONT_NORMAL = new Font("Monospaced", Font.PLAIN, 16);
    private static final Font FONT_BIG    = new Font("Monospaced", Font.BOLD, 24);

    private Font selectedFont = FONT_NORMAL;
    
    private final Canvas canvas;

    public Renderer(Canvas canvas) {
        this.canvas = canvas;
    }

    // --- Main render call, receives the full state snapshot ---

    public void render(GameStateManager state) {
        BufferStrategy bs = canvas.getBufferStrategy();
        Graphics2D g = (Graphics2D) bs.getDrawGraphics();

        g.setFont(selectedFont);

        // Clear screen
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        GameStateManager.GameMode mode = state.getGameMode();

        // Draw world (map + entities) when not in main menu or combat
        boolean showWorld = mode != GameStateManager.GameMode.MAIN_MENU
                && mode != GameStateManager.GameMode.COMBAT
                && !(state.getActiveMenu() instanceof MainMenu);

        if (showWorld && state.getCurrentZone() != null) {
            Zone zone = state.getCurrentZone();
            drawMap(zone.getMap(), Tileset.getTiles(), g);
            drawEnemies(zone.getMap(), g);
            drawNPCs(zone.getMap(), g);
            int playerSpriteIdx = (state.getAllies() != null && !state.getAllies().isEmpty())
                    ? state.getAllies().get(0).getSpriteIdx() : 0;
            drawPlayer(state.getPx(), state.getPy(), playerSpriteIdx, g);
        }

        // Overlays depending on mode
        if (mode == GameStateManager.GameMode.STATUS && !state.getAllies().isEmpty()) {
            int charSel = state.getStatusCharSel();
            Entity player = state.getAllies().get(charSel);
            WindowGraphics.drawStatusPanel(player, g, canvas.getWidth(), canvas.getHeight(),
                    state.getStatusTab(), state.getEquipSlot(), state.isEquipSelectingItem(),
                    state.getEquipItemSel(), state.getSkillSel(),
                    state.getInvSel(), state.getInvActionSel(), state.isInvSelectingAction(),
                    state.getAllies(), charSel, state.isInCharBar());
        }

        if (mode == GameStateManager.GameMode.QUESTS) {
            WindowGraphics.drawQuestPanel(g, canvas.getWidth(), canvas.getHeight(),
                    state.getQuestTab(), state.getQuestSel());
        }

        if (mode == GameStateManager.GameMode.INVENTORY && !state.getAllies().isEmpty()) {
            Entity player = state.getAllies().get(0);
            WindowGraphics.drawInventoryPanel(
                    player, g,
                    canvas.getWidth(), canvas.getHeight(),
                    state.getInvSel(), state.getInvActionSel(), state.isInvSelectingAction());
        }

        // Active menu on top of everything
        if (state.getActiveMenu() != null) {
            state.getActiveMenu().render(g, canvas.getWidth(), canvas.getHeight());
        }

        // Save toast
        if (state.isSaveToastVisible()) drawSaveToast(state.isSaveSuccess(), g, canvas.getWidth(), canvas.getHeight());

        // Debug HUD — remove before release
        drawDebugHUD(state, g);

        g.dispose();
        bs.show();
    }

    // --- World drawing ---

    private void drawMap(Map map, BufferedImage[] tiles, Graphics2D g) {
        int scaledTile = TILE * (int) Config.getSCALE();
        for (int i = 0; i < map.getHeight(); i++) {
            for (int j = 0; j < map.getWidth(); j++) {
                // Frustum cull: skip tiles outside the visible area
                int drawX = j * scaledTile;
                int drawY = i * scaledTile;
                if (drawX > canvas.getWidth() || drawX < -scaledTile) continue;
                if (drawY > canvas.getHeight() || drawY < -scaledTile) continue;

                BufferedImage tile = tiles[map.getMapTile(j, i)];
                g.drawImage(tile, drawX, drawY, scaledTile, scaledTile, null);
            }
        }
    }

    private void drawEnemies(Map map, Graphics2D g) {
        for (String s : map.getEnemies()) {
            String[] parts = s.split("-");
            if (parts.length < 2) continue;
            Sprite sprite = SpriteManager.getMonster(2);
            int x = Integer.parseInt(parts[1].split(":")[0]);
            int y = Integer.parseInt(parts[1].split(":")[1]);
            WindowGraphics.drawTiledSprite(sprite, Config.getSCALE(), x, y, g, canvas);
        }
    }

    private void drawNPCs(Map map, Graphics2D g) {
        for (String s : map.getNPCs()) {
            String[] parts = s.split("-");
            if (parts.length < 2) continue;
            Sprite sprite = SpriteManager.getPerson(39);
            int x = Integer.parseInt(parts[1].split(":")[0]);
            int y = Integer.parseInt(parts[1].split(":")[1]);
            WindowGraphics.drawTiledSprite(sprite, Config.getSCALE(), x, y, g, canvas);
        }
    }

    private void drawPlayer(int px, int py, int spriteIdx, Graphics2D g) {
        Sprite sprite = SpriteManager.getPerson(spriteIdx);
        WindowGraphics.drawTiledSprite(sprite, Config.getSCALE(), px, py, g, canvas);
    }

    // --- Save toast ---

    private void drawSaveToast(boolean success, Graphics2D g, int W, int H) {
        String msg    = success ? "Game saved!" : "Save failed!";
        Color  bg     = success ? new Color(30, 120, 50, 210) : new Color(140, 30, 30, 210);
        Color  border = success ? new Color(80, 220, 110, 180) : new Color(220, 80, 80, 180);

        g.setFont(FONT_NORMAL);
        int tw  = g.getFontMetrics().stringWidth(msg);
        int pad = 14;
        int bw  = tw + pad * 2;
        int bh  = 36;
        int bx  = (W - bw) / 2;
        int by  = H - 80;

        g.setColor(bg);
        g.fillRoundRect(bx, by, bw, bh, 12, 12);
        g.setColor(border);
        g.drawRoundRect(bx, by, bw, bh, 12, 12);
        g.setColor(Color.WHITE);
        g.drawString(msg, bx + pad, by + bh / 2 + g.getFontMetrics().getAscent() / 2 - 2);
    }

    // --- Debug HUD ---

    private void drawDebugHUD(GameStateManager state, Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(FONT_SMALL);
        g.drawString("PX:" + state.getPx() + " PY:" + state.getPy()
                + "  " + state.getGameMode(), 10, 20);
    }

    // --- Font control ---

    public void setFontSmall()  { selectedFont = FONT_SMALL; }
    public void setFontNormal() { selectedFont = FONT_NORMAL; }
    public void setFontBig()    { selectedFont = FONT_BIG; }
    public Font getSelectedFont() { return selectedFont; }
}