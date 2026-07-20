
package com.muyu.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.EnumValue<AstralVisionMode> ASTRAL_VISION_MODE;

    public enum AstralVisionMode {
        VANILLA,    // 黑屏+雾收缩
        SPECTATOR,  // 旁观者式完全透明（直接跳过视锥剔除，渲染所有已加载区块，性能开销大）
        // XRAY,    // ← 删除这行
        FADE        // 半透明淡化（平衡方案）
    }

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("client");

        ASTRAL_VISION_MODE = builder
                .defineEnum("astralVisionMode", AstralVisionMode.SPECTATOR);

        builder.pop();
        SPEC = builder.build();
    }
}