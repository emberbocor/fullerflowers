package io.github.emberbocor.fullerflowers.mixin.neoforge;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.fullerflowers.block.FlowerGrowth;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(BoneMealItem.class)
public abstract class BoneMealItemMixin {
    // After the BonemealEvent, where vanilla checks for a BonemealableBlock: players (useOn) and dispensers (growCrop).
    @Inject(method = "applyBonemeal", at = @At(value = "CONSTANT",
            args = "classValue=net/minecraft/world/level/block/BonemealableBlock", ordinal = 0), cancellable = true)
    private static void fullerflowers$growFlower(ItemStack stack, Level level, BlockPos pos, @Nullable Player player,
            CallbackInfoReturnable<Boolean> cir) {
        Boolean result = FlowerGrowth.applyBoneMeal(stack, level, pos);
        if (result != null) {
            cir.setReturnValue(result);
        }
    }
}
