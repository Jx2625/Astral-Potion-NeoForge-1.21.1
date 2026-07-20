package com.muyu.datagen;

import com.muyu.potion.AstralPotionMod;
import com.muyu.potion.init.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * 方块标签数据生成器
 *
 * 【NeoForge 1.21.1 变更】BlockTagsProvider 构造函数新增 ExistingFileHelper 参数
 * 原 Forge 1.20.1: super(output, lookupProvider, modId)
 * 新 NeoForge 1.21.1: super(output, lookupProvider, modId, existingFileHelper)
 */
public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper  // ← 新增参数
    ) {
        super(output, lookupProvider, AstralPotionMod.MODID, existingFileHelper);  // ← 传入第4个参数
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModTags.OMIT_SPECTRE).add(Blocks.BEDROCK);
    }
}