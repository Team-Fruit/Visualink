package net.teamfruit.visualink.addons.enderstorage;

import net.minecraft.tileentity.TileEntity;
import net.teamfruit.visualink.BlockManager;
import net.teamfruit.visualink.BlockPos;
import net.teamfruit.visualink.Reference;

public class TileFrequencyOwnerHook {
	public static void handleDescriptionPacket(final TileEntity tile) {
		Reference.logger.info("tileFreq: "+tile.xCoord+", "+tile.yCoord+", "+tile.zCoord);
		BlockManager.getInstance().addBlock(new BlockPos(tile.getWorldObj().provider.dimensionId, tile.xCoord, tile.yCoord, tile.zCoord), tile.getBlockType(), null);
	}
}
