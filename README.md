# 🗑️ Trash & Lava Filter (Minecraft 1.21.11)

Trash - v1.0

Advanced inventory management and item disposal plugin for Minecraft servers. It allows players to create filters to avoid auto-pickup of unwanted items and provides a secure way for definitive destruction.

## ✨ Features

*   **Trash Filter (`/trash`):** A virtual chest where players place items they **do not want to pick up** from the ground.
*   **Material-Based Blocking:** Ignores durability, names, or enchantments. If the item type is in the filter, it won't enter the inventory.
*   **Permanent Destruction (`/lava`):** Incineration menu with a security system (chat confirmation).
*   **Automatic Logs:** Records everything deleted in lava including UUID, Nickname, Date, and Time.
*   **Log Purge:** Automatically keeps only the records from the last 5 days to save space.
*   **Multi-Language Support:** English and Portuguese messages (detected via config).
*   **Reload System:** Update messages and settings without restarting the server.

## 🛠️ Commands


| Command | Aliases | Description |
| :--- | :--- | :--- |
| `/trash` | `/lixo` | Opens the item filter inventory. |
| `/trash reload` | `/lixo reload` | Reloads configurations and translations. |
| `/lava` | - | Starts the process for definitive item destruction. |

## 🔑 Permissions

*   `trash.admin`: Allows using the **reload** command.
*   `trash.bypass`: Allows the player (e.g., Admin) to pick up items even if they are in the `/trash` filter.
*   *(Optional)* `/trash` and `/lava` commands are open to everyone by default.

## 📁 File Structure

*   `plugins/Trash/config.yml`: Language and chest size settings.
*   `plugins/Trash/messages_en.yml`: English translations.
*   `plugins/Trash/data/`: Stores player filters via UUID.
*   `plugins/Trash/logs/`: History of destroyed items (5-day rotation).

---
**Developed for Minecraft 1.21.11**
