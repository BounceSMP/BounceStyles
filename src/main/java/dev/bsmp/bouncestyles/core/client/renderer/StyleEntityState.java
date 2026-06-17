//? if >= 1.21.5 {
package dev.bsmp.bouncestyles.core.client.renderer;

import java.util.Set;

public interface StyleEntityState {
    Set<String> bounceStyles$getHiddenParts();
    void bounceStyles$addHiddenPart(String part);
    boolean bounceStyles$isPartHidden(String part);
}
//? }