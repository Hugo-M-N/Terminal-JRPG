package game;

import java.awt.Canvas;
import java.util.ArrayList;

import game.Menus.IMenu;
import game.Menus.LoadMenu;
import game.Menus.MainMenu;
import game.Menus.OptionsMenu;
import game.Menus.VerticalMenu;
import game.combat.Combat;
import game.entity.ClassManager;
import game.entity.EnemyManager;
import game.entity.Entity;
import game.entity.EntityClass;
import game.item.Equippable;
import game.item.Item;
import game.item.ItemManager;
import game.map.Map;
import game.npc.Npc;
import game.quest.QuestManager;
import game.trigger.Trigger;
import game.trigger.TriggerManager;
import game.zone.Zone;
import game.zone.ZoneManager;

public class GameStateManager {

    public enum GameMode { MAIN_MENU, EXPLORE, COMBAT, MENU, STATUS, INVENTORY, QUESTS }

    // --- Game state ---
    private GameMode gameMode = GameMode.MAIN_MENU;
    private GameMode lastGameMode;

    private IMenu activeMenu;
    private IMenu lastMenu;
    
    private static String saveFile;

    private ArrayList<Entity> allies = new ArrayList<>();
    private Zone currentZone;

    // Player position
    private int px = 3, py = 3;

    // Inventory state
    private int invSel = 0;
    private int invActionSel = 0;
    private boolean invSelectingAction = false;

    // Status panel state
    private int statusTab = 0;
    private int statusCharSel = 0;
    private boolean inCharBar = false;

    // Equip tab state
    private int equipSlot = 0;
    private int equipItemSel = 0;
    private boolean equipSelectingItem = false;

    // Skills tab state
    private int skillSel = 0;

    // Quest panel state
    private int questSel = 0;
    private int questTab = 0; // 0 = Active, 1 = Completed

    // NPC interaction
    private Npc currentNpc;

    // Combat
    private boolean inCombat = false;

    // Save toast
    private long saveToastUntil = 0;
    private boolean saveSuccess = false;
    public boolean isSaveToastVisible() { return System.currentTimeMillis() < saveToastUntil; }
    public boolean isSaveSuccess()      { return saveSuccess; }

    // Dependencies
    private final InputManager input;
    private final Canvas canvas;

    public GameStateManager(InputManager input, Canvas canvas) {
        this.input = input;
        this.canvas = canvas;
        activeMenu = new MainMenu();
    }

    // --- Game loop entry point ---

    public void update() {
        cleanLastMenu();
        processInput();
        updateGameMode();
    }

    // --- Cleanup ---

    private void cleanLastMenu() {
        if (lastMenu == activeMenu) lastMenu = null;
    }

    // --- Input processing by mode ---

    private void processInput() {
        // Forward raw key events to active menu (e.g. text input)
        java.awt.event.KeyEvent ke = input.consumeKeyEvent();
        if (ke != null && activeMenu != null) activeMenu.onKey(ke);

        InputManager.NavAction nav = input.consumeNav();
        if (nav == null) return;

        // Active menus take priority: consume nav and return an action string
        if (activeMenu != null) {
            Menus.Nav menuNav = toMenuNav(nav);
            if (menuNav != null) {
                MenuAction action = activeMenu.onNav(menuNav);
                if ((action != MenuAction.NONE) && (action != null)) handleAction(action);
            }
            return;
        }

        // No active menu — route input based on current mode
        switch (gameMode) {
            case EXPLORE   -> handleExploreInput(nav);
            case STATUS    -> handleStatusInput(nav);
            case INVENTORY -> handleInventoryInput(nav);
            case QUESTS    -> handleQuestsInput(nav);
            default        -> {}
        }
    }

