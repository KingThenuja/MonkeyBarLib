package dev.thenu.mk.registry.Item.ItemProperties;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class Properties {

    public static Item.Properties item() { return new Item.Properties(); }
    public static Item.Properties stacksTo(int size) { return new Item.Properties().stacksTo(size); }
    public static Item.Properties noStack() { return stacksTo(1); }
    public static Item.Properties food(FoodProperties food) { return new Item.Properties().food(food); }
    public static Item.Properties durability(int durability) { return new Item.Properties().durability(durability); }
    public static Item.Properties fireResistant() { return new Item.Properties().fireResistant(); }
    public static Item.Properties rarity(Rarity rarity) { return new Item.Properties().rarity(rarity); }

    public static BlockBehaviour.Properties copyOf(Block block) { return BlockBehaviour.Properties.ofFullCopy(block); }
    public static BlockBehaviour.Properties copyStone() { return copyOf(Blocks.STONE); }
    public static BlockBehaviour.Properties copyWood() { return copyOf(Blocks.OAK_PLANKS); }
    public static BlockBehaviour.Properties copyGlass() { return copyOf(Blocks.GLASS); }
    public static BlockBehaviour.Properties copyTintedGlass() { return copyOf(Blocks.TINTED_GLASS); }
    public static BlockBehaviour.Properties copyIronBlock() { return copyOf(Blocks.IRON_BLOCK); }
    public static BlockBehaviour.Properties copyDirt() { return copyOf(Blocks.DIRT); }
    public static BlockBehaviour.Properties copySand() { return copyOf(Blocks.SAND); }

    public static FoodProperties food(int nutrition, float saturation) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build();
    }
    public static FoodProperties fastFood(int nutrition, float saturation) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).fast().build();
    }
    public static FoodProperties meatFood(int nutrition, float saturation) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build();
    }
}
