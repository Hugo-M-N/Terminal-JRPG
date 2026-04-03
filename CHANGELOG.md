# Changelog
log of changes of every version.

## Version: `V0.6`

### 🖥️ Graphical Engine (GUI Mode)
- Full AWT/Swing graphical interface running alongside the existing text mode.
- New `App.java` entry point selects between GUI and text mode via `Config`.
- `GameStateManager`: centralised game state machine (`MAIN_MENU`, `EXPLORE`, `COMBAT`, `MENU`, `STATUS`, `INVENTORY`, `QUESTS`).
- `InputManager`: keyboard listener that translates key events into `NavAction` (UP, DOWN, LEFT, RIGHT, CONFIRM, BACK) consumed each frame.
- `Renderer`: draws the world (map tiles, NPCs, enemies, player sprite) and delegates UI overlays to `WindowGraphics`.
- `WindowGraphics`: all GUI panels (status, inventory, equipment, skills, quests, save toast, level-up).

### ⚔️ Combat System Overhaul
- Refactored `Combat` into three separate classes:
  - `CombatState`: holds all mutable combat data (allies, enemies, log, scroll, menus).
  - `CombatLogic`: pure logic (attack, defend, skill, object, tick, win/lose resolution).
  - `CombatRenderer`: renders the combat scene each frame (background gradient, entity cards, HP/MP bars, action panel, log).
- Visual combat layout: allies positioned in a diagonal column on the left, enemies on the right; positions scale dynamically with party size.
- Action log (up to 20 entries) shows enemy actions and combat results; supports UP/DOWN scroll in the result phase.
- Result phase waits for ENTER before returning to exploration; displays XP earned, gold, items obtained, and level-up lines.
- Enemy actions (attack, defend) are now logged in GUI mode.

### 🗺️ Map System
- New `game.map` package: `Map` loads `.map` files defining tile layout, collision, enemy positions, NPC positions, and trigger entries.
- New `src/maps/` directory for map data files.
- New `src/sprites/` directory; `SpriteManager` loads sprite sheets for player characters and monsters.

### 🧑 NPC System
- New `game.npc` package: `Npc` and `NpcManager`.
- NPCs defined in `src/npcs/Npcs.csv` with dialog lines, shop inventory, and quests.
- NPC interaction menu (Talk / Shop / Quests / Leave) triggered by pressing CONFIRM adjacent to an NPC on the map.
- `ShopMenu`: browse and buy items from an NPC's shop with live gold display.
- `DialogMenu`: paginated dialog with ENTER to advance.

### 📜 Quest System
- New `game.quest` package: `Quest` (abstract), `KillQuest`, `FetchQuest`, `ReachQuest`, `QuestManager`, `QuestStatus`, `QuestReward`.
- Quests are defined inline in the NPC CSV; `NpcManager` parses and registers them with `QuestManager` on load.
- `QuestManager` handles lifecycle (register → start → complete), distributes gold and XP rewards equally across the party, and gives item rewards to the party leader.
- Engine notifications: `notifyKill`, `notifyItemObtained`, `notifyZoneReached` update quest progress automatically.
- `QuestMenu` (NPC): browse available quests with detail pane (type, description, progress, reward); ENTER to accept.
- Pause-menu quest panel (`GameMode.QUESTS`): two tabs (Active / Completed), quest detail pane, green "Complete Quest" button appears when conditions are met; ENTER completes and distributes rewards.

### ⚡ Trigger System
- New `game.trigger` package: `Trigger` and `TriggerManager`.
- Triggers defined in the map file as `active;x;y;ACTION;params…` entries.
- Supported actions: `COMBAT`, `ZONE_TRANSITION`, `DIALOGUE`, `GIVE_ITEM`, `QUEST_START`, `QUEST_END`, `EVENT`.
- `TriggerManager.advance()` supports sequential story beats on the same tile (activate next trigger when current fires).
- Zone transitions notify `QuestManager.notifyZoneReached` automatically.

### 🧬 Item Deep Copy System
- Added abstract `copy()` method to `Item`; implemented in `Weapon`, `Armor`, `Accessory`, and `Potion`.
- `Entity` copy constructor now deep-copies the inventory and re-links equipped items to the new copies, preventing shared-reference bugs (e.g. potion amounts doubling across combats).
- All initial player items and enemy loot use `.copy()` to ensure independent instances.

