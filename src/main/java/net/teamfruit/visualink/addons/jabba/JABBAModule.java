package net.teamfruit.visualink.addons.jabba;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import net.minecraft.tileentity.TileEntity;
import net.teamfruit.visualink.Reference;
import net.teamfruit.visualink.VisualinkBlocks;
import net.teamfruit.visualink.addons.IBlockAccessor;
import net.teamfruit.visualink.addons.IBlockIdentifierProvider;

public class JABBAModule {
	public static Class<?> TileEntityBarrel = null;
	public static Field TileEntityBarrel_Id = null;

	public static void register(final List<VisualinkBlocks> blocks) {
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

		blocks.add(new VisualinkBlocks("JABBA:barrel", new IBlockIdentifierProvider() {
			@Override
			public @Nullable String provide(final @Nonnull IBlockAccessor accessor) {
				try {
					final TileEntity tile = accessor.getTileEntity();
					if (tile==null)
						return null;
					final int e = TileEntityBarrel_Id.getInt(tile);
					final HashSet<Integer> links = BarrelLink.instance.links.get(e);
					if (links!=null) {
						int g = e;
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
}