    private void handleExploreInput(InputManager.NavAction nav) {
        Map map = currentZone.getMap();
        switch (nav) {
            case UP -> {
                py--;
                if (py < 0) py = 0;
                if (map.getCollision(px, py)) py++;
                else checkTrigger();
            }
            case DOWN -> {
                py++;
                if (py > map.getHeight() - 1) py = map.getHeight() - 1;
                if (map.getCollision(px, py)) py--;
                else checkTrigger();
            }
            case LEFT -> {
                px--;
                if (px < 0) px = 0;
                if (map.getCollision(px, py)) px++;
                else checkTrigger();
            }
            case RIGHT -> {
                px++;
                if (px > map.getWidth() - 1) px = map.getWidth() - 1;
                if (map.getCollision(px, py)) px--;
                else checkTrigger();
            }
            case CONFIRM -> tryInteractNpc();
            case BACK -> {
                activeMenu = new VerticalMenu("",
                		new String[]{"Resume", "Status", "Quests", "Inventory", "Menu"},
                		new MenuAction[] {MenuAction.RESUME, MenuAction.STATUS, MenuAction.QUESTS, MenuAction.INVENTORY, MenuAction.GAME_MENU});
                gameMode = GameMode.MENU;
            }
            default -> {}
        }
    }

    private void tryInteractNpc() {
        if (currentZone == null) return;
        for (String s : currentZone.getMap().getNPCs()) {
            String[] parts = s.split("-");
            if (parts.length < 2) continue;
            int nx = Integer.parseInt(parts[1].split(":")[0]);
            int ny = Integer.parseInt(parts[1].split(":")[1]);
            if (Math.abs(px - nx) + Math.abs(py - ny) <= 1) {
                game.npc.Npc npc = game.npc.NpcManager.getNpc(parts[0]);
                if (npc == null) continue;
                currentNpc = npc;
                lastMenu = activeMenu;
                lastGameMode = gameMode;
                activeMenu = new Menus.NpcMenu(npc);
                gameMode = GameMode.MENU;
                return;
            }
        }
    }

    /** The party member currently viewed in the status panel. */
    private Entity statusPlayer() { return allies.get(statusCharSel); }

    private void handleStatusInput(InputManager.NavAction nav) {
        // Character selection bar
        if (inCharBar) {
            switch (nav) {
                case LEFT  -> { if (statusCharSel > 0) { statusCharSel--; resetStatusSubState(); } }
                case RIGHT -> { if (statusCharSel < allies.size() - 1) { statusCharSel++; resetStatusSubState(); } }
                case DOWN, CONFIRM -> inCharBar = false;
                case BACK  -> closeStatus();
                default    -> {}
            }
            return;
        }
        // Tab switching with LEFT/RIGHT (when no sub-menu is open)
        if (!equipSelectingItem && !invSelectingAction &&
                (nav == InputManager.NavAction.LEFT || nav == InputManager.NavAction.RIGHT)) {
            statusTab = (nav == InputManager.NavAction.LEFT) ? (statusTab + 3) % 4 : (statusTab + 1) % 4;
            resetStatusSubState();
            return;
        }
        switch (statusTab) {
            case 0 -> handleStatusTab0(nav);
            case 1 -> handleEquipInput(nav);
            case 2 -> handleSkillsInput(nav);
            case 3 -> handleStatusInventoryInput(nav);
        }
    }

    private void handleStatusTab0(InputManager.NavAction nav) {
        switch (nav) {
            case UP -> { if (allies.size() > 1) inCharBar = true; }
            case BACK, CONFIRM -> closeStatus();
            default -> {}
        }
    }

    private void resetStatusSubState() {
        equipSlot = 0; equipItemSel = 0; equipSelectingItem = false;
        skillSel = 0; invSel = 0; invActionSel = 0; invSelectingAction = false;
    }

    private void closeStatus() {
        activeMenu = lastMenu;
        gameMode = lastMenu != null ? (lastGameMode != null ? lastGameMode : GameMode.MENU) : GameMode.EXPLORE;
    }

