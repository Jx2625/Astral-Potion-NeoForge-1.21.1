package com.muyu.potion.command;

import com.muyu.config.ClientConfig;
import com.muyu.config.ClientConfig.AstralVisionMode;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class AstralCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("astral")
                        .then(Commands.literal("mode")
                                .then(Commands.argument("type", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            for (AstralVisionMode mode : AstralVisionMode.values()) {
                                                builder.suggest(mode.name().toLowerCase());
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(context -> {
                                            String type = StringArgumentType.getString(context, "type");
                                            try {
                                                AstralVisionMode mode = AstralVisionMode.valueOf(type.toUpperCase());
                                                ClientConfig.ASTRAL_VISION_MODE.set(mode);

                                                context.getSource().sendSuccess(
                                                        () -> Component.literal("§a[Astral Potion] 视觉模式已切换为: §e" + mode.name()),
                                                        false
                                                );
                                                return 1;
                                            } catch (IllegalArgumentException e) {
                                                context.getSource().sendFailure(
                                                        Component.literal("§c[Astral Potion] 无效的模式: " + type +
                                                                // "\n§7可用模式: vanilla, spectator, xray, fade")  // ← 修改
                                                                "\n§7可用模式: vanilla, spectator, fade")
                                                );
                                                return 0;
                                            }
                                        })
                                )
                                .executes(context -> {
                                    AstralVisionMode current = ClientConfig.ASTRAL_VISION_MODE.get();
                                    context.getSource().sendSuccess(
                                            () -> Component.literal("§b[Astral Potion] 当前视觉模式: §e" + current.name() +
                                                    // "\n§7用法: /astral mode < vanilla | spectator | xray | fade >"),  // ← 修改
                                                    "\n§7用法: /astral mode < vanilla | spectator | fade >"),
                                            false
                                    );
                                    return 1;
                                })
                        )
        );
    }
}