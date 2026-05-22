package dev.bsmp.bouncestyles.core.data.unlocks;

import dev.bsmp.bouncestyles.core.BounceStyles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UnlockManager {
    public static <T extends Entity> Optional<Set<Identifier>> readUnlockData(T styleEntity) {
        if (styleEntity.level().isClientSide()) return Optional.empty();

        var unlocksDir = ((ServerLevel) styleEntity.level()).getServer().getWorldPath(LevelResource.ROOT).resolve("style_unlocks");
        if (unlocksDir.toFile().mkdirs()) return Optional.empty();

        var dataFile = unlocksDir.resolve(styleEntity.getStringUUID()+".dat");
        if (dataFile.toFile().isDirectory()) {
            try {
                Files.delete(dataFile);
            }
            catch (Exception ignored) {}
            return Optional.empty();
        }
        else if (!dataFile.toFile().exists()) {
            writeUnlockData(styleEntity, Set.of());
            return Optional.of(new HashSet<>());
        }

        try {
            var nbt = NbtIo.read(dataFile);
            if (nbt != null) {
                var unlocks = Identifier.CODEC.listOf().parse(NbtOps.INSTANCE, nbt.getList("").orElse(new ListTag())).resultOrPartial(BounceStyles.LOGGER::error);
                if (unlocks.isPresent()) {
                    var set = new HashSet<>(unlocks.get());
                    return Optional.of(set);
                }
            }
        }
        catch (IOException e) {
            BounceStyles.LOGGER.error("Exception Occurred trying to read Unlock Data for entity {}", styleEntity.getDisplayName(), e);
        }

        return Optional.empty();
    }

    public static <T extends Entity> void writeUnlockData(T styleEntity, Set<Identifier> unlocks) {
        if (styleEntity.level().isClientSide()) return;

        var unlocksDir = ((ServerLevel) styleEntity.level()).getServer().getWorldPath(LevelResource.ROOT).resolve("style_unlocks");
        unlocksDir.toFile().mkdirs();

        var dataFile = unlocksDir.resolve(styleEntity.getStringUUID()+".dat");
        Identifier.CODEC.listOf().encodeStart(NbtOps.INSTANCE, unlocks.stream().toList()).resultOrPartial(BounceStyles.LOGGER::error).ifPresent(tag -> {
            try {
                var compoundTag = new CompoundTag();
                compoundTag.put("", tag);
                NbtIo.write(compoundTag, dataFile);
            } catch (IOException e) {
                BounceStyles.LOGGER.error("Exception Occurred trying to write Unlock Data for entity {}", styleEntity.getDisplayName(), e);
            }
        });
    }

    public static <T extends Entity> boolean unlockStyle(T styleEntity, Identifier styleId) {
        if(styleId == null) return false;

        var unlocks = readUnlockData(styleEntity).orElse(new HashSet<>());
        if (unlocks.add(styleId)) {
            writeUnlockData(styleEntity, unlocks);
            return true;
        }
        return false;
    }

    public static <T extends Entity> boolean lockStyle(T styleEntity, Identifier styleId) {
        if(styleId == null) return false;

        var unlocks = readUnlockData(styleEntity).orElse(new HashSet<>());
        if (unlocks.remove(styleId)) {
            writeUnlockData(styleEntity, unlocks);
            return true;
        }
        return false;
    }

    public static <T extends Entity> boolean hasUnlocked(T styleEntity, Identifier styleId) {
        if(styleId == null) return false;
        return readUnlockData(styleEntity).map(identifiers -> identifiers.contains(styleId)).orElse(false);
    }
}
