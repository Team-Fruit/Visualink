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

import org.apache.commons.lang3.StringUtils;
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
import net.minecraft.network.NetworkManager;

public class BlockManager {
	private static final BlockManager instance = new BlockManager();

	public static BlockManager getInstance() {
		if (instance.map==null) {
			instance.map = Maps.newHashMap();
			instance.loadFromFile();
		}
		return instance;
	}

	public void dispose() {
		this.map = null;
	}

	private Map<BlockPos, Pair<Block, String>> map;

	private BlockManager() {
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

	public static String getLegacyServerName() {
		try {
			final NetworkManager netManager = FMLClientHandler.instance().getClientToServerNetworkManager();
			if (netManager!=null) {
				final SocketAddress socketAddress = netManager.getSocketAddress();
				if (socketAddress!=null&&socketAddress instanceof InetSocketAddress) {
					final InetSocketAddress inetAddr = (InetSocketAddress) socketAddress;
					return inetAddr.getHostName();
				}
			}
		} catch (final Throwable t) {
			Log.log.error("Couldn't get server name: ", t);
		}
		return "server";
	}

	public static String getWorldName(final Minecraft mc) {
		String worldName = null;
		if (mc.isSingleplayer())
			return mc.getIntegratedServer().getFolderName();

		worldName = mc.theWorld.getWorldInfo().getWorldName();
		final String serverName = getLegacyServerName();

		if (serverName==null)
			return "offline";

		if (!"MpServer".equals(worldName))
			worldName = serverName+"_"+worldName;
		else
			worldName = serverName;

		worldName = worldName.trim();

		if (StringUtils.isEmpty(worldName.trim()))
			worldName = "unnamed";

		return worldName;
	}

	private static File getSaveDir() {
		final Minecraft mc = Minecraft.getMinecraft();
		final File mcDir = mc.mcDataDir;
		final File visualinkDir = new File(mcDir, "visualink");
		final File cacheDir = new File(visualinkDir, "caches");
		final File serverDir = new File(cacheDir, getWorldName(mc));
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
			Log.log.info("Visualink cache directory missing. Skipping saving state.");
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
			Log.log.error("Visualink cache data files have been corrupted.");
		} catch (final IOException e) {
			Log.log.error("Visualink cache data files could not been loaded.", e);
		}
	}
}
