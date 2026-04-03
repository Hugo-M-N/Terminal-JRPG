package game.utils;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import game.entity.Entity;
import game.item.Accessory;
import game.item.Armor;
import game.item.Equipment;
import game.item.Equippable;
import game.item.Item;
import game.item.Usable;
import game.item.Weapon;
import game.quest.Quest;
import game.quest.QuestManager;
import game.quest.QuestReward;
import game.skill.Skill;
import game.sprite.Sprite;
import game.sprite.SpriteManager;

public class WindowGraphics {

	  public static void drawTiledSprite(Sprite sprite, double scale, int x, int y, Graphics2D g, Canvas canvas) {
		  int TILE = sprite.getWidth();
		  
		  if((x*TILE*scale > canvas.getWidth()) || (x*TILE*scale < TILE*scale*-1) ||
			(y*TILE*scale > canvas.getHeight()) || (y*TILE*scale < TILE*scale*-1)) return;
		  	g.drawImage(sprite.getImage(), // Sprite
		  			(int) (x*TILE*scale), // corrected X
		  			(int) (y*TILE*scale), // corrected Y
		  			(int) (sprite.getImage().getWidth() * scale), // corrected width
		  			(int) (sprite.getImage().getHeight() * scale), // corrected height
		  			null);
		  } 
	  
	  public static void drawSprite(Sprite sprite, double scale, int x, int y, Graphics2D g, boolean flipX, boolean flipY) {
		  AffineTransform old = g.getTransform();

		  AffineTransform tx = new AffineTransform();

		  tx.translate(x, y);
		  tx.scale(flipX ? -scale : scale, flipY ? -scale : scale);

		  if (flipX) tx.translate(-sprite.getImage().getWidth(), 0);
		  if (flipY) tx.translate(0, -sprite.getImage().getHeight());

		  g.drawImage(sprite.getImage(), tx, null);

		  g.setTransform(old);
	  }
	  
