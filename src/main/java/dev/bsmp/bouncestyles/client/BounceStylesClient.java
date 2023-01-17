package dev.bsmp.bouncestyles.client;

import dev.bsmp.bouncestyles.client.renderer.StyleArmorRenderer;
import dev.bsmp.bouncestyles.client.renderer.StyleModel;
import dev.bsmp.bouncestyles.item.StyleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class BounceStylesClient {
    public static final StyleArmorRenderer STYLE_ARMOR_RENDERER = new StyleArmorRenderer(new StyleModel());

    public static void onRegisterRenderers(final FMLClientSetupEvent event) {
        GeoArmorRenderer.registerArmorRenderer(StyleItem.HeadStyleItem.class, () -> STYLE_ARMOR_RENDERER);
        GeoArmorRenderer.registerArmorRenderer(StyleItem.BodyStyleItem.class, () -> STYLE_ARMOR_RENDERER);
        GeoArmorRenderer.registerArmorRenderer(StyleItem.LegsStyleItem.class, () -> STYLE_ARMOR_RENDERER);
        GeoArmorRenderer.registerArmorRenderer(StyleItem.FeetStyleItem.class, () -> STYLE_ARMOR_RENDERER);
    }

    public static void drawStyleItemTypeOverlay(ItemStack stack, int x, int y) {
//        if(stack.getItem() instanceof StyleItem && ((StyleItem)stack.getItem()).useBackupModel) {
//            RenderSystem.disableDepthTest();
//            RenderSystem.disableBlend();
//
//            ResourceLocation texture = ((StyleItem) stack.getItem()).getIconId();
//            Tesselator tesselator = Tesselator.getInstance();
//            BufferBuilder buffer = tesselator.getBuilder();
//
//            RenderSystem.setShader(GameRenderer::getPositionTexShader);
//            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
//            RenderSystem.setShaderTexture(0, texture);
//            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
//            buffer.vertex(x + 6, y, 0).uv(0,0).endVertex();
//            buffer.vertex(x + 6, y + 10, 0).uv(0,1).endVertex();
//            buffer.vertex(x + 16, y + 10, 0).uv(1,1).endVertex();
//            buffer.vertex(x + 16, y, 0).uv(1,0).endVertex();
//            tesselator.end();
//
//            RenderSystem.enableBlend();
//            RenderSystem.enableDepthTest();
//        }
    }
}
