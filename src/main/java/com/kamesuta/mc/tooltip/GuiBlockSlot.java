package com.kamesuta.mc.tooltip;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;

public class GuiBlockSlot extends GuiSlot {
	int selectedIndex = -1;
	GuiBlockList xrayGui;

	public GuiBlockSlot(final Minecraft par1Minecraft, final int width, final int height, final int top, final int bottom, final int slotHeight,
			final GuiBlockList xrayGui) {
		super(par1Minecraft, width, height, top, bottom, slotHeight);
		this.xrayGui = xrayGui;

		TooltipBlocks.init();
	}

	@Override
	protected int getSize() {
		return TooltipBlocks.blocks.size();
	}

	@Override
	protected boolean isSelected(final int i) {
		return (i == this.selectedIndex);
	}

	@Override
	protected void drawBackground() {
	}

	@Override
	protected void elementClicked(final int i, final boolean var2, final int var3, final int var4) {
		this.selectedIndex = i;
	}

	@Override
	protected void drawSlot(final int i, final int j, final int k, final int var4, final Tessellator var5, final int var6, final int var7) {
		final TooltipBlocks xblock = TooltipBlocks.blocks.get(i);
		Gui.drawRect(175 + j, 1 + k, this.xrayGui.width - j - 20, 15 + k,
				((0xC800 | xblock.r) << 8 | xblock.g) << 8 | xblock.b);
		if ((xblock.id == null) || (!(Block.blockRegistry.containsKey(xblock.id))))
			return;
		this.xrayGui.drawString(this.xrayGui.render,
				((Block) Block.blockRegistry.getObject(xblock.id)).getLocalizedName(), j + 2, k + 1, 16777215);
	}
}