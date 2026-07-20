package com.muyu.potion;

import com.muyu.potion.client.ClientSetup;
import com.muyu.potion.command.CommandEventHandler;
import com.muyu.config.ClientConfig;
import com.muyu.potion.init.ModItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(AstralPotionMod.MODID)
public class AstralPotionMod {
    public static final String MODID = "astralpotion";

    public AstralPotionMod(IEventBus modEventBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);

        ModItems.ITEMS.register(modEventBus);
        SpectreEffect.EFFECTS.register(modEventBus);
        AnimusEffect.EFFECTS.register(modEventBus);
        AstralPotions.POTIONS.register(modEventBus);

        NeoForge.EVENT_BUS.register(AstralPotions.class);

        modEventBus.addListener(this::commonSetup);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            // 注册配置界面
            container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

            ClientSetup.register(modEventBus);
        }

        NeoForge.EVENT_BUS.register(AstralEventHandler.class);
        NeoForge.EVENT_BUS.register(CommandEventHandler.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // 其他初始化...
        });
    }
}