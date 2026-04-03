package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import game.entity.ClassManager;
import game.entity.Entity;
import game.entity.EntityClass;
import game.item.Item;
import game.npc.Npc;
import game.quest.KillQuest;
import game.quest.Quest;
import game.sprite.Sprite;
import game.sprite.SpriteManager;
import game.utils.WindowGraphics;

enum MenuAction{
    NONE, 
    RESUME, // Resume Game
    RETURN, OPTIONS, STATUS, INVENTORY, GAME_MENU, MAIN_MENU, NEW_GAME_MENU, LOAD_MENU,//Go to Menu
    SAVE_GAME, LOAD_GAME, NEW_GAME, EXIT_GAME, // Handle Game
    NPC_DIALOG, NPC_SHOP, NPC_QUESTS,          // NPC interaction sub-menus
    QUESTS,                                    // Pause-menu quests screen
}

public class Menus{
	
    enum Nav { UP, DOWN, LEFT, RIGHT, CONFIRM, BACK, MENU }
    
    static Nav navFromKey(int keyCode) {
        return switch (keyCode) {
            case KeyEvent.VK_UP -> Nav.UP;
            case KeyEvent.VK_DOWN -> Nav.DOWN;
            case KeyEvent.VK_LEFT -> Nav.LEFT;
            case KeyEvent.VK_RIGHT -> Nav.RIGHT;
            case KeyEvent.VK_ENTER -> Nav.CONFIRM;
            case KeyEvent.VK_ESCAPE -> Nav.BACK;
            case KeyEvent.VK_E -> Nav.MENU;
            default -> null;
        };
    }

	
	interface IMenu {
		void render(Graphics2D g, int screenW, int screenH);
		MenuAction onNav(Nav nav);
		default void onKey(KeyEvent e) {}
	}
	
	static class VerticalMenu implements IMenu{
        protected final String title;
        protected final String[] items;
        protected final MenuAction[] actions;
        protected int sel = 0;

        VerticalMenu(String title, String[] items, MenuAction[] actions) {
        	if (items.length != actions.length) throw new IllegalArgumentException("items and actions must have the same length");
            this.title = title;
            this.items = items;
            this.actions = actions;
        }
        
        @Override
        public MenuAction onNav(Nav nav) {
            switch (nav) {
                case UP -> sel = (sel - 1 + items.length) % items.length;
                case DOWN -> sel = (sel + 1) % items.length;
                case CONFIRM -> {return actions[sel];}
                case BACK -> {return MenuAction.RETURN;}
                default -> {}
            }
			return MenuAction.NONE;
        }
		
		@Override
		public void render(Graphics2D g, int screenW, int screenH) {
            int boxW = screenW / 3;
            int boxH = screenH / 2;
            int x = (screenW - boxW) / 2;
            int y = (screenH - boxH) / 2;

            g.setColor(new Color(0, 0, 0, 200));
            g.fillRoundRect(x, y, boxW, boxH, 16, 16);
            g.setColor(Color.WHITE);
            g.drawRoundRect(x, y, boxW, boxH, 16, 16);

            g.drawString(title, x + 20, y + 30);

            int lineY = y + 70;
            for (int i = 0; i < items.length; i++) {
                if (i == sel) g.drawString("▶", x + 20, lineY);
                g.drawString(items[i], x + 50, lineY);
                lineY += 26;
            }

            g.drawString("UP/DOWN: move   ENTER: select", x + 20, y + boxH - 20);
		}
		
	}
	
	static class MainMenu implements IMenu {
        private final String title = "Main menu";
        private final String[] items = {"New Game", "Load Game", "Options", "Exit Game"};
        private final MenuAction[] actions = {MenuAction.NEW_GAME_MENU, MenuAction.LOAD_MENU, MenuAction.OPTIONS, MenuAction.EXIT_GAME};
        private int sel = 0;
        
    	static final Font MAIN_TITLE = new Font("Monospaced", Font.BOLD, 120);
        
        public MainMenu() {}
        
		@Override
		public void render(Graphics2D g, int screenW, int screenH) {
            int boxW = screenW / 3;
            int boxH = screenH / 2;
            int x = (screenW - boxW) / 2;
            int y = (screenH - boxH) / 2;
            
            g.setFont(MAIN_TITLE);
            g.setColor(Color.RED);
            g.drawString("TERMINAL JRPG", screenW/10, screenH/5);
            
            
            g.setFont(new Font("Monospaced", Font.PLAIN, 16));
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRoundRect(x, y, boxW, boxH, 16, 16);
            g.setColor(Color.WHITE);
            g.drawRoundRect(x, y, boxW, boxH, 16, 16);

            g.drawString(title, x + 20, y + 30);

            int lineY = y + 70;
            for (int i = 0; i < items.length; i++) {
                if (i == sel) g.drawString("▶", x + 20, lineY);
                g.drawString(items[i], x + 50, lineY);
                lineY += 26;
            }

            g.drawString("UP/DOWN: move   ENTER: select", x + 20, y + boxH - 20);			
		}

		@Override
		public MenuAction onNav(Nav nav) {
            switch (nav) {
            case UP -> sel = (sel - 1 + items.length) % items.length;
            case DOWN -> sel = (sel + 1) % items.length;
            case CONFIRM -> {return actions[sel];}
            default -> {}
            }
			return MenuAction.NONE;	
            
		}
		
	}
	
	
	// ==========================================================================
	//  CharacterCreationMenu
	// ==========================================================================

