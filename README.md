# 🗑️ Trash & Lava Filter

**Trash** is an advanced inventory management plugin. It allows players to create custom filters to ignore unwanted items (such as mob drops) and provides a secure permanent disposal system.

## 🚀 Key Features

*   **Smart Filter (`/trash`):** A 54-slot virtual chest where added items are blocked from entering the inventory (ignores durability, names, and enchantments).
*   **Anti-AutoLoot System:** High-performance background task that cleans filtered items even if they are forced into the inventory by other Auto-Loot plugins.
*   **Lava Menu (`/lava`):** Permanent destruction system with chat confirmation (`YES`/`SIM`).
*   **Rotational Logs:** Records valuable item deletions with UUID and Nickname. Old logs (5+ days) are automatically purged.
*   **Dynamic Feedback:** In-game notifications when you add or remove items from your filter.
*   **Multi-Language Support:** Full support for PT-BR and EN-US via `config.yml`.

## 🛠️ Commands & Permissions


| Command | Description | Permission |
| :--- | :--- | :--- |
| `/trash` | Opens the 54-slot filter chest | `None` |
| `/trash notify` | Toggles block notifications on/off | `None` |
| `/trash reload` | Reloads configurations and messages | `trash.admin` |
| `/lava` | Opens the 27-slot destruction menu | `None` |

## 📦 Installation

1.  Compile the project using `mvn clean package`.
2.  Drop the `Trash.jar` into your `plugins` folder.
3.  Start the server or use `/plugman load Trash`.

---
*Developed by comonier.*
