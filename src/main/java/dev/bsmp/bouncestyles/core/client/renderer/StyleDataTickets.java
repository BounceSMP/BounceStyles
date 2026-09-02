package dev.bsmp.bouncestyles.core.client.renderer;

import dev.bsmp.bouncestyles.api.animation.StyleAnimState;
import dev.bsmp.bouncestyles.api.data.EquippedStyle;
import dev.bsmp.bouncestyles.api.data.StyleData;
import dev.bsmp.bouncestyles.api.style.Category;
import software.bernie.geckolib.constant.dataticket.DataTicket;

public class StyleDataTickets {
    //~ if >= 1.21.5 'new DataTicket<>' -> 'DataTicket.create' {
    //? if >= 1.21.11
    public static final DataTicket<StyleLayerRenderer.RenderData> TICKET_RENDER_DATA = DataTicket.create("style_render_data", StyleLayerRenderer.RenderData.class);

    public static final DataTicket<StyleData> TICKET_STYLE_DATA = DataTicket.create("style_data", StyleData.class);
    public static final DataTicket<EquippedStyle> TICKET_EQUIPPED = DataTicket.create("style", EquippedStyle.class);
    public static final DataTicket<Category> TICKET_CATEGORY = DataTicket.create("style_category", Category.class);
    public static final DataTicket<StyleAnimState> TICKET_STYLE_ANIM_STATE = DataTicket.create("anim_state", StyleAnimState.class);
    public static final DataTicket<Boolean> TICKET_ON_GROUND = DataTicket.create("in_air", Boolean.class);
    public static final DataTicket<Boolean> TICKET_SPRINTING = DataTicket.create("sprinting", Boolean.class);
    //~ }
}
