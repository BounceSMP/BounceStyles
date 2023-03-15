package dev.bsmp.bouncestyles.mixin;

import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntryListWidget.class)
public interface EntryListAccessor {

    @Accessor("field_26846")
    public void setField_26846(boolean value);

    @Accessor("field_26847")
    public void setField_26847(boolean value);

}
