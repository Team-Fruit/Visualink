package net.teamfruit.visualink.addons.jabba;

import net.minecraft.tileentity.TileEntity;
import net.teamfruit.visualink.Reference;

public class MessageLinkUpdateHook {
	public static void channelRead0x00(final TileEntity tile) {
		Reference.logger.info("msg0x00: "+tile.xCoord+", "+tile.yCoord+", "+tile.zCoord);
	}

	public static void channelRead0x08(final TileEntity tile) {
		Reference.logger.info("msg0x08: "+tile.xCoord+", "+tile.yCoord+", "+tile.zCoord);
	}
}