### 📊 Status & Inventory Panels
- Full-screen status panel with four tabs: Stats, Equipment, Skills, Inventory.
- Equipment tab: select slot (Weapon / Armor / Accessory), browse compatible items, equip or unequip in place.
- Skills tab: view skills with MP cost; use non-damage skills directly from the panel.
- Character selection bar (UP from first row) to switch between party members.
- Standalone inventory panel accessible from the pause menu.

### 🏅 Level-Up Notification Panel
- Outside combat, levelling up shows a paginated modal panel (one page per level gained).
- Each page lists the level line (yellow), stat increases (green), and any newly unlocked skill (blue).
- Page counter (`1/3`) shown when multiple levels are gained at once; hint updates to "Next" / "Continue".

### 💾 Save Feedback
- Save action now shows a timed toast notification (2.5 s) centred at the bottom of the screen: green on success, red on failure.

### 🧩 New Managers & CSV Data
- `ClassManager`: loads player classes from `src/entity/Classes.CSV`.
- `SkillManager`: updated to load skills from CSV.
- `EnemyManager`: enemies moved to `src/entity/Enemies.csv`.
- New item types: `Accessories.csv` and `Potions.csv` added to `src/items/`.
- Typo fix: `Accesory.java` renamed to `Accessory.java`.

### 🌐 Localisation
- All player-facing UI strings are now in English (previously mixed with Spanish).

---

## Version: `V0.5`

### ⚔️ Combat System
- Added inventory menu in combat with options to use, equip/unequip, or discard items.
- Submenu navigation for items using arrow keys.
- Improved skill handling.
- Adjusted damage calculation for basic attacks (taking into account defense and reduced damage while defending).

### 🎒 Inventory and Items
- Redesigned inventory menu with split ASCII interface:
  - Left panel shows item list, right panel shows item description/details.
  - Contextual information depending on selected item.
- Added `Weapon`, `Armor`, and `Accessory` classes.
- Initial implementation of `ItemManager` to load equipment from CSV files (`Weapons.csv`, `Armors.csv`).

### 🌍 Zones and Events
- First prototype of exploration maps in ASCII style (This is a test, may change in the future):
  - Player (`@`) moves with arrow keys on a grid.
  - Encounters triggered by stepping on special tiles (enemies, events).
- Expanded event system (`Event`):
  - Differentiates first exploration, first forest combat, and King Goblin boss fight.
  - Uses flags to control story progression.
  - CSV manager for easy zone development (W.I.P).

### 💾 Save and Load
- Improved `SaveManager`:
  - Inventory and skills are now properly saved and loaded.
  - Player configuration and events remain consistent across sessions.
- Fixed errors when loading saves without skills or items.

### 🖥️ User Interface
- Improved `ScreenBuffer` (W.I.P):
  - Text scrolling with automatic line management.
  - Animated letter-by-letter printing for dialogues (`printAnimatedMessage`).
  - Automatic text formatting to fit screen width.
- Better integration between menu mode and text mode in `InputHelper`.

### 🧩 Refactoring and Modularization
- Changed hardcoded systems to a engine like systems (W.I.P). 
- Added Managers to load from CSV:
  - Enemies.
  - Skills.
  - Items (Armors and Weapons, item will be add in the future).
- Began modularizing the engine:
  - Prepared groundwork for quests and modular zones.

---

## Version: `V0.4`

### 🎨 User Interface
- Animated message system (text appears progressively).
- Interactive menus using arrow keys (`↑ ↓ Enter`).
- Cleaner display for combat, inventory, and skill selection.

### 💾 Save System
- Special events (`SpecialEvents`) are now saved and loaded.
- Player configuration (`TEXT_SPEED`) is saved.
- Improved robustness during file reading.

### 🛠️ Technical Changes
- Refactored classes: `Combat`, `Entity`, `Object`, `Potion`.
- Inventory UI updated to use the new menu system.
- Better code structure for future scalability.

---

## 🚀 Roadmap
- Convert save system to binary format for efficiency. (Delayed until last version of Save System for development reasons).
- Persist quest and trigger state in save files (kill counts, active flags).
- Improve class system and make classes more meaningful.
- Dialogue system (DialogueManager) connected to DIALOGUE triggers and NPC dialog trees.
- GIVE_ITEM and QUEST_START/END triggers fully wired to ItemManager and QuestManager.
- Expand worldbuilding: new enemies, locations, story, and events.
- Add sound/music system.

---
