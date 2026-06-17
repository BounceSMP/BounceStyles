package dev.bsmp.bouncestyles.core.pack;

import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.bsmp.bouncestyles.core.BounceStyles;
import dev.bsmp.bouncestyles.core.client.BounceStylesClient;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

//? if >= 1.21.11 {
import net.minecraft.SharedConstants;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import software.bernie.geckolib.cache.GeckoLibResources;
import org.jspecify.annotations.Nullable;
//? } else
//import net.minecraft.server.packs.metadata.MetadataSectionSerializer;

public class StylesResourcePack extends AbstractPackResources implements Pack.ResourcesSupplier {
    //? if >= 1.21.11 {
    private static final Map<MetadataSectionType<?>, ?> BUILT_IN_METADATA = Map.of(
            PackMetadataSection.CLIENT_TYPE, new PackMetadataSection(Component.literal("BounceStyles"), SharedConstants.getCurrentVersion().packVersion(PackType.CLIENT_RESOURCES).minorRange()),
            PackMetadataSection.SERVER_TYPE, new PackMetadataSection(Component.literal("BounceStyles"), SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA).minorRange()),
            FeatureFlagsMetadataSection.TYPE, new FeatureFlagsMetadataSection(FeatureFlags.DEFAULT_FLAGS)
    );
    //? }
    private final List<PackResources> mergedPacks;
    private final PackMetadataSection metadata;
    private final Map<String, List<PackResources>> dataNamespaces;
    private final Map<String, List<PackResources>> resourceNamespaces;

    public StylesResourcePack(File base, List<PackResources> mergedPacks, PackMetadataSection metadata) {
        //? if <= 1.20.1 {
        /*super(BounceStyles.modId + ":stylePacks", false);
        *///?} else if >= 1.21.1 {
        super(new net.minecraft.server.packs.PackLocationInfo(BounceStyles.modId + ":stylePacks", Component.empty(), PackSource.BUILT_IN, Optional.empty()));
        //?}
        this.mergedPacks = mergedPacks;
        this.metadata = metadata;
        dataNamespaces = buildPackMap(PackType.SERVER_DATA);
        resourceNamespaces = buildPackMap(PackType.CLIENT_RESOURCES);
    }

    private Map<String, List<PackResources>> buildPackMap(PackType type) {
        Map<String, List<PackResources>> map = new HashMap<>();
        for(PackResources pack : this.mergedPacks) {
            for(String namespace : pack.getNamespaces(type)) {
                map.computeIfAbsent(namespace, key -> new ArrayList<>()).add(pack);
            }
        }
        return map;
    }

    @Override
    public IoSupplier<InputStream> getRootResource(String... segments) {
        for (String fileName : segments) {
            if (fileName.equals("pack.png")) {
                Mod mod = Platform.getMod(BounceStyles.modId);
                String logoPath = mod.getLogoFile(120).orElse("");
                Path path = mod.findResource(logoPath).orElse(null);
                if (path != null)
                    return IoSupplier.create(path);
            }
        }
        return null;
    }

    @Override
    public IoSupplier<InputStream> getResource(PackType type, Identifier id) {
        Map<String, List<PackResources>> map = type == PackType.CLIENT_RESOURCES ? resourceNamespaces : dataNamespaces;
        List<PackResources> matchingPacks = map.get(id.getNamespace());
        if (matchingPacks == null) matchingPacks = Collections.emptyList();

        if (type == PackType.CLIENT_RESOURCES && Platform.getEnvironment() == Env.CLIENT && BounceStylesClient.isLookingForLang(id)) {
            return BounceStylesClient.processPackLangs(matchingPacks, id);
        }

        for(PackResources pack : matchingPacks) {
            IoSupplier<InputStream> supplier = pack.getResource(type, id);
            if (supplier != null)
                return supplier;
        }
        return null;
    }

    @Override
    public void listResources(PackType type, String namespace, String path, ResourceOutput consumer) {
        var alternatePath = "";
        //? if >= 1.21.5 {
        if (path.equals(GeckoLibResources.MODELS_PATH.getPath()))
            alternatePath = "geo";
        else if (path.equals(GeckoLibResources.ANIMATIONS_PATH.getPath()))
            alternatePath = "animations";
        //? }

        for (PackResources pack : this.mergedPacks) {
            pack.listResources(type, namespace, path, consumer);
            if (!alternatePath.isBlank())
                pack.listResources(type, namespace, alternatePath, consumer);
        }
    }

    //? if >= 1.21.11 {
    @Override
    public @Nullable <T> T getMetadataSection(MetadataSectionType<T> type) throws IOException {
        return (T) BUILT_IN_METADATA.get(type);
    }
    //? } else {
    /*@Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> metaReader) throws IOException {
        if(metaReader.getMetadataSectionName().equals("pack"))
            return (T) metadata;
        return null;
    }
    *///? }

    @Override
    public Set<String> getNamespaces(PackType type) {
        switch (type) {
            case SERVER_DATA -> {
                return this.dataNamespaces.keySet();
            }
            case CLIENT_RESOURCES -> {
                return this.resourceNamespaces.keySet();
            }
        }
        return null;
    }

    @Override
    public void close() {
        for(PackResources pack : this.mergedPacks)
            pack.close();
    }

    //? if <= 1.20.1 {
    /*@Override
    public PackResources open(String string) {
        return this;
    }
    *///?} else if >= 1.21.1 {
    @Override
    public PackResources openPrimary(net.minecraft.server.packs.PackLocationInfo location) {
        return this;
    }

    @Override
    public PackResources openFull(net.minecraft.server.packs.PackLocationInfo location, Pack.Metadata metadata) {
        return this;
    }


    //?}
}
