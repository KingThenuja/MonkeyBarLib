package dev.thenu.mk.registry.Block.BlockFamilyBuilder;

import dev.thenu.mk.registry.RegistryObjects.RegistryObject;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;

public record BlockFamily(
        RegistryObject<Block> base,
        RegistryObject<Block> stairs,
        RegistryObject<Block> slab,
        RegistryObject<Block> wall,
        RegistryObject<Block> fence,
        RegistryObject<Block> fenceGate,
        RegistryObject<Block> door,
        RegistryObject<Block> trapdoor
) {
    /** Registers every member of this family with the given creative tab. */
    public void addToTab(RegistryObject<CreativeModeTab> tab) {
        base.addToCreativeTab(tab);
        stairs.addToCreativeTab(tab);
        slab.addToCreativeTab(tab);
        wall.addToCreativeTab(tab);
        fence.addToCreativeTab(tab);
        fenceGate.addToCreativeTab(tab);
        door.addToCreativeTab(tab);
        trapdoor.addToCreativeTab(tab);
    }
}
