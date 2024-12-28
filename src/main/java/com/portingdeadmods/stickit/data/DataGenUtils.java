package com.portingdeadmods.stickit.data;

import com.portingdeadmods.stickit.StickIt;
import net.minecraft.resources.ResourceLocation;

public class DataGenUtils {
    public static ResourceLocation minecraft(String path) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", path);
    }

    public static ResourceLocation stickit(String path) {
        return ResourceLocation.fromNamespaceAndPath(StickIt.MOD_ID, path);
    }

    public static ResourceLocation carryOn(String path) {
        return ResourceLocation.fromNamespaceAndPath(StickIt.CARRY_ON_MOD_ID, path);
    }
}