	static class CharacterCreationMenu implements IMenu {

		enum FocusRow { CLASS, SPRITE, NAME, CONFIRM }

		private FocusRow focus    = FocusRow.CLASS;
		private int      classSel  = 0;
		private int      spriteSel = 0;
		private String   name     = "";

		private static final int   MAX_NAME = 16;
		private static final int   STAT_MAX = 8;
		private static final Color COL_STR  = new Color(220, 80,  60);
		private static final Color COL_MAG  = new Color(80,  120, 220);
		private static final Color COL_DEF  = new Color(140, 140, 160);
		private static final Color COL_DEX  = new Color(60,  180, 100);

		// Result fields read by GameStateManager after MenuAction.NEW_GAME
		private static String      confirmedName;
		private static EntityClass confirmedClass;
		private static int         confirmedSpriteIdx;

		private final ArrayList<EntityClass> classes;
		private final int                    spriteCount;

		CharacterCreationMenu() {
			classes     = new ArrayList<>(ClassManager.getClasses().values());
			spriteCount = (SpriteManager.getPeople() != null) ? SpriteManager.getPeople().length : 1;
		}

		public static String      getConfirmedName()      { return confirmedName; }
		public static EntityClass getConfirmedClass()     { return confirmedClass; }
		public static int         getConfirmedSpriteIdx() { return confirmedSpriteIdx; }

		private int[] currentSprites() {
			int[] s = classes.get(classSel).getSprites();
			if (s == null || s.length == 0) {
				s = new int[spriteCount];
				for (int i = 0; i < spriteCount; i++) s[i] = i;
			}
			return s;
		}

		@Override
		public MenuAction onNav(Nav nav) {
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
					if (focus == FocusRow.CONFIRM && !name.isBlank()) return confirm();
					else focus = nextRow(focus);
				}
				case BACK -> {
					if (focus == FocusRow.CLASS) return MenuAction.RETURN;
					else focus = prevRow(focus);
				}
				default -> {}
			}
			return MenuAction.NONE;
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

		private MenuAction confirm() {
			confirmedName      = name.trim();
			confirmedClass     = classes.get(classSel);
			confirmedSpriteIdx = currentSprites()[spriteSel];
			return MenuAction.NEW_GAME;
		}

		// -----------------------------------------------------------------------
		//  Render
		// -----------------------------------------------------------------------

