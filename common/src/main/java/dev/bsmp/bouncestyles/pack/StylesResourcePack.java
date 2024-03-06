package dev.bsmp.bouncestyles.pack;

import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleLoader;
import dev.bsmp.bouncestyles.client.BounceStylesClient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

public class StylesResourcePack extends AbstractPackResources {
    private final List<PackResources> mergedPacks;
    private final PackMetadataSection metadata;
    private final Map<String, List<PackResources>> dataNamespaces;
    private final Map<String, List<PackResources>> resourceNamespaces;

    public StylesResourcePack(File base, List<PackResources> mergedPacks, PackMetadataSection metadata) {
        super(BounceStyles.modId + ":stylePacks", false);
        this.mergedPacks = mergedPacks;
        this.metadata = metadata;
        dataNamespaces = buildPackMap(PackType.SERVER_DATA);
        resourceNamespaces = buildPackMap(PackType.CLIENT_RESOURCES);
    }

    public void registerPackStyles() {
        for(PackResources pack : this.mergedPacks) {
            IoSupplier<InputStream> supplier = pack.getRootResource("styles.json");
            if (supplier == null) {
                BounceStyles.LOGGER.warn("No styles.json file found for pack '" + pack.packId() + "'; Skipping...");
                continue;
            }

            try(InputStream stream = supplier.get()) {
                StyleLoader.loadStyles(pack.packId() + "/styles.json", stream);
            }
            catch (IOException e) {
                BounceStyles.LOGGER.error("Exception occurred while processing styles.json for pack: " + pack.packId());
                BounceStyles.LOGGER.error(e);
            }
        }
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

    @Nullable
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
    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation id) {
        Map<String, List<PackResources>> map = type == PackType.CLIENT_RESOURCES ? resourceNamespaces : dataNamespaces;
        List<PackResources> matchingPacks = map.get(id.getNamespace());
        if (matchingPacks == null) matchingPacks = Collections.emptyList();

        if (type == PackType.CLIENT_RESOURCES && BounceStylesClient.isLookingForLang(id)) {
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
    public void listResources(PackType type, String namespace, String prefix, ResourceOutput consumer) {
        for (PackResources pack : this.mergedPacks) {
            pack.listResources(type, namespace, prefix, consumer);
        }
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> metaReader) throws IOException {
        if(metaReader.getMetadataSectionName().equals("pack"))
            return (T) metadata;
        return null;
    }

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
}
