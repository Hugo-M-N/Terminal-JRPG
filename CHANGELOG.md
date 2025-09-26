# Changelog
log of changes of every version.

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
- Convert save system to binary format for efficiency. (Delayd until last version of Save System for developement reasons).
- Improve class system and make classes more meaningful.
- Improve Exploration system from plain text to map exploration.
- Add Quest System.
- Update Zone System.
- Expand worldbuilding: new enemies, locations, story, and events.

---
