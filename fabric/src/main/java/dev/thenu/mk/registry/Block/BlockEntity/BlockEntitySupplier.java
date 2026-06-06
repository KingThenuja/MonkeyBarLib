package dev.thenu.mk.registry.Block.BlockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

@FunctionalInterface
public interface BlockEntitySupplier<T extends BlockEntity> {
    T create(BlockPos pos, BlockState state);
}