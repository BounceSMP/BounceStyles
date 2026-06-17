package dev.bsmp.bouncestyles.core.client.renderer;

import dev.bsmp.bouncestyles.api.data.EquippedStyle;
import dev.bsmp.bouncestyles.api.data.StyleData;
import dev.bsmp.bouncestyles.api.style.Category;
import software.bernie.geckolib.constant.dataticket.DataTicket;

public class StyleDataTickets {
    //~ if >= 1.21.5 'new DataTicket<>' -> 'DataTicket.create' {
    public static final DataTicket<StyleData> TICKET_STYLE_DATA = DataTicket.create("style_data", StyleData.class);
    public static final DataTicket<EquippedStyle> TICKET_STYLE = DataTicket.create("style", EquippedStyle.class);
    public static final DataTicket<Category> TICKET_CATEGORY = DataTicket.create("style_category", Category.class);
    //~ }
}
