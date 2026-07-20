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
import net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.bus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

/**
 * 客户端相机/视觉效果事件处理器
 */
@OnlyIn(Dist.CLIENT)
public class CameraEvents {

    @SubscribeEvent
    public void computeFogColor(ViewportEvent.ComputeFogColor event) {
        AstralVisionMode mode = ClientConfig.ASTRAL_VISION_MODE.get();
        // ← 修改：SPECTATOR 模式也直接返回（原 XRAY 逻辑合并）
        if (mode == AstralVisionMode.SPECTATOR) {
            return;
        }

        Camera camera = event.getCamera();
        if (spectre$renderShadowPhase(camera.getEntity())
                && spectre$getViewBlockingState((LivingEntity) camera.getEntity()) != null) {

            if (mode == AstralVisionMode.FADE) {
                event.setRed(0.1F);
                event.setGreen(0.1F);
                event.setBlue(0.1F);
            } else {
                event.setRed(0.0F);
                event.setGreen(0.0F);
                event.setBlue(0.0F);
            }
        }
    }

    @SubscribeEvent
    public void renderBlockScreen(RenderBlockScreenEffectEvent event) {
        if (event.getOverlayType() == RenderBlockScreenEffectEvent.OverlayType.BLOCK
                && (event.getPlayer().hasEffect(SpectreEffect.SPECTRE)
                || event.getPlayer().hasEffect(AnimusEffect.ANIMUS))) {
            event.setCanceled(true);
        }
    }

    @Nullable
    private static BlockState spectre$getViewBlockingState(LivingEntity player) {
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

    private static boolean spectre$renderShadowPhase(Entity entity) {
        return entity instanceof LivingEntity livingEntity
                && (livingEntity.hasEffect(SpectreEffect.SPECTRE)
                || livingEntity.hasEffect(AnimusEffect.ANIMUS));
    }
}