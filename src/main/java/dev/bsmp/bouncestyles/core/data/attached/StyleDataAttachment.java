package dev.bsmp.bouncestyles.core.data.attached;

import dev.bsmp.bouncestyles.api.data.StyleData;
//? if fabric {
import dev.bsmp.bouncestyles.fabric.BounceStylesFabric;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
//? } else {
/*import dev.bsmp.bouncestyles.neoforge.BounceStylesNeoforge;
import net.neoforged.neoforge.attachment.AttachmentHolder;
*///? }

public class StyleDataAttachment {

    //~ if fabric 'AttachmentHolder' -> 'AttachmentTarget'
    public static void setEntityData(AttachmentTarget target, StyleData data) {
        //? if fabric {
        target.setAttached(BounceStylesFabric.STYLE_DATA_ATTACHMENT, data);
        //? } else
        //target.setData(BounceStylesNeoforge.STYLE_DATA_ATTACHMENT, data);
    }

    //~ if fabric 'AttachmentHolder' -> 'AttachmentTarget'
    public static StyleData getEntityData(AttachmentTarget target) {
        //? if fabric {
        return target.getAttachedOrCreate(BounceStylesFabric.STYLE_DATA_ATTACHMENT);
        //? } else
        //return target.getData(BounceStylesNeoforge.STYLE_DATA_ATTACHMENT);
    }

}
