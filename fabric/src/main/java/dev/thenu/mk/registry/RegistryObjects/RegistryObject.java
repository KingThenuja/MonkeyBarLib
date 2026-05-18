package dev.thenu.mk.registry.RegistryObjects;

import dev.thenu.mk.api.register.RegistryHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

public class RegistryObject<T> {

    private final T value;
    private final ResourceLocation id;
    private final RegistryHelper registry;

    public RegistryObject(T value, ResourceLocation id, RegistryHelper registry) {
        this.value = value;
        this.id = id;
        this.registry = registry;
    }

    public T get() {
        return value;
    }

    public ResourceLocation getId() {
        return id;
    }

    public RegistryObject<T> addToCreativeTab(RegistryObject<CreativeModeTab> tab) {
        registry.addToTab(tab.getId(), this);
        return this;
    }
}
