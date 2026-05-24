package dev.thenu.mk.api.register;

import dev.thenu.mk.registry.Block.BlockEntity.BlockEntityFactory;
import dev.thenu.mk.registry.Block.BlockFamilyBuilder.BlockFamily;
import dev.thenu.mk.registry.Block.Unprotected.DoorBlock;
import dev.thenu.mk.registry.Block.Unprotected.StairBlock;
import dev.thenu.mk.registry.Block.Unprotected.TrapDoorBlock;
import dev.thenu.mk.registry.RegistryObjects.RegistryObject;
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
    private final List<Runnable> pending = new ArrayList<>();
    private final Map<ResourceLocation, List<RegistryObject<?>>> tabEntries = new HashMap<>();

    public RegistryHelper(String modId) {
        this.modId = modId;
    }

    /** Call this during your mod's init — replaces register(IEventBus) */
    public void register() {
        pending.forEach(Runnable::run);
        pending.clear();
    }

    private ResourceLocation rl(String name) {
        return ResourceLocation.fromNamespaceAndPath(modId, name);
    }

    @SuppressWarnings("unchecked")
    private <T> RegistryObject<T> enqueue(Registry<? super T> registry, String name, Supplier<T> supplier) {
        ResourceLocation id = rl(name);
        // Box the value so we can capture it after registration
        Object[] box = new Object[1];
        pending.add(() -> box[0] = Registry.register((Registry<T>) registry, id, supplier.get()));
        return new RegistryObject<>(() -> (T) box[0], id, this);
    }

    public <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        return enqueue(BuiltInRegistries.BLOCK, name, block);
    }

    public <T extends Block> RegistryObject<T> registerBlockWithoutItem(String name, T block) {
        return registerBlock(name, () -> block);
    }

    public <T extends Block> RegistryObject<T> registerBlockWithItem(
            String name, Supplier<T> block, Item.Properties props) {
        RegistryObject<T> obj = registerBlock(name, block);
        enqueue(BuiltInRegistries.ITEM, name, () -> new BlockItem(obj.get(), props));
        return obj;
    }

    public <T extends Block> RegistryObject<T> registerBlockWithItem(String name, Supplier<T> block) {
        return registerBlockWithItem(name, block, new Item.Properties());
    }

    public <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> item) {
        return enqueue(BuiltInRegistries.ITEM, name, item);
    }

    public RegistryObject<Item> registerSimpleItem(String name, Item.Properties props) {
        return registerItem(name, () -> new Item(props));
    }

    public RegistryObject<Item> registerSimpleItem(String name) {
        return registerSimpleItem(name, new Item.Properties());
    }

    public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBlockEntity(
            String name, BlockEntityFactory<T> factory, Block... validBlocks) {
        return enqueue(BuiltInRegistries.BLOCK_ENTITY_TYPE, name,
                () -> BlockEntityType.Builder.of(factory::create, validBlocks).build(null));
    }

    public RegistryObject<SoundEvent> registerSound(String name) {
        ResourceLocation id = rl(name);
        return enqueue(BuiltInRegistries.SOUND_EVENT, name,
                () -> SoundEvent.createVariableRangeEvent(id));
    }

    public void addToTab(ResourceLocation tabId, RegistryObject<?> entry) {
        tabEntries.computeIfAbsent(tabId, k -> new ArrayList<>()).add(entry);
    }

    public void populateTab(ResourceLocation tabId, CreativeModeTab.Output output) {
        for (RegistryObject<?> entry : tabEntries.getOrDefault(tabId, List.of())) {
            Object obj = entry.get();
            if (obj instanceof Item item) output.accept(item);
            else if (obj instanceof Block block && block.asItem() != net.minecraft.world.item.Items.AIR)
                output.accept(block.asItem());
        }
    }

    public RegistryObject<CreativeModeTab> registerCreativeTab(String name, Supplier<ItemStack> icon) {
        ResourceLocation id = rl(name);
        return enqueue(BuiltInRegistries.CREATIVE_MODE_TAB, name, () ->
                CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                        .title(Component.translatable("itemGroup." + modId + "." + name))
                        .icon(icon)
                        .displayItems((params, output) -> populateTab(id, output))
                        .build());
    }

    public BlockFamily makeFullBlockFamily(String name, BlockBehaviour.Properties props) {
        RegistryObject<Block> base       = registerBlockWithItem(name, () -> new Block(props));
        RegistryObject<Block> stairs     = registerBlockWithItem(name + "_stairs",
                () -> new StairBlock(base.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(base.get())));
        RegistryObject<Block> slab       = registerBlockWithItem(name + "_slab",
                () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(base.get())));
        RegistryObject<Block> wall       = registerBlockWithItem(name + "_wall",
                () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(base.get())));
        RegistryObject<Block> fence      = registerBlockWithItem(name + "_fence",
                () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(base.get())));
        RegistryObject<Block> fenceGate  = registerBlockWithItem(name + "_fence_gate",
                () -> new FenceGateBlock(WoodType.OAK, BlockBehaviour.Properties.ofFullCopy(base.get())));
        RegistryObject<Block> door       = registerBlockWithItem(name + "_door",
                () -> new DoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(base.get()).noOcclusion()));
        RegistryObject<Block> trapdoor   = registerBlockWithItem(name + "_trapdoor",
                () -> new TrapDoorBlock(BlockSetType.OAK, BlockBehaviour.Properties.ofFullCopy(base.get()).noOcclusion()));

        return new BlockFamily(base, stairs, slab, wall, fence, fenceGate, door, trapdoor);
    }
}