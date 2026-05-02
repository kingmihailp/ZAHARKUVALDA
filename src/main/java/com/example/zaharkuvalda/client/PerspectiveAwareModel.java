package com.example.zaharkuvalda.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PerspectiveAwareModel implements BakedModel {

    private final BakedModel model2d;
    private final BakedModel model3d;

    private static final ThreadLocal<BakedModel> ACTIVE = new ThreadLocal<>();

    public PerspectiveAwareModel(BakedModel model2d, BakedModel model3d) {
        this.model2d = model2d;
        this.model3d = model3d;
    }

    private BakedModel active() {
        BakedModel m = ACTIVE.get();
        return m != null ? m : model2d;
    }

    @Override
    public BakedModel applyTransform(ItemDisplayContext ctx, PoseStack poseStack, boolean leftHand) {
        BakedModel chosen = isHandContext(ctx) ? model3d : model2d;
        ACTIVE.set(chosen);
        chosen.applyTransform(ctx, poseStack, leftHand);
        return this;
    }

    private static boolean isHandContext(ItemDisplayContext ctx) {
        return ctx == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
            || ctx == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
            || ctx == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
            || ctx == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        return active().getQuads(state, side, rand);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, @Nullable net.minecraft.client.renderer.RenderType renderType) {
        return active().getQuads(state, side, rand, data, renderType);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return model2d.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return model2d.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return model2d.getParticleIcon();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) {
        return model2d.getParticleIcon(data);
    }

    @Override
    public ItemOverrides getOverrides() {
        return model2d.getOverrides();
    }

    @Override
    public ItemTransforms getTransforms() {
        return model2d.getTransforms();
    }
}
