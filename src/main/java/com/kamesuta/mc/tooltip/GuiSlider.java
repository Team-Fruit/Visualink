package com.kamesuta.mc.tooltip;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiSlider extends GuiButton {
	public float percent;
	public boolean isClicked;

	public GuiSlider(final int id, final int x, final int y, final String name, final float percentage) {
		super(id, x, y, 150, 20, name);
		this.percent = percentage;
	}

	@Override
	public int getHoverState(final boolean p_146114_1_) {
		return 0;
	}

	@Override
	protected void mouseDragged(final Minecraft p_146119_1_, final int p_146119_2_, final int p_146119_3_) {
		if (!(this.visible))
			return;
		if (this.isClicked) {
			this.percent = ((p_146119_2_ - (this.xPosition + 4)) / (this.width - 8));

			if (this.percent < 0.0F) {
				this.percent = 0.0F;
			}

			if (this.percent > 1.0F) {
				this.percent = 1.0F;
			}

		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawTexturedModalRect(this.xPosition + (int) (this.percent * (this.width - 8)), this.yPosition, 0, 66, 4, 20);
		drawTexturedModalRect(this.xPosition + (int) (this.percent * (this.width - 8)) + 4, this.yPosition, 196, 66, 4,
				20);
	}

	@Override
	public boolean mousePressed(final Minecraft p_146116_1_, final int x, final int y) {
		if (super.mousePressed(p_146116_1_, x, y)) {
			this.percent = ((x - (this.xPosition + 4)) / (this.width - 8));

			if (this.percent < 0.0F) {
				this.percent = 0.0F;
			}

			if (this.percent > 1.0F) {
				this.percent = 1.0F;
			}

			this.isClicked = true;
			return true;
		}

		return false;
	}

	@Override
	public void mouseReleased(final int x, final int y) {
		this.isClicked = false;
	}
}