package com.tstmodern.data.recipe;

import static com.gregtechceu.gtceu.api.GTValues.MAX;
import static com.gregtechceu.gtceu.api.GTValues.OpV;
import static com.gregtechceu.gtceu.api.GTValues.UEV;
import static com.gregtechceu.gtceu.api.GTValues.UHV;
import static com.gregtechceu.gtceu.api.GTValues.UIV;
import static com.gregtechceu.gtceu.api.GTValues.UXV;

import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModList;

public final class TSTCircuitTags {

    private TSTCircuitTags() {}

    public static TagKey<Item> get(int requestedTier) {
        ModList mods = ModList.get();
        return switch (resolveTier(requestedTier, mods.isLoaded("start_core"), mods.isLoaded("soggtaddon"))) {
            case UHV -> CustomTags.UHV_CIRCUITS;
            case UEV -> CustomTags.UEV_CIRCUITS;
            case UIV -> CustomTags.UIV_CIRCUITS;
            case UXV -> CustomTags.UXV_CIRCUITS;
            case OpV -> CustomTags.OpV_CIRCUITS;
            case MAX -> CustomTags.MAX_CIRCUITS;
            default -> throw new IllegalArgumentException("Unsupported endgame circuit tier: " + requestedTier);
        };
    }

    static int resolveTier(int requestedTier, boolean starTLoaded, boolean sogLoaded) {
        boolean tierAvailable = sogLoaded || starTLoaded && requestedTier <= UXV;
        return requestedTier > UHV && !tierAvailable ? UHV : requestedTier;
    }
}
