package net.teamfruit.visualink.jabba;

import net.teamfruit.visualink.network.VisualinkPacketHandler;

public class BSpaceStorageHandlerHook {
	public static void writeToNBT() {
		VisualinkPacketHandler.INSTANCE.sendToAll(new Message0x00UpdateBarrelLinks(BarrelLink.getLinks()));
	}
}