    private void handleEquipInput(InputManager.NavAction nav) {
        Entity player = statusPlayer();
        if (equipSelectingItem) {
            ArrayList<Item> filtered = getEquipItemsForSlot(player, equipSlot);
            Item curEquipped = getCurrentEquipped(player, equipSlot);
            boolean hasEquipped = curEquipped != null;
            int listSize = filtered.size() + (hasEquipped ? 1 : 0);
            switch (nav) {
                case UP    -> equipItemSel = Math.max(0, equipItemSel - 1);
                case DOWN  -> equipItemSel = Math.min(Math.max(0, listSize - 1), equipItemSel + 1);
                case CONFIRM -> {
                    if (hasEquipped) {
                        if (equipItemSel == 0) {
                            if (curEquipped instanceof Equippable eq) eq.Unequip(player);
                        } else {
                            Item item = filtered.get(equipItemSel - 1);
                            if (item instanceof Equippable eq) eq.Equip(player);
                        }
                    } else if (!filtered.isEmpty()) {
                        Item item = filtered.get(equipItemSel);
                        if (item instanceof Equippable eq) eq.Equip(player);
                    }
                    equipSelectingItem = false; equipItemSel = 0;
                }
                case BACK  -> { equipSelectingItem = false; equipItemSel = 0; }
                default    -> {}
            }
        } else {
            switch (nav) {
                case UP -> {
                    if (equipSlot == 0 && allies.size() > 1) inCharBar = true;
                    else equipSlot = Math.max(0, equipSlot - 1);
                }
                case DOWN  -> equipSlot = Math.min(2, equipSlot + 1);
                case CONFIRM -> { equipSelectingItem = true; equipItemSel = 0; }
                case BACK  -> closeStatus();
                default    -> {}
            }
        }
    }

    private void handleSkillsInput(InputManager.NavAction nav) {
        Entity player = statusPlayer();
        ArrayList<game.skill.Skill> skills = player.getSkills();
        if (skills == null || skills.isEmpty()) {
            if (nav == InputManager.NavAction.UP && allies.size() > 1) inCharBar = true;
            else if (nav == InputManager.NavAction.BACK) closeStatus();
            return;
        }
        switch (nav) {
            case UP -> {
                if (skillSel == 0 && allies.size() > 1) inCharBar = true;
                else skillSel = Math.max(0, skillSel - 1);
            }
            case DOWN  -> skillSel = Math.min(skills.size() - 1, skillSel + 1);
            case CONFIRM -> {
                game.skill.Skill sk = skills.get(skillSel);
                if (!sk.getEFFECT().equalsIgnoreCase("DAMAGE") && player.getMP() >= sk.getCOST()) {
                    player.setMP(player.getMP() - sk.getCOST());
                    sk.Use(player, player.getEffectiveMAG());
                }
            }
            case BACK  -> closeStatus();
            default    -> {}
        }
    }

    private void handleStatusInventoryInput(InputManager.NavAction nav) {
        Entity player = statusPlayer();
        ArrayList<Item> inventory = player.getInventory();
        int inventorySize = inventory.size();
        switch (nav) {
            case BACK -> { if (invSelectingAction) invSelectingAction = false; else closeStatus(); }
            case UP -> {
                if (invSelectingAction) invActionSel = Math.max(0, invActionSel - 1);
                else if (invSel == 0 && allies.size() > 1) inCharBar = true;
                else invSel = Math.max(0, invSel - 1);
            }
            case DOWN -> {
                if (invSelectingAction && inventorySize > 0) {
                    int maxAction = Item.getItemOptions(inventory.get(invSel)).size() - 1;
                    invActionSel = Math.min(maxAction, invActionSel + 1);
                } else { invSel = Math.min(Math.max(0, inventorySize - 1), invSel + 1); }
            }
            case CONFIRM -> handleInventoryConfirmFor(player);
            default -> {}
        }
    }

    private void handleInventoryConfirmFor(Entity player) {
        if (!invSelectingAction) { invSelectingAction = true; return; }
        ArrayList<Item> inventory = player.getInventory();
        if (inventory.isEmpty()) return;
        Item selectedItem = inventory.get(invSel);
        String action = Item.getItemOptions(selectedItem).get(invActionSel);
        switch (action) {
            case "Use" -> {
                selectedItem.Use(player);
                if (selectedItem.getAMOUNT() <= 0) {
                    inventory.remove(invSel);
                    invSel = Math.max(0, Math.min(invSel, inventory.size() - 1));
                }
            }
            case "Equip"   -> { if (selectedItem instanceof Equippable eq) eq.Equip(player); }
            case "Unequip" -> { if (selectedItem instanceof Equippable eq) eq.Unequip(player); }
            case "Discard" -> {
                inventory.remove(invSel);
                invSel = Math.max(0, Math.min(invSel, inventory.size() - 1));
            }
        }
        invSelectingAction = false;
    }

    private ArrayList<Item> getEquipItemsForSlot(Entity player, int slot) {
        ArrayList<Item> result = new ArrayList<>();
        for (Item item : player.getInventory()) {
            if (slot == 0 && item instanceof game.item.Weapon) result.add(item);
            else if (slot == 1 && item instanceof game.item.Armor) result.add(item);
            else if (slot == 2 && item instanceof game.item.Accessory) result.add(item);
        }
        return result;
    }

