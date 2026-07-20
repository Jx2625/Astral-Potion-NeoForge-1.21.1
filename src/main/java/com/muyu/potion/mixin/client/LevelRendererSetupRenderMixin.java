package com.muyu.potion.mixin.client;

import com.muyu.potion.SpectreEffect;
import com.muyu.potion.AnimusEffect;
import com.muyu.config.ClientConfig;
import com.muyu.config.ClientConfig.AstralVisionMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * LevelRenderer Mixin - 强制旁观者模式视锥剔除
 *
 * 【NeoForge 1.21.1 变更】hasEffect() 参数改为 Holder<MobEffect>
 * DeferredHolder 本身就是 Holder，去掉 .get()
 *
 * 【变更】XRAY 模式已合并到 SPECTATOR，统一使用 isSpectator 控制
 */
@Mixin(LevelRenderer.class)
public class LevelRendererSetupRenderMixin {

    @ModifyVariable(
            method = "setupRender(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/culling/Frustum;ZZ)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 1  // 第2个boolean参数（isSpectator）,跳过视锥剔除
    )
    private boolean astral$forceSpectatorForCulling(boolean isSpectator) {
        if (isSpectator) return true;

        // SPECTATOR 模式（原 XRAY 也走这里）
        if (ClientConfig.ASTRAL_VISION_MODE.get() != AstralVisionMode.SPECTATOR) {
            return false;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;

        // ← 去掉 .get()，DeferredHolder 即 Holder<MobEffect>
        if (mc.player.hasEffect(SpectreEffect.SPECTRE)
                || mc.player.hasEffect(AnimusEffect.ANIMUS)) {
            return true;
        }

        return isSpectator;
    }
}