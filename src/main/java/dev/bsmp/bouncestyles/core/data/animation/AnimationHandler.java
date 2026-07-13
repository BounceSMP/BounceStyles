package dev.bsmp.bouncestyles.core.data.animation;

import dev.bsmp.bouncestyles.api.animation.AnimState;
import dev.bsmp.bouncestyles.core.BounceStyles;
import software.bernie.geckolib.animation.RawAnimation;
import dev.bsmp.bouncestyles.api.style.Style;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import java.util.Map;

//? if >= 1.21.11 {
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.animation.state.AnimationTest;
//? } else {
/*import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
*///? }

public class AnimationHandler {
    //~ if >= 1.21.8 'AnimationState' -> 'AnimationTest' {
    public static PlayState handleAnimState(AnimationTest<Style> state) {
        var animatable = /*? if >= 1.21.8 {*/ state.animatable(); /*? } else { */ /*state.getAnimatable(); *///? }
        return animatable.getAnimationMap().map(map -> mapAnimations(state, map)).orElse(PlayState.STOP);
    }

    private static PlayState mapAnimations(AnimationTest<Style> state, Map<String, RawAnimation> animations) {
        if (!animations.isEmpty()) {
            //ToDo Consider supporting EmoteCraft emote-specific animations, if specified as something like "emote.emote_name"
            RawAnimation anim;
            //~ if >= 1.21.8 'getController' -> 'controller'
            var controller = state.controller();
            var animState =
            //? if >= 1.21.8 {
            state.getDataOrDefault(TICKET_ANIM_STATE, AnimState.IDLE);
            //? } else {
            /*state.getData(TICKET_ANIM_STATE);
            *///? }

            if ((anim = animations.get(animState.name().toLowerCase())) != null)
                return applyAnimation(controller, anim);
        }

        return PlayState.STOP;
    }
    //~ }

    private static PlayState applyAnimation(AnimationController<?> controller, RawAnimation anim) {
        controller.setAnimation(anim);
        return PlayState.CONTINUE;
    }

    //~ if >= 1.21.5 'new DataTicket<>' -> 'DataTicket.create' {
    public static final DataTicket<AnimState> TICKET_ANIM_STATE = DataTicket.create("anim_state", AnimState.class);
    public static final DataTicket<Boolean> TICKET_ON_GROUND = DataTicket.create("in_air", Boolean.class);
    public static final DataTicket<Boolean> TICKET_SPRINTING = DataTicket.create("sprinting", Boolean.class);
    //~ }
}
