package net.teamfruit.visualink;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipException;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Maps;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class BlockManager {
	public static final BlockManager instance = new BlockManager();

	private final Map<BlockPos, Pair<Block, String>> map = Maps.newHashMap();

	private BlockManager() {
		loadFromFile();
	}

	public void addBlock(final BlockPos pos, final Block block, final @Nullable String id) {
		this.map.put(pos, MutablePair.of(block, id));
	}

	private int lastHash = -1;

	public void saveIfChanged() {
		final int hashcode = this.map.hashCode();
		if (this.lastHash!=hashcode) {
			writeToFile();
			this.lastHash = hashcode;
		}
	}

	public Map<BlockPos, Pair<Block, String>> getBlocks() {
		return this.map;
	}

	private static String getServerIP() {
		final SocketAddress address = FMLClientHandler.instance().getClientToServerNetworkManager().getSocketAddress();
		if (address!=null&&address instanceof InetSocketAddress) {
			final InetSocketAddress inetAddr = (InetSocketAddress) address;
			return inetAddr.toString();
		}
		return "unknown";
	}

	private static File getSaveDir() {
		final File mcDir = Minecraft.getMinecraft().mcDataDir;
		final File visualinkDir = new File(mcDir, "visualink");
		final File cacheDir = new File(visualinkDir, "caches");
		final File serverDir = new File(cacheDir, getServerIP());
		return serverDir;
	}

	private void writeToNBT(final NBTTagCompound nbt) {
		final NBTTagCompound links = new NBTTagCompound();
		for (final Iterator<Entry<BlockPos, Pair<Block, String>>> itr = this.map.entrySet().iterator(); itr.hasNext();) {
			final Entry<BlockPos, Pair<Block, String>> entry = itr.next();
			final String posstr = entry.getKey().toString();
			if (posstr!=null) {
				final Pair<Block, String> blockdata = entry.getValue();
				final String id = blockdata.getValue();
				if (id!=null) {
					final NBTTagCompound pair = new NBTTagCompound();
					pair.setInteger("block", Block.getIdFromBlock(blockdata.getKey()));
					pair.setString("id", id);
					links.setTag(posstr, pair);
				}
			}
		}
		nbt.setTag("links", links);

	}

	private void readFromNBT(final NBTTagCompound nbt) {
		if (nbt.hasKey("links")) {
			this.map.clear();
			final NBTTagCompound links = nbt.getCompoundTag("links");
			for (final Iterator<?> itr = links.func_150296_c().iterator(); itr.hasNext();) {
				final String keystr = (String) itr.next();
				final BlockPos pos = BlockPos.fromString(keystr);
				if (pos!=null) {
					final NBTTagCompound pair = links.getCompoundTag(keystr);
					final Block block = (Block) Block.blockRegistry.getObjectById(pair.getInteger("block"));
					if (block!=null) {
						final String id = pair.getString("id");
						this.map.put(pos, MutablePair.of(block, id));
					}
				}
			}
		}
	}

	private File saveDir;
	private NBTTagCompound saveTag;
	private File saveFile;

	public void writeToFile() {
		if (FMLCommonHandler.instance().getEffectiveSide()!=Side.CLIENT)
			return;

		if (this.saveFile==null||this.saveTag==null)
			return;

		try {
			writeToNBT(this.saveTag);

			final File e = this.saveFile;
			if (!e.exists())
				e.createNewFile();
			final DataOutputStream dout = new DataOutputStream(new FileOutputStream(e));
			CompressedStreamTools.writeCompressed(this.saveTag, dout);
			dout.close();
		} catch (final Exception e) {
			Reference.logger.info("Visualink cache directory missing. Skipping saving state.");
		}
	}

	public void loadFromFile() {
		if (FMLCommonHandler.instance().getEffectiveSide()!=Side.CLIENT)
			return;

		System.out.printf("Attemping to load Visualink data.\n", new Object[0]);

		this.saveDir = getSaveDir();

		try {
			if (!this.saveDir.exists())
				this.saveDir.mkdirs();

			this.saveFile = new File(this.saveDir, "links.dat");

			boolean e = false;

			if (this.saveFile.exists()&&this.saveFile.length()>0L) {
				final DataInputStream e1 = new DataInputStream(new FileInputStream(this.saveFile));
				this.saveTag = CompressedStreamTools.readCompressed(e1);
				e1.close();
				e = true;
			}

			if (!e)
				this.saveTag = new NBTTagCompound();

			readFromNBT(this.saveTag);
		} catch (final ZipException e) {
			Reference.logger.error("Visualink cache data files have been corrupted.");
		} catch (final IOException e) {
			Reference.logger.error("Visualink cache data files could not been loaded.", e);
		}
	}
}
