package com.muyu.potion.client;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * 客户端初始化设置
 *
 * 【NeoForge 1.21.1 变更】新增 FogEventHandler 注册
 * 原 FogRendererMixin 因 FogData 不可访问而废弃，
 * 改为使用 ViewportEvent.RenderFog 事件实现雾效控制
 */
public class ClientSetup {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ClientSetup::clientSetup);
    }

    private static void clientSetup(final FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(new CameraEvents());
        NeoForge.EVENT_BUS.register(new FogEventHandler());
    }
}