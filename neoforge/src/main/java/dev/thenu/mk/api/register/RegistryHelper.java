package dev.thenu.mk.api.register;

import dev.thenu.mk.registry.Block.BlockEntity.BlockEntityFactory;
import dev.thenu.mk.registry.Block.BlockFamilyBuilder.BlockFamily;
import dev.thenu.mk.registry.Block.Unprotected.DoorBlock;
import dev.thenu.mk.registry.Block.Unprotected.StairBlock;
import dev.thenu.mk.registry.Block.Unprotected.TrapdoorBlock;
import dev.thenu.mk.registry.RegistryObjects.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.*;
import java.util.function.Supplier;

public class RegistryHelper {

    private final String modId;

    public final DeferredRegister<Block> BLOCKS;
    public final DeferredRegister<Item> ITEMS;
    public final DeferredRegister<CreativeModeTab> CREATIVE_TABS;
    public final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES;
    public final DeferredRegister<EntityType<?>> ENTITIES;
    public final DeferredRegister<SoundEvent> SOUNDS;

    private final Map<ResourceLocation, List<RegistryObject<?>>> tabEntries = new HashMap<>();

    public RegistryHelper(String modId) {
        this.modId = modId;
        this.BLOCKS = DeferredRegister.create(Registries.BLOCK, modId);
        this.ITEMS = DeferredRegister.create(Registries.ITEM, modId);
        this.CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, modId);
        this.BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, modId);
        this.ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, modId);
        this.SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, modId);
    }

    public void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        CREATIVE_TABS.register(bus);
        BLOCK_ENTITIES.register(bus);
        ENTITIES.register(bus);
        SOUNDS.register(bus);
    }

    private ResourceLocation rl(String name) {
        return ResourceLocation.fromNamespaceAndPath(modId, name);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> RegistryObject<T> wrap(DeferredHolder<?, ?> holder) {
        return new RegistryObject<>((Supplier<T>) (DeferredHolder) holder, holder.getId(), this);
    }

    public <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        return wrap(BLOCKS.register(name, block));
    }

    public <T extends Block> RegistryObject<T> registerBlockWithoutItem(String name, T block) {
        return registerBlock(name, () -> block);
    }

    public <T extends Block> RegistryObject<T> registerBlockWithItem(
            String name, Supplier<T> block, Item.Properties props) {
        DeferredHolder<Block, T> obj = BLOCKS.register(name, block);
        ITEMS.register(name, () -> new BlockItem(obj.get(), props));
        return wrap(obj);
    }

    public <T extends Block> RegistryObject<T> registerBlockWithItem(String name, Supplier<T> block) {
        return registerBlockWithItem(name, block, new Item.Properties());
    }

    public <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> item) {
        return wrap(ITEMS.register(name, item));
    }

    public RegistryObject<Item> registerSimpleItem(String name, Item.Properties props) {
        return registerItem(name, () -> new Item(props));
    }

    public RegistryObject<Item> registerSimpleItem(String name) {
        return registerSimpleItem(name, new Item.Properties());
    }

    public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBlockEntity(
            String name, BlockEntityFactory<T> factory, Block... validBlocks) {
        return wrap(BLOCK_ENTITIES.register(name,
                () -> BlockEntityType.Builder.of(factory::create, validBlocks).build(null)));
    }

    public RegistryObject<SoundEvent> registerSound(String name) {
        ResourceLocation id = rl(name);
        return wrap(SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(id)));
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
        DeferredHolder<CreativeModeTab, CreativeModeTab> obj = CREATIVE_TABS.register(name, () ->
                CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup." + modId + "." + name))
                        .icon(icon)
                        .displayItems((params, output) -> populateTab(id, output))
                        .build());
        return wrap(obj);
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
