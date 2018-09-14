package net.teamfruit.visualink.jabba;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import mcp.mobius.betterbarrels.bspace.BSpaceStorageHandler;
import net.minecraft.tileentity.TileEntity;
import net.teamfruit.visualink.IAccessor;
import net.teamfruit.visualink.IdentifierProvider;
import net.teamfruit.visualink.Reference;
import net.teamfruit.visualink.TooltipBlocks;

public class JABBAModule {
	public static Class<?> TileEntityBarrel = null;
	public static Field TileEntityBarrel_Id = null;

	public static void register(final List<TooltipBlocks> blocks) {
		try {
			Class.forName("mcp.mobius.betterbarrels.BetterBarrels");
			Reference.logger.log(Level.INFO, "JABBA mod found.");
		} catch (final ClassNotFoundException arg4) {
			Reference.logger.log(Level.INFO, "[JABBA] JABBA mod not found.");
			return;
		}

		try {
			TileEntityBarrel = Class.forName("mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel");
			TileEntityBarrel_Id = TileEntityBarrel.getField("id");
		} catch (final ClassNotFoundException arg0) {
			Reference.logger.log(Level.WARN, "[JABBA] Class not found. "+arg0);
			return;
		} catch (final NoSuchFieldException arg2) {
			Reference.logger.log(Level.WARN, "[JABBA] Field not found."+arg2);
			return;
		} catch (final Exception arg3) {
			Reference.logger.log(Level.WARN, "[JABBA] Unhandled exception."+arg3);
			return;
		}

		blocks.add(new TooltipBlocks("JABBA:barrel", new IdentifierProvider() {
			@Override
			public @Nullable String provide(final @Nonnull IAccessor accessor) {
				try {
					try {
						final Field field = BSpaceStorageHandler.class.getDeclaredField("links");
						field.setAccessible(true);
						@SuppressWarnings("unchecked")
						final HashMap<Integer, HashSet<Integer>> links = (HashMap<Integer, HashSet<Integer>>) field.get(BSpaceStorageHandler.instance());
						Reference.logger.info("server: "+links);
					} catch (final Exception e) {
						e.printStackTrace();
					}
					final TileEntity tile = accessor.getTileEntity();
					if (tile==null)
						return null;
					final int e = TileEntityBarrel_Id.getInt(tile);
					return accessor.getBlockID()+"@"+e;
				} catch (final Exception arg8) {
					return null;
				}
			}
		}));
	}
}
