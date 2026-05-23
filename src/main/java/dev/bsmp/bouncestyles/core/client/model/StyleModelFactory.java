package dev.bsmp.bouncestyles.core.client.model;

import dev.bsmp.bouncestyles.core.data.Category;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.cache.model.BakedGeoModel;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.cache.model.cuboid.GeoCube;
import software.bernie.geckolib.loading.definition.geometry.GeometryDescription;
import software.bernie.geckolib.loading.json.raw.Cube;
import software.bernie.geckolib.loading.json.raw.ModelProperties;
import software.bernie.geckolib.loading.object.BakedModelFactory;
import software.bernie.geckolib.loading.object.BoneStructure;
import software.bernie.geckolib.loading.object.GeometryTree;
import software.bernie.geckolib.model.GeoModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StyleModelFactory implements BakedModelFactory {
    public static final ConcurrentHashMap<Identifier, BakedGeoModel> separatedStyleModels = new ConcurrentHashMap<>();

    private final Identifier originalPath;

    public StyleModelFactory(Identifier path) {
        this.originalPath = path;
    }

    @Override
    public BakedGeoModel constructGeoModel(GeometryTree geometryTree) {
        Map<Category, List<GeoBone>> boneMap = new HashMap<>();

        for (BoneStructure boneStructure : geometryTree.topLevelBones().values()) {
            boneStructure.children().entrySet().stream().filter(entry ->  entry.getKey().contains("armor"))
            .forEach(entry -> {
                List<GeoBone> bones = new ArrayList<>();

                var bone = entry.getValue();
                var category = getCategoryForPart(entry.getKey().toLowerCase());

                boolean empty = bone.self().cubes().length == 0;
                if (empty) empty = loop(bone);

                if (!empty) {
                    if (category == Category.Legs || category == Category.Feet) {
                        bones.add(constructBone(new BoneStructure(boneStructure.self(), Map.of(entry.getKey(), bone)), geometryTree.properties(), null));
                    }
                    else
                        bones.add(constructBone(boneStructure, geometryTree.properties(), null));

                    if (boneMap.containsKey(category)) boneMap.get(category).addAll(bones);
                    else boneMap.put(category, bones);
                }
            });
        }

        if (boneMap.isEmpty())
            return BakedModelFactory.DEFAULT_FACTORY.constructGeoModel(geometryTree);

        BakedGeoModel firstModel = null;

        for (Map.Entry<Category, List<GeoBone>> entry : boneMap.entrySet()) {
            Category category = entry.getKey();
            List<GeoBone> geoBones = entry.getValue();

            var bakedModel = new BakedGeoModel(geoBones.toArray(new GeoBone[0]), ModelProperties.fromDescription(geometryTree.properties()));
            var newPath = originalPath.getPath().replace(".geo.json", "");
            newPath += "_"+category.getSerializedName()+".geo.json";

            if (firstModel == null) firstModel = bakedModel;
            else separatedStyleModels.put(originalPath.withPath(newPath), bakedModel);
        }

        return firstModel;
    }

    private static boolean loop(BoneStructure bone) {
        for (BoneStructure child : bone.children().values()) {
            if (child.self().cubes().length > 0) return true;
            else if (loop(child)) return true;
        }
        return false;
    }

    private static Category getCategoryForPart(String partName) {
        if (partName.endsWith("head")) return Category.Head;
        if (partName.endsWith("body") || partName.endsWith("arm")) return Category.Body;
        if (partName.endsWith("leg")) return Category.Legs;
        return Category.Feet;
    }

    @Override
    public GeoBone constructBone(BoneStructure boneStructure, GeometryDescription properties, @Nullable GeoBone parent) {
        return BakedModelFactory.DEFAULT_FACTORY.constructBone(boneStructure, properties, parent);
    }

    @Override
    public GeoCube constructCube(Cube cube, GeometryDescription properties, float boneInflation) {
        return BakedModelFactory.DEFAULT_FACTORY.constructCube(cube, properties, boneInflation);
    }
}
