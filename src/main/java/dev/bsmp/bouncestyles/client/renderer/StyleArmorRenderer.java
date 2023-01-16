package dev.bsmp.bouncestyles.client.renderer;

import dev.bsmp.bouncestyles.item.StyleItem;
import net.minecraft.client.model.PlayerModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class StyleArmorRenderer extends GeoArmorRenderer<StyleItem> {

    public StyleArmorRenderer(AnimatedGeoModel<StyleItem> model) {
        super(model);
    }

    public static void hideParts(PlayerModel<?> playerEntityModel, StyleItem item) {
        if(item.hiddenParts != null && !item.hiddenParts.isEmpty()) {
            for(String s : item.hiddenParts) {
                switch (s) {
                    case "head": {
                        playerEntityModel.head.visible = false;
                        playerEntityModel.hat.visible = false;
                    }
                    case "body": {
                        playerEntityModel.body.visible = false;
                        playerEntityModel.jacket.visible = false;
                    }
                    case "left_arm": {
                        playerEntityModel.leftArm.visible = false;
                        playerEntityModel.leftSleeve.visible = false;
                    }
                    case "right_arm": {
                        playerEntityModel.rightArm.visible = false;
                        playerEntityModel.rightSleeve.visible = false;
                    }
                    case "left_leg": {
                        playerEntityModel.leftLeg.visible = false;
                        playerEntityModel.leftPants.visible = false;
                    }
                    case "right_leg": {
                        playerEntityModel.rightLeg.visible = false;
                        playerEntityModel.rightPants.visible = false;
                    }
                }
            }
        }
    }

}
