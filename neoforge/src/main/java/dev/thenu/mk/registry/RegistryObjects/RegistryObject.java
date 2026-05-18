package dev.thenu.mk.registry.RegistryObjects;

import dev.thenu.mk.api.register.RegistryHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Supplier;

public class RegistryObject<T> {

    private final Supplier<T> supplier;
    private final ResourceLocation id;
    private final RegistryHelper registry;

    /**
     * @param supplier the loader's lazy holder (Forge {@code RegistryObject} or
     *                 NeoForge {@code DeferredHolder}) — both implement {@link Supplier}.
     * @param id       the ResourceLocation this was registered under.
     */
    public RegistryObject(Supplier<T> supplier, ResourceLocation id, RegistryHelper registry) {
        this.supplier = supplier;
        this.id = id;
        this.registry = registry;
    }

    /** Returns the registered object. Safe to call after registry events have fired. */
    public T get() {
        return supplier.get();
    }

    /** Returns the ResourceLocation this object was registered under. */
    public ResourceLocation getId() {
        return id;
    }

    /**
     * Queues this object to appear in the given creative tab.
     * Must be called before the tab is built (i.e. during your mod's init).
     */
    public RegistryObject<T> addToCreativeTab(RegistryObject<CreativeModeTab> tab) {
        registry.addToTab(tab.getId(), this);
        return this;
    }
}
