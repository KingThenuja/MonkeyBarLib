package dev.thenu.mk.registry.Item.ItemGroup;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemGroupRegistry {

    private static final List<Item> AUTO_ITEMS = new ArrayList<>();

    public static void add(Item item) {
        AUTO_ITEMS.add(item);
    }

    public static List<Item> getItems() {
        return AUTO_ITEMS;
    }
}
