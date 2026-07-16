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
- **Misc**
  - Wardrobe UI now remembers last viewed Category and last used Search Term

## Fixes/Compat
- Supported Minecraft versions: **1.21.1** and **1.21.11**