package dev.bsmp.bouncestyles.core.data.animation;

import dev.bsmp.bouncestyles.core.data.style.Style;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.animation.state.AnimationTest;
import software.bernie.geckolib.constant.dataticket.DataTicket;

public class AnimationHandler {
    public static PlayState handleAnimState(AnimationTest<Style> state) {
        return state.animatable()
                .getAnimationMap()
                .map(animations -> {
                    if (!animations.isEmpty()) {
                        //ToDo Consider supporting EmoteCraft emote-specific animations, if specified as something like "emote.emote_name"
                        RawAnimation anim;
                        var controller = state.controller();
                        var animState = state.getDataOrDefault(TICKET_ANIM_STATE, AnimState.IDLE);

                        if ((anim = animations.get(animState.name().toLowerCase())) != null)
                            return applyAnimation(controller, anim);
                    }

                    return PlayState.STOP;
                }).orElse(PlayState.STOP);
    }

    private static PlayState applyAnimation(AnimationController<?> controller, RawAnimation anim) {
        controller.setAnimation(anim);
        return PlayState.CONTINUE;
    }

    public static DataTicket<AnimState> TICKET_ANIM_STATE = DataTicket.create("anim_state", AnimState.class);
    public static final DataTicket<Boolean> TICKET_ON_GROUND = DataTicket.create("in_air", Boolean.class);
    public static final DataTicket<Boolean> TICKET_SPRINTING = DataTicket.create("sprinting", Boolean.class);
}
