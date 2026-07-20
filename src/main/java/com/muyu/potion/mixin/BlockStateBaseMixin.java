package com.muyu.potion.mixin;

import com.muyu.potion.SpectreEffect;
import com.muyu.potion.AnimusEffect;
import com.muyu.potion.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * BlockStateBase Mixin - 魂体/灵息效果穿墙碰撞处理
 *
 * 【NeoForge 1.21.1 变更】hasEffect() 参数改为 Holder<MobEffect>
 * DeferredHolder 本身就是 Holder，去掉 .get()
 *
 * 【修复】去掉 isSolid() 检查，改为判断碰撞形状是否为空
 * 原 isSolid() 会过滤掉箱子、楼梯、玻璃等非固体但有碰撞的方块
 *
 * 【修复】isShiftKeyDown() → isCrouching()，支持改键配置
 *
 * 【Animus 效果】完全无视碰撞（复刻 1.20.1 noPhysics = true 行为）
 * 【Spectre 效果】保留"站立/潜行"逻辑
 */
@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockStateBaseMixin {

    @Inject(
            at = @At("RETURN"),
            method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
            cancellable = true
    )
    private void spectre$getCollisionShape(
            BlockGetter blockGetter,
            BlockPos blockPos,
            CollisionContext collisionContext,
            CallbackInfoReturnable<VoxelShape> cir
    ) {
        // 已经是空碰撞，不需要处理
        if (cir.getReturnValue().isEmpty()) return;

        if (!(collisionContext instanceof EntityCollisionContext entityCollisionContext)) return;
        if (!(entityCollisionContext.getEntity() instanceof LivingEntity livingEntity)) return;

        boolean hasSpectre = livingEntity.hasEffect(SpectreEffect.SPECTRE);
        boolean hasAnimus = livingEntity.hasEffect(AnimusEffect.ANIMUS);

        // 没有魂体/灵息效果，不处理
        if (!hasSpectre && !hasAnimus) return;

        // OMIT_SPECTRE 标签的方块始终保留碰撞（基岩等）
        BlockState blockState = blockGetter.getBlockState(blockPos);
        if (blockState.is(ModTags.OMIT_SPECTRE)) return;

        // ========== Animus 效果：完全无视碰撞 ==========
        // 复刻 1.20.1 noPhysics = true 的原生行为
        // 不需要 flag 判断，不需要潜行，完全自由飞行
        if (hasAnimus) {
            cir.setReturnValue(Shapes.empty());
            return;
        }

        // ========== Spectre 效果：保留"站立/潜行"逻辑 ==========
        // above = true: 玩家站在方块顶部附近，保留碰撞（可以正常站立）
        // above = false: 玩家在方块侧面/下方，取消碰撞（可以穿墙）
        boolean above = livingEntity.getY() > blockPos.getY()
                + cir.getReturnValue().max(Direction.Axis.Y)
                - (livingEntity.onGround() ? 0.5F : 0.001F);

        // isCrouching() 支持玩家改键配置
        // flag = true: 取消碰撞（穿墙）
        // flag = false: 保留碰撞（站立）
        boolean flag = !above || livingEntity.isCrouching();

        if (hasSpectre && flag) {
            cir.setReturnValue(Shapes.empty());
        }
    }

    @Inject(at = @At("RETURN"), method = "entityInside", cancellable = true)
    private void spectre$entityInside(
            Level level,
            BlockPos blockPos,
            Entity entity,
            CallbackInfo ci
    ) {
        if (entity instanceof LivingEntity livingEntity
                && (livingEntity.hasEffect(SpectreEffect.SPECTRE)
                || livingEntity.hasEffect(AnimusEffect.ANIMUS))
                && !level.getBlockState(blockPos).is(ModTags.OMIT_SPECTRE)) {

            ci.cancel();

            // 只在穿越固体方块时产生粒子效果
            if (level instanceof ServerLevel serverLevel
                    && ((BlockBehaviour.BlockStateBase)(Object)this).isSolid()) {
                boolean bl = entity.xOld != entity.getX() || entity.zOld != entity.getZ();
                RandomSource randomSource = level.getRandom();
                if (bl && randomSource.nextBoolean()) {
                    serverLevel.sendParticles(
                            new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(blockPos)),
                            entity.getX(),
                            blockPos.getY() + 1,
                            entity.getZ(),
                            1,
                            Mth.randomBetween(randomSource, -1.0f, 1.0f) * 0.083333336f,
                            0.05f,
                            Mth.randomBetween(randomSource, -1.0f, 1.0f) * 0.083333336f,
                            0
                    );
                }
            }
        }
    }
}