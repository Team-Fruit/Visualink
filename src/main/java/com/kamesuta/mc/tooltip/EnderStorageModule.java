package com.kamesuta.mc.tooltip;

import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import net.minecraft.tileentity.TileEntity;

public class EnderStorageModule {
	public static Class<?> TileFrequencyOwner = null;
	public static Field TileFrequencyOwner_Freq = null;
	public static Class<?> TileEnderTank = null;

	public static void register(final List<TooltipBlocks> blocks) {
		try {
			Class.forName("codechicken.enderstorage.EnderStorage");
			Reference.logger.log(Level.INFO, "EnderStorage mod found.");
		} catch (final ClassNotFoundException arg4) {
			Reference.logger.log(Level.INFO, "[EnderStorage] EnderStorage mod not found.");
			return;
		}

		try {
			TileFrequencyOwner = Class.forName("codechicken.enderstorage.common.TileFrequencyOwner");
			TileFrequencyOwner_Freq = TileFrequencyOwner.getField("freq");

			TileEnderTank = Class.forName("codechicken.enderstorage.storage.liquid.TileEnderTank");
		} catch (final ClassNotFoundException arg0) {
			Reference.logger.log(Level.WARN, "[EnderStorage] Class not found. "+arg0);
			return;
		} catch (final NoSuchFieldException arg2) {
			Reference.logger.log(Level.WARN, "[EnderStorage] Field not found."+arg2);
			return;
		} catch (final Exception arg3) {
			Reference.logger.log(Level.WARN, "[EnderStorage] Unhandled exception."+arg3);
			return;
		}

		blocks.add(new TooltipBlocks("EnderStorage:enderChest", new IdentifierProvider() {
			@Override
			public @Nullable String provide(final @Nonnull IAccessor accessor) {
				try {
					final TileEntity tile = accessor.getTileEntity();
					if (tile==null)
						return null;
					final int e = TileFrequencyOwner_Freq.getInt(tile);
					final boolean b = TileEnderTank.isInstance(tile);
					return accessor.getBlockID()+"@"+(b ? "t" : "c")+e;
				} catch (final Exception arg8) {
					return null;
				}
			}
		}));
	}
}
