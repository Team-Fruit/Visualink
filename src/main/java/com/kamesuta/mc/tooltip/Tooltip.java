package com.kamesuta.mc.tooltip;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = Tooltip.MODID, version = Tooltip.VERSION, name = "Tooltip")
public class Tooltip {
	public static final String MODID = "Tooltip";
	public static final String VERSION = "2.0";
	public static boolean toggleXray = false;

	public static int radius = 45;
	private KeyBinding toggleXrayBinding;
	private KeyBinding toggleXrayGui;
	public static int displayListid = 0;
	public static int cooldownTicks = 0;
	private Minecraft mc;

	@Mod.EventHandler
	public void preInit(final FMLPreInitializationEvent event) {
		if (event.getSide() == Side.SERVER) {
			return;
		}
		final Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
		cfg.load();
		radius = cfg.get("Xray-Variables", "radius", 45, "Radius for X-ray").getInt();
		toggleXray = cfg.get("Xray-Variables", "toggleXray", false, "X-ray enabled on start-up?").getBoolean(false);
		cfg.save();
		this.toggleXrayBinding = new KeyBinding("Toggle Xray", 45, "Xray");

		this.toggleXrayGui = new KeyBinding("Toggle Xray-Gui", 65, "Xray");

		ClientRegistry.registerKeyBinding(this.toggleXrayBinding);
		ClientRegistry.registerKeyBinding(this.toggleXrayGui);
	}

	@Mod.EventHandler
	public void init(final FMLInitializationEvent event) {
		if (event.getSide() == Side.SERVER)
			return;
		this.mc = Minecraft.getMinecraft();

		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);

		TooltipBlocks.init();
	}

	@Mod.EventHandler
	public void postInit(final FMLPostInitializationEvent event) {
		if (event.getSide() == Side.SERVER)
			return;
		displayListid = GL11.glGenLists(5) + 3;
	}

	@SubscribeEvent
	public boolean onTickInGame(final TickEvent.ClientTickEvent e) {
		if ((!(toggleXray)) || (this.mc.theWorld == null))
			return true;
		if (cooldownTicks < 1) {
			compileDL();
			cooldownTicks = 80;
		}
		cooldownTicks -= 1;
		return true;
	}

	private void compileDL() {
		GL11.glNewList(displayListid, 4864);

		GL11.glDisable(3553);
		GL11.glDisable(2929);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);

		GL11.glBegin(1);
		final WorldClient world = this.mc.theWorld;

		final EntityClientPlayerMP player = this.mc.thePlayer;
		if ((world == null) || (player == null))
			return;
		for (int i = (int) player.posX - radius; i <= (int) player.posX + radius; ++i) {
			for (int j = (int) player.posZ - radius; j <= (int) player.posZ + radius; ++j) {
				int k = 0;
				Block bId;
				for (final int height = world.getHeightValue(i, j); k <= height; ++k) {
					bId = world.getBlock(i, k, j);
					if (bId == Blocks.air)
						continue;
					if (bId != Blocks.stone)
						for (final TooltipBlocks block : TooltipBlocks.blocks) {
							if (block.enabled)
								;
							final Block blocki = (Block) Block.blockRegistry.getObject(block.id);
							if ((blocki == bId)
									&& (((block.meta == -1) || (block.meta == world.getBlockMetadata(i, k, j))))) {
								renderBlock(i, k, j, block);
								break;
							}
						}
				}
			}
		}
		GL11.glEnd();
		GL11.glEnable(2929);
		GL11.glDisable(3042);
		GL11.glEnable(3553);
		GL11.glEndList();
	}

	private void renderBlock(final int x, final int y, final int z, final TooltipBlocks block) {
		GL11.glColor4ub((byte) block.r, (byte) block.g, (byte) block.b, (byte) block.a);

		GL11.glVertex3f(x, y, z);
		GL11.glVertex3f(x + 1, y, z);

		GL11.glVertex3f(x + 1, y, z);
		GL11.glVertex3f(x + 1, y, z + 1);

		GL11.glVertex3f(x, y, z);
		GL11.glVertex3f(x, y, z + 1);

		GL11.glVertex3f(x, y, z + 1);
		GL11.glVertex3f(x + 1, y, z + 1);

		GL11.glVertex3f(x, y + 1, z);
		GL11.glVertex3f(x + 1, y + 1, z);

		GL11.glVertex3f(x + 1, y + 1, z);
		GL11.glVertex3f(x + 1, y + 1, z + 1);

		GL11.glVertex3f(x, y + 1, z);
		GL11.glVertex3f(x, y + 1, z + 1);

		GL11.glVertex3f(x, y + 1, z + 1);
		GL11.glVertex3f(x + 1, y + 1, z + 1);

		GL11.glVertex3f(x, y, z);
		GL11.glVertex3f(x, y + 1, z);

		GL11.glVertex3f(x, y, z + 1);
		GL11.glVertex3f(x, y + 1, z + 1);

		GL11.glVertex3f(x + 1, y, z);
		GL11.glVertex3f(x + 1, y + 1, z);

		GL11.glVertex3f(x + 1, y, z + 1);
		GL11.glVertex3f(x + 1, y + 1, z + 1);
	}

	@SubscribeEvent
	public void keyboardEvent(final InputEvent.KeyInputEvent key) {
		if (!(this.mc.currentScreen instanceof GuiScreen)) {
			if (this.toggleXrayBinding.isPressed()) {
				toggleXray = !(toggleXray);
				if (toggleXray)
					cooldownTicks = 0;
				else
					GL11.glDeleteLists(displayListid, 1);
			}

			if (this.toggleXrayGui.isPressed())
				GuiBlockList.show();
		}
	}

	@SubscribeEvent
	public void renderWorldLastEvent(final RenderWorldLastEvent evt) {
		if ((!(toggleXray)) || (this.mc.theWorld == null))
			return;
		final double doubleX = this.mc.thePlayer.lastTickPosX
				+ (this.mc.thePlayer.posX - this.mc.thePlayer.lastTickPosX) * evt.partialTicks;

		final double doubleY = this.mc.thePlayer.lastTickPosY
				+ (this.mc.thePlayer.posY - this.mc.thePlayer.lastTickPosY) * evt.partialTicks;

		final double doubleZ = this.mc.thePlayer.lastTickPosZ
				+ (this.mc.thePlayer.posZ - this.mc.thePlayer.lastTickPosZ) * evt.partialTicks;

		GL11.glPushMatrix();
		GL11.glTranslated(-doubleX, -doubleY, -doubleZ);
		GL11.glCallList(displayListid);
		GL11.glPopMatrix();
	}
}