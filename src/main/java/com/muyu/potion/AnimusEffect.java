package com.muyu.potion;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AnimusEffect extends MobEffect {

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, AstralPotionMod.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> ANIMUS = EFFECTS.register("animus",
            () -> new AnimusEffect(MobEffectCategory.NEUTRAL, 0x00FFFF));

    public AnimusEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    // ✅ NeoForge 1.21.1: 没有 ServerLevel 参数！
    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        return true;
    }

    // ✅ 方法名是 shouldApplyEffectTickThisTick，不是 isDurationEffectTick
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}