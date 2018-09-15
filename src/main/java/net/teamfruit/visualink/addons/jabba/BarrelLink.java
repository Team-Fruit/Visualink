package net.teamfruit.visualink.addons.jabba;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.teamfruit.visualink.Log;

public class BarrelLink {
	public static final BarrelLink instance = new BarrelLink();

	public final HashMap<Integer, HashSet<Integer>> links;

	public BarrelLink(final HashMap<Integer, HashSet<Integer>> links) {
		this.links = links;
	}

	public BarrelLink() {
		this(new HashMap<Integer, HashSet<Integer>>());
	}

	public void readFromNBT(final NBTTagCompound nbt) {
		try {
			final Field field = JABBAModuleServer.BSpaceStorageHandler.getDeclaredField("links");
			field.setAccessible(true);

			if (nbt.hasKey("links")) {
				final NBTTagCompound tag = nbt.getCompoundTag("links");
				final Iterator<?> arg2 = tag.func_150296_c().iterator();
				while (arg2.hasNext()) {
					final Object key = arg2.next();
					this.links.put(Integer.valueOf((String) key), convertHashSet(tag.getIntArray((String) key)));
				}
			}
		} catch (final Exception e) {
			Log.log.error("Failed to load barrel link packet data", e);
		}
	}

	public void writeToNBT(final NBTTagCompound nbt) {
		try {
			final Field field = JABBAModuleServer.BSpaceStorageHandler.getDeclaredField("links");
			field.setAccessible(true);
			final Object instance = JABBAModuleServer.BSpaceStorageHandler_Instance.invoke(null);
			@SuppressWarnings("unchecked")
			final HashMap<Integer, HashSet<Integer>> links = (HashMap<Integer, HashSet<Integer>>) field.get(instance);

			final NBTTagCompound list2 = new NBTTagCompound();
			for (final Integer key1 : links.keySet())
				list2.setIntArray(String.valueOf(key1), convertInts(links.get(key1)));
			nbt.setTag("links", list2);
		} catch (final Exception e) {
			Log.log.error("Failed to create barrel link packet data", e);
		}
	}

	private int[] convertInts(final Set<Integer> integers) {
		final int[] ret = new int[integers.size()];
		final Iterator<Integer> iterator = integers.iterator();
		for (int i = 0; i<ret.length; ++i)
			ret[i] = iterator.next().intValue();
		return ret;
	}

	private HashSet<Integer> convertHashSet(final int[] list) {
		final HashSet<Integer> ret = new HashSet<Integer>();
		for (int i = 0; i<list.length; ++i)
			ret.add(Integer.valueOf(list[i]));
		return ret;
	}

	public static BarrelLink getLinks() {
		try {
			final Field field = JABBAModuleServer.BSpaceStorageHandler.getDeclaredField("links");
			field.setAccessible(true);
			@SuppressWarnings("unchecked")
			final HashMap<Integer, HashSet<Integer>> links = (HashMap<Integer, HashSet<Integer>>) field.get(JABBAModuleServer.BSpaceStorageHandler_Instance.invoke(null));
			return new BarrelLink(links);
		} catch (final Exception e) {
			Log.log.error("Failed to get barrel links: ", e);
		}
		return new BarrelLink();
	}
}
