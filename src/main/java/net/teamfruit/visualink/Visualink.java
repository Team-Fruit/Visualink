package net.teamfruit.visualink;

import java.util.Map;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import net.teamfruit.visualink.addons.jabba.BarrelLinkNetwork;
import net.teamfruit.visualink.addons.jabba.JABBAModule;
import net.teamfruit.visualink.network.VisualinkPacketHandler;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class Visualink {
	public static int displayListid = 0;

	@Mod.EventHandler
	public void preInit(final FMLPreInitializationEvent event) {
		VisualinkPacketHandler.INSTANCE.ordinal();

		if (event.getSide()==Side.SERVER)
			return;

		ClientHandler.instance.loadConfig(event.getSuggestedConfigurationFile());
		ClientHandler.instance.init();
	}

	@Mod.EventHandler
	public void init(final FMLInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(new BarrelLinkNetwork());
		JABBAModule.register();

		if (event.getSide()==Side.SERVER)
			return;

		FMLCommonHandler.instance().bus().register(ClientHandler.instance);
		MinecraftForge.EVENT_BUS.register(ClientHandler.instance);

		VisualinkItems.init();
		VisualinkBlocks.init();

		FMLInterModComms.sendMessage("Waila", "register", "net.teamfruit.visualink.addons.WailaHandler.callbackRegister");
	}

	@Mod.EventHandler
	public void postInit(final FMLPostInitializationEvent event) {
		if (event.getSide()==Side.SERVER)
			return;
		displayListid = GL11.glGenLists(5)+3;
	}

	@NetworkCheckHandler
	public boolean checkModList(final @Nonnull Map<String, String> versions, final @Nonnull Side side) {
		return true;
	}
}