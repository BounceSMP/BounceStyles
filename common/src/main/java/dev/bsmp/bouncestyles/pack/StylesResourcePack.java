package dev.bsmp.bouncestyles.pack;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleLoader;
import dev.bsmp.bouncestyles.client.BounceStylesClient;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class StylesResourcePack extends AbstractFileResourcePack {
    private final List<ResourcePack> mergedPacks;
    private final PackResourceMetadata metadata;
    private final Map<String, List<ResourcePack>> dataNamespaces;
    private final Map<String, List<ResourcePack>> resourceNamespaces;

    public StylesResourcePack(File base, List<ResourcePack> mergedPacks, PackResourceMetadata metadata) {
        super(BounceStyles.modId + ":stylePacks", false);
        this.mergedPacks = mergedPacks;
        this.metadata = metadata;
        dataNamespaces = buildPackMap(ResourceType.SERVER_DATA);
        resourceNamespaces = buildPackMap(ResourceType.CLIENT_RESOURCES);
    }

    public void registerPackStyles() {
        for(ResourcePack pack : this.mergedPacks) {
            InputSupplier<InputStream> supplier = pack.openRoot("styles.json");
            if (supplier == null) {
                BounceStyles.LOGGER.warn("No styles.json file found for pack '" + pack.getName() + "'; Skipping...");
                continue;
            }

            try(InputStream stream = supplier.get()) {
                StyleLoader.loadStyles(pack.getName() + "/styles.json", stream);
            }
            catch (IOException e) {
                BounceStyles.LOGGER.error("Exception occurred while processing styles.json for pack: " + pack.getName());
                BounceStyles.LOGGER.error(e);
            }
        }
    }

    private Map<String, List<ResourcePack>> buildPackMap(ResourceType type) {
        Map<String, List<ResourcePack>> map = new HashMap<>();
        for(ResourcePack pack : this.mergedPacks) {
            for(String namespace : pack.getNamespaces(type)) {
                map.computeIfAbsent(namespace, key -> new ArrayList<>()).add(pack);
            }
        }
        return map;
    }

    @Nullable
    @Override
    public InputSupplier<InputStream> openRoot(String... segments) {
        for (String fileName : segments) {
            if (fileName.equals("pack.png")) {
                Mod mod = Platform.getMod(BounceStyles.modId);
                String logoPath = mod.getLogoFile(120).orElse("");
                Path path = mod.findResource(logoPath).orElse(null);
                if (path != null)
                    return InputSupplier.create(path);
            }
        }
        return null;
    }

    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        Map<String, List<ResourcePack>> map = type == ResourceType.CLIENT_RESOURCES ? resourceNamespaces : dataNamespaces;
        List<ResourcePack> matchingPacks = map.get(id.getNamespace());
        if (matchingPacks == null) matchingPacks = Collections.emptyList();

        if (type == ResourceType.CLIENT_RESOURCES && BounceStylesClient.isLookingForLang(id)) {
            return BounceStylesClient.processPackLangs(matchingPacks, id);
        }

        for(ResourcePack pack : matchingPacks) {
            InputSupplier<InputStream> supplier = pack.open(type, id);
            if (supplier != null)
                return supplier;
        }
        return null;
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
        for (ResourcePack pack : this.mergedPacks) {
            pack.findResources(type, namespace, prefix, consumer);
        }
    }

    @Nullable
    @Override
    public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
        if(metaReader.getKey().equals("pack"))
            return (T) metadata;
        return null;
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
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
        for(ResourcePack pack : this.mergedPacks)
            pack.close();
    }
}
