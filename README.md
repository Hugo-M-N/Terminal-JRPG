# Terminal_JRPG
A JRPG engine built for fun and learning — data-driven, graphical, and extensible.

> [!WARNING]
> This project is still in development.<br>
> Some bugs may appear.

> [!IMPORTANT]
> To run the game, download the `.jar` file and execute `java -jar Terminal_JRPG.jar`<br>
> Requires a Java JDK installed.

> [!IMPORTANT]
> The save system is still in development. **Save files may not work across versions due to changes in the save file structure.**

---

## Version: `V0.6` — World Systems

### 🖥️ Graphical Engine (GUI Mode)
- Full AWT/Swing graphical interface running alongside the existing text mode.
- `GameStateManager`: centralised state machine (`MAIN_MENU`, `EXPLORE`, `COMBAT`, `MENU`, `STATUS`, `INVENTORY`, `QUESTS`).
- `InputManager`: keyboard listener that maps key events to navigation actions consumed each frame.
- `Renderer`: draws the world (map tiles, NPCs, enemies, player sprite) and delegates UI overlays.
- `WindowGraphics`: all GUI panels — status, inventory, equipment, skills, quests, save toast, level-up.

### ⚔️ Combat System Overhaul
- Refactored into three classes: `CombatState`, `CombatLogic`, `CombatRenderer`.
- Visual layout: allies on the left, enemies on the right, positions scale dynamically with party size.
- Action log (up to 20 entries) shows enemy actions and combat results with UP/DOWN scroll.
- Result phase waits for ENTER before returning to exploration; displays XP, gold, items, and level-up info.

### 🗺️ Map & World Systems
- Tile-based `.map` files define layout, collision, NPC positions, enemy positions, and triggers.
- **Trigger system**: data-driven tile triggers — `COMBAT`, `ZONE_TRANSITION`, `DIALOGUE`, `GIVE_ITEM`, `QUEST_START`, `QUEST_END`, `EVENT`. Sequential story beats supported on the same tile.
- Sprite system: `SpriteManager` loads sprite sheets for player characters and monsters.

### 🧑 NPC System
- NPCs defined in `src/npcs/Npcs.csv` with dialog lines, shop inventory, and quests.
- Interaction menu (Talk / Shop / Quests / Leave) triggered by pressing CONFIRM adjacent to an NPC.
- `ShopMenu`: browse and buy items with live gold display.
- `DialogMenu`: paginated dialog advanced with ENTER.

### 📜 Quest System
- Quest types: `KillQuest`, `FetchQuest`, `ReachQuest`.
- Quests defined inline in the NPC CSV; registered and tracked by `QuestManager`.
- Rewards (gold, XP, items) distributed equally across the party on completion.
- Engine notifications update quest progress automatically on kills, item pickups, and zone transitions.
- **NPC quest menu**: browse available quests with detail pane; ENTER to accept.
- **Pause-menu quest panel**: Active / Completed tabs, progress and reward details, green *Complete Quest* button when conditions are met.

### 🏅 Level-Up Panel
- Levelling up outside combat shows a paginated modal — one page per level gained.
- Each page shows the level line (yellow), stat increases (green), and newly unlocked skills (blue).
- Page counter shown when multiple levels are gained at once.

### 📊 Status & Inventory Panels
- Full-screen status panel with four tabs: Stats, Equipment, Skills, Inventory.
- Equipment tab: select slot, browse compatible items, equip or unequip in place.
- Skills tab: view skills with MP cost; use non-damage skills directly from the panel.
- Character selection bar to switch between party members.

### 💾 Save Feedback
- Save action shows a timed toast notification (green on success, red on failure).

### 🧩 Engine & Data
- `ClassManager`: loads player classes from CSV.
- New item type CSVs: `Accessories.csv`, `Potions.csv`.
- Item deep copy pattern (`copy()`) prevents shared-reference bugs across combats.
- `Accessory.java` renamed from `Accesory.java` (typo fix).
- All UI strings localised to English.

---

## 🚀 Roadmap
- Persist quest and trigger state in save files (kill counts, active flags).
- Improve class system and make classes more meaningful.
- Dialogue system connected to NPC dialog trees and DIALOGUE triggers.
- GIVE_ITEM and QUEST_START/END triggers fully wired to ItemManager and QuestManager.
- Convert save system to binary format for efficiency.
- Expand worldbuilding: new enemies, locations, story, and events.
- Add sound/music system.

---
