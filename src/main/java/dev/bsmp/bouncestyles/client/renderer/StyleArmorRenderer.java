package dev.bsmp.bouncestyles.client.renderer;

import dev.bsmp.bouncestyles.item.StyleItem;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

public class StyleArmorRenderer extends GeoArmorRenderer<StyleItem> {

    public StyleArmorRenderer() {
        super(new StyleModel());
    }

}
