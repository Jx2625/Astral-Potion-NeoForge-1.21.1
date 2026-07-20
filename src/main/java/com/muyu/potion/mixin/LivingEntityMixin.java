package com.muyu.potion.mixin;

import com.muyu.potion.SpectreEffect;
import com.muyu.potion.AnimusEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * LivingEntity Mixin - 魂体效果穿墙检测
 *
 * 【NeoForge 1.21.1 变更】hasEffect() 参数改为 Holder<MobEffect>
 * DeferredHolder 本身就是 Holder，去掉 .get()
 */
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(at = @At("HEAD"), method = "isInWall", cancellable = true)
    private void spectre$isInWall(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (self.hasEffect(SpectreEffect.SPECTRE)
                || self.hasEffect(AnimusEffect.ANIMUS)) {
            cir.setReturnValue(false);
        }
    }
}