# 🗑️ Trash - Human Dispenser & Item Filter
### Version 1.1

## 🚀 Main Functions
*   **Human Dispenser Mechanic:** Blocked items are physically ejected away from the player with velocity and a dispenser sound effect.
*   **Permanent Incineration (LAVA):** Secure menu for definitive item destruction with chat confirmation requirement.
*   **Layout Lock:** Filter slots maintain their exact positions, even after server reloads or plugin restarts.
*   **Smart Notifications:** Customizable alerts with a 5-second anti-spam cooldown.
*   **Multi-language Support:** Native English (EN) and Portuguese (PT) translation files.
*   **Automated Logs:** Records all incinerated items and automatically purges logs older than 7 days.

## 🎮 Commands

| Command | Description | Aliases |
| :--- | :--- | :--- |
| `/trash` | Opens the item filter GUI (54 slots). | `/lixo` |
| `/trash reload` | Reloads all configurations and messages. | - |
| `/lava` | Opens the permanent destruction menu. | - |
| `/to` | Toggles your own block notifications. | - |
| `/ta` | Toggles nearby players' block notifications. | - |

## 🛠️ Permissions
*   `trash.admin`: Grants access to the `/trash reload` command.

## 📝 Brief Summary
Trash is a professional-grade item management plugin. It allows players to create custom filters for unwanted drops. Instead of simply deleting items, it uses a unique "Human Dispenser" mechanic to bounce items away, keeping the inventory clean while maintaining server immersion.

## ⚠️ IMPORTANT WARNINGS
*   **UPDATING FROM v1.0:** You **MUST DELETE** your old `messages_pt.yml`, `messages_en.yml`, and `config.yml` files before starting version 1.1.
*   **REGENERATION:** The plugin will automatically generate the new updated files with the necessary keys for the "Human Dispenser" system and differentiated notifications.
