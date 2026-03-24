# 🗑️ Trash (v1.2) - Ultimate Item Disposal

A lightweight, high-performance solution for item destruction. Designed for modern Minecraft servers (1.21.1+), **Trash** provides a safe and intuitive way for players to incinerate unwanted items permanently.

---

### 🌟 Main Features

*   **🔥 Triple-Alias Disposal:** Access the destruction menu using `/lava`, `/trash`, or `/lixo`. All paths lead to the same high-efficiency incinerator.
*   **🛡️ High-Safety Protocol:** Optional chat confirmation (`YES`/`SIM`) prevents accidental opening and loss of items.
*   **⚡ Optimized Core:** Built for **Java 21** and **Spigot/Paper 1.21.1**. Zero background tasks and zero main-thread overhead.
*   **🌍 Multi-Language Core:** Native, out-of-the-box support for **English (EN)** and **Portuguese (PT)**, easily toggled in the config.
*   **📦 Flexible GUI:** Configurable inventory size (up to 54 slots) to fit your server's needs.
*   **🎨 Premium Look:** Full support for Legacy color codes in all messages and GUI titles.

---

### 💻 Commands


| Command | Aliases | Description | Permission |
| :--- | :--- | :--- | :--- |
| `/lava` | `/trash`, `/lixo` | Opens the item destruction menu. | `trash.use` |
| `/lava reload` | - | Hot-reloads configurations and messages. | `trash.admin` |

---

### 🔑 Permissions

*   **`trash.use`**: Allows players to use the destruction system (Default: true).
*   **`trash.admin`**: Master permission to reload settings and manage the plugin (Default: op).

---

### ⚙️ Configuration (`config.yml`)

```yaml
# Language selection (en or pt)
language: en

# Size of the destruction inventory (multiples of 9, max 54)
lava-gui-size: 27

# If true, requires chat confirmation before opening the menu
lava-confirmation: false
```

⚠️ Important Notice
Compatibility: Fully optimized for Minecraft 1.21.1 and Java 21.
Clean Slate: Version 1.2 removes old filter/database dependencies for a "plug-and-play" experience focusing strictly on item disposal.
Update Alert: If upgrading from 1.1, please delete your old config.yml and messages files to allow the new simplified structure to generate.

Developed with ❤️ by Comonier