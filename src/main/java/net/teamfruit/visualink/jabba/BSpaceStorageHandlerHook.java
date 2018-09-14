package net.teamfruit.visualink.jabba;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;

import mcp.mobius.betterbarrels.bspace.BSpaceStorageHandler;
import net.teamfruit.visualink.Reference;
import net.teamfruit.visualink.network.VisualinkPacketHandler;

public class BSpaceStorageHandlerHook {
	public static void writeToNBT() {
		try {
			final Field field = BSpaceStorageHandler.class.getDeclaredField("links");
			field.setAccessible(true);
			@SuppressWarnings("unchecked")
			final HashMap<Integer, HashSet<Integer>> links = (HashMap<Integer, HashSet<Integer>>) field.get(BSpaceStorageHandler.instance());
			BarrelLinkManager.instance.links = links;
			VisualinkPacketHandler.INSTANCE.sendToAll(new Message0x00UpdateBarrelLinks(BarrelLinkManager.instance));
		} catch (final Exception e) {
			Reference.logger.error("Failed to send barrel links: ", e);
		}
	}
}