		@Override
		public void render(Graphics2D g, int screenW, int screenH) {
			g.setColor(new Color(0,0,0));
			g.fillRect(0, 0, screenW, screenH);

			Font headF  = new Font("Monospaced", Font.BOLD,  17);
			Font textF  = new Font("Monospaced", Font.PLAIN, 15);
			Font smallF = new Font("Monospaced", Font.PLAIN, 12);

			int panW = 520;
			int panH = screenH - 60;
			int panX = (screenW - panW) / 2;
			int panY = 30;

			g.setColor(new Color(0, 0, 0));
			g.fillRoundRect(panX, panY, panW, panH, 20, 20);

			int pad = 26;
			int cx  = panX + panW / 2;
			int y   = panY + pad -10;

			// ----- TÍTULO -----
			g.setFont(new Font("Monospaced", Font.BOLD, 20));
			g.setColor(Color.WHITE);
			drawCentered(g, "CHARACTER CREATION", cx, y + 18);
			y += 36;
			drawSep(g, panX + pad, y, panW - pad * 2);
			y += 18;

			// ----- CLASE  (◀ WARRIOR ▶) -----
			EntityClass ec          = classes.get(classSel);
			boolean     classFocused = (focus == FocusRow.CLASS);

			if (classFocused) drawFocusHighlight(g, panX + pad, y - 4, panW - pad * 2, 30);

			g.setFont(new Font("Monospaced", Font.BOLD, 19));
			String row = "◀  " + ec.getNAME().toUpperCase() + "  ▶";
			g.setColor(classFocused ? new Color(200, 180, 255) : new Color(200, 200, 215));
			drawCentered(g, row, cx, y + 20);
			y += 38;

			// ----- SPRITE -----
			boolean spriteFocused = (focus == FocusRow.SPRITE);
			int[]   validSprites  = currentSprites();
			int     sprIdx        = validSprites[Math.min(spriteSel, validSprites.length - 1)];
			int     scale         = 5;
			int     sprPx         = 32 * scale;
			int     sprX          = cx - sprPx / 2;

			Color boxBorder = spriteFocused ? new Color(200, 180, 255, 120) : new Color(255, 255, 255, 25);
			Color boxFill   = spriteFocused ? new Color(80, 60, 160, 50)    : new Color(0, 0, 0, 50);
			g.setColor(boxFill);
			g.fillRoundRect(sprX - 10, y, sprPx + 20, sprPx + 10, 12, 12);
			g.setColor(boxBorder);
			g.drawRoundRect(sprX - 10, y, sprPx + 20, sprPx + 10, 12, 12);

			Sprite[] people = SpriteManager.getPeople();
			if (people != null && sprIdx < people.length) {
				WindowGraphics.drawSprite(SpriteManager.getPerson(sprIdx), scale, sprX, y + 5, g, true, false);
			}
			y += sprPx + 14;

			g.setFont(textF);
			String sprLabel = "◀  " + (spriteSel + 1) + " / " + validSprites.length + "  ▶";
			g.setColor(spriteFocused ? new Color(200, 180, 255) : new Color(150, 150, 170));
			drawCentered(g, sprLabel, cx, y+20);
			y += 32;

			drawSep(g, panX + pad, y, panW - pad * 2);
			y += 16;

			// ----- STATS -----
			int barX   = panX + pad + 52;
			int barW   = panW - pad * 2 - 70;
			int barH   = 14;
			int rowGap = 26;

			drawStatBar(g, headF, smallF, barX, y,              barW, barH, "STR", ec.getSTR(), STAT_MAX, COL_STR);
			drawStatBar(g, headF, smallF, barX, y + rowGap,     barW, barH, "MAG", ec.getMAG(), STAT_MAX, COL_MAG);
			drawStatBar(g, headF, smallF, barX, y + rowGap * 2, barW, barH, "DEF", ec.getDEF(), STAT_MAX, COL_DEF);
			drawStatBar(g, headF, smallF, barX, y + rowGap * 3, barW, barH, "DEX", ec.getDEX(), STAT_MAX, COL_DEX);
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
			String btnTxt = confirmFocused ? "▶  CONFIRM  ◀" : "CONFIRM";
			drawCentered(g, btnTxt, cx, y + 22);

			// ----- HINT -----
			g.setFont(smallF);
			g.setColor(new Color(140, 140, 170, 150));
			FontMetrics fm = g.getFontMetrics();
			String hint = switch (focus) {
				case CLASS   -> "←→ change class   ↑↓ navigate   ESC back";
				case SPRITE  -> "←→ change sprite   ↑↓ navigate";
				case NAME    -> "type name   BACKSPACE delete   ↑↓ navigate";
				case CONFIRM -> canConfirm ? "ENTER confirm   ↑↓ navigate"
				                           : "enter a name first";
			};
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
	//  NPC Menus
	// ==========================================================================

	// --- NpcMenu: top-level interaction panel ---

	static class NpcMenu implements IMenu {

		private final Npc npc;
		private final String[]     options;
		private final MenuAction[] actions;
		private int sel = 0;

		NpcMenu(Npc npc) {
			this.npc = npc;
			ArrayList<String>     opts = new ArrayList<>(List.of("Talk"));
			ArrayList<MenuAction> acts = new ArrayList<>(List.of(MenuAction.NPC_DIALOG));
			if (npc.HasShop())  { opts.add("Shop");   acts.add(MenuAction.NPC_SHOP); }
			if (npc.HasQuest()) { opts.add("Quests"); acts.add(MenuAction.NPC_QUESTS); }
			opts.add("Leave"); acts.add(MenuAction.RETURN);
			options = opts.toArray(new String[0]);
			actions = acts.toArray(new MenuAction[0]);
		}

		@Override
		public MenuAction onNav(Nav nav) {
			return switch (nav) {
				case UP    -> { sel = (sel - 1 + options.length) % options.length; yield MenuAction.NONE; }
				case DOWN  -> { sel = (sel + 1) % options.length; yield MenuAction.NONE; }
				case CONFIRM -> actions[sel];
				case BACK    -> MenuAction.RETURN;
				default      -> MenuAction.NONE;
			};
		}

		@Override
		public void render(Graphics2D g, int screenW, int screenH) {
			Font headF = new Font("Monospaced", Font.BOLD,  17);
			Font textF = new Font("Monospaced", Font.PLAIN, 15);

			int panW = 320, panH = 60 + options.length * 36 + 20;
			int panX = (screenW - panW) / 2;
			int panY = screenH / 2 - panH / 2;

			g.setColor(new Color(18, 15, 40, 240));
			g.fillRoundRect(panX, panY, panW, panH, 16, 16);
			g.setColor(new Color(255, 255, 255, 50));
			g.drawRoundRect(panX, panY, panW, panH, 16, 16);

			// NPC name header
			g.setFont(headF);
			g.setColor(new Color(200, 180, 255));
			FontMetrics fm = g.getFontMetrics();
			g.drawString(npc.getName(), panX + (panW - fm.stringWidth(npc.getName())) / 2, panY + 28);

			g.setColor(new Color(255, 255, 255, 30));
			g.drawLine(panX + 16, panY + 38, panX + panW - 16, panY + 38);

			// Options
			g.setFont(textF);
			for (int i = 0; i < options.length; i++) {
				int oy = panY + 56 + i * 36;
				if (i == sel) {
					g.setColor(new Color(255, 255, 255, 22));
					g.fillRoundRect(panX + 12, oy - 18, panW - 24, 28, 8, 8);
					g.setColor(new Color(200, 180, 255));
				} else {
					g.setColor(new Color(200, 200, 215));
				}
				fm = g.getFontMetrics();
				g.drawString((i == sel ? "▶ " : "  ") + options[i],
						panX + (panW - fm.stringWidth((i == sel ? "▶ " : "  ") + options[i])) / 2, oy);
			}
		}
	}

	// --- DialogMenu: sequential dialog display ---

	static class DialogMenu implements IMenu {

		private final String   npcName;
		private final String[] lines;
		private int idx = 0;

		DialogMenu(Npc npc) {
			this.npcName = npc.getName();
			String[] raw = npc.getDialogs();
			this.lines = (raw != null && raw.length > 0 && !raw[0].isBlank())
					? raw : new String[]{"..."};
		}

		@Override
		public MenuAction onNav(Nav nav) {
			return switch (nav) {
				case CONFIRM -> {
					if (idx < lines.length - 1) { idx++; yield MenuAction.NONE; }
					else yield MenuAction.RETURN;
				}
				case BACK -> MenuAction.RETURN;
				default   -> MenuAction.NONE;
			};
		}

		@Override
		public void render(Graphics2D g, int screenW, int screenH) {
			Font headF  = new Font("Monospaced", Font.BOLD,  15);
			Font textF  = new Font("Monospaced", Font.PLAIN, 14);
			Font smallF = new Font("Monospaced", Font.PLAIN, 12);

			int panW = 500, panH = 130;
			int panX = (screenW - panW) / 2;
			int panY = screenH - panH - 30;

			g.setColor(new Color(18, 15, 40, 245));
			g.fillRoundRect(panX, panY, panW, panH, 14, 14);
			g.setColor(new Color(255, 255, 255, 50));
			g.drawRoundRect(panX, panY, panW, panH, 14, 14);

			// Speaker name
			g.setFont(headF);
			g.setColor(new Color(200, 180, 255));
			g.drawString(npcName, panX + 16, panY + 22);

			// Dialog text (simple word-wrap)
			g.setFont(textF);
			g.setColor(Color.WHITE);
			String text = lines[idx];
			int maxW = panW - 32;
			FontMetrics fm = g.getFontMetrics();
			int lineY = panY + 44;
			String word = "";
			String currentLine = "";
			for (char c : (text + " ").toCharArray()) {
				if (c == ' ') {
					if (fm.stringWidth(currentLine + word) > maxW) {
						g.drawString(currentLine.trim(), panX + 16, lineY);
						lineY += 18;
						currentLine = word + " ";
					} else {
						currentLine += word + " ";
					}
					word = "";
				} else {
					word += c;
				}
			}
			if (!currentLine.isBlank()) g.drawString(currentLine.trim(), panX + 16, lineY);

			// Hint + page indicator
			g.setFont(smallF);
			g.setColor(new Color(160, 160, 180, 160));
			String hint = idx < lines.length - 1 ? "ENTER: next" : "ENTER: close";
			String page = (idx + 1) + "/" + lines.length;
			g.drawString(hint, panX + 16, panY + panH - 10);
			FontMetrics fmS = g.getFontMetrics();
			g.drawString(page, panX + panW - fmS.stringWidth(page) - 16, panY + panH - 10);
		}
	}

	// --- ShopMenu: buy items from NPC ---

	static class ShopMenu implements IMenu {

		private final String          npcName;
		private final List<Item>      items;
		private final ArrayList<Entity> allies;
		private int    sel     = 0;
		private String message = "";
		private int    msgTimer = 0;

		ShopMenu(String npcName, List<Item> items, ArrayList<Entity> allies) {
			this.npcName = npcName;
			this.items   = items;
			this.allies  = allies;
		}

		@Override
		public MenuAction onNav(Nav nav) {
			if (items.isEmpty()) {
				if (nav == Nav.BACK || nav == Nav.CONFIRM) return MenuAction.RETURN;
				return MenuAction.NONE;
			}
			return switch (nav) {
				case UP    -> { sel = (sel - 1 + items.size()) % items.size(); yield MenuAction.NONE; }
				case DOWN  -> { sel = (sel + 1) % items.size(); yield MenuAction.NONE; }
				case CONFIRM -> { buy(); yield MenuAction.NONE; }
				case BACK    -> MenuAction.RETURN;
				default      -> MenuAction.NONE;
			};
		}

		private void buy() {
			if (allies.isEmpty()) return;
			Entity player = allies.get(0);
			Item item = items.get(sel);
			if (player.getGOLD() < item.getPRICE()) {
				message = "Not enough gold!"; msgTimer = 90;
			} else {
				player.setGOLD(player.getGOLD() - item.getPRICE());
				player.addToInventory(item);
				message = "Bought " + item.getNAME() + "!"; msgTimer = 90;
			}
		}

		@Override
		public void render(Graphics2D g, int screenW, int screenH) {
			if (msgTimer > 0) msgTimer--;

			Font headF  = new Font("Monospaced", Font.BOLD,  17);
			Font textF  = new Font("Monospaced", Font.PLAIN, 14);
			Font smallF = new Font("Monospaced", Font.PLAIN, 12);

			int panW = 460, rowH = 32;
			int rows = Math.max(1, items.size());
			int panH = 70 + rows * rowH + 50;
			int panX = (screenW - panW) / 2;
			int panY = (screenH - panH) / 2;

			g.setColor(new Color(18, 15, 40, 245));
			g.fillRoundRect(panX, panY, panW, panH, 16, 16);
			g.setColor(new Color(255, 255, 255, 50));
			g.drawRoundRect(panX, panY, panW, panH, 16, 16);

			// Header
			g.setFont(headF);
			g.setColor(new Color(200, 180, 255));
			FontMetrics fm = g.getFontMetrics();
			String title = npcName + " — Shop";
			g.drawString(title, panX + (panW - fm.stringWidth(title)) / 2, panY + 26);

			// Player gold
			String goldStr = allies.isEmpty() ? "" : "G: " + allies.get(0).getGOLD();
			g.setFont(smallF);
			g.setColor(new Color(255, 215, 60));
			fm = g.getFontMetrics();
			g.drawString(goldStr, panX + panW - fm.stringWidth(goldStr) - 14, panY + 26);

			g.setColor(new Color(255, 255, 255, 30));
			g.drawLine(panX + 14, panY + 36, panX + panW - 14, panY + 36);

			// Items
			if (items.isEmpty()) {
				g.setFont(textF);
				g.setColor(new Color(150, 150, 165));
				g.drawString("No items for sale.", panX + 20, panY + 64);
			} else {
				for (int i = 0; i < items.size(); i++) {
					Item it = items.get(i);
					int iy = panY + 54 + i * rowH;
					if (i == sel) {
						g.setColor(new Color(255, 255, 255, 22));
						g.fillRoundRect(panX + 10, iy - 16, panW - 20, rowH - 4, 8, 8);
					}
					g.setFont(textF);
					g.setColor(i == sel ? new Color(220, 200, 255) : new Color(200, 200, 215));
					g.drawString((i == sel ? "▶ " : "  ") + it.getNAME(), panX + 16, iy);
					g.setFont(smallF);
					g.setColor(new Color(255, 215, 60));
					fm = g.getFontMetrics();
					String price = it.getPRICE() + "G";
					g.drawString(price, panX + panW - fm.stringWidth(price) - 16, iy);
				}
			}

			// Message bar
			int barY = panY + panH - 30;
			g.setColor(new Color(255, 255, 255, 30));
			g.drawLine(panX + 14, barY - 6, panX + panW - 14, barY - 6);
			g.setFont(smallF);
			if (msgTimer > 0) {
				g.setColor(new Color(100, 220, 130));
				g.drawString(message, panX + 16, barY + 12);
			} else {
				g.setColor(new Color(140, 140, 165, 150));
				g.drawString("ENTER: buy   ESC: exit", panX + 16, barY + 12);
			}
		}
	}

	// --- QuestMenu: view and accept quests ---

	static class QuestMenu implements IMenu {

		private final String            npcName;
		private final List<Quest>       quests;
		private final ArrayList<Entity> allies;
		private int    sel     = 0;
		private String message = "";
		private int    msgTimer = 0;

		QuestMenu(String npcName, List<Quest> quests, ArrayList<Entity> allies) {
			this.npcName = npcName;
			this.quests  = quests;
			this.allies  = allies;
		}

		@Override
		public MenuAction onNav(Nav nav) {
			if (quests.isEmpty()) {
				if (nav == Nav.BACK || nav == Nav.CONFIRM) return MenuAction.RETURN;
				return MenuAction.NONE;
			}
			return switch (nav) {
				case UP    -> { sel = (sel - 1 + quests.size()) % quests.size(); yield MenuAction.NONE; }
				case DOWN  -> { sel = (sel + 1) % quests.size(); yield MenuAction.NONE; }
				case CONFIRM -> { accept(); yield MenuAction.NONE; }
				case BACK    -> MenuAction.RETURN;
				default      -> MenuAction.NONE;
			};
		}

		private void accept() {
			if (allies.isEmpty()) return;
			Entity player = allies.get(0);
			Quest q = quests.get(sel);
			if (player.getQuests().contains(q)) {
				message = "Quest already accepted!"; msgTimer = 90;
			} else {
				player.addQuest(q);
				game.quest.QuestManager.startQuest(q.getId());
				message = "Quest accepted!"; msgTimer = 90;
			}
		}

		@Override
		public void render(Graphics2D g, int screenW, int screenH) {
			if (msgTimer > 0) msgTimer--;

			Font headF  = new Font("Monospaced", Font.BOLD,  17);
			Font textF  = new Font("Monospaced", Font.PLAIN, 14);
			Font smallF = new Font("Monospaced", Font.PLAIN, 12);

			int panW = 480, panH = 320;
			int panX = (screenW - panW) / 2;
			int panY = (screenH - panH) / 2;

			g.setColor(new Color(18, 15, 40, 245));
			g.fillRoundRect(panX, panY, panW, panH, 16, 16);
			g.setColor(new Color(255, 255, 255, 50));
			g.drawRoundRect(panX, panY, panW, panH, 16, 16);

			// Header
			g.setFont(headF);
			g.setColor(new Color(200, 180, 255));
			FontMetrics fm = g.getFontMetrics();
			String title = npcName + " — Quests";
			g.drawString(title, panX + (panW - fm.stringWidth(title)) / 2, panY + 26);

			g.setColor(new Color(255, 255, 255, 30));
			g.drawLine(panX + 14, panY + 36, panX + panW - 14, panY + 36);

			if (quests.isEmpty()) {
				g.setFont(textF);
				g.setColor(new Color(150, 150, 165));
				g.drawString("No quests available.", panX + 20, panY + 64);
			} else {
				// Quest list (left panel)
				int listW = 180;
				for (int i = 0; i < quests.size(); i++) {
					int qy = panY + 54 + i * 30;
					Quest q = quests.get(i);
					boolean accepted = !allies.isEmpty() && allies.get(0).getQuests().contains(q);
					if (i == sel) {
						g.setColor(new Color(255, 255, 255, 22));
						g.fillRoundRect(panX + 10, qy - 16, listW, 26, 8, 8);
					}
					g.setFont(textF);
					g.setColor(accepted ? new Color(100, 220, 130) : (i == sel ? new Color(220, 200, 255) : new Color(200, 200, 215)));
					g.drawString((i == sel ? "▶ " : "  ") + q.getDescription().substring(0, Math.min(q.getDescription().length(), 18)), panX + 14, qy);
				}

				// Vertical separator
				g.setColor(new Color(255, 255, 255, 25));
				g.drawLine(panX + listW + 14, panY + 42, panX + listW + 14, panY + panH - 40);

				// Quest detail (right panel)
				Quest q = quests.get(sel);
				int detX = panX + listW + 24;
				int detY = panY + 54;
				int detW = panW - listW - 38;

				g.setFont(headF);
				g.setColor(new Color(200, 180, 255));
				String qType = q.getClass().getSimpleName().replace("Quest", "").toUpperCase();
				g.drawString(qType, detX, detY);
				detY += 22;

				g.setFont(smallF);
				g.setColor(Color.WHITE);
				// Word-wrap description
				fm = g.getFontMetrics();
				String[] words = q.getDescription().split(" ");
				String cur = "";
				for (String w : words) {
					if (fm.stringWidth(cur + w) > detW) {
						g.drawString(cur.trim(), detX, detY); detY += 16; cur = "";
					}
					cur += w + " ";
				}
				if (!cur.isBlank()) { g.drawString(cur.trim(), detX, detY); detY += 16; }
				detY += 8;

				if (q instanceof KillQuest kq) {
					g.setColor(new Color(255, 180, 80));
					g.drawString("Kills needed: " + kq.getRequired(), detX, detY);
					detY += 16;
				}

				g.setFont(smallF);
				g.setColor(new Color(180, 220, 255));
				g.drawString("Progress: " + q.getProgressText(), detX, detY);

				// Accept status
				boolean accepted = !allies.isEmpty() && allies.get(0).getQuests().contains(q);
				int btnY = panY + panH - 54;
				g.setColor(accepted ? new Color(100, 220, 130, 80) : new Color(110, 85, 215, 80));
				g.fillRoundRect(detX, btnY, detW, 26, 8, 8);
				g.setColor(accepted ? new Color(100, 220, 130) : new Color(200, 180, 255));
				g.setFont(headF);
				fm = g.getFontMetrics();
				String btnLabel = accepted ? "✓ Accepted" : "ENTER: Accept";
				g.drawString(btnLabel, detX + (detW - fm.stringWidth(btnLabel)) / 2, btnY + 18);
			}

			// Message / hint bar
			int barY = panY + panH - 18;
			g.setFont(smallF);
			if (msgTimer > 0) {
				g.setColor(new Color(100, 220, 130));
				g.drawString(message, panX + 16, barY);
			} else {
				g.setColor(new Color(140, 140, 165, 150));
				g.drawString("↑↓ navigate   ENTER: accept   ESC: exit", panX + 16, barY);
			}
		}
	}

	// --- LevelUpMenu: shown after leveling up outside combat ---

	static class LevelUpMenu implements IMenu {

		private final List<List<String>> pages;
		private int page = 0;

		LevelUpMenu(List<List<String>> pages) {
			this.pages = pages;
		}

		private List<String> current() { return pages.get(page); }

		@Override
		public MenuAction onNav(Nav nav) {
			if (nav == Nav.CONFIRM || nav == Nav.BACK) {
				page++;
				if (page >= pages.size()) return MenuAction.RETURN;
			}
			return MenuAction.NONE;
		}

		@Override
		public void render(Graphics2D g, int screenW, int screenH) {
			Font headF  = new Font("Monospaced", Font.BOLD,  20);
			Font textF  = new Font("Monospaced", Font.PLAIN, 14);
			Font hintF  = new Font("Monospaced", Font.PLAIN, 11);

			List<String> messages = current();
			int panW = 380;
			int lineH = 20;
			int panH = 60 + messages.size() * lineH + 30;
			int panX = (screenW - panW) / 2;
			int panY = (screenH - panH) / 2;

			// Dim background
			g.setColor(new Color(0, 0, 0, 160));
			g.fillRect(0, 0, screenW, screenH);

			// Panel
			g.setColor(new Color(20, 18, 50, 245));
			g.fillRoundRect(panX, panY, panW, panH, 18, 18);
			g.setColor(new Color(255, 220, 80, 200));
			g.drawRoundRect(panX, panY, panW, panH, 18, 18);
			g.setColor(new Color(255, 255, 255, 15));
			g.drawRoundRect(panX + 2, panY + 2, panW - 4, panH - 4, 16, 16);

			// Title + page counter
			g.setFont(headF);
			g.setColor(new Color(255, 220, 80));
			FontMetrics fm = g.getFontMetrics();
			String title = "LEVEL UP!";
			g.drawString(title, panX + (panW - fm.stringWidth(title)) / 2, panY + 34);
			if (pages.size() > 1) {
				g.setFont(hintF);
				g.setColor(new Color(200, 180, 100));
				String counter = (page + 1) + "/" + pages.size();
				fm = g.getFontMetrics();
				g.drawString(counter, panX + panW - fm.stringWidth(counter) - 12, panY + 20);
			}

			// Separator
			g.setColor(new Color(255, 220, 80, 60));
			g.drawLine(panX + 20, panY + 42, panX + panW - 20, panY + 42);

			// Messages
			g.setFont(textF);
			fm = g.getFontMetrics();
			int ty = panY + 42 + lineH;
			for (String msg : messages) {
				Color c;
				if (msg.contains("lvl") || msg.contains("Level"))
					c = new Color(255, 220, 80);
				else if (msg.contains("unlocked"))
					c = new Color(120, 200, 255);
				else
					c = new Color(200, 240, 200);
				g.setColor(c);
				g.drawString(msg, panX + (panW - fm.stringWidth(msg)) / 2, ty);
				ty += lineH;
			}

			// Hint
			g.setFont(hintF);
			g.setColor(new Color(160, 160, 180));
			String hint = page < pages.size() - 1 ? "ENTER — Next" : "ENTER — Continue";
			fm = g.getFontMetrics();
			g.drawString(hint, panX + (panW - fm.stringWidth(hint)) / 2, panY + panH - 10);
		}
	}

	// ---------- GB-style Options Menu ----------
    // Features:
    // 1) ENTER to "edit" an option
    // 2) While editing: LEFT/RIGHT moves a cursor between choices (ON OFF) or (SLOW MED FAST)
    // 3) ENTER confirms, ESC cancels
    static class OptionsMenu implements IMenu {

        // Rows:
        // 0: Music (ON/OFF with cursor)
        // 1: Text Speed (SLOW/MED/FAST)
        // 2: Back
        private static final int ROW_MUSIC = 0;
        private static final int ROW_TEXT_SPEED = 1;
        private static final int ROW_SCALE = 2;
        private static final int ROW_BACK = 3;
        private static final int ROWS = 4;

        private int selRow = 0;
        private boolean editing = false;

        // Committed indices
        private int musicIdx = 0;      // 0=ON, 1=OFF
        private int textSpeedIdx = 1;  // 0=SLOW, 1=MED, 2=FAST
        private int scaleIdx = 2;	   // 0=x1, 1= 2=x1.5, 2=x2

        // Temp indices while editing
        private int tmpMusicIdx;
        private int tmpTextSpeedIdx;
        private int tmpScaleIdx;

        private final Option music = new Option("Music", new String[]{"ON", "OFF"});
        private final Option textSpeed = new Option("Text Speed", new String[]{"SLOW", "MED", "FAST"});
        private final Option scale = new Option("Scale", new String[]{"X1", "X1.5", "X2"});

        @Override
        public MenuAction onNav(Nav nav) {
            if (!editing) {
                switch (nav) {
                    case UP -> selRow = (selRow - 1 + ROWS) % ROWS;
                    case DOWN -> selRow = (selRow + 1) % ROWS;

                    case CONFIRM -> {
                        if (selRow == ROW_BACK) { return MenuAction.RETURN;}
                        // enter edit mode
                        editing = true;
                        tmpMusicIdx = musicIdx;
                        tmpTextSpeedIdx = textSpeedIdx;
                        tmpScaleIdx = scaleIdx;
                    }

                    case BACK -> {return MenuAction.RETURN;}
                    default -> {}
                }
            }

            // editing mode
            switch (nav) {
                case LEFT -> shiftTemp(-1);
                case RIGHT -> shiftTemp(+1);

                case CONFIRM -> {
                    // commit
                    musicIdx = tmpMusicIdx;
                    textSpeedIdx = tmpTextSpeedIdx;
                    scaleIdx = tmpScaleIdx;
                    editing = false;

                    // Demo output
                    System.out.println("Settings -> Music=" + music.values[musicIdx] + ", TextSpeed=" + textSpeed.values[textSpeedIdx]);
                }

                case BACK -> {
                    // cancel
                    editing = false;
                }

                default -> {
                    // Optional: block UP/DOWN while editing (GB-like)
                }
            }
			return null;
        }

        private void shiftTemp(int delta) {
            if (selRow == ROW_MUSIC) {
                tmpMusicIdx = wrap(tmpMusicIdx + delta, music.values.length);
            } else if (selRow == ROW_TEXT_SPEED) {
                tmpTextSpeedIdx = wrap(tmpTextSpeedIdx + delta, textSpeed.values.length);
            } else if (selRow == ROW_SCALE) {
            	tmpScaleIdx = wrap(tmpScaleIdx + delta, scale.values.length);
            }
        }

        private int wrap(int v, int len) {
            int r = v % len;
            if (r < 0) r += len;
            return r;
        }

        @Override
        public void render(Graphics2D g, int screenW, int screenH) {
            int boxW = screenW / 2;
            int boxH = screenH / 2;
            int x = (screenW - boxW) / 2;
            int y = (screenH - boxH) / 2;

            g.setColor(new Color(0, 0, 0, 200));
            g.fillRoundRect(x, y, boxW, boxH, 16, 16);
            g.setColor(Color.WHITE);
            g.drawRoundRect(x, y, boxW, boxH, 16, 16);

            g.drawString("OPTIONS", x + 20, y + 30);

            int labelX = x + 50;
            int valueX = x + 240; // start of choices area
            int lineY = y + 80;

            // Row 0: Music (ON OFF with cursor)
            drawChoiceRow(g, ROW_MUSIC, music.label, music.values,
                    editing ? tmpMusicIdx : musicIdx,
                    labelX, valueX, lineY);

            lineY += 34;

            // Row 1: Text Speed (SLOW MED FAST)
            drawChoiceRow(g, ROW_TEXT_SPEED, textSpeed.label, textSpeed.values,
                    editing ? tmpTextSpeedIdx : textSpeedIdx,
                    labelX, valueX, lineY);

            lineY += 34;
            // Row 2: Text Speed (X1 X1.5 X2)
            drawChoiceRow(g, ROW_SCALE, scale.label, scale.values,
            		editing ? tmpScaleIdx: scaleIdx,
            				labelX, valueX, lineY);
            
            lineY += 34;

            // Row 3: Back
            drawBackRow(g, ROW_BACK, labelX, lineY);

            if (!editing) {
                g.drawString("UP/DOWN: move   ENTER: edit   ESC: back", x + 20, y + boxH - 20);
            } else {
                g.drawString("LEFT/RIGHT: change   ENTER: confirm   ESC: cancel", x + 20, y + boxH - 20);
            }
        }

        // Draws: Label + all choices (e.g., ON  OFF) and highlights/cursors the selected one
        private void drawChoiceRow(Graphics2D g, int row, String label, String[] choices, int chosenIdx,
                                   int labelX, int valueX, int lineY) {

            boolean rowSelected = (row == selRow);

            if (rowSelected) g.drawString("▶", labelX - 30, lineY);
            g.drawString(label, labelX, lineY);

            // Draw choices inline, like classic GB
            // Example: [ON]  OFF  or  SLOW  [MED]  FAST
            FontMetrics fm = g.getFontMetrics();

            int cx = valueX;
            for (int i = 0; i < choices.length; i++) {
                String txt = choices[i];

                boolean thisChosen = (i == chosenIdx);

                if (thisChosen) {
                    if (editing && rowSelected) {
                        // Editing: stronger highlight (filled)
                        int padX = 8, padY = 4;
                        int tw = fm.stringWidth(txt);
                        int th = fm.getAscent();

                        g.setColor(Color.WHITE);
                        g.fillRoundRect(cx - padX, lineY - th, tw + padX * 2, th + padY * 2, 8, 8);
                        g.setColor(Color.BLACK);
                        g.drawString(txt, cx, lineY);
                        g.setColor(Color.WHITE);
                    } else {
                        // Not editing: light highlight (outline)
                        int padX = 8, padY = 4;
                        int tw = fm.stringWidth(txt);
                        int th = fm.getAscent();

                        g.drawRoundRect(cx - padX, lineY - th, tw + padX * 2, th + padY * 2, 8, 8);
                        g.drawString(txt, cx, lineY);
                    }
                } else {
                    g.drawString(txt, cx, lineY);
                }

                cx += fm.stringWidth(txt) + 28; // spacing between options
            }

            // Optional: when row is selected but not editing, you can show a small hint
            if (rowSelected && !editing) {
                // (kept minimal)
            }
        }

        private void drawBackRow(Graphics2D g, int row, int labelX, int lineY) {
            boolean rowSelected = (row == selRow);

            if (rowSelected) g.drawString("▶", labelX - 30, lineY);
            g.drawString("Back", labelX, lineY);

            if (editing) g.drawString("(finish edit first)", labelX + 90, lineY);
        }

        static class Option {
            final String label;
            final String[] values;
            Option(String label, String[] values) {
                this.label = label;
                this.values = values;
            }
        }
    }

    static class LoadMenu implements IMenu{
    	private String[] items;
    	private int sel;
    	
    	public LoadMenu(String[] files) {
    		items = files;
    	}

		@Override
		public void render(Graphics2D g, int screenW, int screenH) {
            int boxW = screenW / 3;
            int boxH = screenH / 2;
            int x = (screenW - boxW) / 2;
            int y = (screenH - boxH) / 2;

            g.setColor(new Color(0, 0, 0, 200));
            g.fillRoundRect(x, y, boxW, boxH, 16, 16);
            g.setColor(Color.WHITE);
            g.drawRoundRect(x, y, boxW, boxH, 16, 16);

            g.drawString("Saves", x + 20, y + 30);

            int lineY = y + 70;
            for (int i = 0; i < items.length; i++) {
                if (i == sel) g.drawString("▶", x + 20, lineY);
                g.drawString(items[i], x + 50, lineY);
                lineY += 26;
            }
            g.drawString("UP/DOWN: move   ENTER: select", x + 20, y + boxH - 20);
			
		}

		@Override
		public MenuAction onNav(Nav nav) {
            switch (nav) {
            case UP -> sel = (sel - 1 + items.length) % items.length;
            case DOWN -> sel = (sel + 1) % items.length;
            case CONFIRM -> {
            	if(sel==0) return MenuAction.RETURN;
            	else {
            		GameStateManager.setSaveFile(items[sel]);
            		return MenuAction.LOAD_GAME;
            	}
            }
            default -> {}
            }
			return MenuAction.NONE;
		}
    	
    }
}
