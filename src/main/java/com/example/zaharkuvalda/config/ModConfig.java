package com.example.zaharkuvalda.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<String> BRUSH_RESOURCE;
    public static final ModConfigSpec.IntValue BRUSH_RESOURCE_AMOUNT;
    public static final ModConfigSpec.DoubleValue BRUSH_RESOURCE_CHANCE;

    static {
        BUILDER.comment("Brush grass settings").push("brush");

        BRUSH_RESOURCE = BUILDER
                .comment("Item ID that can drop when brushing grass (e.g. minecraft:gold_nugget)")
                .define("resource", "minecraft:gold_nugget");

        BRUSH_RESOURCE_AMOUNT = BUILDER
                .comment("Amount of items to drop")
                .defineInRange("amount", 1, 1, 64);

        BRUSH_RESOURCE_CHANCE = BUILDER
                .comment("Chance to drop the resource (0.0 - 1.0)")
                .defineInRange("chance", 0.25, 0.0, 1.0);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
