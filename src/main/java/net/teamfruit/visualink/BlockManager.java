package net.teamfruit.visualink;

import java.util.Map;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;

public class BlockManager {
	public static final BlockManager instance = new BlockManager();

	private final Map<BlockPos, Pair<Block, String>> map = Maps.newHashMap();

	private BlockManager() {
	}

	public void addBlock(final BlockPos pos, final Block block, final String id) {
		this.map.put(pos, MutablePair.of(block, id));
	}

	public Map<BlockPos, Pair<Block, String>> getBlocks() {
		return this.map;
	}
}
