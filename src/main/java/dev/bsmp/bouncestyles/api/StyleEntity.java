package dev.bsmp.bouncestyles.api;

import dev.bsmp.bouncestyles.core.data.StyleData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Avatar;

public interface StyleEntity {
    EntityDataSerializer<StyleData> STYLE_DATA_SERIALIZER = EntityDataSerializer.forValueType(StyleData.STREAM_CODEC);

    void bounceStyles$setStyleData(StyleData styleData);
    StyleData bounceStyles$getStyleData();
}
