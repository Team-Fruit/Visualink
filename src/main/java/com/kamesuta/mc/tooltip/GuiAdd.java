package com.kamesuta.mc.tooltip;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.RenderHelper;

public class GuiAdd extends GuiScreen {
	String id;
	GuiSlider colorR;
	GuiSlider colorG;
	GuiSlider colorB;
	GuiSlider colorA;
	private GuiButton add;
	private GuiButton cancel;
	private GuiButton matterMeta;
	private GuiButton isEnabled;
	private final int selectedIndex;
	Minecraft mc;
	private final int r;
	private final int g;
	private final int b;
	private final int a;
	private boolean enabled;
	private boolean bmeta;
	private int meta;
	private GuiTextField searchbar;

	public GuiAdd(final int r, final int g, final int b, final int a, final int meta, final String id, final boolean enabled, final int index) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		this.meta = meta;
		this.bmeta = (meta != -1);
		this.id = id;
		this.enabled = enabled;
		this.selectedIndex = index;
	}

	public GuiAdd() {
		this.selectedIndex = -1;

		this.r = 128;
		this.g = 128;
		this.b = 128;
		this.a = 255;
		this.enabled = true;
		this.bmeta = false;

		this.mc = Minecraft.getMinecraft();
	}

	public GuiAdd(final TooltipBlocks xrayBlocks, final int index) {
		this(xrayBlocks.r, xrayBlocks.g, xrayBlocks.b, xrayBlocks.a, xrayBlocks.meta, xrayBlocks.id, xrayBlocks.enabled,
				index);
	}

	@Override
	protected void actionPerformed(final GuiButton par1GuiButton) {
		if (par1GuiButton.id == 0) {
			if (this.selectedIndex != -1)
				TooltipBlocks.blocks.remove(this.selectedIndex);
			TooltipBlocks.blocks.add(new TooltipBlocks((int) (this.colorR.percent * 255.0F),
					(int) (this.colorG.percent * 255.0F), (int) (this.colorB.percent * 255.0F),
					(int) (this.colorA.percent * 255.0F), (this.bmeta) ? this.meta : -1, this.id, this.enabled));
			try {
				TooltipBlocks.save();
			} catch (final IOException e) {
				e.printStackTrace();
			}
			this.mc.displayGuiScreen(new GuiBlockList());
		} else if (par1GuiButton.id == 1) {
			this.mc.displayGuiScreen(new GuiBlockList());
		} else if (par1GuiButton.id == 6) {
			this.enabled = (!(this.enabled));
			this.isEnabled.displayString = ((this.enabled) ? "Enabled" : "Disabled");
		} else if (par1GuiButton.id == 7) {
			this.bmeta = (!(this.bmeta));
			this.matterMeta.displayString = ((this.bmeta) ? "Meta-Check Enabled" : "Meta-Check Disabled");
		}

		super.actionPerformed(par1GuiButton);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		this.add = new GuiButton(0, this.width / 2 - 42, this.height - 22, 40, 20, "Add");
		this.buttonList.add(this.add);
		this.cancel = new GuiButton(1, this.width / 2 + 42, this.height - 22, 40, 20, "Cancel");
		this.buttonList.add(this.cancel);
		this.colorR = new GuiSlider(2, this.width - 160, this.height / 10 * 5, "Red-Value", this.r / 255.0F);
		this.buttonList.add(this.colorR);
		this.colorG = new GuiSlider(3, this.width - 160, this.height / 10 * 6, "Green-Value", this.g / 255.0F);
		this.buttonList.add(this.colorG);
		this.colorB = new GuiSlider(4, this.width - 160, this.height / 10 * 7, "Blue-Value", this.b / 255.0F);
		this.buttonList.add(this.colorB);
		this.colorA = new GuiSlider(5, this.width - 160, this.height / 10 * 8, "Alpha-Value", this.a / 255.0F);
		this.buttonList.add(this.colorA);
		this.isEnabled = new GuiButton(6, this.width - 160, this.height / 10 * 4, 70, 20,
				(this.enabled) ? "Enabled" : "Disabled");
		this.buttonList.add(this.isEnabled);
		this.matterMeta = new GuiButton(7, this.width - 90, this.height / 10 * 4, 80, 20,
				(this.bmeta) ? "Meta-Check Enabled" : "Meta-Check Disabled");
		this.buttonList.add(this.matterMeta);
		Keyboard.enableRepeatEvents(true);
		this.searchbar = new GuiTextField(this.fontRendererObj, 60, 45, 120, this.fontRendererObj.FONT_HEIGHT);
		this.searchbar.setMaxStringLength(30);
		this.searchbar.setCanLoseFocus(false);
		this.searchbar.setFocused(true);
		this.searchbar.setTextColor(16777215);
	}

	@Override
	public void drawScreen(final int x, final int y, final float par3) {
		super.drawScreen(x, y, par3);
		drawBackground(0);
		drawString(this.fontRendererObj, "Search for Blocks by their name ", 5, 10, 16777215);

		drawString(this.fontRendererObj, "or their ID and meta using @ (e.g. @12:0 or @12:1) ", 7, 20, 16777215);

		String text = this.searchbar.getText();
		if (text.startsWith("@")) {
			try {
				text = text.substring(1);
				final String[] data = text.split(":");
				int meta = -1;
				if (data.length > 1) {
					meta = Integer.parseInt(data[1]);
				}
				if (data.length > 0) {
					final int id = Integer.parseInt(data[0]);
					if (Block.blockRegistry.containsId(id)) {
						this.id = Block.blockRegistry.getNameForObject(Block.blockRegistry.getObjectById(id));
						this.meta = meta;
						if (meta != 1)
							this.bmeta = true;
					}
				}
			} catch (final Exception e) {
			}

		}

		this.add.enabled = (this.id != null);
		drawInfo();
		this.searchbar.drawTextBox();
		super.drawScreen(x, y, par3);

		RenderHelper.enableGUIStandardItemLighting();
		drawRect(this.width / 3 * 2, this.height / 6, this.width - 30, this.height / 6 * 2,
				(((int) (this.colorA.percent * 255.0F) << 8 | (int) (this.colorR.percent * 255.0F)) << 8
						| (int) (this.colorG.percent * 255.0F)) << 8 | (int) (this.colorB.percent * 255.0F));
	}

	@Override
	public void updateScreen() {
		this.searchbar.updateCursorCounter();
	}

	private void drawInfo() {
		drawString(this.fontRendererObj, "Search", 15, 45, 16777215);

		drawString(this.fontRendererObj,
				(this.id == null) ? "No Block selected"
						: ((Block) Block.blockRegistry.getObject(this.id)).getLocalizedName(),
						this.width / 3 * 2 + 20, 20, 16777215);
	}

	@Override
	protected void keyTyped(final char par1, final int par2) {
		this.searchbar.textboxKeyTyped(par1, par2);
		super.keyTyped(par1, par2);
	}

	@Override
	protected void mouseClicked(final int x, final int y, final int mouseButton) {
		super.mouseClicked(x, y, mouseButton);
		this.searchbar.mouseClicked(x, y, mouseButton);
		if (mouseButton != 0)
			return;
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
	}
}