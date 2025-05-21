package dev.bsmp.bouncestyles.core.data;

public interface StyleEntity {
    void setStyleData(StyleData styleData);
    StyleData getOrCreateStyleData();
}