    private Item getCurrentEquipped(Entity player, int slot) {
        return switch (slot) {
            case 0 -> player.getWeapon();
            case 1 -> player.getArmor();
            case 2 -> player.getAccesory();
            default -> null;
        };
    }

    private void handleQuestsInput(InputManager.NavAction nav) {
        java.util.List<game.quest.Quest> list = questTab == 0
                ? QuestManager.getActive() : QuestManager.getCompleted();
        switch (nav) {
            case LEFT  -> { questTab = (questTab + 1) % 2; questSel = 0; }
            case RIGHT -> { questTab = (questTab + 1) % 2; questSel = 0; }
            case UP    -> questSel = Math.max(0, questSel - 1);
            case DOWN  -> { if (!list.isEmpty()) questSel = Math.min(list.size() - 1, questSel + 1); }
            case CONFIRM -> {
                if (questTab == 0 && !list.isEmpty() && questSel < list.size()) {
                    game.quest.Quest q = list.get(questSel);
                    if (q.checkCompletion()) {
                        java.util.List<java.util.List<String>> pages = QuestManager.tryComplete(q.getId(), allies);
                        questSel = Math.max(0, Math.min(questSel, QuestManager.getActive().size() - 1));
                        if (!pages.isEmpty()) {
                            activeMenu = new Menus.LevelUpMenu(pages);
                        }
                    }
                } else {
                    activeMenu = lastMenu;
                    gameMode = lastMenu != null ? (lastGameMode != null ? lastGameMode : GameMode.MENU) : GameMode.EXPLORE;
                }
            }
            case BACK -> {
                activeMenu = lastMenu;
                gameMode = lastMenu != null ? (lastGameMode != null ? lastGameMode : GameMode.MENU) : GameMode.EXPLORE;
            }
            default -> {}
        }
    }

    private void handleInventoryInput(InputManager.NavAction nav) {
        ArrayList<Item> inventory = allies.get(0).getInventory();
        int inventorySize = inventory.size();
        switch (nav) {
            case BACK -> {
                if (invSelectingAction) invSelectingAction = false;
                else {
                    activeMenu = lastMenu;
                    gameMode = GameMode.MENU;
                }
            }
            case UP -> {
                if (invSelectingAction) invActionSel = Math.max(0, invActionSel - 1);
                else invSel = Math.max(0, invSel - 1);
            }
            case DOWN -> {
                if (invSelectingAction && inventorySize > 0) {
                    int maxAction = Item.getItemOptions(inventory.get(invSel)).size() - 1;
                    invActionSel = Math.min(maxAction, invActionSel + 1);
                } else {
                    invSel = Math.min(inventorySize - 1, invSel + 1);
                }
            }
            case LEFT, RIGHT -> invSelectingAction = !invSelectingAction;
            case CONFIRM -> handleInventoryConfirm();
        }
    }

    private void handleInventoryConfirm() {
        handleInventoryConfirmFor(allies.get(0));
    }

    // --- Menu actions ---

