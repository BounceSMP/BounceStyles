## Changes
- MC 1.21.1 version support returns
- Added Emissive Texture support 
  - Automatically detects emissive/glowmask textures that are located alongside their base texture, and appropriately suffixed with "_glowmask"
  - For Example: For "my_style.png" you would also include "my_style_glowmask.png" in the same directory, and the emissive texture will be automatically used. 

## Fixes/Compat
- Supported Minecraft versions: 1.21.1 and 1.21.11