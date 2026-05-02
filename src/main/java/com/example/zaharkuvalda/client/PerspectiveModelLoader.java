package com.example.zaharkuvalda.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public class PerspectiveModelLoader implements IGeometryLoader<PerspectiveModelLoader.Geometry> {

    public static final String ID = "perspective_aware";

    @Override
    public Geometry read(JsonObject json, JsonDeserializationContext ctx) {
        ResourceLocation model2d = ResourceLocation.parse(json.get("model_2d").getAsString());
        ResourceLocation model3d = ResourceLocation.parse(json.get("model_3d").getAsString());
        return new Geometry(model2d, model3d);
    }

    public static class Geometry implements IUnbakedGeometry<Geometry> {

        private final ResourceLocation model2dRL;
        private final ResourceLocation model3dRL;

        public Geometry(ResourceLocation model2dRL, ResourceLocation model3dRL) {
            this.model2dRL = model2dRL;
            this.model3dRL = model3dRL;
        }

        @Override
        public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter,
                                   IGeometryBakingContext context) {
            modelGetter.apply(model2dRL);
            modelGetter.apply(model3dRL);
        }

        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker,
                               Function<Material, TextureAtlasSprite> spriteGetter,
                               ModelState modelState, ItemOverrides overrides) {
            BakedModel baked2d = baker.bake(model2dRL, modelState);
            BakedModel baked3d = baker.bake(model3dRL, modelState);

            if (baked2d == null) baked2d = baker.bake(
                    ResourceLocation.withDefaultNamespace("builtin/missing"), modelState);
            if (baked3d == null) baked3d = baked2d;

            assert baked2d != null;
            return new PerspectiveAwareModel(baked2d, baked3d);
        }
    }
}
