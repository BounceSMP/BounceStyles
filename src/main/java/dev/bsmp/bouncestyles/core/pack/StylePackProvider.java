package dev.bsmp.bouncestyles.core.pack;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.data.style.StyleLoader;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.level.validation.DirectoryValidator;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class StylePackProvider implements RepositorySource {
    public static final StylePackProvider INSTANCE = new StylePackProvider();
    private static final FileFilter filter = file -> isPackZip(file) || isPackFolder(file);

    @Override
    public void loadPacks(Consumer<Pack> profileAdder) {
        BounceStyles.LOGGER.info("Loading Style Packs...");

        PackType packType = Platform.getEnvironment() == Env.CLIENT ? PackType.CLIENT_RESOURCES : PackType.SERVER_DATA;
        List<Pack> profiles = new ArrayList<>();

        try {
            //? if <= 1.20.1 {
            /*boolean secondArg = false;
            *///?} else if >= 1.21.1 {
            DirectoryValidator secondArg = new DirectoryValidator(path -> filter.accept(path.toFile()));
            //?}
            FolderRepositorySource.discoverPacks(StyleLoader.getStylesDirectory().toPath(), secondArg, (path, resourcesSupplier) -> {
                //? if <= 1.20.1 {
                /*Pack profile = Pack.readMetaAndCreate(
                        BounceStyles.modId + ":" + path.toFile().getName(),
                        Component.literal("Styles Packs"),
                        true,
                        resourcesSupplier,
                        packType,
                        Pack.Position.BOTTOM,
                        PackSource.DEFAULT
                );
                *///?} else if >= 1.21.1 {
                Pack profile = Pack.readMetaAndCreate(
                        new net.minecraft.server.packs.PackLocationInfo(path.toFile().getName(), Component.empty(), PackSource.DEFAULT, Optional.empty()),
                        resourcesSupplier,
                        packType,
                        new net.minecraft.server.packs.PackSelectionConfig(true, Pack.Position.BOTTOM, false)
                );
                //?}

                if (profile != null) profiles.add(profile);
            });
        } catch (IOException e) {
            BounceStyles.LOGGER.error("Exception Occurred trying to read Style Packs", e);
        }

        //? if >= 1.21.11 {
        var version = SharedConstants.getCurrentVersion().packVersion(packType);
        //? } else
//        int version = SharedConstants.getCurrentVersion().getPackVersion(packType);
        List<PackResources> packs = profiles.stream().map(Pack::open).toList();

        //? if <= 1.20.1 {
        /*PackMetadataSection metadata = new PackMetadataSection(Component.translatable(BounceStyles.modId + ".resources.styles"), version);
        Pack mergedProfile = Pack.readMetaAndCreate(
                "Styles",
                Component.literal("Style Packs"),
                true,
                new StylesResourcePack(StyleLoader.getStylesDirectory(), packs, metadata),
                packType,
                Pack.Position.BOTTOM,
                PackSource.DEFAULT
        );
        *///?} else if >= 1.21.1 {
        //? if >= 1.21.11 {
        PackMetadataSection metadata = new PackMetadataSection(Component.translatable(BounceStyles.modId + ".resources.styles"), version.minorRange());
        //? } else
//        PackMetadataSection metadata = new PackMetadataSection(Component.translatable(BounceStyles.modId + ".resources.styles"), version, Optional.empty());

        Pack mergedProfile = Pack.readMetaAndCreate(
                new net.minecraft.server.packs.PackLocationInfo("style_packs", Component.literal("Style Packs"), PackSource.DEFAULT, Optional.empty()),
                new StylesResourcePack(StyleLoader.getStylesDirectory(), packs, metadata),
                packType,
                new net.minecraft.server.packs.PackSelectionConfig(true, Pack.Position.BOTTOM, false)
        );
        //?}

        if(mergedProfile != null) profileAdder.accept(mergedProfile);
    }

    private static boolean isPackZip(File file) {
        return file.isFile() && file.getName().endsWith(".zip");
    }

    private static boolean isPackFolder(File file) {
        return file.isDirectory() && (
                new File(file, "pack.mcmeta").isFile()
                || new File(file, "data").isDirectory()
        );
    }
}
