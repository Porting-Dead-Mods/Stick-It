package com.portingdeadmods.stickit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class StickItConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int DEFAULT_STACK_LIMIT = 64;

    private static StickItConfig instance = new StickItConfig();
    private static Path file;

    public int maxStackSize = -1;
    public List<String> unplaceableItems = new ArrayList<>();
    private transient Set<Identifier> unplaceable = Set.of();

    public static StickItConfig get() {
        return instance;
    }

    public static void load(Path configDir) {
        file = configDir.resolve(StickIt.MOD_ID + ".json");
        if (Files.notExists(file)) {
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(file)) {
            StickItConfig loaded = GSON.fromJson(reader, StickItConfig.class);
            if (loaded != null) instance = loaded;
        } catch (IOException | JsonParseException ignored) {
        }
        instance.refresh();
    }

    public static void save() {
        instance.refresh();
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file)) {
                GSON.toJson(instance, writer);
            }
        } catch (IOException ignored) {
        }
    }

    public static int stackLimit() {
        return instance.maxStackSize <= 0 ? DEFAULT_STACK_LIMIT : instance.maxStackSize;
    }

    public static boolean canPlace(ItemStack stack) {
        return !stack.is(StickIt.UNPLACEABLE) && !instance.unplaceable.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    private void refresh() {
        if (unplaceableItems == null) unplaceableItems = new ArrayList<>();
        unplaceable = unplaceableItems.stream()
                .map(Identifier::tryParse)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }
}
