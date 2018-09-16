package net.teamfruit.visualink.addons;

import mcp.mobius.waila.api.IWailaRegistrar;
import net.teamfruit.visualink.addons.enderstorage.EnderStorageWailaHandler;
import net.teamfruit.visualink.addons.jabba.JABBAWailaHandler;

public class WailaHandler {
	public static void callbackRegister(final IWailaRegistrar registrar) {
		EnderStorageWailaHandler.register(registrar);
		JABBAWailaHandler.register(registrar);
	}
}
