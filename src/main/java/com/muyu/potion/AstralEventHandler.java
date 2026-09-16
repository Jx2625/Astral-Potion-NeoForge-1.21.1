package com.muyu.potion;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.EntityTickEvent;  // ← 变更导入
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * 星灵药水全局事件处理器
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

        // ← 去掉 .get()，DeferredHolder 即 Holder<MobEffect>
        boolean hasSpectre = player.hasEffect(SpectreEffect.SPECTRE);
        boolean hasAnimus = player.hasEffect(AnimusEffect.ANIMUS);

        if (hasAnimus) {
            player.noPhysics = true;
            player.getAbilities().flying = true;
            player.onUpdateAbilities();
        } else if (player.noPhysics) {
            player.noPhysics = false;
            player.onUpdateAbilities();
        }
    }
}