    private void handleAction(MenuAction action) {
        switch (action) {
        	case NEW_GAME_MENU -> {
        		lastMenu = activeMenu;
        		activeMenu = new Menus.CharacterCreationMenu();
        		gameMode= GameMode.MENU;
        	}
            case NEW_GAME -> {
                activeMenu = null;
                lastMenu = null;
                gameMode = GameMode.EXPLORE;
                
            }
            case LOAD_GAME -> {
                allies = SaveManager.LoadGame(saveFile);
                activeMenu = null;
                lastMenu = null;
                gameMode = GameMode.EXPLORE;
            }
            case OPTIONS -> {
                lastMenu = activeMenu;
                activeMenu = new OptionsMenu();
                gameMode = GameMode.MENU;
            }
            case EXIT_GAME -> System.exit(0);

            case NPC_DIALOG -> {
                lastMenu = activeMenu;
                lastGameMode = gameMode;
                activeMenu = new Menus.DialogMenu(currentNpc);
                gameMode = GameMode.MENU;
            }
            case NPC_SHOP -> {
                lastMenu = activeMenu;
                lastGameMode = gameMode;
                activeMenu = new Menus.ShopMenu(currentNpc.getName(), currentNpc.getShopItems(), allies);
                gameMode = GameMode.MENU;
            }
            case NPC_QUESTS -> {
                lastMenu = activeMenu;
                lastGameMode = gameMode;
                activeMenu = new Menus.QuestMenu(currentNpc.getName(), currentNpc.getQuests(), allies);
                gameMode = GameMode.MENU;
            }

            case RETURN -> {
                activeMenu = lastMenu;
                gameMode = lastMenu != null
                        ? (lastGameMode != null ? lastGameMode : GameMode.MENU)
                        : GameMode.EXPLORE;
            }
            case RESUME -> {
                activeMenu = null;
                gameMode = GameMode.EXPLORE;
            }
            case STATUS -> {
                lastMenu = activeMenu;
                lastGameMode = gameMode;
                activeMenu = null;
                statusTab = 0; statusCharSel = 0; inCharBar = false;
                resetStatusSubState();
                gameMode = GameMode.STATUS;
            }
            case INVENTORY -> {
                lastMenu = activeMenu;
                lastGameMode = gameMode;
                activeMenu = null;
                gameMode = GameMode.INVENTORY;
                invSel = 0;
                invActionSel = 0;
                invSelectingAction = false;
            }
            case QUESTS -> {
                lastMenu = activeMenu;
                lastGameMode = gameMode;
                activeMenu = null;
                questSel = 0;
                questTab = 0;
                gameMode = GameMode.QUESTS;
            }
            case GAME_MENU-> {
                lastMenu = activeMenu;
                lastGameMode = gameMode;
                activeMenu = new VerticalMenu("Menu", new String[]{"Return", "Options", "Save Game", "Load Game", "Main Menu"},
                		new MenuAction[] {MenuAction.RETURN, MenuAction.OPTIONS, MenuAction.SAVE_GAME, MenuAction.LOAD_MENU, MenuAction.MAIN_MENU});
                gameMode = GameMode.MENU;
            }
            case SAVE_GAME -> {
                saveSuccess = SaveManager.SaveGame(allies, allies.get(0).getNAME());
                saveToastUntil = System.currentTimeMillis() + 2500;
            }
            case LOAD_MENU -> {
                lastMenu = activeMenu;
                lastGameMode = gameMode;
                ArrayList<String> files = new ArrayList<>();
                files.add("Return");
                for (String s : SaveManager.getFileNames()) files.add(s);
                activeMenu = new LoadMenu(files.toArray(new String[0]));
                gameMode= GameMode.MENU;
            }
            case MAIN_MENU -> {
                lastMenu = null;
                activeMenu = new MainMenu();
                gameMode = GameMode.MAIN_MENU;
            }
            case NONE -> {}
        }
    }

    // --- Mode transition logic ---

    private void updateGameMode() {
    	switch(gameMode) {
	    	case GameMode.MAIN_MENU -> allies=null;
    		case GameMode.COMBAT -> onEnterCombat();
    		case GameMode.EXPLORE -> onEnterExplore();
    		default -> {}
    	}
    }
    
    // ── Trigger system ────────────────────────────────────────────────────────

    private void checkTrigger() {
        Trigger t = TriggerManager.getActiveAt(px, py);
        if (t != null) executeTrigger(t);
    }

    private void executeTrigger(Trigger t) {
        switch (t.getAction()) {
            case COMBAT -> {
                startTriggerCombat(t.getParams());
            }
            case ZONE_TRANSITION -> {
                String zoneName = t.getParam(0);
                int destX = t.getParam(1) != null ? Integer.parseInt(t.getParam(1)) : 0;
                int destY = t.getParam(2) != null ? Integer.parseInt(t.getParam(2)) : 0;
                currentZone = ZoneManager.getZone(zoneName);
                px = destX;
                py = destY;
                QuestManager.notifyZoneReached(zoneName);
                TriggerManager.advance(t.getX(), t.getY());
            }
            case DIALOGUE -> {
                // TODO: connect to DialogueManager when implemented
                System.out.println("[DIALOGUE] " + t.getParam(0));
                TriggerManager.advance(t.getX(), t.getY());
            }
            case GIVE_ITEM -> {
                // TODO: look up item in ItemManager and give to allies.get(0)
                System.out.println("[GIVE_ITEM] " + t.getParam(0));
                TriggerManager.advance(t.getX(), t.getY());
            }
            case QUEST_START, QUEST_END, EVENT -> {
                // TODO: connect to QuestManager / EventManager when implemented
                System.out.println("[" + t.getAction() + "] " + t.getParam(0));
                TriggerManager.advance(t.getX(), t.getY());
            }
        }
    }

