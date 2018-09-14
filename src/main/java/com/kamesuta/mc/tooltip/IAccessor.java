package com.kamesuta.mc.tooltip;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

public interface IAccessor {
	Block getBlock();

	@Nullable
	TileEntity getTileEntity();

	BlockPos getPosition();

	int getMetadata();

	String getBlockID();
}