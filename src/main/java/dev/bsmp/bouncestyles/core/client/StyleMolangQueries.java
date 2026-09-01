package dev.bsmp.bouncestyles.core.client;

import dev.bsmp.bouncestyles.api.style.Style;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.loading.math.MolangQueries;
//? if >= 1.21.11 {
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import dev.bsmp.bouncestyles.core.client.renderer.StyleDataTickets;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.base.GeoRenderState;
//? }

public class StyleMolangQueries {
    public static void registerQueries() {
        //? if >= 1.21.11 {
        MolangQueries.<Style>setActorVariable(MolangQueries.BODY_Y_ROTATION, actor -> getStateFromActor(actor).bodyRot);
        MolangQueries.<Style>setActorVariable(MolangQueries.HEAD_X_ROTATION, actor -> getStateFromActor(actor).xRot);
        MolangQueries.<Style>setActorVariable(MolangQueries.HEAD_Y_ROTATION, actor -> getStateFromActor(actor).yRot);
        MolangQueries.<Style>setActorVariable(MolangQueries.IS_ON_FIRE, actor -> getStateFromActor(actor).displayFireAnimation ? 1 : 0);
        //? } else {
        /*MolangQueries.<Style>setActorVariable(MolangQueries.BODY_Y_ROTATION, actor -> getPlayerFromActor(actor).yBodyRot);
        MolangQueries.<Style>setActorVariable(MolangQueries.HEAD_X_ROTATION, actor -> getPlayerFromActor(actor).getXRot());
        MolangQueries.<Style>setActorVariable(MolangQueries.HEAD_Y_ROTATION, actor -> getPlayerFromActor(actor).getYHeadRot());
        MolangQueries.<Style>setActorVariable(MolangQueries.IS_ON_FIRE, actor -> getPlayerFromActor(actor).isOnFire() ? 1 : 0);
        *///? }
    }

    //? if >= 1.21.11 {
    private static AvatarRenderState getStateFromActor(MolangQueries.Actor<Style> actor) {
        return actor.renderState().getGeckolibData(StyleDataTickets.TICKET_RENDER_DATA).playerState();
    }
    //? } else {
    /*private static Player getPlayerFromActor(MolangQueries.Actor<Style> actor) {
        return actor.animationState().getData(Style.PLAYER);
    }
    *///? }
}