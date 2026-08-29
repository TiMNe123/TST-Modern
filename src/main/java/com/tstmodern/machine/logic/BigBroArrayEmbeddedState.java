package com.tstmodern.machine.logic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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

    /** Whether this state has a valid definition that exists in the catalog. */
    public boolean isValid() {
        if (isEmpty()) return false;
        return BigBroArrayMachineCatalog.find(definitionId).isPresent();
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
        return resolveFromCatalog(version, id, count, itemTag);
    }

    /**
     * Migrate from legacy 4-key format used before versioned state was introduced.
     * Legacy keys: embeddedMachineId, embeddedCount, embeddedTier, embeddedMode.
     */
    public static BigBroArrayEmbeddedState migrateFromLegacy(CompoundTag parentTag) {
        if (parentTag == null) return EMPTY;

        // Try legacy key format
        String idStr = parentTag.contains("embeddedMachineId") ? parentTag.getString("embeddedMachineId") : null;
        int count = parentTag.getInt("embeddedCount");

        if (idStr == null || idStr.isEmpty() || count <= 0) {
            return EMPTY;
        }

        ResourceLocation id = ResourceLocation.tryParse(idStr);
        if (id == null) return EMPTY;

        CompoundTag itemTag = parentTag.contains("embeddedItemTag")
                ? parentTag.getCompound("embeddedItemTag").copy() : null;

        return resolveFromCatalog(CURRENT_VERSION, id, count, itemTag);
    }

    /**
     * Resolve mode and tier from the catalog for the given definition ID.
     * If the ID is not in the catalog, preserves the raw data with mode/tier from NBT fallback.
     */
    private static BigBroArrayEmbeddedState resolveFromCatalog(
            int version, ResourceLocation id, int count, @Nullable CompoundTag itemTag) {
        Optional<BigBroArrayMachineCatalog.Entry> catalogEntry = BigBroArrayMachineCatalog.find(id);
        if (catalogEntry.isPresent()) {
            BigBroArrayMachineCatalog.Entry entry = catalogEntry.get();
            return new BigBroArrayEmbeddedState(version, id, entry.mode(), entry.tier(), count, itemTag);
        }
        // Stale/unknown ID — preserve data but with null mode (isValid() will return false)
        return new BigBroArrayEmbeddedState(version, id, null, 0, count, itemTag);
    }
}
