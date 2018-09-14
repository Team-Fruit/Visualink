package net.teamfruit.visualink.jabba;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.teamfruit.visualink.Reference;
import net.teamfruit.visualink.network.VisualinkPacketHandler;

public class BarrelLinkNetwork {
	@SubscribeEvent
	public void onPlayerLoggedIn(final PlayerLoggedInEvent event) {
		Reference.logger.info(String.format("Player %s connected. Sending barrel links", event.player));
		VisualinkPacketHandler.INSTANCE.sendTo(new Message0x00UpdateBarrelLinks(BarrelLink.getLinks()), (EntityPlayerMP) event.player);
	}
}