    private void startTriggerCombat(String[] params) {
        ArrayList<Entity> enemies = new ArrayList<>();
        for (String id : params) {
            Entity e = EnemyManager.getEnemy(id.trim());
            if (e != null) enemies.add(e);
        }
        if (enemies.isEmpty()) return;
        try {
            inCombat = true;
            new Combat(allies, enemies, canvas);
            inCombat = false;
            gameMode = GameMode.EXPLORE;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void onEnterCombat() {
    	ArrayList<Entity> enemies = new ArrayList<>();
    	
    	// Testing
    	enemies.add(new Entity(EnemyManager.getEnemy("GOBLIN")));
    	Entity tmp = new Entity(EnemyManager.getEnemy("GOBLIN"));
    	tmp.addToInventory(ItemManager.getPotion("HERBS_POTION").copy());
    	enemies.add(tmp);
    	
    	
    	try {
    		inCombat = true;
    		new Combat(allies, enemies, canvas);
    		inCombat = false;
    		gameMode = GameMode.EXPLORE;
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
    }
    
    private void onEnterExplore() {
        if (allies == null) {
            allies = new ArrayList<Entity>();

            String name         = Menus.CharacterCreationMenu.getConfirmedName();
            EntityClass cls = Menus.CharacterCreationMenu.getConfirmedClass();
            int spriteIdx       = Menus.CharacterCreationMenu.getConfirmedSpriteIdx();

            if (name == null || name.isBlank() || cls == null) {
                name      = "Warrior";
                cls       = ClassManager.getClass("WARRIOR");
                spriteIdx = 0;
            }

            Entity player = new Entity(name, cls, 1);
            player.setSpriteIdx(spriteIdx);
            player.addToInventory(ItemManager.getPotion("HERBS_POTION").copy());
            player.addToInventory(ItemManager.getWeapon("RUSTY_SWORD").copy());
            player.addToInventory(ItemManager.getArmor("RIPPED_CLOTH").copy());
            allies.add(player);
        }
    }

    // --- Utility: converts InputManager.NavAction to Menus.Nav ---

    private Menus.Nav toMenuNav(InputManager.NavAction nav) {
        return switch (nav) {
            case UP      -> Menus.Nav.UP;
            case DOWN    -> Menus.Nav.DOWN;
            case LEFT    -> Menus.Nav.LEFT;
            case RIGHT   -> Menus.Nav.RIGHT;
            case CONFIRM -> Menus.Nav.CONFIRM;
            case BACK    -> Menus.Nav.BACK;
        };
    }

    // --- Getters for the Renderer ---

    public GameMode getGameMode()         { return gameMode; }
    public IMenu getActiveMenu()          { return activeMenu; }
    public ArrayList<Entity> getAllies()  { return allies; }
    public Zone getCurrentZone()          { return currentZone; }
    public int getPx()                    { return px; }
    public int getPy()                    { return py; }
    public int getInvSel()                { return invSel; }
    public int getInvActionSel()          { return invActionSel; }
    public boolean isInvSelectingAction() { return invSelectingAction; }
    public int getStatusTab()             { return statusTab; }
    public int getStatusCharSel()         { return statusCharSel; }
    public boolean isInCharBar()          { return inCharBar; }
    public int getEquipSlot()             { return equipSlot; }
    public int getEquipItemSel()          { return equipItemSel; }
    public boolean isEquipSelectingItem() { return equipSelectingItem; }
    public int getSkillSel()              { return skillSel; }
    public int getQuestSel()              { return questSel; }
    public int getQuestTab()              { return questTab; }

    // --- Setters for initialization (new game / load) ---

    public void setAllies(ArrayList<Entity> allies)   { this.allies = allies; }
    public void setCurrentZone(Zone zone)             { this.currentZone = zone; }
    public void setGameMode(GameMode mode)            { this.gameMode = mode; }
    public static void setSaveFile(String fileName)   {saveFile = fileName; }
}