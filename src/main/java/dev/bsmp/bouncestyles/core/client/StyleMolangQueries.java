package dev.bsmp.bouncestyles.core.client;

import dev.bsmp.bouncestyles.api.style.Style;
import dev.bsmp.bouncestyles.core.client.renderer.StyleDataTickets;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.loading.math.MolangQueries;

public class StyleMolangQueries {
    public static void registerQueries() {
        MolangQueries.<Style>setActorVariable(MolangQueries.BODY_X_ROTATION, actor -> getStateFromActor(actor).bodyRot);
        MolangQueries.<Style>setActorVariable(MolangQueries.HEAD_X_ROTATION, actor -> getStateFromActor(actor).xRot);
        MolangQueries.<Style>setActorVariable(MolangQueries.HEAD_Y_ROTATION, actor -> getStateFromActor(actor).yRot);
        MolangQueries.<Style>setActorVariable(MolangQueries.DEATH_TICKS, actor -> getStateFromActor(actor).deathTime == 0 ? 0 : getStateFromActor(actor).deathTime + actor.partialTick());
        MolangQueries.<Style>setActorVariable(MolangQueries.SCALE, actor -> getStateFromActor(actor).scale);
        MolangQueries.<Style>setActorVariable(MolangQueries.DISTANCE_FROM_CAMERA, actor -> getStateFromActor(actor).distanceToCameraSq);
        MolangQueries.<Style>setActorVariable(MolangQueries.IS_ON_FIRE, actor -> getStateFromActor(actor).displayFireAnimation ? 1 : 0);
        MolangQueries.<Style>setActorVariable(MolangQueries.GROUND_SPEED, actor -> getStateFromActor(actor).getOrDefaultGeckolibData(DataTickets.VELOCITY, Vec3.ZERO).length());
    }

    private static AvatarRenderState getStateFromActor(MolangQueries.Actor<Style> actor) {
        return actor.renderState().getGeckolibData(StyleDataTickets.TICKET_RENDER_DATA).playerState();
    }
}