	  public static void drawStatusPanel(Entity e, Graphics2D g, int screenW, int screenH, int tab,
          int equipSlot, boolean equipSelectingItem, int equipItemSel, int skillSel,
          int invSel, int invActionSel, boolean invSelectingAction,
          ArrayList<Entity> allies, int charSel, boolean inCharBar) {
		    // --- dark Overlay
		    g.setColor(new Color(0, 0, 0, 140));
		    g.fillRect(0, 0, screenW, screenH);

		    // --- panel size
		    int w = 3*(screenW/5);
		    int h = 2*(screenH/3);
		    int x = (screenW - w) / 2;
		    int y = (screenH - h) / 2;
		    int pad = 22;

		    // --- Base panel
		    g.setColor(new Color(10, 10, 10, 225));
		    g.fillRoundRect(x, y, w, h, 26, 26);
		    g.setColor(new Color(0, 0, 0, 90));
		    g.drawRoundRect(x + 1, y + 3, w - 2, h - 2, 26, 26);
		    g.setColor(new Color(230, 230, 230, 170));
		    g.drawRoundRect(x, y, w, h, 26, 26);
		    g.setColor(new Color(255, 255, 255, 25));
		    g.drawRoundRect(x + 2, y + 2, w - 4, h - 4, 24, 24);

		    // --- FONTS
		    Font titleF = new Font("Monospaced", Font.BOLD, 28);
		    Font labelF = new Font("Monospaced", Font.PLAIN, 16);
		    Font valueF = new Font("Monospaced", Font.BOLD, 16);
		    Font smallF = new Font("Monospaced", Font.PLAIN, 13);

		    // --- Title
		    g.setFont(titleF);
		    g.setColor(Color.WHITE);
		    g.drawString("STATUS", x + pad, y + pad + 26);

		    // --- Hint
		    g.setFont(smallF);
		    g.setColor(new Color(200, 200, 200, 160));
		    String hint = "ESC / ENTER to close   ←→ change tab";
		    int hintW = g.getFontMetrics().stringWidth(hint);
		    g.drawString(hint, x + w - pad - hintW, y + pad + 20);

		    // --- Separator
		    int sepY = y + pad + 38;
		    g.setColor(new Color(255, 255, 255, 40));
		    g.drawLine(x + pad, sepY, x + w - pad, sepY);

		    int leftX = x + pad;

		    // =====================================================================
		    // CHARACTER SELECTION BAR (shown above content when party > 1)
		    // =====================================================================
		    boolean showCharBar = allies != null && allies.size() > 1;
		    int charBarH = showCharBar ? 34 : 0;

		    if (showCharBar) {
		        int cbY  = sepY + 6;
		        int cbW  = w - pad * 2;
		        int slotW = cbW / allies.size();

		        for (int i = 0; i < allies.size(); i++) {
		            int sx = leftX + i * slotW;
		            boolean selected = (i == charSel);

		            if (selected) {
		                g.setColor(inCharBar ? new Color(255, 220, 100, 60) : new Color(255, 255, 255, 30));
		                g.fillRoundRect(sx + 2, cbY, slotW - 4, charBarH - 4, 8, 8);
		                g.setColor(inCharBar ? new Color(255, 220, 100, 200) : new Color(255, 255, 255, 70));
		                g.drawRoundRect(sx + 2, cbY, slotW - 4, charBarH - 4, 8, 8);
		            }

		            g.setFont(selected ? new Font("Monospaced", Font.BOLD, 13) : smallF);
		            g.setColor(selected ? Color.WHITE : new Color(160, 160, 160, 160));
		            String charName = allies.get(i).getNAME();
		            FontMetrics fm2 = g.getFontMetrics();
		            int tw2 = fm2.stringWidth(charName);
		            g.drawString(charName, sx + (slotW - tw2) / 2, cbY + charBarH / 2 + 5);
		        }

		        // hint when active
		        if (inCharBar) {
		            g.setFont(smallF);
		            g.setColor(new Color(255, 220, 100, 160));
		            g.drawString("← → select   ↓ confirm", leftX, cbY + charBarH + 4);
		        }

		        // bottom border of bar
		        g.setColor(new Color(255, 255, 255, 25));
		        g.drawLine(leftX, cbY + charBarH, x + w - pad, cbY + charBarH);
		    }

		    // =====================================================================
		    // TAB BAR FOOTER (shown above content when party > 1)
		    // =====================================================================
		    String[] tabNames = {"Status", "Equip", "Skills", "Inventory"};
		    int footerH = 34;
		    int footerY = y + h - footerH - 6;
		    int tabBarW = w - pad * 2;
		    int tabW    = tabBarW / tabNames.length;

		    g.setColor(new Color(0, 0, 0, 80));
		    g.fillRoundRect(leftX, footerY, tabBarW, footerH, 10, 10);

		    for (int i = 0; i < tabNames.length; i++) {
		        int tx = leftX + i * tabW;
		        if (i == tab) {
		            g.setColor(new Color(255, 255, 255, 50));
		            g.fillRoundRect(tx + 2, footerY + 2, tabW - 4, footerH - 4, 8, 8);
		            g.setColor(new Color(255, 255, 255, 120));
		            g.drawRoundRect(tx + 2, footerY + 2, tabW - 4, footerH - 4, 8, 8);
		            g.setFont(new Font("Monospaced", Font.BOLD, 13));
		            g.setColor(Color.WHITE);
		        } else {
		            g.setFont(smallF);
		            g.setColor(new Color(180, 180, 180, 150));
		        }
		        FontMetrics fm = g.getFontMetrics();
		        int tw = fm.stringWidth(tabNames[i]);
		        g.drawString(tabNames[i], tx + (tabW - tw) / 2, footerY + footerH / 2 + 5);
		    }

		    int contentBottom = footerY - 8;

		    // =====================================================================
		    // TAB 0 – STATUS (original full layout)
		    // =====================================================================
		    if (tab == 0) {
		        int topY = sepY + charBarH + (showCharBar ? 14 : 26);

		        // Portrait
		        int portraitW = 150;
		        int portraitH = 150;
		        g.setColor(new Color(0, 0, 0, 130));
		        g.fillRoundRect(leftX, topY, portraitW, portraitH, 18, 18);
		        g.setColor(new Color(255, 255, 255, 45));
		        g.drawRoundRect(leftX, topY, portraitW, portraitH, 18, 18);
		        Sprite spr = SpriteManager.getPerson(e.getSpriteIdx());
		        int sprScale = 4;
		        int sw = spr.getWidth() * sprScale;
		        int sh = spr.getHeight() * sprScale;
		        WindowGraphics.drawSprite(spr, sprScale,
		                leftX + (portraitW - sw) / 2, topY + (portraitH - sh) / 2, g, true, false);

		        // Name / class / lvl
		        int infoX = leftX + portraitW + 30;
		        int infoY = topY + 18;
		        String className = (e.getCLASS() != null) ? e.getCLASS().getNAME() : "-";
		        g.setFont(valueF);
		        g.setColor(Color.WHITE);
		        g.drawString(e.getNAME(), infoX, infoY);
		        g.setFont(labelF);
		        g.setColor(new Color(220, 220, 220, 180));
		        g.drawString("(" + className + ")", infoX + 220, infoY);
		        g.drawString("Lvl: " + e.getLVL() + "   XP: " + e.getXP() + " / " + e.getLvlXP() + "   Gold: " + e.getGOLD(), infoX, infoY + 24);

		        // HP / MP bars
		        int barW = (w / 2) - pad - 20;
		        int barH2 = 14;
		        int hpY = topY + 70;
		        g.setFont(labelF);
		        g.setColor(Color.WHITE);
		        g.drawString("HP  " + e.getHP() + "/" + e.getMAX_HP(), infoX, hpY);
		        drawBar(g, infoX, hpY + 10, barW, barH2, e.getHP(), e.getMAX_HP(), Color.RED, Color.GREEN);
		        int mpY = hpY + 44;
		        String mpLabel = (e.getMAX_MP() <= 0) ? "MP  --" : "MP  " + e.getMP() + "/" + e.getMAX_MP();
		        g.setColor(Color.WHITE);
		        g.drawString(mpLabel, infoX, mpY);
		        drawBar(g, infoX, mpY + 10, barW, barH2, e.getMP(), e.getMAX_MP(), Color.WHITE, Color.BLUE);

		        // Attributes box (top-right)
		        int rightX = x + w / 2 + 10;
		        int boxW = (x + w - pad - rightX) / 2;
		        int boxH = 150;
		        g.setColor(new Color(0, 0, 0, 130));
		        g.fillRoundRect(x + 3 * (w / 4), topY, boxW, boxH, 18, 18);
		        g.setColor(new Color(255, 255, 255, 45));
		        g.drawRoundRect(x + 3 * (w / 4), topY, boxW, boxH, 18, 18);
		        int ax = x + 3 * (w / 4) + 16;
		        int ay = topY + 44;
		        g.setFont(valueF);
		        g.setColor(Color.WHITE);
		        g.drawString("ATTRIBUTES", ax, topY + 24);
		        g.setFont(labelF);
		        g.drawString("STR: " + e.getSTR(), ax, ay + 10);
		        if (e.getEffectiveSTR() > e.getSTR()) g.drawString("+(" + (e.getEffectiveSTR() - e.getSTR()) + ")", ax + 70, ay + 10);
		        g.drawString("MAG: " + e.getMAG(), ax, ay + 35);
		        if (e.getEffectiveMAG() > e.getMAG()) g.drawString("+(" + (e.getEffectiveMAG() - e.getMAG()) + ")", ax + 70, ay + 35);
		        g.drawString("DEF: " + e.getDEF(), ax, ay + 60);
		        if (e.getEffectiveDEF() > e.getDEF()) g.drawString("+(" + (e.getEffectiveDEF() - e.getDEF()) + ")", ax + 70, ay + 60);
		        g.drawString("DEX: " + e.getDEX(), ax, ay + 85);
		        if (e.getEffectiveDEX() > e.getDEX()) g.drawString("+(" + (e.getEffectiveDEX() - e.getDEX()) + ")", ax + 70, ay + 85);

		        // Equipment box (bottom section)
		        int eqY = topY + portraitH + 14;
		        int eqW = w - pad * 2;
		        int eqH = contentBottom - eqY;
		        g.setColor(new Color(0, 0, 0, 130));
		        g.fillRoundRect(leftX, eqY, eqW, eqH, 18, 18);
		        g.setColor(new Color(255, 255, 255, 45));
		        g.drawRoundRect(leftX, eqY, eqW, eqH, 18, 18);
		        g.setFont(valueF);
		        g.setColor(Color.WHITE);
		        g.drawString("EQUIPMENT", leftX + 16, eqY + 26);
		        g.setFont(labelF);
		        g.setColor(new Color(230, 230, 230, 190));
		        String weapon0 = (e.getWeapon()   != null) ? e.getWeapon().getNAME()   : "-";
		        String armor0  = (e.getArmor()    != null) ? e.getArmor().getNAME()    : "-";
		        String acc0    = (e.getAccesory() != null) ? e.getAccesory().getNAME() : "-";
		        int ly = eqY + 60;
		        g.drawString("Weapon : " + weapon0, leftX + 18, ly); ly += 24;
		        g.drawString("Armor  : " + armor0,  leftX + 18, ly); ly += 24;
		        g.drawString("Acc    : " + acc0,    leftX + 18, ly);

		    // =====================================================================
		    // TABS 1-3 — compact header + full body
		    // =====================================================================
		    } else {
		        int topY = sepY + charBarH + (showCharBar ? 6 : 16);
		        String className = (e.getCLASS() != null) ? e.getCLASS().getNAME() : "-";

		        // Compact one-line header
		        g.setFont(labelF);
		        g.setColor(new Color(220, 220, 220, 200));
		        g.drawString(e.getNAME() + "  (" + className + ")  Lvl " + e.getLVL(), leftX, topY + 16);
		        String hpMp = "HP " + e.getHP() + "/" + e.getMAX_HP() + "   MP " + e.getMP() + "/" + e.getMAX_MP();
		        g.drawString(hpMp, leftX + (w - pad * 2) / 2, topY + 16);
		        g.setColor(new Color(255, 255, 255, 30));
		        g.drawLine(leftX, topY + 24, x + w - pad, topY + 24);

		        int bodyX = leftX;
		        int bodyY = topY + 32;
		        int bodyW = w - pad * 2;
		        int bodyH = contentBottom - bodyY;

		        if (tab == 1) {
		            drawEquipTab(e, g, bodyX, bodyY, bodyW, bodyH,
		                    equipSlot, equipSelectingItem, equipItemSel, labelF, valueF, smallF);
		        } else if (tab == 2) {
		            drawSkillsTab(e, g, bodyX, bodyY, bodyW, bodyH,
		                    skillSel, labelF, valueF, smallF);
		        } else if (tab == 3) {
		            drawInventoryTabContent(e, g, bodyX, bodyY, bodyW, bodyH,
		                    invSel, invActionSel, invSelectingAction, labelF, valueF, smallF);
		        }
		    }
		}

