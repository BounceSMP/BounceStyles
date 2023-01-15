package dev.bsmp.bouncestyles.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.bsmp.bouncestyles.item.StyleItem;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class StyleArmorRenderer extends GeoArmorRenderer<StyleItem> implements TrinketRenderer {

    public StyleArmorRenderer() {
        super(new StyleModel());
    }

    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, PoseStack matrices, MultiBufferSource vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (contextModel instanceof HumanoidModel<? extends LivingEntity>) {
            EquipmentSlot slot = getEquipmentSlotForTrinketSlot(slotReference);
            if (slot != null)
                render(matrices, vertexConsumers, stack, entity, slot, light, (HumanoidModel<LivingEntity>) contextModel);
        }
    }

    private EquipmentSlot getEquipmentSlotForTrinketSlot(SlotReference slotReference) {
        return switch (slotReference.inventory().getSlotType().getGroup()) {
            case "head" -> EquipmentSlot.HEAD;
            case "chest" -> EquipmentSlot.CHEST;
            case "legs" -> EquipmentSlot.LEGS;
            case "feet" -> EquipmentSlot.FEET;
            default -> null;
        };
    }

    @Override
    public ResourceLocation getTextureLocation_geckolib(StyleItem animatable) {
        return animatable.textureID;
    }

}
