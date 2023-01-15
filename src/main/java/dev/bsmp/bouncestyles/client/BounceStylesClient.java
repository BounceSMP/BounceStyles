package dev.bsmp.bouncestyles.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import dev.bsmp.bouncestyles.ItemLoader;
import dev.bsmp.bouncestyles.client.renderer.StyleArmorRenderer;
import dev.bsmp.bouncestyles.item.StyleItem;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class BounceStylesClient {
    public static final StyleArmorRenderer STYLE_ARMOR_RENDERER = new StyleArmorRenderer();

    public static void onInitializeClient(final FMLClientSetupEvent event) {
        ItemLoader.HEAD_ITEMS.forEach(BounceStylesClient::registerRenderers);
        ItemLoader.BODY_ITEMS.forEach(BounceStylesClient::registerRenderers);
        ItemLoader.FEET_ITEMS.forEach(BounceStylesClient::registerRenderers);
        ItemLoader.LEGS_ITEMS.forEach(BounceStylesClient::registerRenderers);
    }

    private static void registerRenderers(StyleItem item) {
        GeoArmorRenderer.registerArmorRenderer(StyleItem.class, () -> STYLE_ARMOR_RENDERER);
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
