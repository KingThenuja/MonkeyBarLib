# MonkeyBarLib — RegistryHelper Documentation

A loader-agnostic registration library for Minecraft mods.
Supports **Fabric/Quilt**, **Forge**, and **NeoForge** from one shared API.

---

## Table of Contents

1. [Setup](#1-setup)
2. [Blocks](#2-blocks)
3. [Items](#3-items)
4. [Block Families](#4-block-families)
5. [Block Entities](#5-block-entities)
6. [Creative Tabs](#6-creative-tabs)
7. [Sounds](#7-sounds)
8. [Properties Helper](#8-properties-helper)
9. [RegistryObject](#9-registryobject)
10. [Loader Entrypoints](#10-loader-entrypoints)
11. [Full Example](#11-full-example)

---

## 1. Setup

Create a single `RegistryHelper` instance per mod, accessible statically.

```java
public class MyMod {
    public static final String MOD_ID = "mymod";
    public static final RegistryHelper REGISTRY // <- you can change the REGISTRY to anything you want just change the other REGISTRY references to your  
            = new RegistryHelper(MOD_ID);
}
```

Then wire it up in your loader entrypoint — see [Section 10](#10-loader-entrypoints).

---

## 2. Blocks

### `registerBlockWithItem(name, supplier)`
Registers a block and automatically creates a `BlockItem` for it using default item properties.

```java
public static final RegistryObject<Block> RUBY_BLOCK =
        REGISTRY.registerBlockWithItem("ruby_block",
                () -> new Block(Properties.copyStone()));
```

### `registerBlockWithItem(name, supplier, itemProperties)`
Same as above but with custom `Item.Properties`.

```java
public static final RegistryObject<Block> RUBY_BLOCK =
        REGISTRY.registerBlockWithItem("ruby_block",
                () -> new Block(Properties.copyStone()),
                new Item.Properties().rarity(Rarity.RARE));
```

### `registerBlock(name, supplier)`
Registers a block **without** a `BlockItem`. Use for blocks that are never held as items (e.g. wall torches, crop blocks).

```java
public static final RegistryObject<Block> WALL_TORCH =
        REGISTRY.registerBlock("wall_ruby_torch",
                () -> new WallTorchBlock(ParticleTypes.FLAME, Properties.copyOf(Blocks.WALL_TORCH)));
```

### `registerBlockWithoutItem(name, block)`
Same as `registerBlock` but takes an already-constructed block instance.

```java
public static final RegistryObject<Block> MY_BLOCK =
        REGISTRY.registerBlockWithoutItem("my_block", new Block(Properties.copyStone()));
```

### Custom block classes

Pass any `Block` subclass via the supplier:

```java
public static final RegistryObject<MyOreBlock> RUBY_ORE =
        REGISTRY.registerBlockWithItem("ruby_ore",
                () -> new MyOreBlock(3, Properties.copyStone()));
```

---

## 3. Items

### `registerSimpleItem(name)`
Registers a plain `Item` with default properties.

```java
public static final RegistryObject<Item> RUBY =
        REGISTRY.registerSimpleItem("ruby");
```

### `registerSimpleItem(name, itemProperties)`
Registers a plain `Item` with custom properties.

```java
public static final RegistryObject<Item> RUBY =
        REGISTRY.registerSimpleItem("ruby",
                new Item.Properties().rarity(Rarity.UNCOMMON));
```

### `registerItem(name, supplier)`
Registers any `Item` subclass.

```java
public static final RegistryObject<SwordItem> RUBY_SWORD =
        REGISTRY.registerItem("ruby_sword",
                () -> new SwordItem(Tiers.DIAMOND, new Item.Properties()));
```

### Food items

Use `Properties.food(...)` to build `FoodProperties` inline:

```java
public static final RegistryObject<Item> RUBY_APPLE =
        REGISTRY.registerSimpleItem("ruby_apple",
                new Item.Properties().food(Properties.food(6, 1.2f)));

// Fast-eating food (like dried kelp)
new Item.Properties().food(Properties.fastFood(1, 0.3f));
```

### Unstackable items

```java
new Item.Properties().stacksTo(1)
// or shorthand:
Properties.noStack()
```

---

## 4. Block Families

`makeFullBlockFamily` registers 8 blocks at once from a single base name and set of properties:

| Suffix | Block type |
|---|---|
| *(none)* | `Block` |
| `_stairs` | `StairBlock` |
| `_slab` | `SlabBlock` |
| `_wall` | `WallBlock` |
| `_fence` | `FenceBlock` |
| `_fence_gate` | `FenceGateBlock` |
| `_door` | `DoorBlock` |
| `_trapdoor` | `TrapdoorBlock` |

All 8 get a `BlockItem` automatically.

```java
public static final BlockFamily RUBY_FAMILY =
    REGISTRY.makeFullBlockFamily("ruby", Properties.copyStone());
```

### Accessing family members

```java
Block base = RUBY_FAMILY.base().get();
Block stairs = RUBY_FAMILY.stairs().get();
Block slab = RUBY_FAMILY.slab().get();
Block wall = RUBY_FAMILY.wall().get();
Block fence = RUBY_FAMILY.fence().get();
Block fenceGate = RUBY_FAMILY.fenceGate().get();
Block door = RUBY_FAMILY.door().get();
Block trapdoor  = RUBY_FAMILY.trapdoor().get();
```

### Adding a whole family to a creative tab

```java
RUBY_FAMILY.addToTab(ModCreativeTabs.MY_TAB);
```

---

## 5. Block Entities

### `registerBlockEntity(name, factory, validBlocks...)`

Pass a `BlockEntityFactory<T>` (a functional interface: `(BlockPos, BlockState) -> T`) and the blocks that can host this entity.

```java
public static final RegistryObject<BlockEntityType<RubyBlockEntity>> RUBY_BLOCK_ENTITY =
    REGISTRY.registerBlockEntity(
        "ruby_block_entity",
        RubyBlockEntity::new,           // constructor reference
        ModBlocks.RUBY_BLOCK.get()      // valid host blocks
    );
```

Multiple valid blocks:

```java
REGISTRY.registerBlockEntity(
    "ruby_block_entity",
    RubyBlockEntity::new,
    ModBlocks.RUBY_BLOCK.get(),
    ModBlocks.RUBY_BLOCK_SLAB.get()
);
```

Your block entity class just needs the standard constructor:

```java
public class RubyBlockEntity extends BlockEntity {
    public RubyBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RUBY_BLOCK_ENTITY.get(), pos, state);
    }
}
```

> **Forge note:** The Forge version uses a reflection-based workaround because `BlockEntityType.Builder.of()` is protected. This is handled internally — your usage code is identical across all loaders.

---

## 6. Creative Tabs

### `registerCreativeTab(name, iconSupplier)`

The icon supplier is called lazily, so it's safe to reference items that are registered in the same class.

```java
public static final RegistryObject<CreativeModeTab> MY_TAB =
        REGISTRY.registerCreativeTab("mymod",
                () -> new ItemStack(ModItems.RUBY.get()));
```

### Adding entries to a tab

Call `addToCreativeTab` on any `RegistryObject`:

```java
ModItems.RUBY.addToCreativeTab(MY_TAB);
ModBlocks.RUBY_BLOCK.addToCreativeTab(MY_TAB);
```

Items and blocks can be chained fluently:

```java
public static final RegistryObject<Item> RUBY =
        REGISTRY.registerSimpleItem("ruby")
                .addToCreativeTab(ModCreativeTabs.MY_TAB);
```

### Adding a whole `BlockFamily` to a tab

```java
ModBlocks.RUBY_FAMILY.addToTab(ModCreativeTabs.MY_TAB);
```

### Tab ordering

Entries appear in the tab in the order `addToCreativeTab` / `addToTab` is called. Define your tab first, then your content, or call `addToCreativeTab` explicitly in an `init()` method after both exist.

---

## 7. Sounds

### `registerSound(name)`

Registers a variable-range `SoundEvent`. The `ResourceLocation` is set to `modid:name` automatically.

```java
public static final RegistryObject<SoundEvent> RUBY_CHIME =
        REGISTRY.registerSound("ruby_chime");
```

Use it anywhere a `SoundEvent` is needed:

```java
level.playSound(null, pos, ModSounds.RUBY_CHIME.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
```

Don't forget to add a matching entry in `sounds.json`.

---

## 8. Properties Helper

`dev.thenu.mk.registry.Item.ItemProperties.Properties` is a static utility class with shorthand factories to avoid boilerplate.

### Block properties

| Method | Equivalent |
|---|---|
| `Properties.copyOf(block)` | `BlockBehaviour.Properties.ofFullCopy(block)` |
| `Properties.copyStone()` | copy of `Blocks.STONE` |
| `Properties.copyWood()` | copy of `Blocks.OAK_PLANKS` |
| `Properties.copyGlass()` | copy of `Blocks.GLASS` |
| `Properties.copyTintedGlass()` | copy of `Blocks.TINTED_GLASS` |
| `Properties.copyIronBlock()` | copy of `Blocks.IRON_BLOCK` |
| `Properties.copyDirt()` | copy of `Blocks.DIRT` |
| `Properties.copySand()` | copy of `Blocks.SAND` |

You can chain vanilla modifiers after any of these:

```java
Properties.copyStone().requiresCorrectToolForDrops().strength(3.0f, 4.0f)
```

### Item properties

| Method | Result |
|---|---|
| `Properties.item()` | `new Item.Properties()` |
| `Properties.stacksTo(n)` | stack size of `n` |
| `Properties.noStack()` | stack size of `1` |
| `Properties.durability(n)` | tool/armor durability |
| `Properties.fireResistant()` | immune to fire/lava |
| `Properties.rarity(r)` | item rarity |
| `Properties.food(FoodProperties)` | sets food value |

### Food properties

| Method | Result |
|---|---|
| `Properties.food(nutrition, saturation)` | normal food |
| `Properties.fastFood(nutrition, saturation)` | fast-eating food |
| `Properties.meatFood(nutrition, saturation)` | meat food (wolves accept it) |

```java
// A food item that eats fast and gives 4 hunger
new Item.Properties().food(Properties.fastFood(4, 0.8f))
```

---

## 9. RegistryObject

`RegistryObject<T>` is the wrapper returned by every `register*` method. It wraps the loader's lazy holder transparently.

### `.get()`
Returns the registered object. Safe to call after the registry phase (i.e. not in static initializers, but fine in event handlers, block constructors, etc.).

```java
Block block = ModBlocks.RUBY_BLOCK.get();
Item  item  = ModItems.RUBY.get();
```

### `.getId()`
Returns the `ResourceLocation` the object was registered under.

```java
ResourceLocation id = ModBlocks.RUBY_BLOCK.getId();
// → mymod:ruby_block
```

### `.addToCreativeTab(tab)`
Queues this entry into a creative tab. Returns `this` for chaining.

```java
public static final RegistryObject<Item> RUBY =
        REGISTRY.registerSimpleItem("ruby")
                .addToCreativeTab(ModCreativeTabs.MY_TAB);
```

---

## 10. Loader Entrypoints

The only code that differs per loader is the mod entrypoint. All registration and content classes are identical.

### Forge

```java
@Mod(MyMod.MOD_ID)
public class MyMod {
    public static final String MOD_ID = "mymod";
    public static final RegistryHelper REGISTRY = new RegistryHelper(MOD_ID);

    public MyMod(FMLJavaModLoadingContext ctx) {
        // Registers all DeferredRegisters with the event bus
        REGISTRY.register(ctx.getModEventBus());

        // Queue creative tab entries before the tab event fires
        ModCreativeTabs.init();
    }
}
```

### NeoForge

```java
@Mod(MyMod.MOD_ID)
public class MyMod {
    public static final String MOD_ID = "mymod";
    public static final RegistryHelper REGISTRY = new RegistryHelper(MOD_ID);

    public MyMod(IEventBus modBus) {
        REGISTRY.register(modBus);
        ModCreativeTabs.init();
    }
}
```

### Fabric

There is no deferred system — classes must be **explicitly referenced** in `onInitialize` to trigger their static initializers and run the registration calls.

```java
public class MyMod implements ModInitializer {
    public static final String MOD_ID = "mymod";
    public static final RegistryHelper REGISTRY = new RegistryHelper(MOD_ID);

    @Override
    public void onInitialize() {
        // Touching each class forces its static fields to initialize,
        // which calls REGISTRY.register*(...) for each entry.
        ModBlocks.init();
        ModItems.init();
        ModCreativeTabs.init();
    }
}
```

Add a dummy `init()` to each content class to make the touch explicit:

```java
public class ModBlocks {
    public static final RegistryObject<Block> RUBY_BLOCK = ...;

    public static void init() {}
}
```

---

## 11. Full Example

A complete mod registering a gem item, an ore block, a full stone family, a block entity, a sound, and a creative tab.

```java
// MyMod.java
@Mod(MyMod.MOD_ID)
public class MyMod {
    public static final String MOD_ID = "mymod";
    public static final RegistryHelper REGISTRY = new RegistryHelper(MOD_ID);

    public MyMod(IEventBus modBus) {
        // NeoForge — swap for Forge/Fabric variant
        REGISTRY.register(modBus);
        ModCreativeTabs.init();
    }
}

// ModItems.java
public class ModItems {
    public static final RegistryObject<Item> RUBY =
            REGISTRY.registerSimpleItem("ruby",
                    new Item.Properties().rarity(Rarity.UNCOMMON));

    public static final RegistryObject<Item> RUBY_APPLE =
            REGISTRY.registerSimpleItem("ruby_apple",
                    new Item.Properties().food(Properties.food(8, 1.5f)));

    public static void init() {}
}

// ModBlocks.java
public class ModBlocks {
    public static final RegistryObject<Block> RUBY_ORE =
            REGISTRY.registerBlockWithItem("ruby_ore",
                    () -> new DropExperienceBlock(UniformInt.of(3, 7),
                            Properties.copyStone().requiresCorrectToolForDrops()));

    public static final BlockFamily RUBY_BRICKS =
            REGISTRY.makeFullBlockFamily("ruby_bricks",
                    Properties.copyStone().requiresCorrectToolForDrops().strength(2.0f, 6.0f));

    public static void init() {}
}

// ModBlockEntities.java
public class ModBlockEntities {
    public static final RegistryObject<BlockEntityType<RubyBlockEntity>> RUBY =
            REGISTRY.registerBlockEntity(
                    "ruby",
                    RubyBlockEntity::new,
                    ModBlocks.RUBY_ORE.get()
            );

    public static void init() {}
}

// ModSounds.java
public class ModSounds {
    public static final RegistryObject<SoundEvent> RUBY_CHIME =
            REGISTRY.registerSound("ruby_chime");

    public static void init() {}
}

// ModCreativeTabs.java
public class ModCreativeTabs {
    public static final RegistryObject<CreativeModeTab> RUBY_TAB =
            REGISTRY.registerCreativeTab("ruby_tab",
                    () -> new ItemStack(ModItems.RUBY.get()));

    public static void init() {
        ModItems.RUBY.addToCreativeTab(RUBY_TAB);
        ModItems.RUBY_APPLE.addToCreativeTab(RUBY_TAB);
        ModBlocks.RUBY_ORE.addToCreativeTab(RUBY_TAB);
        ModBlocks.RUBY_BRICKS.addToTab(RUBY_TAB);
    }
}
```
