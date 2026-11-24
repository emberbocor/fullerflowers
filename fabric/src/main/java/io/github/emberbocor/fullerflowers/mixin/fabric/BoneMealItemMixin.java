package io.github.emberbocor.fullerflowers.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.emberbocor.fullerflowers.block.FlowerGrowth;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(BoneMealItem.class)
public abstract class BoneMealItemMixin {
    // Vanilla growCrop serves players (useOn) and dispensers; nothing runs before its BonemealableBlock check.
    @Inject(method = "growCrop", at = @At("HEAD"), cancellable = true)
    private static void fullerflowers$growFlower(ItemStack stack, Level level, BlockPos pos,
            CallbackInfoReturnable<Boolean> cir) {
        Boolean result = FlowerGrowth.applyBoneMeal(stack, level, pos);
        if (result != null) {
            cir.setReturnValue(result);
        }
    }
}
