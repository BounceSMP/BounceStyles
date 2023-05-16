package dev.bsmp.bouncestyles.pack;

import com.mojang.bridge.game.PackType;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.mixin.ResourcePackManagerAccessor;
import net.minecraft.SharedConstants;
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class StylePackProvider implements ResourcePackProvider {
    public static final StylePackProvider INSTANCE = new StylePackProvider();
    private static final File stylePackDir = Platform.getGameFolder().resolve("styles").toFile();
    private static final FileFilter filter = file -> (file.isFile() && file.getName().endsWith(".zip")) || (file.isDirectory() && new File(file, "pack.mcmeta").isFile());

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder, ResourcePackProfile.Factory factory) {
        File[] files;
        if ((files = stylePackDir.listFiles(filter)) == null) return;

        List<ResourcePackProfile> profiles = new ArrayList<>();
        for(File file : files) {
            Supplier<ResourcePack> supplier = () -> file.isDirectory() ? new DirectoryResourcePack(file) : new ZipResourcePack(file);
            ResourcePackProfile profile = ResourcePackProfile.of(
                    BounceStyles.modId + ":" + file.getName(),
                    true,
                    supplier,
                    factory,
                    ResourcePackProfile.InsertionPosition.TOP,
                    ResourcePackSource.PACK_SOURCE_NONE
            );
            if(profile == null) continue;
            profiles.add(profile);
        }

        if(Platform.getEnvironment() == Env.SERVER) {
            for(ResourcePackProfile profile : profiles) profileAdder.accept(profile);
            return;
        }

        List<ResourcePack> packs = profiles.stream().map(ResourcePackProfile::createResourcePack).toList();
        int version = SharedConstants.getGameVersion().getPackVersion(PackType.RESOURCE);
        PackResourceMetadata metadata = new PackResourceMetadata(new TranslatableText(BounceStyles.modId + ".resources.styles"), version);
        ResourcePackProfile mergedProfile = ResourcePackProfile.of("Styles", true, () -> new StylesResourcePack(stylePackDir, packs, metadata), factory, ResourcePackProfile.InsertionPosition.TOP, ResourcePackSource.PACK_SOURCE_NONE);
        if(mergedProfile != null) profileAdder.accept(mergedProfile);
    }

    public static void registerToDataPacks(MinecraftServer server) {
        try {
            ResourcePackManager rpManager = server.getDataPackManager();
            ((ResourcePackManagerAccessor) rpManager).getProviders().add(INSTANCE);
            rpManager.scanPacks();
            server.reloadResources(rpManager.getEnabledNames()).get();
        }
        catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
