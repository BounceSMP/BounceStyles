package dev.bsmp.bouncestyles.core.data.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashMap;

public class Config {
    private boolean unlocksEnabled = true;

    public boolean unlocksEnabled() {
        return this.unlocksEnabled;
    }

    public void setUnlocksEnabled(boolean enabled) {
        this.unlocksEnabled = enabled;
    }

    private Config() {}

    private Config(boolean requireUnlocks) {
        this.unlocksEnabled = requireUnlocks;
    }

    public static Config createDefaultConfig() {
        return new Config();
    }

    public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("unlock_system_enabled", true).forGetter(Config::unlocksEnabled)
    ).apply(instance, Config::new));
}
