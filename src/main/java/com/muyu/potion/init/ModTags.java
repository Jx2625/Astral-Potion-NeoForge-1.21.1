package com.muyu.potion.init;

import com.muyu.potion.AstralPotionMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static final TagKey<Block> OMIT_SPECTRE = BlockTags.create(
            ResourceLocation.fromNamespaceAndPath(AstralPotionMod.MODID, "omit_spectre"));
}