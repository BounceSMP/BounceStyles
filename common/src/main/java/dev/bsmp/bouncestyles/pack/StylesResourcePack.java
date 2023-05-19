package dev.bsmp.bouncestyles.pack;

import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import dev.bsmp.bouncestyles.BounceStyles;
import dev.bsmp.bouncestyles.StyleLoader;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.ResourceNotFoundException;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StylesResourcePack extends AbstractFileResourcePack {
    private final List<ResourcePack> mergedPacks;
    private final PackResourceMetadata metadata;
    private final Map<String, List<ResourcePack>> dataNamespaces;
    private final Map<String, List<ResourcePack>> resourceNamespaces;

    public StylesResourcePack(File base, List<ResourcePack> mergedPacks, PackResourceMetadata metadata) {
        super(base);
        this.mergedPacks = mergedPacks;
        this.metadata = metadata;
        dataNamespaces = buildPackMap(ResourceType.SERVER_DATA);
        resourceNamespaces = buildPackMap(ResourceType.CLIENT_RESOURCES);
    }

    public void registerPackStyles() {
        for(ResourcePack pack : this.mergedPacks) {
            try(InputStream stream = pack.openRoot("styles.json")) {
                StyleLoader.reload();
                StyleLoader.loadStyles(pack.getName() + "/styles.json", stream);
            }
            catch (FileNotFoundException e) {
                BounceStyles.LOGGER.warn("No styles.json file found for pack '" + pack.getName() + "'; Skipping...");
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

    @Override
    public InputStream openRoot(String fileName) throws IOException {
        if (fileName.equals("pack.png")) {
            Mod mod = Platform.getMod(BounceStyles.modId);
            Optional<InputStream> stream = mod.getLogoFile(480).flatMap(s -> mod.findResource(s).map(path -> {
                try {
                    return Files.newInputStream(path);
                }
                catch (IOException e) {
                    return null;
                }
            }));
            if (stream.isPresent()) {
                return stream.get();
            }
        }
        throw new ResourceNotFoundException(this.base, fileName);
    }

    @Override
    public InputStream open(ResourceType type, Identifier id) throws IOException {
        for(ResourcePack pack : this.mergedPacks)
            if(pack.contains(type, id))
                return pack.open(type, id);

        throw new ResourceNotFoundException(this.base, id.toString());
    }

    @Override
    public boolean contains(ResourceType type, Identifier id) {
        for(ResourcePack pack : this.mergedPacks)
            if(pack.contains(type, id))
                return true;

        return false;
    }

    @Override
    public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
        return this.mergedPacks.stream().flatMap(pack -> pack.findResources(type, namespace, prefix, maxDepth, pathFilter).stream()).collect(Collectors.toList());
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

    @Override
    public String getName() {
        return "BounceStyles";
    }

    //Unneeded Methods overwritten
    @Override
    protected InputStream openFile(String name) throws IOException {
        throw new ResourceNotFoundException(this.base, name);
    }

    @Override
    protected boolean containsFile(String name) {
        return false;
    }
}
