package com.tstmodern.machine.logic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

/**
 * Persisted state of embedded machines inside the Big Bro Array.
 * Single source of truth for the embedded machine identity, mode, tier, count, and NBT.
 * Mode and tier are always resolved from the catalog at load time, not trusted from save data.
 */
public record BigBroArrayEmbeddedState(
        int version,
        @Nullable ResourceLocation definitionId,
        @Nullable BigBroArrayMode mode,
        int tier,
        int count,
        @Nullable CompoundTag itemTag
) {
    public static final int CURRENT_VERSION = 1;
    public static final String NBT_KEY = "BigBroArrayEmbeddedState";
    public static final BigBroArrayEmbeddedState EMPTY = new BigBroArrayEmbeddedState(
            CURRENT_VERSION, null, null, 0, 0, null);

    public boolean isEmpty() {
        return definitionId == null || count <= 0;
    }

    /** Whether this state was resolved by the catalog when it was created or loaded. */
    public boolean isValid() {
        return !isEmpty() && mode != null && tier >= 0;
    }

    public CompoundTag writeToNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("version", version);
        if (definitionId != null) {
            tag.putString("id", definitionId.toString());
        }
        if (mode != null) {
            tag.putString("mode", mode.name());
        }
        tag.putInt("tier", tier);
        tag.putInt("count", count);
        if (itemTag != null) {
            tag.put("itemTag", itemTag.copy());
        }
        return tag;
    }

    /**
     * Read from versioned compound, resolving mode/tier from catalog.
     * If the ID is not in the catalog, returns a state with isEmpty()=false but isValid()=false,
     * preserving the data for potential future re-registration.
     */
    public static BigBroArrayEmbeddedState readFromNbt(CompoundTag tag) {
        return readFromNbt(tag, BigBroArrayMachineCatalog::find);
    }

    static BigBroArrayEmbeddedState readFromNbt(
            CompoundTag tag,
            Function<ResourceLocation, Optional<BigBroArrayMachineCatalog.Entry>> resolver) {
        if (tag == null || tag.isEmpty()) {
            return EMPTY;
        }
        int version = tag.getInt("version");
        if (version <= 0) {
            version = CURRENT_VERSION;
        }
        ResourceLocation id = tag.contains("id") ? ResourceLocation.tryParse(tag.getString("id")) : null;
        int count = tag.getInt("count");
        CompoundTag itemTag = tag.contains("itemTag") ? tag.getCompound("itemTag").copy() : null;

        if (id == null || count <= 0) {
            return EMPTY;
        }

        // Resolve mode/tier from catalog — don't trust saved NBT values
        return resolveFromCatalog(version, id, count, itemTag, resolver);
    }

    /**
     * Migrate from legacy 4-key format used before versioned state was introduced.
     * Legacy keys: embeddedMachineStack, embeddedCount, embeddedTier, embeddedMode.
     */
    public static BigBroArrayEmbeddedState migrateFromLegacy(CompoundTag parentTag) {
        if (parentTag == null) return EMPTY;

        ResourceLocation id = null;
        CompoundTag itemTag = null;

        // Try serialized ItemStack first (standard LDLib / Forge MetaMachine ItemStack serialization)
        if (parentTag.contains("embeddedMachineStack")) {
            CompoundTag stackTag = parentTag.getCompound("embeddedMachineStack");
            net.minecraft.world.item.ItemStack stack = net.minecraft.world.item.ItemStack.of(stackTag);
            if (!stack.isEmpty() && stack.getItem() instanceof com.gregtechceu.gtceu.api.item.MetaMachineItem machineItem) {
                com.gregtechceu.gtceu.api.machine.MachineDefinition def = machineItem.getDefinition();
                if (def != null) {
                    id = def.getId();
                    if (stack.getTag() != null) {
                        itemTag = stack.getTag().copy();
                    }
                }
            }
        }

        int count = parentTag.getInt("embeddedCount");
        if (id == null || count <= 0) {
            return EMPTY;
        }

        if (itemTag == null && parentTag.contains("embeddedItemTag")) {
            itemTag = parentTag.getCompound("embeddedItemTag").copy();
        }

        return resolveFromCatalog(CURRENT_VERSION, id, count, itemTag, BigBroArrayMachineCatalog::find);
    }

    /**
     * Resolve mode and tier from the catalog for the given definition ID.
     * If the ID is not in the catalog, preserves the raw ID/count/item data while marking
     * mode and tier unresolved so stale state cannot become operational accidentally.
     */
    private static BigBroArrayEmbeddedState resolveFromCatalog(
            int version,
            ResourceLocation id,
            int count,
            @Nullable CompoundTag itemTag,
            Function<ResourceLocation, Optional<BigBroArrayMachineCatalog.Entry>> resolver) {
        Optional<BigBroArrayMachineCatalog.Entry> catalogEntry = resolver.apply(id);
        if (catalogEntry.isPresent()) {
            BigBroArrayMachineCatalog.Entry entry = catalogEntry.get();
            return new BigBroArrayEmbeddedState(version, id, entry.mode(), entry.tier(), count, itemTag);
        }
        // Stale/unknown ID — preserve data but with null mode (isValid() will return false)
        return new BigBroArrayEmbeddedState(version, id, null, 0, count, itemTag);
    }
}
