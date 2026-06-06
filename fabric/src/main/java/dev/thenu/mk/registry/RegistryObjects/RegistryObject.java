package dev.thenu.mk.registry.RegistryObjects;

import dev.thenu.mk.api.register.RegistryHelper;
import net.minecraft.util.Identifier;

public class RegistryObject<T> {

    private final T value;
    private final Identifier id;
    private final RegistryHelper helper;

    public RegistryObject(T value, Identifier id, RegistryHelper helper) {
        this.value = value;
        this.id = id;
        this.helper = helper;
    }

    public T get() {
        return this.value;
    }

    public Identifier getId() {
        return this.id;
    }

    public RegistryObject<T> addToCreativeTab(RegistryObject<net.minecraft.item.ItemGroup> tab) {
        this.helper.addToTab(tab.getId(), this);
        return this;
    }
}