package net.teamfruit.visualink.addons.jabba;

import net.minecraft.tileentity.TileEntity;
import net.teamfruit.visualink.BlockManager;
import net.teamfruit.visualink.BlockPos;
import net.teamfruit.visualink.Log;

public class MessageLinkUpdateHook {
	public static void channelRead0x00(final TileEntity tile) {
		Log.log.info("msg0x00: "+tile.xCoord+", "+tile.yCoord+", "+tile.zCoord);
		BlockManager.getInstance().addBlock(new BlockPos(tile.getWorldObj().provider.dimensionId, tile.xCoord, tile.yCoord, tile.zCoord), tile.getBlockType(), null);
	}

	public static void channelRead0x08(final TileEntity tile) {
		Log.log.info("msg0x08: "+tile.xCoord+", "+tile.yCoord+", "+tile.zCoord);
	}
}
