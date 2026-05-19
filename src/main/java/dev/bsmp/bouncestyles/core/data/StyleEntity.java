package dev.bsmp.bouncestyles.core.data;

public interface StyleEntity {
    void bounceStyles$setStyleData(StyleData styleData);
    StyleData bounceStyles$getOrCreateStyleData();
}
