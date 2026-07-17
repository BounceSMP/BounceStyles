## Changes
- **Minecraft** version **1.21.1** support returns
- Added **Emissive Texture** support
  - Automatically detects emissive/glowmask textures that are located alongside their base texture, and appropriately suffixed with "_glowmask"
  - For Example; The texture "my_style.png" you can also include "my_style_glowmask.png" in the same directory, and the emissive texture will be automatically used.
- **Preset Overhaul**
  - Added Hotkey support to Presets
    - Hotkeys are saved per-world/server. Server keybinds are saved using the ip address used to connect, so connecting via a different ip will not load existing hotkeys.
  - Server to Client Preset Syncing
    - Servers can now define presets to be sent to players, both Globally and Player-Specific
  - *With these changes, existing presets will unfortunately be reset unless you manually migrate your existing presets. Apologies for the inconvenience.*
- **Configs**
  - Now includes a Config file (via [Configurable](https://github.com/Bawnorton/Configurable), packaged in-jar so it does not require downloading by players)
  - Config file is located at `configs/bounce_styles.toml`
  - Configurable settings include;
    - Style GUI button in the Inventory GUI
    - Unlock System and it's requirements
- **Misc**
  - Wardrobe UI now remembers last viewed Category and last used Search Term
  - `/bouncestyles equip` command now respects Unlock System bypass requirements

## Fixes/Compat
- Supported Minecraft versions: **1.21.1** and **1.21.11**

## Known Issues
- Looping animations sometimes cause style models to disappear for a frame
- Neoforge 1.21.1 has some issues with server synchronization. Ensure both client and server are both on the same, latest compatible versions of NeoForge if you find yourself running into a `Tried to applied snapshot with registry name neoforge:synced_attachment_types but was not found` error
- Emissive Textures don't work particularly well with shaders. Unsure if there's much I can do or if this is just because of how GeckoLib does emissive textures.