package net.teamfruit.visualink;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.util.Collection;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.config.Configuration;
import net.teamfruit.visualink.addons.IAccessor;
import net.teamfruit.visualink.addons.IdentifierProvider;

public class ClientHandler {
	public static final ClientHandler instance = new ClientHandler();

	public static int radius = 45;
	public static boolean toggleVisualink = false;
	public static int cooldownTicks = 0;

	private ClientHandler() {
	}

	private KeyBinding toggleVisualinkBinding;
	private final Minecraft mc = Minecraft.getMinecraft();

	public void init() {
		this.toggleVisualinkBinding = new KeyBinding("Toggle Xray", 45, "Xray");
		ClientRegistry.registerKeyBinding(this.toggleVisualinkBinding);
	}

	public void loadConfig(final File file) {
		final Configuration cfg = new Configuration(file);
		cfg.load();
		radius = cfg.get("Visualink-Variables", "radius", 128, "Radius for Visualink").getInt();
		toggleVisualink = cfg.get("Visualink-Variables", "toggleVisualink", false, "Visualink enabled on start-up?").getBoolean(false);
		cfg.save();
	}

	@SubscribeEvent
	public boolean onTickInGame(final TickEvent.ClientTickEvent e) {
		if (!toggleVisualink||this.mc.theWorld==null)
			return true;
		if (cooldownTicks<1) {
			compileDL();
			cooldownTicks = 80;
		}
		cooldownTicks -= 1;
		return true;
	}

	private void compileDL() {
		GL11.glNewList(Visualink.displayListid, GL11.GL_COMPILE);

		GL11.glDisable(GL_TEXTURE_2D);
		GL11.glDisable(GL_DEPTH_TEST);
		GL11.glEnable(GL_BLEND);
		GL11.glBlendFunc(770, 771);

		GL11.glLineWidth(.5f);
		GL11.glBegin(GL_LINES);
		final WorldClient world = this.mc.theWorld;

		final EntityClientPlayerMP player = this.mc.thePlayer;
		if (world!=null&&player!=null) {
			final Multimap<String, BlockPos> map = ArrayListMultimap.create();
			for (int i = (int) player.posX-radius; i<=(int) player.posX+radius; ++i)
				for (int j = (int) player.posZ-radius; j<=(int) player.posZ+radius; ++j) {
					int k = 0;
					Block bId;
					for (final int height = world.getHeightValue(i, j); k<=height; ++k) {
						bId = world.getBlock(i, k, j);
						if (bId==Blocks.air)
							continue;
						if (bId!=Blocks.stone)
							for (final VisualinkBlocks block : VisualinkBlocks.blocks) {
								final Block blocki = block.getBlock();
								if (
									blocki==bId
								) {
									final BlockPos pos = new BlockPos(i, k, j);
									final IdentifierProvider provider = block.provider;
									final String id = provider==null ? block.id : provider.provide(new IAccessor() {
										@Override
										public TileEntity getTileEntity() {
											if (blocki.hasTileEntity(world.getBlockMetadata(pos.x, pos.y, pos.z)))
												return world.getTileEntity(pos.x, pos.y, pos.z);
											return null;
										}

										@Override
										public BlockPos getPosition() {
											return pos;
										}

										@Override
										public int getMetadata() {
											return world.getBlockMetadata(pos.x, pos.y, pos.z);
										}

										@Override
										public String getBlockID() {
											return block.id;
										}

										@Override
										public Block getBlock() {
											return blocki;
										}
									});
									//renderBlock(pos, block);
									if (id!=null)
										map.put(id, pos);
									break;
								}
							}
					}
				}
			for (final Entry<String, Collection<BlockPos>> entry : map.asMap().entrySet()) {
				//Block block = entry.getKey();
				final Collection<BlockPos> poses = entry.getValue();

				int count = 0;
				float x = 0, y = 0, z = 0;
				for (final BlockPos pos : poses)
					if (count++<=0) {
						x = pos.x;
						y = pos.y;
						z = pos.z;
					} else {
						x += pos.x;
						y += pos.y;
						z += pos.z;
					}

				if (count<=0)
					continue;

				x /= count;
				y /= count;
				z /= count;

				for (final BlockPos pos : poses) {
					glVertex3f(x+.5f, y+.5f, z+.5f);
					glVertex3f(pos.x+.5f, pos.y+.5f, pos.z+.5f);
				}
			}
		}
		GL11.glEnd();
		GL11.glEnable(GL_DEPTH_TEST);
		GL11.glDisable(GL_BLEND);
		GL11.glEnable(GL_TEXTURE_2D);
		GL11.glEndList();
	}