		// ---------- tab body helpers ----------

		private static void drawEquipTab(Entity e, Graphics2D g, int bx, int by, int bw, int bh,
		        int equipSlot, boolean selectingItem, int equipItemSel,
		        Font labelF, Font valueF, Font smallF) {

		    String[] slotLabels = {"Weapon  ", "Armor   ", "Acc     "};
		    Item[] equipped = {e.getWeapon(), e.getArmor(), e.getAccesory()};
		    int rowH = 54;

		    for (int i = 0; i < 3; i++) {
		        int ry = by + i * rowH;
		        boolean sel = (i == equipSlot);

		        if (sel) {
		            g.setColor(new Color(255, 255, 255, 28));
		            g.fillRoundRect(bx, ry, bw, rowH - 4, 10, 10);
		            g.setColor(new Color(255, 220, 100, 100));
		            g.drawRoundRect(bx, ry, bw, rowH - 4, 10, 10);
		        }

		        g.setFont(labelF);
		        g.setColor(sel ? new Color(255, 220, 100) : new Color(180, 200, 255));
		        g.drawString((sel ? "▶ " : "  ") + slotLabels[i] + ":", bx + 10, ry + 22);

		        g.setFont(valueF);
		        g.setColor(equipped[i] != null ? Color.WHITE : new Color(140, 140, 140, 160));
		        g.drawString(equipped[i] != null ? equipped[i].getNAME() : "(none)", bx + 160, ry + 22);

		        if (equipped[i] instanceof Equipment eq) {
		            String bonus = bonusLine(eq.getBonusSTR(), eq.getBonusMAG(), eq.getBonusDEF(), eq.getBonusDEX());
		            if (!bonus.isEmpty()) {
		                g.setFont(smallF);
		                g.setColor(new Color(100, 220, 120, 210));
		                g.drawString(bonus, bx + 160, ry + 40);
		            }
		        }
		    }

		    g.setFont(smallF);
		    g.setColor(new Color(180, 180, 180, 130));
		    g.drawString("ENTER: change   ESC: close", bx + 10, by + 3 * rowH + 16);

		    // Item picker sub-panel
		    if (selectingItem) {
		        ArrayList<Item> filtered = new ArrayList<>();
		        for (Item item : e.getInventory()) {
		            if (equipSlot == 0 && item instanceof Weapon) filtered.add(item);
		            else if (equipSlot == 1 && item instanceof Armor) filtered.add(item);
		            else if (equipSlot == 2 && item instanceof Accessory) filtered.add(item);
		        }
		        boolean hasEquipped = equipped[equipSlot] != null;

		        ArrayList<String> list = new ArrayList<>();
		        if (hasEquipped) list.add("Unequip");
		        for (Item it : filtered) list.add(it.getNAME() + " x" + it.getAMOUNT());
		        if (list.isEmpty()) list.add("(sin items disponibles)");

		        int spW = bw * 2 / 3;
		        int lineH = 24;
		        int spH = Math.min(list.size() * lineH + 50, bh - 10);
		        int spX = bx + bw - spW - 4;
		        int spY = Math.min(by + equipSlot * rowH, by + bh - spH - 4);

		        g.setColor(new Color(12, 14, 30, 235));
		        g.fillRoundRect(spX, spY, spW, spH, 14, 14);
		        g.setColor(new Color(255, 220, 100, 160));
		        g.drawRoundRect(spX, spY, spW, spH, 14, 14);

		        String[] slotTitles = {"Weapon", "Armor", "Accessory"};
		        g.setFont(valueF);
		        g.setColor(new Color(255, 220, 100));
		        g.drawString(slotTitles[equipSlot], spX + 12, spY + 22);
		        g.setColor(new Color(255, 255, 255, 30));
		        g.drawLine(spX + 8, spY + 28, spX + spW - 8, spY + 28);

		        int itemY = spY + 46;
		        for (int i = 0; i < list.size(); i++) {
		            boolean sel = (i == equipItemSel);
		            if (sel) {
		                g.setColor(new Color(255, 255, 255, 40));
		                g.fillRoundRect(spX + 6, itemY - 14, spW - 12, lineH - 2, 6, 6);
		                g.setFont(valueF);
		                g.setColor(Color.WHITE);
		            } else {
		                g.setFont(labelF);
		                g.setColor(new Color(210, 210, 210, 190));
		            }
		            g.drawString((sel ? "▶ " : "  ") + list.get(i), spX + 12, itemY);
		            itemY += lineH;
		        }
		    }
		}

