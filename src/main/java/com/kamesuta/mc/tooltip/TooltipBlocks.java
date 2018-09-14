package com.kamesuta.mc.tooltip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;

public class TooltipBlocks {
	public static ArrayList<TooltipBlocks> blocks = new ArrayList<TooltipBlocks>();
	public int r;
	public int g;
	public int b;
	public int a;
	public int meta;
	public String id = "";
	public boolean enabled = true;

	public TooltipBlocks() {
	}

	public TooltipBlocks(final int r, final int g, final int b, final int a, final int meta, final String id, final boolean enabled) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		this.id = id;
		this.meta = meta;
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return this.r+" "+this.g+" "+this.b+" "+this.a+" "+this.meta+" "+this.id+" "
				+this.enabled;
	}

	public static TooltipBlocks fromString(final String s) {
		final TooltipBlocks result = new TooltipBlocks();
		final String[] info = s.split(" ");
		result.r = Integer.parseInt(info[0]);
		result.g = Integer.parseInt(info[1]);
		result.b = Integer.parseInt(info[2]);
		result.a = Integer.parseInt(info[3]);
		result.meta = Integer.parseInt(info[4]);
		result.id = info[5];
		result.enabled = Boolean.parseBoolean(info[6]);
		return result;
	}

	public static void setStandardList() {
		final ArrayList<TooltipBlocks> block = new ArrayList<TooltipBlocks>();
		block.add(new TooltipBlocks(0, 0, 128, 200, -1, "minecraft:lapis_ore", true));
		block.add(new TooltipBlocks(255, 0, 0, 200, -1, "minecraft:redstone_ore", true));
		block.add(new TooltipBlocks(255, 255, 0, 200, -1, "minecraft:gold_ore", true));
		block.add(new TooltipBlocks(0, 255, 0, 200, -1, "minecraft:emerald_ore", true));
		block.add(new TooltipBlocks(0, 191, 255, 200, -1, "minecraft:diamond_ore", true));

		blocks = block;
		try {
			save();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static void removeInvalidBlocks() {
		for (int i = 0; i<blocks.size(); ++i) {
			final TooltipBlocks block = blocks.get(i);
			if (Block.blockRegistry.containsKey(block.id))
				continue;
			blocks.remove(block);
		}
	}

	public static void init() {
		try {
			load();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		removeInvalidBlocks();
		if (blocks.size()!=0)
			return;
		setStandardList();
	}

	private static void load() throws Exception {
		final File toLoad = new File(Minecraft.getMinecraft().mcDataDir, "xrayBlocks.dat");
		if (toLoad.exists()&&!toLoad.isDirectory()) {
			final ArrayList<TooltipBlocks> block = new ArrayList<TooltipBlocks>();
			final BufferedReader br = new BufferedReader(new FileReader(toLoad));
			String s;
			for (; (s = br.readLine())!=null; block.add(fromString(s)))
				;
			br.close();
			blocks = block;
		}
	}

	static void save() throws IOException {
		final File toSave = new File(Minecraft.getMinecraft().mcDataDir, "xrayBlocks.dat");
		if (toSave.exists())
			toSave.delete();
		final BufferedWriter bw = new BufferedWriter(new FileWriter(toSave));
		for (int i = 0; i<blocks.size(); ++i) {
			bw.write(blocks.get(i).toString());
			bw.newLine();
		}
		bw.flush();
		bw.close();
	}
}