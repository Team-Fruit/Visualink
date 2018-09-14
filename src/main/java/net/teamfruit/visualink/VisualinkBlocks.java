package net.teamfruit.visualink;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.teamfruit.visualink.addons.IdentifierProvider;
import net.teamfruit.visualink.addons.enderstorage.EnderStorageModule;
import net.teamfruit.visualink.addons.jabba.JABBAModule;

public class VisualinkBlocks {
	public static List<VisualinkBlocks> blocks = new ArrayList<VisualinkBlocks>();
	public final String id;
	public final @Nullable IdentifierProvider provider;
	private final AtomicReference<Object> block = new AtomicReference<Object>();

	public VisualinkBlocks(final String id, @Nullable final IdentifierProvider provider) {
		this.id = id;
		this.provider = provider;
	}

	public Block getBlock() {
		Object value = this.block.get();
		if (value==null)
			synchronized (this.block) {
				value = this.block.get();
				if (value==null) {
					final Object actualValue = Block.blockRegistry.getObject(this.id);
					value = actualValue==null ? this.block : actualValue;
					this.block.set(value);
				}
			}
		return (Block) (value==this.block ? null : value);
	}

	@Override
	public String toString() {
		return String.format("TooltipBlocks [id=%s, provider=%s]", this.id, this.provider);
	}

	public static void setStandardList() {
		EnderStorageModule.register(blocks);
		JABBAModule.register(blocks);
	}

	public static void init() {
		setStandardList();
	}
}