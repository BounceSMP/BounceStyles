package dev.bsmp.bouncestyles.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.bsmp.bouncestyles.item.StyleItem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class StyleArmorRenderer extends GeoArmorRenderer<StyleItem> implements ICurioRenderer {

    public StyleArmorRenderer(AnimatedGeoModel<StyleItem> model) {
        super(model);
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        EquipmentSlot slot = getEquipmentSlotForTrinketSlot(slotContext);
        LivingEntity entity = slotContext.entity();

        if(slot != null && entity instanceof Player) {
            StyleItem item = (StyleItem) stack.getItem();

            ((PlayerModel<?>) renderLayerParent.getModel()).copyPropertiesTo(this);
            setCurrentItem(entity, stack, slot);
            applySlot(slot);

            VertexConsumer vertexConsumer = renderTypeBuffer.getBuffer(RenderType.armorCutoutNoCull(this.getTextureLocation(item)));
            render(partialTicks, matrixStack, vertexConsumer, light);
        }
    }

    private EquipmentSlot getEquipmentSlotForTrinketSlot(SlotContext slotContext) {
        return switch (slotContext.identifier()) {
            case StyleItem.HeadStyleItem.curioSlot -> EquipmentSlot.HEAD;
            case StyleItem.BodyStyleItem.curioSlot -> EquipmentSlot.CHEST;
            case StyleItem.LegsStyleItem.curioSlot -> EquipmentSlot.LEGS;
            case StyleItem.FeetStyleItem.curioSlot -> EquipmentSlot.FEET;
            default -> null;
        };
    }

    public static void hideParts(PlayerModel<?> playerEntityModel, StyleItem item) {
        if(item.hiddenParts != null && !item.hiddenParts.isEmpty()) {
            for(String s : item.hiddenParts) {
                switch (s) {
                    case "head" -> {
                        playerEntityModel.head.visible = false;
                        playerEntityModel.hat.visible = false;
                    }
                    case "body" -> {
                        playerEntityModel.body.visible = false;
                        playerEntityModel.jacket.visible = false;
                    }
                    case "left_arm" -> {
                        playerEntityModel.leftArm.visible = false;
                        playerEntityModel.leftSleeve.visible = false;
                    }
                    case "right_arm" -> {
                        playerEntityModel.rightArm.visible = false;
                        playerEntityModel.rightSleeve.visible = false;
                    }
                    case "left_leg" -> {
                        playerEntityModel.leftLeg.visible = false;
                        playerEntityModel.leftPants.visible = false;
                    }
                    case "right_leg" -> {
                        playerEntityModel.rightLeg.visible = false;
                        playerEntityModel.rightPants.visible = false;
                    }
                }
            }
        }
    }

}
