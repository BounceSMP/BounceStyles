//? if >= 1.21.11 {
package dev.bsmp.bouncestyles.mixin;

import dev.bsmp.bouncestyles.core.client.renderer.StyleEntityState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(AvatarRenderState.class)
public abstract class AvatarRenderStateMixin implements StyleEntityState {
    @Unique private final List<String> bounceStyles$hiddenParts = new ArrayList<>();

    @Override
    public List<String> bounceStyles$getHiddenParts() {
        return this.bounceStyles$hiddenParts;
    }

    @Override
    public void bounceStyles$addHiddenPart(String part) {
        if (!this.bounceStyles$hiddenParts.contains(part))
            this.bounceStyles$hiddenParts.add(part);
    }

    @Override
    public boolean bounceStyles$isPartHidden(String part) {
        return this.bounceStyles$hiddenParts.contains(part);
    }
}
//? }