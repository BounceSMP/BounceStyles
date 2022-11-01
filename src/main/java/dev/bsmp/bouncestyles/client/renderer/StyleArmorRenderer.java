package dev.bsmp.bouncestyles.client.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import dev.bsmp.bouncestyles.item.StyleItem;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class StyleArmorRenderer extends GeoArmorRenderer<StyleItem> implements TrinketRenderer {

    public StyleArmorRenderer() {
        super(new StyleModel());
    }

    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (contextModel instanceof BipedEntityModel<? extends LivingEntity>) {
            EquipmentSlot slot = getEquipmentSlotForTrinketSlot(slotReference);
            if (slot != null)
                render(matrices, vertexConsumers, stack, entity, slot, light, (BipedEntityModel<LivingEntity>) contextModel);
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
}
