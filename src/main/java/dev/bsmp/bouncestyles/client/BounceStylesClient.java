package dev.bsmp.bouncestyles.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
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

            ResourceLocation texture = ((StyleItem) stack.getItem()).getIconId();
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder buffer = tesselator.getBuilder();

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, texture);
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            buffer.vertex(x + 6, y, 0).uv(0,0).endVertex();
            buffer.vertex(x + 6, y + 10, 0).uv(0,1).endVertex();
            buffer.vertex(x + 16, y + 10, 0).uv(1,1).endVertex();
            buffer.vertex(x + 16, y, 0).uv(1,0).endVertex();
            tesselator.end();

            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
        }
    }

}
