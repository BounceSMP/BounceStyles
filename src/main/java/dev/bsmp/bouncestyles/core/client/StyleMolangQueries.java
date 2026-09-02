package dev.bsmp.bouncestyles.core.client;

import dev.bsmp.bouncestyles.api.style.Style;
import software.bernie.geckolib.loading.math.MolangQueries;

//? if >= 1.21.11 {
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import dev.bsmp.bouncestyles.core.client.renderer.StyleDataTickets;
import software.bernie.geckolib.renderer.base.GeoRenderState;
//? } else {
/*import net.minecraft.world.entity.player.Player;
*///? }

public class StyleMolangQueries {
    public static final String IS_IN_AIR = "query.is_in_air";

    public static void registerQueries() {
        //? if >= 1.21.11 {
        MolangQueries.<Style>setActorVariable(MolangQueries.BODY_Y_ROTATION, actor -> getStateFromActor(actor).bodyRot);
        MolangQueries.<Style>setActorVariable(MolangQueries.HEAD_X_ROTATION, actor -> getStateFromActor(actor).xRot);
        MolangQueries.<Style>setActorVariable(MolangQueries.HEAD_Y_ROTATION, actor -> getStateFromActor(actor).yRot);
        MolangQueries.<Style>setActorVariable(MolangQueries.IS_ON_FIRE, actor -> getStateFromActor(actor).displayFireAnimation ? 1 : 0);
        MolangQueries.<Style>setActorVariable(MolangQueries.IS_ON_GROUND, actor -> ((GeoRenderState) getStateFromActor(actor)).getGeckolibData(StyleDataTickets.TICKET_ON_GROUND) ? 1 : 0);
        MolangQueries.<Style>setActorVariable(IS_IN_AIR, actor -> ((GeoRenderState) getStateFromActor(actor)).getGeckolibData(StyleDataTickets.TICKET_ON_GROUND) ? 0 : 1);
        //? } else {
        /*MolangQueries.<Style>setActorVariable(MolangQueries.BODY_Y_ROTATION, actor -> getPlayerFromActor(actor).yBodyRot);
        MolangQueries.<Style>setActorVariable(MolangQueries.HEAD_X_ROTATION, actor -> getPlayerFromActor(actor).getXRot());
        MolangQueries.<Style>setActorVariable(MolangQueries.HEAD_Y_ROTATION, actor -> getPlayerFromActor(actor).getYHeadRot());
        MolangQueries.<Style>setActorVariable(MolangQueries.IS_ON_FIRE, actor -> getPlayerFromActor(actor).isOnFire() ? 1 : 0);
        MolangQueries.<Style>setActorVariable(MolangQueries.IS_ON_GROUND, actor -> getPlayerFromActor(actor).onGround() ? 1 : 0);
        MolangQueries.<Style>setActorVariable(IS_IN_AIR, actor -> getPlayerFromActor(actor).onGround() ? 0 : 1);
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