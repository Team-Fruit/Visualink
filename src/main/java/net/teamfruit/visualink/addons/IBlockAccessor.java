package net.teamfruit.visualink.addons;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.teamfruit.visualink.BlockPos;

public interface IBlockAccessor {
	Block getBlock();

	@Nullable
	TileEntity getTileEntity();

	BlockPos getPosition();

	int getMetadata();

	String getBlockID();
}