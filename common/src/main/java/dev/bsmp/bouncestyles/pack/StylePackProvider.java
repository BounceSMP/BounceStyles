package dev.bsmp.bouncestyles.pack;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.mixin.ResourcePackManagerAccessor;
import net.minecraft.SharedConstants;
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

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
    public void register(Consumer<ResourcePackProfile> profileAdder) {
        File[] files;
        if ((files = stylePackDir.listFiles(filter)) == null) return;

        ResourceType packType = Platform.getEnvironment() == Env.CLIENT ? ResourceType.CLIENT_RESOURCES : ResourceType.SERVER_DATA;
        List<ResourcePackProfile> profiles = new ArrayList<>();
        for(File file : files) {
            ResourcePackProfile.PackFactory factory = (name) -> file.isDirectory() ? new DirectoryResourcePack(file.getName(), file.toPath(), false) : new ZipResourcePack(file.getName(), file, false);
            ResourcePackProfile profile = ResourcePackProfile.create(
                    BounceStyles.modId + ":" + file.getName(),
                    Text.literal("Styles Packs"),
                    true,
                    factory,
                    packType,
                    ResourcePackProfile.InsertionPosition.BOTTOM,
                    ResourcePackSource.NONE
            );
            if(profile == null) continue;
            profiles.add(profile);
        }

        int version = SharedConstants.getGameVersion().getResourceVersion(packType);
        List<ResourcePack> packs = profiles.stream().map(ResourcePackProfile::createResourcePack).toList();
        PackResourceMetadata metadata = new PackResourceMetadata(Text.translatable(BounceStyles.modId + ".resources.styles"), version);
        ResourcePackProfile mergedProfile = ResourcePackProfile.create("Styles", Text.literal("Style Packs2"), true, (name) -> new StylesResourcePack(stylePackDir, packs, metadata), packType, ResourcePackProfile.InsertionPosition.BOTTOM, ResourcePackSource.NONE);
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
