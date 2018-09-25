package net.teamfruit.visualink.addons.enderstorage;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.teamfruit.visualink.Log;
import net.teamfruit.visualink.VisualinkBlocks;
import net.teamfruit.visualink.VisualinkItems;
import net.teamfruit.visualink.addons.IBlockAccessor;
import net.teamfruit.visualink.addons.IBlockIdentifierProvider;
import net.teamfruit.visualink.addons.IItemAccessor;
import net.teamfruit.visualink.addons.IItemIdentifierProvider;
import net.teamfruit.visualink.addons.IItemTooltipProvider;

public class EnderStorageModule {
	public static Class<?> TileFrequencyOwner = null;
	public static Field TileFrequencyOwner_Freq = null;
	public static Field TileFrequencyOwner_Owner = null;
	public static Class<?> TileEnderTank = null;
	public static Class<?> ItemEnderStorage = null;
	public static Method ItemEnderStorage_GetFreq = null;
	public static Method ItemEnderStorage_GetOwner = null;
	public static Class<?> ItemEnderPouch = null;
	public static Method ItemEnderPouch_GetOwner = null;
	public static Class<?> EnderStorageManager = null;
	public static Method GetColourFromFreq = null;

	public static final String[] colors = new String[] {
			I18n.format("visualink.hud.msg.white"),
			I18n.format("visualink.hud.msg.orange"),
			I18n.format("visualink.hud.msg.magenta"),
			I18n.format("visualink.hud.msg.lblue"),
			I18n.format("visualink.hud.msg.yellow"),
			I18n.format("visualink.hud.msg.lime"),
			I18n.format("visualink.hud.msg.pink"),
			I18n.format("visualink.hud.msg.gray"),
			I18n.format("visualink.hud.msg.lgray"),
			I18n.format("visualink.hud.msg.cyan"),
			I18n.format("visualink.hud.msg.purple"),
			I18n.format("visualink.hud.msg.blue"),
			I18n.format("visualink.hud.msg.brown"),
			I18n.format("visualink.hud.msg.green"),
			I18n.format("visualink.hud.msg.red"),
			I18n.format("visualink.hud.msg.black")
	};

	public static boolean installed = isInstalled();

	private static final boolean isInstalled() {
		try {
			Class.forName("codechicken.enderstorage.EnderStorage");
			Log.log.log(Level.INFO, "EnderStorage mod found.");
			return true;
		} catch (final ClassNotFoundException arg4) {
			Log.log.log(Level.INFO, "[EnderStorage] EnderStorage mod not found.");
		}
		return false;
	}

	public static void registerBlocks(final List<VisualinkBlocks> blocks) {
		if (!installed)
			return;

		try {
			TileFrequencyOwner = Class.forName("codechicken.enderstorage.common.TileFrequencyOwner");
			TileFrequencyOwner_Freq = TileFrequencyOwner.getField("freq");
			TileFrequencyOwner_Owner = TileFrequencyOwner.getField("owner");

			TileEnderTank = Class.forName("codechicken.enderstorage.storage.liquid.TileEnderTank");
		} catch (final ClassNotFoundException arg0) {
			Log.log.log(Level.WARN, "[EnderStorage] Class not found. "+arg0);
			return;
		} catch (final NoSuchFieldException arg2) {
			Log.log.log(Level.WARN, "[EnderStorage] Field not found."+arg2);
			return;
		} catch (final Exception arg3) {
			Log.log.log(Level.WARN, "[EnderStorage] Unhandled exception."+arg3);
			return;
		}

		blocks.add(new VisualinkBlocks("EnderStorage:enderChest", new IBlockIdentifierProvider() {
			@Override
			public @Nullable String provide(final @Nonnull IBlockAccessor accessor) {
				try {
					final TileEntity tile = accessor.getTileEntity();
					if (tile==null)
						return null;
					final int freq = TileFrequencyOwner_Freq.getInt(tile);
					final String owner = (String) TileFrequencyOwner_Owner.get(tile);
					final boolean isTank = TileEnderTank.isInstance(tile);
					return accessor.getBlockID()+"@"+(isTank ? "t" : "c")+freq+"@"+owner;
				} catch (final Exception arg8) {
					Log.log.error("[EnderStorage] Could not load ender storage contents: ", arg8);
				}
				return null;
			}
		}));
	}

