package com.tstmodern.mixin.client;

import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

/**
 * GTCEu 7.4.0 can create a JEI proxy slot whose numeric suffix equals the flat widget list size.
 * Its hover predicate indexes the list without a bounds check and crashes the client. Guard only
 * malformed or out-of-range proxy slots so valid slots retain normal JEI hover, R/U and click
 * behaviour inside the 3D multiblock preview.
 */
@Mixin(targets = "com.gregtechceu.gtceu.integration.jei.multipage.MultiblockInfoCategory$1ProxyRecipeWidget",
       remap = false)
public abstract class MultiblockInfoSlotGuardMixin {

    @Inject(
            method = "lambda$getSlotUnderMouse$0(Ljava/util/List;DDLmezz/jei/api/gui/ingredient/IRecipeSlotDrawable;)Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = false)
    private static void tstmodern$skipInvalidProxySlot(List<?> widgets, double mouseX, double mouseY,
                                                        IRecipeSlotDrawable slot,
                                                        CallbackInfoReturnable<Boolean> callback) {
        Optional<String> slotName = slot.getSlotName();
        if (slotName.isEmpty()) {
            return;
        }

        String name = slotName.get();
        if (!name.startsWith("slot_") || name.length() == 5) {
            callback.setReturnValue(false);
            return;
        }

        try {
            int index = Integer.parseInt(name.substring(5));
            if (index < 0 || index >= widgets.size()) {
                callback.setReturnValue(false);
            }
        } catch (NumberFormatException ignored) {
            callback.setReturnValue(false);
        }
    }
}
