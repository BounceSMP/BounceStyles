package dev.bsmp.bouncestyles.client;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import dev.bsmp.bouncestyles.ItemLoader;
import dev.bsmp.bouncestyles.client.renderer.StyleArmorRenderer;
import dev.bsmp.bouncestyles.item.StyleItem;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

@Environment(EnvType.CLIENT)
public class BounceStylesClient implements ClientModInitializer {
    public static final StyleArmorRenderer STYLE_ARMOR_RENDERER = new StyleArmorRenderer();

    @Override
    public void onInitializeClient() {
        ItemLoader.HEAD_ITEMS.forEach(this::registerRenderers);
        ItemLoader.BODY_ITEMS.forEach(this::registerRenderers);
        ItemLoader.FEET_ITEMS.forEach(this::registerRenderers);
        ItemLoader.LEGS_ITEMS.forEach(this::registerRenderers);
    }

    private void registerRenderers(StyleItem item) {
        GeoArmorRenderer.registerArmorRenderer(STYLE_ARMOR_RENDERER, item);
        TrinketRendererRegistry.registerRenderer(item, STYLE_ARMOR_RENDERER);
    }

    public static void drawStyleItemTypeOverlay(ItemStack stack, int x, int y) {
        if(stack.getItem() instanceof StyleItem && ((StyleItem)stack.getItem()).useBackupModel) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();

            Identifier texture = ((StyleItem) stack.getItem()).getIconId();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, texture);
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
            buffer.vertex(x + 6, y, 0).texture(0,0).next();
            buffer.vertex(x + 6, y + 10, 0).texture(0,1).next();
            buffer.vertex(x + 16, y + 10, 0).texture(1,1).next();
            buffer.vertex(x + 16, y, 0).texture(1,0).next();
            tessellator.draw();

            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
        }
    }

}
