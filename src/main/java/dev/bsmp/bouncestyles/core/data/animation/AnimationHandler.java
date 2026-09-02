package dev.bsmp.bouncestyles.core.data.animation;

import dev.bsmp.bouncestyles.api.animation.StyleAnimState;
import dev.bsmp.bouncestyles.core.client.renderer.StyleDataTickets;
import software.bernie.geckolib.animation.RawAnimation;
import dev.bsmp.bouncestyles.api.style.Style;

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

            var animController = /*~ if >= 1.21.8 'getController' -> 'controller'*/ state.controller();
            var animState =
            //? if >= 1.21.8 {
            state.getDataOrDefault(StyleDataTickets.TICKET_STYLE_ANIM_STATE, StyleAnimState.IDLE);
            //? } else {
            /*state.getData(StyleDataTickets.TICKET_STYLE_ANIM_STATE);
            *///? }

            if ((anim = animations.get(animState.name().toLowerCase())) != null)
                return applyAnimation(animController, anim);
        }

        return PlayState.STOP;
    }
    //~ }

    private static PlayState applyAnimation(AnimationController<?> controller, RawAnimation anim) {
        controller.setAnimation(anim);
        return PlayState.CONTINUE;
    }
}
