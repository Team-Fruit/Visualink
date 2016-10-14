package com.kamesuta.mc.tooltip;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiBlockList extends GuiScreen {
	GuiBlockSlot slot;
	GuiButton add;
	GuiButton del;
	GuiButton edit;
	GuiButton exit;
	FontRenderer render;

	public static void show() {
		Minecraft.getMinecraft().displayGuiScreen(new GuiBlockList());
	}

	public static void close() {
		Minecraft.getMinecraft().displayGuiScreen(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		this.render = this.fontRendererObj;
		this.slot = new GuiBlockSlot(Minecraft.getMinecraft(), this.width, this.height, 25, this.height - 25, 20,
				this);
		this.add = new GuiButton(0, this.width / 9, this.height - 22, 70, 20, "Add Block");
		this.del = new GuiButton(1, this.width / 9 * 3, this.height - 22, 70, 20, "Delete Block");

		this.del.enabled = false;
		this.edit = new GuiButton(2, this.width / 9 * 5, this.height - 22, 70, 20, "Edit Block");

		this.edit.enabled = false;
		this.exit = new GuiButton(3, this.width / 9 * 7, this.height - 22, 70, 20, "Exit");
		this.buttonList.add(this.add);
		this.buttonList.add(this.del);
		this.buttonList.add(this.edit);
		this.buttonList.add(this.exit);
	}

	@Override
	public void drawScreen(final int par1, final int par2, final float par3) {
		this.slot.drawScreen(par1, par2, par3);
		super.drawScreen(par1, par2, par3);
		if (this.slot.selectedIndex != -1) {
			this.del.enabled = true;
			this.edit.enabled = true;
		} else {
			this.del.enabled = false;
			this.edit.enabled = false;
		}
	}

	@Override
	protected void actionPerformed(final GuiButton par1GuiButton) {
		switch (par1GuiButton.id) {
		case 0:
			Minecraft.getMinecraft().displayGuiScreen(new GuiAdd());
			break;
		case 1:
			TooltipBlocks.blocks.remove(this.slot.selectedIndex);
			this.slot.selectedIndex = -1;
			try {
				TooltipBlocks.save();
			} catch (final IOException e) {
				e.printStackTrace();
			}
			break;
		case 2:
			Minecraft.getMinecraft().displayGuiScreen(new GuiAdd(
					TooltipBlocks.blocks.get(this.slot.selectedIndex), this.slot.selectedIndex));
			break;
		case 3:
			Minecraft.getMinecraft().displayGuiScreen(null);
			Tooltip.cooldownTicks = 0;
			break;
		default:
			this.slot.actionPerformed(par1GuiButton);
		}
	}
}