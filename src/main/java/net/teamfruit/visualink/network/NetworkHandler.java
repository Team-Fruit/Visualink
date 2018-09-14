package net.teamfruit.visualink.network;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import mcp.mobius.betterbarrels.bspace.BSpaceStorageHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.teamfruit.visualink.Reference;
import net.teamfruit.visualink.jabba.BarrelLinkManager;
import net.teamfruit.visualink.jabba.Message0x00UpdateBarrelLinks;

public class NetworkHandler {
	@SubscribeEvent
	public void onPlayerLoggedIn(final PlayerLoggedInEvent event) {
		try {
			Reference.logger.info(String.format("Player %s connected. Sending barrel links", event.player));
			final Field field = BSpaceStorageHandler.class.getDeclaredField("links");
			field.setAccessible(true);
			@SuppressWarnings("unchecked")
			final HashMap<Integer, HashSet<Integer>> links = (HashMap<Integer, HashSet<Integer>>) field.get(BSpaceStorageHandler.instance());
			BarrelLinkManager.instance.links = links;
			VisualinkPacketHandler.INSTANCE.sendTo(new Message0x00UpdateBarrelLinks(BarrelLinkManager.instance), (EntityPlayerMP) event.player);
		} catch (final Exception e) {
			Reference.logger.error("Failed to send barrel links: ", e);
		}
	}
}