package com.kamesuta.mc.tooltip;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class TooltipBlocks {
	public static List<TooltipBlocks> blocks = new ArrayList<TooltipBlocks>();
	public String id = "";
	public @Nullable IdentifierProvider provider;

	public TooltipBlocks() {
	}

	public TooltipBlocks(final String id, @Nullable final IdentifierProvider provider) {
		this.id = id;
		this.provider = provider;
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