# Terminal_JRPG
At least by now its just a proyect made for fun and learn.

> [!WARNING]
> This proyect its still in development.<br>
Some bugs might appear.

>[!IMPORTANT]
>Now to play the game you only need to download the .jar file and execute ```java -jar Terminal_JRPG.jar```
>If this doesn't work check if you have a java JDK.

>[!IMPORTANT]
> The save system is still in development. **Save files migth not work due to changes on save file structure**

---

## 📦 Current Version: `V0.4`

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
- Convert save system to binary format for efficiency.
- Improve class system and make classes more meaningful.
- Add equipment system: weapons, armor, accessories.
- Expand worldbuilding: new enemies, locations, story, and events.

---