		private static void drawSkillsTab(Entity e, Graphics2D g, int bx, int by, int bw, int bh,
		        int skillSel, Font labelF, Font valueF, Font smallF) {

		    ArrayList<Skill> skills = e.getSkills();
		    if (skills == null || skills.isEmpty()) {
		        g.setFont(labelF);
		        g.setColor(new Color(180, 180, 180, 160));
		        g.drawString("No has aprendido habilidades.", bx, by + 30);
		        return;
		    }

		    int listH = bh * 2 / 3;
		    int rowH  = 28;
		    int maxVisible = listH / rowH;

		    int scrollTop = 0;
		    if (skills.size() > maxVisible) {
		        scrollTop = Math.max(0, skillSel - maxVisible / 2);
		        scrollTop = Math.min(scrollTop, skills.size() - maxVisible);
		    }

		    for (int i = scrollTop; i < Math.min(skills.size(), scrollTop + maxVisible); i++) {
		        Skill sk = skills.get(i);
		        boolean sel = (i == skillSel);
		        int ry = by + (i - scrollTop) * rowH;

		        if (sel) {
		            g.setColor(new Color(255, 255, 255, 28));
		            g.fillRoundRect(bx, ry, bw, rowH - 2, 8, 8);
		            g.setColor(new Color(100, 255, 180, 90));
		            g.drawRoundRect(bx, ry, bw, rowH - 2, 8, 8);
		        }

		        boolean isCombat = "DAMAGE".equalsIgnoreCase(sk.getEFFECT());
		        g.setFont(labelF);
		        g.setColor(sel ? Color.WHITE : new Color(210, 210, 210, 200));
		        g.drawString((sel ? "▶ " : "  ") + sk.getNAME(), bx + 8, ry + 20);
		        g.setFont(smallF);
		        g.setColor(new Color(160, 220, 255, 200));
		        g.drawString("MP:" + sk.getCOST(), bx + 220, ry + 20);
		        g.setColor(isCombat ? new Color(255, 140, 140, 180) : new Color(140, 255, 180, 180));
		        g.drawString("[" + sk.getEFFECT() + "]", bx + 280, ry + 20);
		    }

		    // Detail area
		    int detailY = by + listH + 14;
		    g.setColor(new Color(255, 255, 255, 20));
		    g.drawLine(bx, detailY - 8, bx + bw, detailY - 8);

		    if (skillSel < skills.size()) {
		        Skill sk = skills.get(skillSel);
		        boolean isCombat = "DAMAGE".equalsIgnoreCase(sk.getEFFECT());
		        boolean canUse   = !isCombat && e.getMP() >= sk.getCOST();

		        g.setFont(valueF);
		        g.setColor(Color.WHITE);
		        g.drawString(sk.getNAME(), bx, detailY + 18);
		        g.setFont(smallF);
		        g.setColor(new Color(200, 200, 200, 180));
		        String desc = safe(sk.getDESCRIPTION());
		        if (!desc.isEmpty()) drawWrappedText(g, desc, bx, detailY + 36, bw, 16);

		        if (isCombat) {
		            g.setColor(new Color(255, 140, 140, 200));
		            g.drawString("Solo disponible en combate", bx, detailY + 60);
		        } else if (!canUse) {
		            g.setColor(new Color(255, 200, 100, 200));
		            g.drawString("MP insuficiente", bx, detailY + 60);
		        } else {
		            g.setColor(new Color(100, 255, 180, 200));
		            g.drawString("ENTER: usar", bx, detailY + 60);
		        }
		    }
		}

