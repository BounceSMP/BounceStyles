package dev.bsmp.bouncestyles.core.pack;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.mixin.ResourcePackManagerAccessor;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class StylePackProvider implements RepositorySource {
    public static final StylePackProvider INSTANCE = new StylePackProvider();
    private static final File stylePackDir = Platform.getGameFolder().resolve("styles").toFile();
    private static final FileFilter filter = file -> (file.isFile() && file.getName().endsWith(".zip")) || (file.isDirectory() && new File(file, "pack.mcmeta").isFile());

    @Override
    public void loadPacks(Consumer<Pack> profileAdder) {
        File[] files;
        if ((files = stylePackDir.listFiles(filter)) == null) return;

        PackType packType = Platform.getEnvironment() == Env.CLIENT ? PackType.CLIENT_RESOURCES : PackType.SERVER_DATA;
        List<Pack> profiles = new ArrayList<>();
        for(File file : files) {
            Pack.ResourcesSupplier factory = (name) -> file.isDirectory() ? new PathPackResources(file.getName(), file.toPath(), false) : new FilePackResources(file.getName(), file, false);
            Pack profile = Pack.readMetaAndCreate(
                    BounceStyles.modId + ":" + file.getName(),
                    Component.literal("Styles Packs"),
                    true,
                    factory,
                    packType,
                    Pack.Position.BOTTOM,
                    PackSource.DEFAULT
            );
            if(profile == null) continue;
            profiles.add(profile);
        }

        int version = SharedConstants.getCurrentVersion().getPackVersion(packType);
        List<PackResources> packs = profiles.stream().map(Pack::open).toList();
        PackMetadataSection metadata = new PackMetadataSection(Component.translatable(BounceStyles.modId + ".resources.styles"), version);
        Pack mergedProfile = Pack.readMetaAndCreate("Styles", Component.literal("Style Packs"), true, (name) -> new StylesResourcePack(stylePackDir, packs, metadata), packType, Pack.Position.BOTTOM, PackSource.DEFAULT);
        if(mergedProfile != null) profileAdder.accept(mergedProfile);
    }

    public static void registerToDataPacks(MinecraftServer server) {
        try {
            PackRepository rpManager = server.getPackRepository();
            ((ResourcePackManagerAccessor) rpManager).getProviders().add(INSTANCE);
            rpManager.reload();
            server.reloadResources(rpManager.getSelectedIds()).get();
        }
        catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
