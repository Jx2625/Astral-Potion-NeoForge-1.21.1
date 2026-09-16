package com.muyu.potion.mixin.compat;

import com.muyu.potion.AnimusEffect;
import com.muyu.potion.SpectreEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 兼容 Mixin：让灵息效果穿过 Sable 物理结构（航空学子维度）。
 *
 * 原理：Sable 的 SubLevelEntityCollision.collide() 会尊重实体的 noPhysics 标记。
 * 在 Entity.move() 的 HEAD 处设置 noPhysics，确保在 Sable 的碰撞检测
 * 读取该标记之前就已经生效，从而让玩家穿过子维度。
 *
 * noPhysics 的完整生命周期（设置 + 恢复）全部由本类管理。
 * AstralEventHandler 只负责灵息效果的飞行能力，不再碰 noPhysics。
 *
 * 【范围限定】只有灵息效果会穿过 Sable 物理结构。
 * 魂体效果不穿透 Sable 结构，只穿透原版方块（由 BlockStateBaseMixin 处理）。
 */
@Mixin(Entity.class)
public abstract class SableEntityCollisionMixin {

    @Inject(
            method = "move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V",
            at = @At("HEAD")
    )
    private void astral$applyNoclipForSubLevels(MoverType type, Vec3 pos, CallbackInfo ci) {
        if (!((Object) this instanceof Player player)) {
            return;
        }

        boolean hasAnimus = player.hasEffect(AnimusEffect.ANIMUS);

        // 只有灵息效果才设置 noPhysics 以穿透 Sable 结构
        // 魂体效果不处理，保持 Sable 结构对其的碰撞
        player.noPhysics = hasAnimus;
    }
}