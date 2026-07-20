package com.muyu.potion;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;  // ← 新增
import net.neoforged.neoforge.common.brewing.BrewingRecipeRegistry;  // ← 保留用于兼容
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.bus.api.SubscribeEvent;

import com.muyu.potion.init.ModItems;

/**
 * 星灵药水注册与酿造配方
 *
 * 【NeoForge 1.21.1 重大变更】
 *
 * 1. DeferredHolder<Potion, Potion> 本身就是 Holder<Potion>
 *    原 Forge 1.20.1: SPECTRE.get() 获取 Potion 实例
 *    新 NeoForge 1.21.1: SPECTRE 直接作为 Holder<Potion> 使用
 *    因此所有 .get() 调用全部去掉
 *
 * 2. 酿造配方注册方式变更
 *    原 Forge 1.20.1: 在 FMLCommonSetupEvent 中直接调用 BrewingRecipeRegistry.addRecipe()
 *    新 NeoForge 1.21.1: 使用 RegisterBrewingRecipesEvent 事件注册
 *    该事件在数据生成/注册阶段触发，通过 builder.addMix() 添加配方
 *    NeoForge 会自动处理普通/喷溅/滞留三种药水容器的配方衍生
 *
 * 3. MobEffectInstance 构造参数变更
 *    原: new MobEffectInstance(SpectreEffect.SPECTRE.get(), duration, amp)
 *    新: new MobEffectInstance(SpectreEffect.SPECTRE, duration, amp)
 */
public class AstralPotions {

    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(Registries.POTION, AstralPotionMod.MODID);

    // DeferredHolder<Potion, Potion> 即 Holder<Potion>，无需 .get()
    public static final DeferredHolder<Potion, Potion> SPECTRE = POTIONS.register("spectre",
            () -> new Potion(new net.minecraft.world.effect.MobEffectInstance(
                    SpectreEffect.SPECTRE, 3600, 0)));  // ← 去掉 .get()

    public static final DeferredHolder<Potion, Potion> LONG_SPECTRE = POTIONS.register("long_spectre",
            () -> new Potion(new net.minecraft.world.effect.MobEffectInstance(
                    SpectreEffect.SPECTRE, 9600, 0)));  // ← 去掉 .get()

    public static final DeferredHolder<Potion, Potion> ANIMUS = POTIONS.register("animus",
            () -> new Potion(new net.minecraft.world.effect.MobEffectInstance(
                    AnimusEffect.ANIMUS, 900, 0)));  // ← 去掉 .get()

    public static final DeferredHolder<Potion, Potion> LONG_ANIMUS = POTIONS.register("long_animus",
            () -> new Potion(new net.minecraft.world.effect.MobEffectInstance(
                    AnimusEffect.ANIMUS, 2400, 0)));  // ← 去掉 .get()

    /**
     * 【NeoForge 1.21.1 新方式】通过事件注册酿造配方
     *
     * 使用 @SubscribeEvent 监听 RegisterBrewingRecipesEvent
     * 通过 event.getBuilder().addMix() 添加配方
     *
     * addMix(输入药水, 酿造材料, 输出药水) - 自动处理所有容器类型
     */
    @SubscribeEvent
    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        var builder = event.getBuilder();

        // ========== 魂体药水配方 ==========

        // 粗制药水 + 灵魂碎片 = 魂体药水
        builder.addMix(Potions.AWKWARD, ModItems.SOUL_SHARD.get(), SPECTRE);  // ← SPECTRE 直接传，无需 .get()

        // 魂体药水 + 红石粉 = 长效魂体药水
        builder.addMix(SPECTRE, Items.REDSTONE, LONG_SPECTRE);

        // 魂体药水 + 火药 = 喷溅魂体药水（自动衍生）
        // 喷溅魂体药水 + 龙息 = 滞留魂体药水（自动衍生）
        // NeoForge 1.21 会自动处理喷溅/滞留版本的配方

        // ========== 灵息药水配方 ==========

        // 魂体药水 + 灵魂碎片 = 灵息药水
        builder.addMix(SPECTRE, ModItems.SOUL_SHARD.get(), ANIMUS);

        // 长效魂体药水 + 灵魂碎片 = 长效灵息药水
        builder.addMix(LONG_SPECTRE, ModItems.SOUL_SHARD.get(), LONG_ANIMUS);

        // 灵息药水 + 红石粉 = 长效灵息药水
        builder.addMix(ANIMUS, Items.REDSTONE, LONG_ANIMUS);
    }

    /**
     * 【兼容性保留】旧版直接注册方式
     *
     * 如果某些场景仍需手动创建药水物品栈（如创造模式物品、命令等），
     * 使用 PotionContents.createItemStack()，传入 Holder<Potion>
     *
     * 示例:
     * PotionContents.createItemStack(Items.POTION, SPECTRE)  // ← 无需 .get()
     */
    public static void registerBrewingRecipes() {
        // 此方法保留用于兼容性调用，实际配方已在事件处理器中注册
        // 如果其他地方需要手动添加配方，可在此调用 BrewingRecipeRegistry
    }
}