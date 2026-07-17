package dev.bsmp.bouncestyles.core.data.unlocks;

import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.data.config.Config;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

//? if >= 1.21.11 {
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.server.permissions.Permissions;
//? }

public class UnlockManager {
    public static final LevelResource LEVEL_DIR_UNLOCKS = new LevelResource("style_unlocks");

    public static Optional<Set<Identifier>> readUnlockData(Entity entity) {
        if (entity.level().isClientSide()) return Optional.empty();

        var unlocksDir = ((ServerLevel) entity.level()).getServer().getWorldPath(LEVEL_DIR_UNLOCKS);
        if (unlocksDir.toFile().mkdirs()) return Optional.empty();

        var dataFile = unlocksDir.resolve(entity.getStringUUID()+".dat");
        if (dataFile.toFile().isDirectory()) {
            try {
                Files.delete(dataFile);
            }
            catch (Exception ignored) {}
            return Optional.empty();
        }
        else if (!dataFile.toFile().exists()) {
            writeUnlockData(entity, Set.of());
            return Optional.of(new HashSet<>());
        }

        try {
            var nbt = NbtIo.read(dataFile);
            if (nbt != null) {
                var unlocks = Identifier.CODEC.listOf().parse(
                        NbtOps.INSTANCE,
                        getList(nbt, "").orElse(new ListTag())
                ).resultOrPartial(BounceStyles.LOGGER::error);

                if (unlocks.isPresent()) {
                    var set = new HashSet<>(unlocks.get());
                    return Optional.of(set);
                }
            }
        }
        catch (IOException e) {
            BounceStyles.LOGGER.error("Exception Occurred trying to read Unlock Data for entity {}", entity.getDisplayName(), e);
        }

        return Optional.empty();
    }

    private static Optional<ListTag> getList(CompoundTag nbt, String key) {
        //? if >= 1.21.8 {
        return nbt.getList(key);
        //? } else {
        /*return Optional.of(nbt.getList(key, ListTag.TAG_STRING)); //ToDo This probably wont work but y'kno, it compiles for now
        *///? }
    }

    public static void writeUnlockData(Entity entity, Set<Identifier> unlocks) {
        if (entity.level().isClientSide()) return;

        var unlocksDir = ((ServerLevel) entity.level()).getServer().getWorldPath(LevelResource.ROOT).resolve("style_unlocks");
        unlocksDir.toFile().mkdirs();

        var dataFile = unlocksDir.resolve(entity.getStringUUID()+".dat");
        Identifier.CODEC.listOf().encodeStart(NbtOps.INSTANCE, unlocks.stream().toList()).resultOrPartial(BounceStyles.LOGGER::error).ifPresent(tag -> {
            try {
                var compoundTag = new CompoundTag();
                compoundTag.put("", tag);
                NbtIo.write(compoundTag, dataFile);
            } catch (IOException e) {
                BounceStyles.LOGGER.error("Exception Occurred trying to write Unlock Data for entity {}", entity.getDisplayName(), e);
            }
        });
    }

    public static boolean unlockStyle(Entity entity, Identifier styleId) {
        if(styleId == null) return false;

        var unlocks = readUnlockData(entity).orElse(new HashSet<>());
        if (unlocks.add(styleId)) {
            writeUnlockData(entity, unlocks);
            return true;
        }
        return false;
    }

    public static boolean lockStyle(Entity entity, Identifier styleId) {
        if(styleId == null) return false;

        var unlocks = readUnlockData(entity).orElse(new HashSet<>());
        if (unlocks.remove(styleId)) {
            writeUnlockData(entity, unlocks);
            return true;
        }
        return false;
    }

    public static boolean hasUnlocked(Entity entity, Identifier styleId) {
        if(styleId == null) return false;
        return readUnlockData(entity).map(identifiers -> identifiers.contains(styleId)).orElse(false);
    }

    public static boolean requiresUnlocks(Entity entity) {
        if (!Config.requireUnlocks) return true;

        if (entity instanceof Player player) {
            //? if >= 1.21.11 {
            return !(
                    (!Config.unlockBypassRequiresCreative || player.isCreative())
                    &&
                    player.permissions().hasPermission(getRequiredPermission())
            );
            //? } else {
            /*return !(
                  (!Config.unlockBypassRequiresCreative || player.isCreative())
                  &&
                  player.hasPermissions(Config.unlockBypassPermissionLevel)
            );
            *///? }
        }
        return false;
    }

    //? if >= 1.21.11 {
    public static Permission getRequiredPermission() {
        return switch (Config.unlockBypassPermissionLevel) {
            case 1 -> Permissions.COMMANDS_MODERATOR;
            case 2 -> Permissions.COMMANDS_GAMEMASTER;
            case 3 -> Permissions.COMMANDS_ADMIN;
            case 4 -> Permissions.COMMANDS_OWNER;
            default -> new Permission.HasCommandLevel(PermissionLevel.ALL);
        };
    }
    //? }
}
