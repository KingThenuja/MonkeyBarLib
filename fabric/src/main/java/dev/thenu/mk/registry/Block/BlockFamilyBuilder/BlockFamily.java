package dev.thenu.mk.registry.Block.BlockFamilyBuilder;

import dev.thenu.mk.registry.RegistryObjects.RegistryObject;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;

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
    public void addToTab(RegistryObject<ItemGroup> tab) {
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
