package com.muyu.potion;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SpectreEffect extends MobEffect {

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, AstralPotionMod.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> SPECTRE = EFFECTS.register("spectre",
            () -> new SpectreEffect(MobEffectCategory.NEUTRAL, 0x8E44AD));

    public SpectreEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    // ✅ 返回 boolean，不是 void
    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        return true;
    }

    // ✅ 方法名修正
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}