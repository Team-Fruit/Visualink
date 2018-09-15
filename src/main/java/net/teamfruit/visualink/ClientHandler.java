package net.teamfruit.visualink;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.teamfruit.visualink.addons.IBlockAccessor;
import net.teamfruit.visualink.addons.IBlockIdentifierProvider;
import net.teamfruit.visualink.addons.IItemAccessor;
import net.teamfruit.visualink.addons.IItemIdentifierProvider;
import net.teamfruit.visualink.addons.enderstorage.EnderStorageHUDHandler;
import net.teamfruit.visualink.addons.enderstorage.EnderStorageModule;
import net.teamfruit.visualink.addons.jabba.BarrelLink;

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
			BlockManager.getInstance().saveIfChanged();
			cooldownTicks = 80;
		}
		cooldownTicks -= 1;
		return true;
	}

	public String getItemId(final ItemStack itemStack) {
		String itemId = null;
		if (itemStack!=null) {
			final Item item = itemStack.getItem();
			for (final VisualinkItems visualinkitem : VisualinkItems.items) {
				final Item itemi = visualinkitem.getItem();
				if (
					itemi==item
				) {
					final IItemIdentifierProvider provider = visualinkitem.provider;
					itemId = provider==null ? visualinkitem.id : provider.provide(new IItemAccessor() {
						@Override
						public ItemStack getItemStack() {
							return itemStack;
						}

						@Override
						public String getItemID() {
							return visualinkitem.id;
						}

						@Override
						public Item getItem() {
							return item;
						}
					});

					break;
				}
			}
		}
		return itemId;
	}

	@SubscribeEvent
	public void onTooltip(final @Nonnull ItemTooltipEvent event) {
		if (toggleVisualink) {
			final ItemStack handItemStack = event.itemStack;
			final String handItemId = getItemId(handItemStack);
			if (handItemStack!=null&&handItemId!=null)
				if (event.itemStack.getItem()==handItemStack.getItem()) {
					int count = 0;
					final Map<Integer, Integer> dimCount = Maps.newHashMap();
					for (final Entry<BlockPos, Pair<Block, String>> entry : BlockManager.getInstance().getBlocks().entrySet()) {
						final BlockPos pos = entry.getKey();
						final Pair<Block, String> pair = entry.getValue();
						if (StringUtils.equals(handItemId, pair.getValue())) {
							count++;
							final Integer dimCountInt = dimCount.get(pos.dim);
							dimCount.put(pos.dim, (dimCountInt!=null ? dimCountInt : 0)+1);
						}
					}
					if (count>0) {
						final List<String> tooltip = event.toolTip;
						tooltip.add(I18n.format("visualink.tooltip.connected", count));
						final EntityClientPlayerMP player = this.mc.thePlayer;
						if (player!=null) {
							final int playerDim = player.dimension;
							for (final Entry<Integer, Integer> entrydim : dimCount.entrySet()) {
								final int dim = entrydim.getKey();
								final int countdim = entrydim.getValue();
								if (playerDim==dim)
									tooltip.add(I18n.format("visualink.tooltip.connected.dim.player", dim, countdim));
								else
									tooltip.add(I18n.format("visualink.tooltip.connected.dim", dim, countdim));
							}
						}
					}
				}
		}
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
			final ItemStack handItemStack = player.getHeldItem();
			final String handItemId = getItemId(handItemStack);

			final Multimap<String, BlockPos> map = ArrayListMultimap.create();
			for (final Iterator<Entry<BlockPos, Pair<Block, String>>> itr = BlockManager.getInstance().getBlocks().entrySet().iterator(); itr.hasNext();) {
				final Entry<BlockPos, Pair<Block, String>> entry = itr.next();
				final BlockPos pos = entry.getKey();
				if (world.provider.dimensionId!=pos.dim)
					continue;
				final Block worldblock = world.getBlock(pos.x, pos.y, pos.z);
				final Pair<Block, String> blockdata = entry.getValue();
				final boolean chunkexists = world.getChunkFromBlockCoords(pos.x, pos.z).isChunkLoaded;
				if (chunkexists)
					b: {
						for (final VisualinkBlocks visualinkblock : VisualinkBlocks.blocks) {
							final Block candidateblock = visualinkblock.getBlock();
							if (
								candidateblock==worldblock
							) {
								final IBlockIdentifierProvider provider = visualinkblock.provider;
								blockdata.setValue(provider==null ? visualinkblock.id : provider.provide(new IBlockAccessor() {
									@Override
									public TileEntity getTileEntity() {
										if (candidateblock.hasTileEntity(world.getBlockMetadata(pos.x, pos.y, pos.z)))
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
										return visualinkblock.id;
									}

									@Override
									public Block getBlock() {
										return candidateblock;
									}
								}));
								if (blockdata.getValue()!=null) {
									map.put(blockdata.getValue(), pos);
									if (StringUtils.equals(handItemId, blockdata.getValue()))
										renderBlock(pos, visualinkblock);
								}
								break b;
							}
						}
						itr.remove();
					}
				else if (blockdata.getValue()!=null)
					map.put(blockdata.getValue(), pos);
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

				int cx = (int) x, cy = (int) y, cz = (int) z;

				x /= count;
				y /= count;
				z /= count;

				for (final BlockPos pos : poses) {
					final float length1 = (cx-x)*(cx-x)+(cy-y)*(cy-y)+(cz-z)*(cz-z);
					final float length2 = (pos.x-x)*(pos.x-x)+(pos.y-y)*(pos.y-y)+(pos.z-z)*(pos.z-z);
					if (length2<=length1) {
						cx = pos.x;
						cy = pos.y;
						cz = pos.z;
					}
				}

				for (final BlockPos pos : poses) {
					GL11.glColor4ub((byte) 0, (byte) 0, (byte) 255, (byte) 255);
					glVertex3f(cx+.5f, cy+.5f, cz+.5f);
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

	@SubscribeEvent
	public void invoke(final Unload event) {
		if (FMLCommonHandler.instance().getEffectiveSide()==Side.CLIENT) {
			BarrelLink.instance.links.clear();
			BlockManager.getInstance().dispose();
		}
	}

	public static void callbackRegister(final IWailaRegistrar registrar) {
		EnderStorageModule.registerWaila(registrar);
	}
}