		private static void drawInventoryTabContent(Entity e, Graphics2D g, int bx, int by, int bw, int bh,
		        int itemSel, int actionSel, boolean selectingAction,
		        Font labelF, Font valueF, Font smallF) {

		    ArrayList<Item> inv = e.getInventory();
		    if (inv == null) inv = new ArrayList<>();

		    int leftW  = (int)(bw * 0.60);
		    int rightW = bw - leftW - 10;
		    int rightX = bx + leftW + 10;
		    int infoH  = (int)(bh * 0.70);
		    int actH   = bh - infoH - 10;

		    drawBox(g, bx,     by,            leftW,  bh,    "Inventario", valueF);
		    drawBox(g, rightX, by,            rightW, infoH, "Info",       valueF);
		    drawBox(g, rightX, by + infoH + 10, rightW, actH, "",          valueF);

		    if (!inv.isEmpty()) itemSel = Math.max(0, Math.min(itemSel, inv.size() - 1));

		    int listX  = bx + 14;
		    int listY  = by + 50;
		    int lineH  = 22;

		    if (inv.isEmpty()) {
		        g.setFont(smallF);
		        g.setColor(new Color(200, 200, 200, 140));
		        g.drawString("(empty)", listX, listY);
		    } else {
		        int maxVis = (bh - 58) / lineH;
		        int scrollTop = 0;
		        if (inv.size() > maxVis) {
		            scrollTop = Math.max(0, itemSel - maxVis / 2);
		            scrollTop = Math.min(scrollTop, inv.size() - maxVis);
		        }
		        for (int i = scrollTop; i < Math.min(inv.size(), scrollTop + maxVis); i++) {
		            Item it = inv.get(i);
		            boolean sel = (i == itemSel) && !selectingAction;
		            int iy = listY + (i - scrollTop) * lineH;
		            if (sel) {
		                g.setColor(new Color(255, 255, 255, 40));
		                g.fillRoundRect(listX - 8, iy - 14, leftW - 16, lineH, 8, 8);
		                g.setColor(Color.WHITE);
		            } else {
		                g.setColor(new Color(230, 230, 230, 210));
		            }
		            g.setFont(labelF);
		            g.drawString((sel ? "-> " : "   ") + it.getNAME(), listX, iy);
		            g.setColor(new Color(180, 200, 255, 200));
		            g.setFont(smallF);
		            g.drawString("x" + it.getAMOUNT(), bx + leftW - 40, iy);
		        }
		    }

		    // Right: info
		    int infoX = rightX + 14;
		    int infoY = by + 50;
		    if (!inv.isEmpty()) {
		        Item it = inv.get(itemSel);
		        g.setFont(valueF);
		        g.setColor(Color.WHITE);
		        g.drawString(it.getNAME(), infoX, infoY);
		        g.setFont(smallF);
		        g.setColor(new Color(200, 200, 200, 170));
		        drawWrappedText(g, safe(it.getDESC()), infoX, infoY + 20, rightW - 28, 15);

		        // Actions
		        ArrayList<String> actions = Item.getItemOptions(it);
		        actionSel = Math.max(0, Math.min(actionSel, actions.size() - 1));
		        int actX = rightX + 14;
		        int actY = by + infoH + 10 + 32;
		        g.setFont(labelF);
		        for (int i = 0; i < actions.size(); i++) {
		            boolean sel = selectingAction && (i == actionSel);
		            if (sel) {
		                g.setColor(new Color(255, 255, 255, 40));
		                g.fillRoundRect(actX - 8, actY + i * 22 - 16, rightW - 24, 20, 8, 8);
		                g.setColor(Color.WHITE);
		                g.drawString("-> " + actions.get(i), actX, actY + i * 22);
		            } else {
		                g.setColor(new Color(220, 220, 220, 190));
		                g.drawString("   " + actions.get(i), actX, actY + i * 22);
		            }
		        }
		    }

		    g.setFont(smallF);
		    g.setColor(new Color(180, 180, 180, 120));
		    g.drawString("ENTER select   ESC back   ↑↓ navigate", bx, by + bh - 4);
		}

		// ---------- helpers ----------

		private static String bonusLine(int str, int mag, int def, int dex) {
		    StringBuilder sb = new StringBuilder();
		    if (str != 0) sb.append("STR").append(str > 0 ? "+" : "").append(str).append("  ");
		    if (mag != 0) sb.append("MAG").append(mag > 0 ? "+" : "").append(mag).append("  ");
		    if (def != 0) sb.append("DEF").append(def > 0 ? "+" : "").append(def).append("  ");
		    if (dex != 0) sb.append("DEX").append(dex > 0 ? "+" : "").append(dex).append("  ");
		    return sb.toString().trim();
		}