	public static void registerItems(final List<VisualinkItems> items) {
		if (!installed)
			return;

		try {
			ItemEnderStorage = Class.forName("codechicken.enderstorage.common.ItemEnderStorage");
			ItemEnderStorage_GetFreq = ItemEnderStorage.getMethod("getFreq", ItemStack.class);
			ItemEnderStorage_GetOwner = ItemEnderStorage.getMethod("getOwner", ItemStack.class);

			ItemEnderPouch = Class.forName("codechicken.enderstorage.storage.item.ItemEnderPouch");
			ItemEnderPouch_GetOwner = ItemEnderPouch.getMethod("getOwner", ItemStack.class);

			EnderStorageManager = Class.forName("codechicken.enderstorage.api.EnderStorageManager");
			GetColourFromFreq = EnderStorageManager.getDeclaredMethod("getColourFromFreq", int.class, int.class);
		} catch (final ClassNotFoundException arg0) {
			Log.log.log(Level.WARN, "[EnderStorage] Class not found. "+arg0);
			return;
		} catch (final NoSuchMethodException arg2) {
			Log.log.log(Level.WARN, "[EnderStorage] Field not found."+arg2);
			return;
		} catch (final Exception arg3) {
			Log.log.log(Level.WARN, "[EnderStorage] Unhandled exception."+arg3);
			return;
		}

		items.add(new VisualinkItems("EnderStorage:enderChest", new IItemIdentifierProvider() {
			@Override
			public @Nullable String provide(final @Nonnull IItemAccessor accessor) {
				try {
					final ItemStack itemstack = accessor.getItemStack();
					if (itemstack==null)
						return null;
					final Item item = accessor.getItem();
					if (ItemEnderStorage.isInstance(item)) {
						final int freq = (Integer) ItemEnderStorage_GetFreq.invoke(item, itemstack);
						final String owner = (String) ItemEnderStorage_GetOwner.invoke(item, itemstack);
						final int metadata = item.getMetadata(itemstack.getItemDamage());
						final boolean isTank = metadata==1;
						return accessor.getItemID()+"@"+(isTank ? "t" : "c")+freq+"@"+owner;
					}
				} catch (final Exception arg8) {
					Log.log.error("[EnderStorage] Could not load ender storage contents: ", arg8);
				}
				return null;
			}
		}, new IItemTooltipProvider() {
			@Override
			public void provide(final IItemAccessor accessor, final List<String> tooltip) {
				try {
					final ItemStack itemstack = accessor.getItemStack();
					if (itemstack==null)
						return;
					final Item item = accessor.getItem();
					if (ItemEnderStorage.isInstance(item)) {
						final int freq = (Integer) ItemEnderStorage_GetFreq.invoke(item, itemstack);
						final int freqLeft = ((Integer) EnderStorageModule.GetColourFromFreq.invoke(null, freq, 0)).intValue();
						final int freqCenter = ((Integer) EnderStorageModule.GetColourFromFreq.invoke(null, freq, 1)).intValue();
						final int freqRight = ((Integer) EnderStorageModule.GetColourFromFreq.invoke(null, freq, 2)).intValue();

						final int metadata = item.getMetadata(itemstack.getItemDamage());
						final boolean isTank = metadata==1;
						if (!isTank)
							tooltip.add(String.format("%s/%s/%s", new Object[] { colors[freqLeft], colors[freqCenter], colors[freqRight] }));
						else
							tooltip.add(String.format("%s/%s/%s", new Object[] { colors[freqRight], colors[freqCenter], colors[freqLeft] }));
					}
				} catch (final Exception arg8) {
					Log.log.debug("[EnderStorage] Could not load ender storage color: ", arg8);
				}
			}
		}));

		items.add(new VisualinkItems("EnderStorage:enderPouch", new IItemIdentifierProvider() {
			@Override
			public @Nullable String provide(final @Nonnull IItemAccessor accessor) {
				try {
					final ItemStack itemstack = accessor.getItemStack();
					if (itemstack==null)
						return null;
					final Item item = accessor.getItem();
					if (ItemEnderPouch.isInstance(item)) {
						final int freq = itemstack.getItemDamage();
						final String owner = (String) ItemEnderPouch_GetOwner.invoke(item, itemstack);
						return "EnderStorage:enderChest"+"@"+"c"+freq+"@"+owner;
					}
				} catch (final Exception arg8) {
					Log.log.error("[EnderStorage] Could not load ender pouch contents: ", arg8);
				}
				return null;
			}
		}, new IItemTooltipProvider() {
			@Override
			public void provide(final IItemAccessor accessor, final List<String> tooltip) {
				try {
					final ItemStack itemstack = accessor.getItemStack();
					if (itemstack==null)
						return;
					final Item item = accessor.getItem();
					if (ItemEnderPouch.isInstance(item)) {
						final int freq = itemstack.getItemDamage();
						final int freqLeft = ((Integer) EnderStorageModule.GetColourFromFreq.invoke(null, freq, 0)).intValue();
						final int freqCenter = ((Integer) EnderStorageModule.GetColourFromFreq.invoke(null, freq, 1)).intValue();
						final int freqRight = ((Integer) EnderStorageModule.GetColourFromFreq.invoke(null, freq, 2)).intValue();

						tooltip.add(String.format("%s/%s/%s", new Object[] { colors[freqLeft], colors[freqCenter], colors[freqRight] }));
					}
				} catch (final Exception arg8) {
					Log.log.debug("[EnderStorage] Could not load ender pouch color: ", arg8);
				}
			}
		}));
	}
}
