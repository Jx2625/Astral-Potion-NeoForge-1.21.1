package com.muyu.potion.init;

import com.muyu.potion.AstralPotionMod;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 物品注册与创意模式标签页处理
 *
 * 【NeoForge 1.21.1 变更】@EventBusSubscriber 的 bus 参数已过时并标记为删除
 * 原 Forge 1.20.1: @EventBusSubscriber(modid = ..., bus = EventBusSubscriber.Bus.MOD)
 * 新 NeoForge 1.21.1: @EventBusSubscriber(modid = ...)  // 默认即为 MOD bus
 */
@EventBusSubscriber(modid = AstralPotionMod.MODID)  // ← 去掉 bus = EventBusSubscriber.Bus.MOD
public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(AstralPotionMod.MODID);

    public static final DeferredItem<Item> SOUL_SHARD = ITEMS.registerItem("soul_shard",
            Item::new, new Item.Properties());

    @SubscribeEvent
    public static void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(SOUL_SHARD.get());
        }
    }
}