	private void renderBlock(final BlockPos pos, final VisualinkBlocks block) {
		final int x = pos.x;
		final int y = pos.y;
		final int z = pos.z;

		GL11.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 255);

		GL11.glVertex3f(x, y, z);
		GL11.glVertex3f(x+1, y, z);

		GL11.glVertex3f(x+1, y, z);
		GL11.glVertex3f(x+1, y, z+1);

		GL11.glVertex3f(x, y, z);
		GL11.glVertex3f(x, y, z+1);

		GL11.glVertex3f(x, y, z+1);
		GL11.glVertex3f(x+1, y, z+1);

		GL11.glVertex3f(x, y+1, z);
		GL11.glVertex3f(x+1, y+1, z);

		GL11.glVertex3f(x+1, y+1, z);
		GL11.glVertex3f(x+1, y+1, z+1);

		GL11.glVertex3f(x, y+1, z);
		GL11.glVertex3f(x, y+1, z+1);

		GL11.glVertex3f(x, y+1, z+1);
		GL11.glVertex3f(x+1, y+1, z+1);

		GL11.glVertex3f(x, y, z);
		GL11.glVertex3f(x, y+1, z);

		GL11.glVertex3f(x, y, z+1);
		GL11.glVertex3f(x, y+1, z+1);

		GL11.glVertex3f(x+1, y, z);
		GL11.glVertex3f(x+1, y+1, z);

		GL11.glVertex3f(x+1, y, z+1);
		GL11.glVertex3f(x+1, y+1, z+1);
	}

	@SubscribeEvent
	public void keyboardEvent(final InputEvent.KeyInputEvent key) {
		if (!(this.mc.currentScreen instanceof GuiScreen))
			if (this.toggleVisualinkBinding.isPressed()) {
				toggleVisualink = !toggleVisualink;
				//Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText("Link: "+(toggleXray ? "Visible" : "Hidden")), 1001419);
				if (toggleVisualink)
					cooldownTicks = 0;
				else
					GL11.glDeleteLists(Visualink.displayListid, 1);
			}
	}

	@SubscribeEvent
	public void renderWorldLastEvent(final RenderWorldLastEvent evt) {
		if (!toggleVisualink||this.mc.theWorld==null)
			return;
		final double doubleX = this.mc.thePlayer.lastTickPosX
				+(this.mc.thePlayer.posX-this.mc.thePlayer.lastTickPosX)*evt.partialTicks;

		final double doubleY = this.mc.thePlayer.lastTickPosY
				+(this.mc.thePlayer.posY-this.mc.thePlayer.lastTickPosY)*evt.partialTicks;

		final double doubleZ = this.mc.thePlayer.lastTickPosZ
				+(this.mc.thePlayer.posZ-this.mc.thePlayer.lastTickPosZ)*evt.partialTicks;

		GL11.glPushMatrix();
		GL11.glTranslated(-doubleX, -doubleY, -doubleZ);
		GL11.glCallList(Visualink.displayListid);
		GL11.glPopMatrix();
	}

	@SubscribeEvent
	public void onDraw(final RenderGameOverlayEvent.Post event) {
		if (event.type==ElementType.EXPERIENCE)
			if (toggleVisualink) {
				final FontRenderer font = this.mc.fontRenderer;

				glPushMatrix();

				final String str = "Visualink";
				font.drawStringWithShadow(str, this.mc.displayWidth/2-font.getStringWidth(str), 0, 0xffffff);

				glPopMatrix();
			}
	}
}
