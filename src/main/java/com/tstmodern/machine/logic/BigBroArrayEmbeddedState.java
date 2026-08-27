package com.tstmodern.machine.logic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Persisted state of embedded machines inside the Big Bro Array.
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
    public static final BigBroArrayEmbeddedState EMPTY = new BigBroArrayEmbeddedState(
            CURRENT_VERSION, null, null, 0, 0, null);

    public boolean isEmpty() {
        return definitionId == null || count <= 0;
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

    public static BigBroArrayEmbeddedState readFromNbt(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            return EMPTY;
        }
        int version = tag.getInt("version");
        if (version <= 0) {
            version = CURRENT_VERSION;
        }
        ResourceLocation id = tag.contains("id") ? ResourceLocation.tryParse(tag.getString("id")) : null;
        BigBroArrayMode mode = tag.contains("mode") ? parseMode(tag.getString("mode")) : null;
        int tier = tag.getInt("tier");
        int count = tag.getInt("count");
        CompoundTag itemTag = tag.contains("itemTag") ? tag.getCompound("itemTag").copy() : null;

        if (id == null || count <= 0) {
            return EMPTY;
        }
        return new BigBroArrayEmbeddedState(version, id, mode, tier, count, itemTag);
    }

    private static BigBroArrayMode parseMode(String name) {
        try {
            return BigBroArrayMode.valueOf(name);
        } catch (Exception e) {
            return null;
        }
    }
}
