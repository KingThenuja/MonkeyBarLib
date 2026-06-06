package dev.thenu.mk.api.register;

import dev.thenu.mk.registry.Block.BlockEntity.BlockEntitySupplier;
import dev.thenu.mk.registry.Block.BlockFamilyBuilder.BlockFamily;
import dev.thenu.mk.registry.Block.Unprotected.DoorBlock;
import dev.thenu.mk.registry.Block.Unprotected.StairBlock;
import dev.thenu.mk.registry.Block.Unprotected.TrapdoorBlock;
import dev.thenu.mk.registry.RegistryObjects.RegistryObject;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.sound.SoundEvent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

import java.util.*;
import java.util.function.Supplier;

public class RegistryHelper {

    private final String modId;

    private final Map<Identifier, List<RegistryObject<?>>> tabEntries = new HashMap<>();

    public RegistryHelper(String modId) {
        this.modId = modId;
    }

    public Identifier rl(String name) {
        return Identifier.of(modId, name);
    }

    public <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        Identifier id = rl(name);
        T registered = Registry.register(Registries.BLOCK, id, block.get());
        return new RegistryObject<>(registered, id, this);
    }

    public <T extends Block> RegistryObject<T> registerBlockWithoutItem(String name, T block) {
        return registerBlock(name, () -> block);
    }

    public <T extends Block> RegistryObject<T> registerBlockWithItem(
            String name, Supplier<T> block, Item.Settings props) {
        Identifier id = rl(name);
        T registered = Registry.register(Registries.BLOCK, id, block.get());
        Registry.register(Registries.ITEM, id, new BlockItem(registered, props));
        return new RegistryObject<>(registered, id, this);
    }

    public <T extends Block> RegistryObject<T> registerBlockWithItem(String name, Supplier<T> block) {
        return registerBlockWithItem(name, block, new Item.Settings());
    }

    public <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> item) {
        Identifier id = rl(name);
        T registered = Registry.register(Registries.ITEM, id, item.get());
        return new RegistryObject<>(registered, id, this);
    }

    public RegistryObject<Item> registerSimpleItem(String name, Item.Settings props) {
        return registerItem(name, () -> new Item(props));
    }

    public RegistryObject<Item> registerSimpleItem(String name) {
        return registerSimpleItem(name, new Item.Settings());
    }

    public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBlockEntity(
            String name,
            BlockEntitySupplier<? extends T> factory,
            Block... validBlocks) {
        Identifier id = rl(name);

        BlockEntityType<T> type = FabricBlockEntityTypeBuilder.<T>create(
                (pos, state) -> factory.create(pos, state),
                validBlocks
        ).build(null);

        BlockEntityType<T> registered = Registry.register(Registries.BLOCK_ENTITY_TYPE, id, type);
        return new RegistryObject<>(registered, id, this);
    }

    public RegistryObject<SoundEvent> registerSound(String name) {
        Identifier id = rl(name);

        SoundEvent event = SoundEvent.of(id);

        Registry.register(Registries.SOUND_EVENT, id, event);
        return new RegistryObject<>(event, id, this);
    }

    public void addToTab(Identifier tabId, RegistryObject<?> entry) {
        tabEntries.computeIfAbsent(tabId, k -> new ArrayList<>()).add(entry);
    }

    public void populateTab(Identifier tabId, ItemGroup.Entries output) {
        List<RegistryObject<?>> entries = tabEntries.getOrDefault(tabId, List.of());
        for (RegistryObject<?> entry : entries) {
            Object obj = entry.get();
            if (obj instanceof Item item) {
                output.add(item);
            } else if (obj instanceof Block block && block.asItem() != Items.AIR) {
                output.add(block.asItem());
            }
        }
    }

    public RegistryObject<ItemGroup> registerCreativeTab(String name, Supplier<ItemStack> icon) {
        Identifier id = rl(name);
        ItemGroup tab = FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup." + modId + "." + name))
                .icon(icon)
                .entries((params, output) -> populateTab(id, output))
                .build();
        Registry.register(Registries.ITEM_GROUP, id, tab);
        return new RegistryObject<>(tab, id, this);
    }

    public BlockFamily makeFullBlockFamily(String name, AbstractBlock.Settings props) {

        RegistryObject<Block> base = registerBlockWithItem(name,
                () -> new Block(props));

        RegistryObject<Block> stairs = registerBlockWithItem(name + "_stairs",
                () -> new StairBlock(base.get().getDefaultState(),
                        AbstractBlock.Settings.copy(base.get())));

        RegistryObject<Block> slab = registerBlockWithItem(name + "_slab",
                () -> new SlabBlock(
                        AbstractBlock.Settings.copy(base.get())));

        RegistryObject<Block> wall = registerBlockWithItem(name + "_wall",
                () -> new WallBlock(
                        AbstractBlock.Settings.copy(base.get())));

        RegistryObject<Block> fence = registerBlockWithItem(name + "_fence",
                () -> new FenceBlock(
                        AbstractBlock.Settings.copy(base.get())));

        RegistryObject<Block> fenceGate = registerBlockWithItem(name + "_fence_gate",
                () -> new FenceGateBlock(WoodType.OAK,
                        AbstractBlock.Settings.copy(base.get())));

        RegistryObject<Block> door = registerBlockWithItem(name + "_door",
                () -> new DoorBlock(BlockSetType.OAK,
                        AbstractBlock.Settings.copy(base.get()).nonOpaque()));

        RegistryObject<Block> trapdoor = registerBlockWithItem(name + "_trapdoor",
                () -> new TrapdoorBlock(BlockSetType.OAK,
                        AbstractBlock.Settings.copy(base.get()).nonOpaque()));

        return new BlockFamily(base, stairs, slab, wall, fence, fenceGate, door, trapdoor);
    }
}