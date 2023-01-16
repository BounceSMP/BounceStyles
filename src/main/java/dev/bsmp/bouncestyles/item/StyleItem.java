package dev.bsmp.bouncestyles.item;

import java.util.HashMap;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.bsmp.bouncestyles.client.BounceStylesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import dev.bsmp.bouncestyles.BounceStyles;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;
import software.bernie.geckolib3.util.GeckoLibUtil;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public abstract class StyleItem extends GeoArmorItem implements IAnimatable, ICurioItem {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public final ResourceLocation modelID;
    public final ResourceLocation textureID;
    @Nullable
    public final ResourceLocation animationID;
    @Nullable public final HashMap<String, String> animationMap;

    public boolean useBackupModel = false;
    public List<String> hiddenParts;
    public int transitionTicks;

    public StyleItem(String name, CreativeModeTab group, ResourceLocation modelID, ResourceLocation textureID, @Nullable ResourceLocation animationID, @Nullable HashMap<String, String> animationMap, EquipmentSlot slot) {
        super(BounceStyles.STYLE_MATERIAL, slot, new Properties().tab(group));
        setRegistryName(BounceStyles.modId, name);
        this.modelID = modelID;
        this.textureID = textureID;
        this.animationID = animationID;
        this.animationMap = animationMap;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(String identifier, int index, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, ItemStack stack) {
        VertexConsumer vertexConsumer = renderTypeBuffer.getBuffer(RenderType.armorCutoutNoCull(BounceStylesClient.STYLE_ARMOR_RENDERER.getTextureLocation(this)));
        ((PlayerModel) ((LivingEntityRenderer<?, ?>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(livingEntity)).getModel()).copyPropertiesTo(BounceStylesClient.STYLE_ARMOR_RENDERER);
        BounceStylesClient.STYLE_ARMOR_RENDERER.setCurrentItem(livingEntity, stack, slot);
        BounceStylesClient.STYLE_ARMOR_RENDERER.applySlot(slot);
        BounceStylesClient.STYLE_ARMOR_RENDERER.render(partialTicks, matrixStack, vertexConsumer, light);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean canRender(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        return livingEntity instanceof Player;
    }

    public static class HeadStyleItem extends StyleItem {
        public static final String curioSlot = "head";

        public HeadStyleItem(String name, ResourceLocation model, ResourceLocation texture, @Nullable ResourceLocation animationID, @Nullable HashMap<String, String> animationMap) {
            super(name, StyleTabs.HEAD_GROUP, model, texture, animationID, animationMap, EquipmentSlot.HEAD);
        }

        @Override
        public ResourceLocation getIconId() {
            return new ResourceLocation(BounceStyles.modId, "textures/item/bounce_head.png");
        }

        @Override
        public boolean canEquip(String identifier, LivingEntity livingEntity, ItemStack stack) {
            return identifier.equalsIgnoreCase(curioSlot);
        }
    }
    public static class BodyStyleItem extends StyleItem {
        public static final String curioSlot = "body";

        public BodyStyleItem(String name, ResourceLocation model, ResourceLocation texture, @Nullable ResourceLocation animationID, @Nullable HashMap<String, String> animationMap) {
            super(name, StyleTabs.BODY_GROUP, model, texture, animationID,  animationMap, EquipmentSlot.CHEST);
        }

        @Override
        public ResourceLocation getIconId() {
            return new ResourceLocation(BounceStyles.modId, "textures/item/bounce_body.png");
        }

        @Override
        public boolean canEquip(String identifier, LivingEntity livingEntity, ItemStack stack) {
            return identifier.equalsIgnoreCase(curioSlot);
        }
    }
    public static class LegsStyleItem extends StyleItem {
        public static final String curioSlot = "legs";

        public LegsStyleItem(String name, ResourceLocation model, ResourceLocation texture, @Nullable ResourceLocation animationID, @Nullable HashMap<String, String> animationMap) {
            super(name, StyleTabs.LEGS_GROUP, model, texture, animationID,  animationMap, EquipmentSlot.LEGS);
        }

        @Override
        public ResourceLocation getIconId() {
            return new ResourceLocation(BounceStyles.modId, "textures/item/bounce_legs.png");
        }

        @Override
        public boolean canEquip(String identifier, LivingEntity livingEntity, ItemStack stack) {
            return identifier.equalsIgnoreCase(curioSlot);
        }
    }
    public static class FeetStyleItem extends StyleItem {
        public static final String curioSlot = "feet";

        public FeetStyleItem(String name, ResourceLocation model, ResourceLocation texture, @Nullable ResourceLocation animationID, @Nullable HashMap<String, String> animationMap) {
            super(name, StyleTabs.FEET_GROUP, model, texture, animationID,  animationMap, EquipmentSlot.FEET);
        }

        @Override
        public ResourceLocation getIconId() {
            return new ResourceLocation(BounceStyles.modId, "textures/item/bounce_feet.png");
        }


        @Override
        public boolean canEquip(String identifier, LivingEntity livingEntity, ItemStack stack) {
            return identifier.equalsIgnoreCase(curioSlot);
        }
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        if(this.animationMap != null && !this.animationMap.isEmpty())
            animationData.addAnimationController(new AnimationController<>(this, "controller", this.transitionTicks, this::predicate));
    }

    private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        LivingEntity entity = event.getExtraDataOfType(LivingEntity.class).get(0);
        AnimationController<?> controller = event.getController();
        if(animationMap != null && !animationMap.isEmpty()) {
            String anim;
            if(entity.isFallFlying() && (anim = animationMap.get("flying")) != null)
                return applyAnimation(controller, anim);

            if(!entity.isOnGround() && (anim = animationMap.get("in_air")) != null)
                return applyAnimation(controller, anim);

            else if(entity.isShiftKeyDown() && (anim = animationMap.get("sneaking")) != null)
                return applyAnimation(controller, anim);

            else if (entity.isSwimming() && (anim = animationMap.get("swimming")) != null)
                return applyAnimation(controller, anim);

            else if(entity.isSprinting() && (anim = animationMap.get("sprinting")) != null)
                return applyAnimation(controller, anim);

            else if(isEntityMoving(entity) && (anim = animationMap.get("walking")) != null)
                return applyAnimation(controller, anim);

            else if(entity.isSleeping() && (anim = animationMap.get("sleeping")) != null)
                return applyAnimation(controller, anim);

            else if((anim = animationMap.get("idle")) != null)
                return applyAnimation(controller, anim);
        }
        return PlayState.STOP;
    }

    private static PlayState applyAnimation(AnimationController<?> controller, String anim) {
        if(isCurrentAnimation(controller, anim))
            return PlayState.CONTINUE;
        controller.setAnimation(new AnimationBuilder().addAnimation(anim));
        return PlayState.CONTINUE;
    }

    private static boolean isCurrentAnimation(AnimationController<?> controller, String animation) {
        return controller.getCurrentAnimation() != null && controller.getCurrentAnimation().animationName.equalsIgnoreCase(animation);
    }

    private static boolean isEntityMoving(LivingEntity entity) {
        return !(entity.animationSpeedOld > -0.15F && entity.animationSpeedOld < 0.15F);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public abstract ResourceLocation getIconId();

}
