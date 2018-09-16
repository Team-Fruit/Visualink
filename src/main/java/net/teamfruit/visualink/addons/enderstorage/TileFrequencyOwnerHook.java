package net.teamfruit.visualink.addons.enderstorage;

import net.minecraft.tileentity.TileEntity;
import net.teamfruit.visualink.BlockManager;
import net.teamfruit.visualink.BlockPos;

public class TileFrequencyOwnerHook {
	public static void handleDescriptionPacket(final TileEntity tile) {
		//Log.log.info("tileFreq: "+tile.xCoord+", "+tile.yCoord+", "+tile.zCoord);
		BlockManager.getInstance().addBlock(new BlockPos(tile.getWorldObj().provider.dimensionId, tile.xCoord, tile.yCoord, tile.zCoord), tile.getBlockType(), null);
	}
}
