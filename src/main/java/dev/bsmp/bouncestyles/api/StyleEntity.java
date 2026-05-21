package dev.bsmp.bouncestyles.api;

import dev.bsmp.bouncestyles.core.data.StyleData;

public interface StyleEntity {
    void bounceStyles$setStyleData(StyleData styleData);
    StyleData bounceStyles$getOrCreateStyleData();
}
