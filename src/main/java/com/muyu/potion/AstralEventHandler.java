package com.muyu.potion;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * 星灵药水全局事件处理器----现只处理灵息药水
 *
 * 【职责变更】noPhysics 的完整生命周期已交给 SableEntityCollisionMixin 管理。
 * 本类现在只负责灵息效果的飞行能力，不再修改 noPhysics，
 * 避免两处写入导致的状态频繁切换和屏幕抖动。
 *
 * 【NeoForge 1.21.1 重大变更】LivingEvent.LivingTickEvent 已移除
 *
 * 原 Forge 1.20.1: LivingEvent.LivingTickEvent
 * 新 NeoForge 1.21.1: EntityTickEvent.Pre / EntityTickEvent.Post
 *
 * EntityTickEvent.Pre 在实体 tick 之前触发，效果与旧版 LivingTickEvent 类似
 * 通过 instanceof Player 检查确保只处理玩家
 *
 * 【NeoForge 1.21.1 变更】hasEffect() 参数改为 Holder<MobEffect>
 * DeferredHolder 本身就是 Holder，去掉 .get()
 */
@EventBusSubscriber(modid = AstralPotionMod.MODID)
public class AstralEventHandler {

    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Pre event) {  // ← 变更事件类型
        // 只处理玩家实体（替代原 LivingTickEvent 的 LivingEntity 过滤）
        if (!(event.getEntity() instanceof Player player)) return;

        boolean hasAnimus = player.hasEffect(AnimusEffect.ANIMUS);

        if (hasAnimus) {
            // 灵息：授予飞行能力（noPhysics 由 SableEntityCollisionMixin 管理）
            player.getAbilities().flying = true;
            player.onUpdateAbilities();
        }
    }
}