		private static void drawBar(Graphics2D g, int x, int y, int w, int h,
		                            int value, int max, Color bg, Color fill) {
		    g.setColor(bg);
		    g.fillRoundRect(x, y, w, h, 10, 10);

		    // Si no aplica (MP 0/0), deja una barra neutra
		    if (max <= 0) {
		        g.setColor(new Color(80, 80, 80, 140));
		        g.fillRoundRect(x, y, w, h, 10, 10);
		        g.setColor(new Color(0, 0, 0, 120));
		        g.drawRoundRect(x, y, w, h, 10, 10);
		        return;
		    }

		    double pct = Math.max(0, Math.min(1, (double) value / (double) max));
		    int fw = (int) Math.round(w * pct);

		    g.setColor(fill);
		    g.fillRoundRect(x, y, fw, h, 10, 10);

		    g.setColor(new Color(0, 0, 0, 120));
		    g.drawRoundRect(x, y, w, h, 10, 10);
		}

		public static void drawInventoryPanel(Entity e, Graphics2D g,int screenW, int screenH,int itemSel, int actionSel,boolean selectingAction) {
			// Overlay
			g.setColor(new Color(0,0,0,140));
			g.fillRect(0,0,screenW,screenH);
			
			// Panel size
			int w = Math.min(900, screenW - 140);
			int h = Math.min(560, screenH - 140);
			int x = (screenW - w)/2;
			int y = (screenH - h)/2;
			int pad = 18;
			
			// Panel bg + borders (similar al Status)
			g.setColor(new Color(10,10,10,225));
			g.fillRoundRect(x,y,w,h,22,22);
			
			g.setColor(new Color(0,0,0,90));
			g.drawRoundRect(x+1,y+3,w-2,h-2,22,22);
			
			g.setColor(new Color(230,230,230,170));
			g.drawRoundRect(x,y,w,h,22,22);
			
			g.setColor(new Color(255,255,255,25));
			g.drawRoundRect(x+2,y+2,w-4,h-4,20,20);
			
			// Fonts
			Font titleF = new Font("Monospaced", Font.BOLD, 24);
			Font textF  = new Font("Monospaced", Font.PLAIN, 16);
			Font smallF = new Font("Monospaced", Font.PLAIN, 13);
			
			// Layout columns (como tu mock)
			int leftW = (int)(w * 0.67);
			int rightW = w - leftW - 10;
			int leftX = x + pad;
			int leftY = y + pad;
			int rightX = x + leftW + 10;
			int rightY = y + pad;
			
			int innerH = h - pad*2;
			
			// Boxes heights on right
			int infoH = (int)(innerH * 0.72);
			int actH  = innerH - infoH - 10;
			
			// Draw list box (left)
			drawBox(g, leftX, leftY, leftW - pad, innerH, "Inventory", titleF);
			
			// Draw info box (right top)
			drawBox(g, rightX, rightY, rightW, infoH, "Item info", titleF);
			
			// Draw actions box (right bottom)
			drawBox(g, rightX, rightY + infoH + 10, rightW, actH, "", titleF);
			
			// Content
			ArrayList<Item> inv = e.getInventory();
			if (inv == null) inv = new ArrayList<>();
			
			// Clamp selection
			if (inv.size() == 0) itemSel = 0;
			else itemSel = Math.max(0, Math.min(itemSel, inv.size()-1));
			
			// Left list content
			g.setFont(textF);
			g.setColor(new Color(230,230,230,210));
			
			int listStartX = leftX + 16;
			int listStartY = leftY + 56;
			int lineH = g.getFontMetrics().getHeight() + 4;
			
			if (inv.size() == 0) {
				g.setColor(new Color(200,200,200,140));
				g.drawString("(empty)", listStartX, listStartY);
			} else {
				// scroll simple si hay muchos
				int maxVisible = (innerH - 72) / lineH;
				int scrollTop = 0;
				if (inv.size() > maxVisible) {
					scrollTop = Math.max(0, itemSel - maxVisible/2);
					scrollTop = Math.min(scrollTop, inv.size() - maxVisible);
				}
				int end = Math.min(inv.size(), scrollTop + maxVisible);
				
				for (int i = scrollTop; i < end; i++) {
					Item it = inv.get(i);
					String name = it.getNAME();
					int amt = it.getAMOUNT();
					
					boolean selected = (i == itemSel) && !selectingAction;
					if (selected) {
						g.setColor(new Color(255,255,255,40));
						g.fillRoundRect(listStartX - 10, listStartY + (i-scrollTop)*lineH - lineH + 6,
								leftW - pad - 40, lineH, 10, 10);
						g.setColor(Color.WHITE);
					} else {
						g.setColor(new Color(230,230,230,210));
					}
					
					String prefix = (selected ? "-> " : "   ");
					g.drawString(prefix + name + "  x" + amt, listStartX, listStartY + (i-scrollTop)*lineH);
				}
				
				// scrollbar mini
				if (inv.size() > maxVisible) {
					int barX = leftX + leftW - pad - 18;
					int barY = leftY + 56;
					int barH = innerH - 72;
					g.setColor(new Color(255,255,255,25));
					g.fillRoundRect(barX, barY, 6, barH, 6, 6);
					
					double pct = (double)maxVisible / inv.size();
					int knobH = Math.max(18, (int)(barH * pct));
					double posPct = (double)scrollTop / (inv.size() - maxVisible);
					int knobY = barY + (int)((barH - knobH) * posPct);
					
					g.setColor(new Color(255,255,255,90));
					g.fillRoundRect(barX, knobY, 6, knobH, 6, 6);
				}
			}
			
			// Right info content (selected item)
			int infoTextX = rightX + 16;
			int infoTextY = rightY + 56;
			
			g.setFont(textF);
			g.setColor(new Color(230,230,230,210));
			
			if (inv.size() == 0) {
				g.setColor(new Color(200,200,200,140));
				g.drawString("No items.", infoTextX, infoTextY);
			} else {
				Item it = inv.get(itemSel);
				g.setColor(Color.WHITE);
				g.drawString(it.getNAME(), infoTextX, infoTextY);
				
				g.setFont(smallF);
				g.setColor(new Color(200,200,200,170));
				String desc = safe(it.getDESC()); // si no tienes getDESC, cambia por it.getINFO() o lo que uses
				drawWrappedText(g, desc, infoTextX, infoTextY + 22, rightW - 32, 16);
			}
			
			// Actions content
			
			ArrayList<String> actions = new ArrayList<String>();
			if(e.getInventory().get(itemSel) instanceof Usable) actions.add("Use"); 
			if(e.getInventory().get(itemSel) instanceof Equippable) {
				actions.add("Equip"); 
				actions.add("Unequip");
			}
			actions.add("Discard");
			actionSel = Math.max(0, Math.min(actionSel, actions.size()-1));
			
			int actX = rightX + 16;
			int actY = rightY + infoH + 10 + 34;
			
			g.setFont(textF);
			for (int i=0;i<actions.size();i++) {
				boolean selected = selectingAction && (i==actionSel);
				if (selected) {
					g.setColor(new Color(255,255,255,40));
					g.fillRoundRect(actX - 10, actY + i*24 - 18, rightW - 32, 22, 10, 10);
					g.setColor(Color.WHITE);
					g.drawString("-> " + actions.get(i), actX, actY + i*24);
				} else {
					g.setColor(new Color(220,220,220,190));
					g.drawString("   " + actions.get(i), actX, actY + i*24);
				}
			}
			
			// Footer hint
			g.setFont(smallF);
			g.setColor(new Color(200,200,200,120));
			g.drawString("ENTER: select   ESC: back   ↑↓/←→ navigate", x + pad, y + h - pad);
		}
		
