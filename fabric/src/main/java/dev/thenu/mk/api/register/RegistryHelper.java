package dev.thenu.mk.api.register;

import dev.thenu.mk.registry.Block.BlockEntity.BlockEntityFactory;
import dev.thenu.mk.registry.Block.BlockFamilyBuilder.BlockFamily;
import dev.thenu.mk.registry.Block.Unprotected.DoorBlock;
import dev.thenu.mk.registry.Block.Unprotected.StairBlock;
import dev.thenu.mk.registry.Block.Unprotected.TrapdoorBlock;
import dev.thenu.mk.registry.RegistryObjects.RegistryObject;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.*;
import java.util.function.Supplier;

public class RegistryHelper {

    private final String modId;

    private final Map<ResourceLocation, List<RegistryObject<?>>> tabEntries = new HashMap<>();

    public RegistryHelper(String modId) {
        this.modId = modId;
    }

    public ResourceLocation rl(String name) {
        return ResourceLocation.fromNamespaceAndPath(modId, name);
    }

    public <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        ResourceLocation id = rl(name);
        T registered = Registry.register(BuiltInRegistries.BLOCK, id, block.get());
        return new RegistryObject<>(registered, id, this);
    }

    public <T extends Block> RegistryObject<T> registerBlockWithoutItem(String name, T block) {
        return registerBlock(name, () -> block);
    }

    public <T extends Block> RegistryObject<T> registerBlockWithItem(
            String name, Supplier<T> block, Item.Properties props) {
        ResourceLocation id = rl(name);
        T registered = Registry.register(BuiltInRegistries.BLOCK, id, block.get());
        Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(registered, props));
        return new RegistryObject<>(registered, id, this);
    }

    public <T extends Block> RegistryObject<T> registerBlockWithItem(String name, Supplier<T> block) {
        return registerBlockWithItem(name, block, new Item.Properties());
    }

    public <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> item) {
        ResourceLocation id = rl(name);
        T registered = Registry.register(BuiltInRegistries.ITEM, id, item.get());
        return new RegistryObject<>(registered, id, this);
    }

    public RegistryObject<Item> registerSimpleItem(String name, Item.Properties props) {
        return registerItem(name, () -> new Item(props));
    }

    public RegistryObject<Item> registerSimpleItem(String name) {
        return registerSimpleItem(name, new Item.Properties());
    }

    public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBlockEntity(
            String name, BlockEntityFactory<T> factory, Block... validBlocks) {
        ResourceLocation id = rl(name);
        BlockEntityType<T> type = BlockEntityType.Builder.of(factory::create, validBlocks).build();
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, type);
        return new RegistryObject<>(type, id, this);
    }

    public RegistryObject<SoundEvent> registerSound(String name) {
        ResourceLocation id = rl(name);
        SoundEvent event = SoundEvent.createVariableRangeEvent(id);
        Registry.register(BuiltInRegistries.SOUND_EVENT, id, event);
        return new RegistryObject<>(event, id, this);
    }

    public void addToTab(ResourceLocation tabId, RegistryObject<?> entry) {
        tabEntries.computeIfAbsent(tabId, k -> new ArrayList<>()).add(entry);
    }

    public void populateTab(ResourceLocation tabId, CreativeModeTab.Output output) {
        List<RegistryObject<?>> entries = tabEntries.getOrDefault(tabId, List.of());
        for (RegistryObject<?> entry : entries) {
            Object obj = entry.get();
            if (obj instanceof Item item) output.accept(item);
            else if (obj instanceof Block block && block.asItem() != net.minecraft.world.item.Items.AIR)
                output.accept(block.asItem());
        }
    }

    public RegistryObject<CreativeModeTab> registerCreativeTab(String name, Supplier<ItemStack> icon) {
        ResourceLocation id = rl(name);
        CreativeModeTab tab = FabricItemGroup.builder()
                .title(Component.translatable("itemGroup." + modId + "." + name))
                .icon(icon)
                .displayItems((params, output) -> populateTab(id, output))
                .build();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id, tab);
        return new RegistryObject<>(tab, id, this);
    }

    public BlockFamily makeFullBlockFamily(String name, BlockBehaviour.Properties props) {

        RegistryObject<Block> base = registerBlockWithItem(name,
                () -> new Block(props));

        RegistryObject<Block> stairs = registerBlockWithItem(name + "_stairs",
                () -> new StairBlock(base.get().defaultBlockState(),
                        BlockBehaviour.Properties.ofFullCopy(base.get())));

        RegistryObject<Block> slab = registerBlockWithItem(name + "_slab",
                () -> new SlabBlock(
                        BlockBehaviour.Properties.ofFullCopy(base.get())));

        RegistryObject<Block> wall = registerBlockWithItem(name + "_wall",
                () -> new WallBlock(
                        BlockBehaviour.Properties.ofFullCopy(base.get())));

        RegistryObject<Block> fence = registerBlockWithItem(name + "_fence",
                () -> new FenceBlock(
                        BlockBehaviour.Properties.ofFullCopy(base.get())));

        RegistryObject<Block> fenceGate = registerBlockWithItem(name + "_fence_gate",
                () -> new FenceGateBlock(WoodType.OAK,
                        BlockBehaviour.Properties.ofFullCopy(base.get())));

        RegistryObject<Block> door = registerBlockWithItem(name + "_door",
                () -> new DoorBlock(BlockSetType.OAK,
                        BlockBehaviour.Properties.ofFullCopy(base.get()).noOcclusion()));

        RegistryObject<Block> trapdoor = registerBlockWithItem(name + "_trapdoor",
                () -> new TrapdoorBlock(BlockSetType.OAK,
                        BlockBehaviour.Properties.ofFullCopy(base.get()).noOcclusion()));

        return new BlockFamily(base, stairs, slab, wall, fence, fenceGate, door, trapdoor);
    }
}
