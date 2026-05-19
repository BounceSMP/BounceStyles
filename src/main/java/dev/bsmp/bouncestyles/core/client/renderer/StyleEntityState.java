package dev.bsmp.bouncestyles.core.client.renderer;

import java.util.List;

public interface StyleEntityState {
    List<String> bounceStyles$getHiddenParts();
    void bounceStyles$addHiddenPart(String part);
    boolean bounceStyles$isPartHidden(String part);
}
