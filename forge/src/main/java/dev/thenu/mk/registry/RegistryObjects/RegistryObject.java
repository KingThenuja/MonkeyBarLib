package dev.thenu.mk.registry.RegistryObjects;

import dev.thenu.mk.api.register.RegistryHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Supplier;

public class RegistryObject<T> {

    private final Supplier<T> supplier;
    private final ResourceLocation id;
    private final RegistryHelper registry;

    public RegistryObject(Supplier<T> supplier, ResourceLocation id, RegistryHelper registry) {
        this.supplier = supplier;
        this.id = id;
        this.registry = registry;
    }

    public T get() {
        return supplier.get();
    }

    public ResourceLocation getId() {
        return id;
    }

    public RegistryObject<T> addToCreativeTab(RegistryObject<CreativeModeTab> tab) {
        registry.addToTab(tab.getId(), this);
        return this;
    }
}
