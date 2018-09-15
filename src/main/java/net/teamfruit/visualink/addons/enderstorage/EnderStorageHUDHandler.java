package net.teamfruit.visualink.addons.enderstorage;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class EnderStorageHUDHandler implements IWailaDataProvider {
	@Override
	public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(final ItemStack itemStack, List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
		if (config.getConfig("enderstorage.colors"))
			try {
				final int e = EnderStorageModule.TileFrequencyOwner_Freq.getInt(accessor.getTileEntity());
				final String o = (String) EnderStorageModule.TileFrequencyOwner_Owner.get(accessor.getTileEntity());

				currenttip.add(String.format("Owner: %s", o));
			} catch (final Exception arg8) {
				currenttip = WailaExceptionHandler.handleErr(arg8, accessor.getTileEntity().getClass().getName(), currenttip);

			}

		return currenttip;
	}

	@Override
	public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(final EntityPlayerMP player, final TileEntity te, final NBTTagCompound tag, final World world, final int x, final int y, final int z) {
		if (te!=null)
			te.writeToNBT(tag);
		return tag;
	}
}
