package com.muyu.potion.command;

import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import com.muyu.potion.AstralPotionMod;

@EventBusSubscriber(modid = AstralPotionMod.MODID)
public class CommandEventHandler {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        AstralCommand.register(event.getDispatcher());
    }
}