package dev.thenu.mk.registry.Item.ItemProperties;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class Properties {

    public static Item.Settings item() { return new Item.Settings(); }
    public static Item.Settings stacksTo(int size) { return new Item.Settings().maxCount(size); }
    public static Item.Settings noStack() { return stacksTo(1); }
    public static Item.Settings food(FoodComponent food) { return new Item.Settings().food(food); }
    public static Item.Settings durability(int durability) { return new Item.Settings().maxDamage(durability); }
    public static Item.Settings fireResistant() { return new Item.Settings().fireproof(); }
    public static Item.Settings rarity(Rarity rarity) { return new Item.Settings().rarity(rarity); }

    public static AbstractBlock.Settings copyOf(Block block) { return AbstractBlock.Settings.copy(block); }
    public static AbstractBlock.Settings copyStone() { return copyOf(Blocks.STONE); }
    public static AbstractBlock.Settings copyWood() { return copyOf(Blocks.OAK_PLANKS); }
    public static AbstractBlock.Settings copyGlass() { return copyOf(Blocks.GLASS); }
    public static AbstractBlock.Settings copyTintedGlass() { return copyOf(Blocks.TINTED_GLASS); }
    public static AbstractBlock.Settings copyIronBlock() { return copyOf(Blocks.IRON_BLOCK); }
    public static AbstractBlock.Settings copyDirt() { return copyOf(Blocks.DIRT); }
    public static AbstractBlock.Settings copySand() { return copyOf(Blocks.SAND); }

    public static FoodComponent food(int nutrition, float saturation) {
        return new FoodComponent.Builder()
                .nutrition(nutrition)
                .saturationModifier(saturation)
                .build();
    }

    public static FoodComponent fastFood(int nutrition, float saturation) {
        return new FoodComponent.Builder()
                .nutrition(nutrition)
                .saturationModifier(saturation)
                .build();
    }

    public static FoodComponent meatFood(int nutrition, float saturation) {
        return new FoodComponent.Builder()
                .nutrition(nutrition)
                .saturationModifier(saturation)
                .build();
    }
}