		// --- helpers ---
		private static void drawBox(Graphics2D g, int x, int y, int w, int h, String title, Font titleF) {
			g.setColor(new Color(0,0,0,130));
			g.fillRoundRect(x, y, w, h, 18, 18);
			g.setColor(new Color(255,255,255,45));
			g.drawRoundRect(x, y, w, h, 18, 18);
			
			if (title != null && !title.isEmpty()) {
				g.setFont(titleF);
				g.setColor(Color.WHITE);
				g.drawString(title, x + 14, y + 26);
				
				g.setColor(new Color(255,255,255,30));
				g.drawLine(x + 12, y + 34, x + w - 12, y + 34);
			}
		}
		
		private static String safe(String s) {
			return (s == null) ? "" : s;
		}
		
		private static void drawWrappedText(Graphics2D g, String text, int x, int y, int maxW, int lineH) {
			if (text == null) return;
			FontMetrics fm = g.getFontMetrics();
			String[] words = text.split("\\s+");
			StringBuilder line = new StringBuilder();
			int cy = y;
			
			for (String word : words) {
				String test = line.length() == 0 ? word : (line + " " + word);
				if (fm.stringWidth(test) > maxW) {
					g.drawString(line.toString(), x, cy);
					line.setLength(0);
					line.append(word);
					cy += lineH;
				} else {
					line.setLength(0);
					line.append(test);
				}
			}
			if (line.length() > 0) g.drawString(line.toString(), x, cy);
		}


