package net.teamfruit.visualink.addons.jabba;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.teamfruit.visualink.Reference;
import net.teamfruit.visualink.VisualinkBlocks;
import net.teamfruit.visualink.VisualinkItems;
import net.teamfruit.visualink.addons.IBlockAccessor;
import net.teamfruit.visualink.addons.IBlockIdentifierProvider;
import net.teamfruit.visualink.addons.IItemAccessor;
import net.teamfruit.visualink.addons.IItemIdentifierProvider;

public class JABBAModule {
	public static Class<?> TileEntityBarrel = null;
	public static Field TileEntityBarrel_Id = null;
	public static Class<?> BSpaceStorageHandler = null;
	public static Method BSpaceStorageHandler_Instance = null;

	private static boolean installed = isInstalled();

	private static final boolean isInstalled() {
		try {
			Class.forName("mcp.mobius.betterbarrels.BetterBarrels");
			Reference.logger.log(Level.INFO, "JABBA mod found.");
			return true;
		} catch (final ClassNotFoundException arg4) {
			Reference.logger.log(Level.INFO, "[JABBA] JABBA mod not found.");
		}
		return false;
	}

	public static void register(final List<VisualinkBlocks> blocks) {
		if (!installed)
			return;

		try {
			TileEntityBarrel = Class.forName("mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel");
			TileEntityBarrel_Id = TileEntityBarrel.getField("id");

			BSpaceStorageHandler = Class.forName("mcp.mobius.betterbarrels.bspace.BSpaceStorageHandler");
			BSpaceStorageHandler_Instance = BSpaceStorageHandler.getMethod("instance");
		} catch (final ClassNotFoundException arg0) {
			Reference.logger.log(Level.WARN, "[JABBA] Class not found. "+arg0);
			return;
		} catch (final NoSuchMethodException arg1) {
			Reference.logger.log(Level.WARN, "[JABBA] Method not found."+arg1);
			return;
		} catch (final NoSuchFieldException arg2) {
			Reference.logger.log(Level.WARN, "[JABBA] Field not found."+arg2);
			return;
		} catch (final Exception arg3) {
			Reference.logger.log(Level.WARN, "[JABBA] Unhandled exception."+arg3);
			return;
		}

		blocks.add(new VisualinkBlocks("JABBA:barrel", new IBlockIdentifierProvider() {
			@Override
			public @Nullable String provide(final @Nonnull IBlockAccessor accessor) {
				try {
					final TileEntity tile = accessor.getTileEntity();
					if (tile==null)
						return null;
					final int id = TileEntityBarrel_Id.getInt(tile);
					final HashSet<Integer> links = BarrelLink.instance.links.get(id);
					if (links!=null) {
						int g = id;
						for (final int link : links)
							g = Math.min(g, link);
						return accessor.getBlockID()+"@"+g;
					}
				} catch (final Exception arg8) {
				}
				return null;
			}
		}));
	}

	public static void registerItems(final List<VisualinkItems> items) {
		if (!installed)
			return;

		final IItemIdentifierProvider provider = new IItemIdentifierProvider() {
			@Override
			public @Nullable String provide(final @Nonnull IItemAccessor accessor) {
				try {
					final ItemStack itemstack = accessor.getItemStack();
					if (itemstack==null)
						return null;
					final NBTTagCompound nbtRoot = itemstack.getTagCompound();
					if (nbtRoot!=null) {
						final NBTTagCompound nbtContainer = nbtRoot.getCompoundTag("Container");
						if (nbtContainer!=null&&StringUtils.equals(nbtContainer.getString("TEClass"), TileEntityBarrel.getName())) {
							final NBTTagCompound nbtTile = nbtContainer.getCompoundTag("NBT");
							final String block = nbtContainer.getString("Block");
							if (nbtTile!=null&&block!=null) {
								final int id = nbtTile.getInteger("bspaceid");
								final HashSet<Integer> links = BarrelLink.instance.links.get(id);
								if (links!=null) {
									int g = id;
									for (final int link : links)
										g = Math.min(g, link);
									return block+"@"+g;
								}
							}
						}
					}
				} catch (final Exception arg8) {
				}
				return null;
			}
		};
		items.add(new VisualinkItems("JABBA:mover", provider));
		items.add(new VisualinkItems("JABBA:moverDiamond", provider));
	}

	public static void registerWaila(final IWailaRegistrar registrar) {
		registrar.addConfig("JABBA", "jabba.connections");
		final JABBAHUDHandler handler = new JABBAHUDHandler();
		registrar.registerBodyProvider(handler, TileEntityBarrel);
		registrar.registerNBTProvider(handler, TileEntityBarrel);
	}
}
