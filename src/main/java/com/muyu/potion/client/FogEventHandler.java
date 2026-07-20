package com.muyu.potion.client;

import com.muyu.potion.SpectreEffect;
import com.muyu.potion.AnimusEffect;
import com.muyu.config.ClientConfig;
import com.muyu.config.ClientConfig.AstralVisionMode;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.jetbrains.annotations.Nullable;

/**
 * 雾效渲染事件处理器
 */
@OnlyIn(Dist.CLIENT)
public class FogEventHandler {

    // 雾效距离渐变值
    private float spectreDistance = 24.0F;

    @SubscribeEvent
    public void onRenderFog(ViewportEvent.RenderFog event) {
        Camera camera = event.getCamera();
        Entity entity = camera.getEntity();

        if (!(entity instanceof LivingEntity livingEntity)) return;
        if (!spectre$renderShadowPhase(livingEntity)) return;

        AstralVisionMode mode = ClientConfig.ASTRAL_VISION_MODE.get();

        // SPECTATOR 模式：完全禁用雾效（原 XRAY 也走这里）
        if (mode == AstralVisionMode.SPECTATOR) {
            event.setNearPlaneDistance(Float.MAX_VALUE);
            event.setFarPlaneDistance(Float.MAX_VALUE);
            event.setCanceled(true);
            return;
        }

        // FADE 模式
        boolean lock = spectre$getViewBlockingState(livingEntity) != null;

        if (mode == AstralVisionMode.FADE && lock) {
            event.setNearPlaneDistance(8.0f);
            event.setFarPlaneDistance(32.0f);
            event.setCanceled(true);
            return;
        }

        // 动态雾效距离调整
        float defaultFarPlane = event.getFarPlaneDistance();

        if (spectreDistance == defaultFarPlane && !lock) return;
        if (spectreDistance > 12 && lock) spectreDistance -= 2.0F;
        if (!lock && spectreDistance < defaultFarPlane) spectreDistance += 1.0F;

        event.setNearPlaneDistance(spectreDistance * 0.75f);
        event.setFarPlaneDistance(spectreDistance);
        event.setCanceled(true);
    }

    private boolean spectre$renderShadowPhase(LivingEntity entity) {
        return entity.hasEffect(SpectreEffect.SPECTRE)
                || entity.hasEffect(AnimusEffect.ANIMUS);
    }

    @Nullable
    private BlockState spectre$getViewBlockingState(LivingEntity player) {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 8; ++i) {
            double d = player.getX() + (double)(((float)((i) % 2) - 0.5f) * player.getBbWidth() * 0.8f);
            double e = player.getEyeY() + (double)(((float)((i >> 1) % 2) - 0.5f) * 0.1f);
            double f = player.getZ() + (double)(((float)((i >> 2) % 2) - 0.5f) * player.getBbWidth() * 0.8f);
            mutableBlockPos.set(d, e, f);
            BlockState blockState = player.level().getBlockState(mutableBlockPos);
            if (blockState.getRenderShape() == RenderShape.INVISIBLE
                    || !blockState.isViewBlocking(player.level(), mutableBlockPos)) {
                continue;
            }
            return blockState;
        }
        return null;
    }
}