	public static void drawQuestPanel(Graphics2D g, int screenW, int screenH, int questTab, int questSel) {
		Font headF  = new Font("Monospaced", Font.BOLD,  18);
		Font tabF   = new Font("Monospaced", Font.BOLD,  14);
		Font textF  = new Font("Monospaced", Font.PLAIN, 13);
		Font smallF = new Font("Monospaced", Font.PLAIN, 11);

		// Overlay
		g.setColor(new Color(0, 0, 0, 140));
		g.fillRect(0, 0, screenW, screenH);

		int w = Math.min(820, screenW - 100);
		int h = Math.min(500, screenH - 100);
		int x = (screenW - w) / 2;
		int y = (screenH - h) / 2;
		int pad = 16;

		// Panel background
		g.setColor(new Color(10, 10, 10, 225));
		g.fillRoundRect(x, y, w, h, 22, 22);
		g.setColor(new Color(0, 0, 0, 90));
		g.drawRoundRect(x + 1, y + 3, w - 2, h - 2, 22, 22);
		g.setColor(new Color(230, 230, 230, 170));
		g.drawRoundRect(x, y, w, h, 22, 22);

		// Title
		g.setFont(headF);
		g.setColor(new Color(200, 180, 255));
		FontMetrics fm = g.getFontMetrics();
		String title = "QUESTS";
		g.drawString(title, x + (w - fm.stringWidth(title)) / 2, y + 30);

		// Tabs
		String[] tabs = {"Active", "Completed"};
		Color[] tabColors = {new Color(100, 200, 255), new Color(120, 220, 130)};
		int tabW = 130, tabH = 26, tabY = y + 40;
		int tabsX = x + (w - tabs.length * tabW - (tabs.length - 1) * 6) / 2;
		for (int i = 0; i < tabs.length; i++) {
			int tx = tabsX + i * (tabW + 6);
			if (i == questTab) {
				g.setColor(tabColors[i].darker());
				g.fillRoundRect(tx, tabY, tabW, tabH, 8, 8);
				g.setColor(tabColors[i]);
				g.drawRoundRect(tx, tabY, tabW, tabH, 8, 8);
			} else {
				g.setColor(new Color(255, 255, 255, 20));
				g.fillRoundRect(tx, tabY, tabW, tabH, 8, 8);
				g.setColor(new Color(180, 180, 180, 100));
				g.drawRoundRect(tx, tabY, tabW, tabH, 8, 8);
			}
			g.setFont(tabF);
			g.setColor(i == questTab ? Color.WHITE : new Color(160, 160, 175));
			fm = g.getFontMetrics();
			g.drawString(tabs[i], tx + (tabW - fm.stringWidth(tabs[i])) / 2, tabY + 18);
		}

		// Divider below tabs
		int bodyY = tabY + tabH + 10;
		g.setColor(new Color(255, 255, 255, 25));
		g.drawLine(x + pad, bodyY, x + w - pad, bodyY);
		bodyY += 8;

		java.util.List<Quest> list = questTab == 0 ? QuestManager.getActive() : QuestManager.getCompleted();

		if (list.isEmpty()) {
			g.setFont(textF);
			g.setColor(new Color(140, 140, 155));
			String msg = questTab == 0 ? "No active quests." : "No completed quests.";
			fm = g.getFontMetrics();
			g.drawString(msg, x + (w - fm.stringWidth(msg)) / 2, bodyY + 40);
		} else {
			// Quest list (left column)
			int listW = 230;
			int listX = x + pad;
			int itemH = 28;
			for (int i = 0; i < list.size(); i++) {
				Quest q = list.get(i);
				int qy = bodyY + i * itemH;
				if (i == questSel) {
					g.setColor(new Color(255, 255, 255, 20));
					g.fillRoundRect(listX - 4, qy, listW + 4, itemH - 2, 8, 8);
				}
				g.setFont(textF);
				Color qColor = questTab == 1 ? new Color(120, 220, 130)
						: (i == questSel ? new Color(220, 200, 255) : new Color(200, 200, 215));
				g.setColor(qColor);
				String prefix = i == questSel ? "▶ " : "  ";
				String label  = prefix + truncate(q.getTitle(), 20);
				g.drawString(label, listX, qy + 18);
			}

			// Vertical separator
			int sepX = x + pad + listW + 8;
			g.setColor(new Color(255, 255, 255, 25));
			g.drawLine(sepX, bodyY, sepX, y + h - pad);

			// Quest detail (right column)
			int detX = sepX + 14;
			int detW = w - (sepX - x) - 14 - pad;
			int detY = bodyY;

			if (questSel < list.size()) {
				Quest q = list.get(questSel);
				boolean ready = questTab == 0 && q.checkCompletion();

				// Title
				g.setFont(headF);
				g.setColor(ready ? new Color(120, 230, 140) : new Color(200, 180, 255));
				fm = g.getFontMetrics();
				String titleStr = truncate(q.getTitle(), 28);
				g.drawString(titleStr, detX, detY + 18);
				if (ready) {
					g.setFont(smallF);
					g.setColor(new Color(120, 230, 140));
					g.drawString("  Ready!", detX + fm.stringWidth(titleStr) + 6, detY + 18);
					g.setFont(headF);
				}
				detY += 24;

				// Description (word-wrap)
				g.setFont(textF);
				g.setColor(Color.WHITE);
				fm = g.getFontMetrics();
				for (String line : wordWrap(q.getDescription(), fm, detW)) {
					g.drawString(line, detX, detY + 14);
					detY += 16;
				}
				detY += 6;

				// Progress
				g.setFont(smallF);
				g.setColor(ready ? new Color(120, 230, 140) : new Color(180, 220, 255));
				g.drawString("Progress: " + q.getProgressText(), detX, detY + 12);
				detY += 18;

				// Reward
				QuestReward reward = q.getReward();
				if (reward != null) {
					g.setColor(new Color(255, 215, 80));
					g.drawString("Reward: " + reward.getGold() + " G  " + reward.getXp() + " XP", detX, detY + 12);
				}

				// Complete button
				if (ready) {
					int btnH = 26, btnW = detW;
					int btnY2 = y + h - 46;
					g.setColor(new Color(40, 140, 60, 200));
					g.fillRoundRect(detX, btnY2, btnW, btnH, 10, 10);
					g.setColor(new Color(100, 255, 130));
					g.drawRoundRect(detX, btnY2, btnW, btnH, 10, 10);
					g.setFont(tabF);
					fm = g.getFontMetrics();
					String btnLabel = "[ ENTER - Complete Quest ]";
					g.setColor(Color.WHITE);
					g.drawString(btnLabel, detX + (btnW - fm.stringWidth(btnLabel)) / 2, btnY2 + 18);
				}
			}
		}

		// Footer hint
		g.setFont(smallF);
		g.setColor(new Color(150, 150, 165));
		String hint = "◄► Switch tab   ▲▼ Navigate   ESC Back";
		fm = g.getFontMetrics();
		g.drawString(hint, x + (w - fm.stringWidth(hint)) / 2, y + h - 10);
	}

	private static String truncate(String s, int max) {
		if (s == null) return "";
		return s.length() <= max ? s : s.substring(0, max - 1) + "…";
	}

	private static java.util.List<String> wordWrap(String text, FontMetrics fm, int maxW) {
		java.util.List<String> lines = new java.util.ArrayList<>();
		if (text == null || text.isBlank()) return lines;
		String[] words = text.split(" ");
		StringBuilder cur = new StringBuilder();
		for (String w : words) {
			String test = cur.length() == 0 ? w : cur + " " + w;
			if (fm.stringWidth(test) > maxW && cur.length() > 0) {
				lines.add(cur.toString()); cur = new StringBuilder(w);
			} else { cur = new StringBuilder(test); }
		}
		if (cur.length() > 0) lines.add(cur.toString());
		return lines;
	}

}
