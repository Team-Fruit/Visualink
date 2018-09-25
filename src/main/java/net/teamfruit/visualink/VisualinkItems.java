package net.teamfruit.visualink;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.teamfruit.visualink.addons.IItemIdentifierProvider;
import net.teamfruit.visualink.addons.IItemTooltipProvider;
import net.teamfruit.visualink.addons.enderstorage.EnderStorageModule;
import net.teamfruit.visualink.addons.jabba.JABBAModule;

public class VisualinkItems {
	public static List<VisualinkItems> items = new ArrayList<VisualinkItems>();
	public final String id;
	public final @Nullable IItemIdentifierProvider provider;
	public final @Nullable IItemTooltipProvider tooltipProvider;
	private final AtomicReference<Object> item = new AtomicReference<Object>();

	public VisualinkItems(final String id, @Nullable final IItemIdentifierProvider provider, @Nullable final IItemTooltipProvider tooltipProvider) {
		this.id = id;
		this.provider = provider;
		this.tooltipProvider = tooltipProvider;
	}

	public Item getItem() {
		Object value = this.item.get();
		if (value==null)
			synchronized (this.item) {
				value = this.item.get();
				if (value==null) {
					final Object actualValue = Item.itemRegistry.getObject(this.id);
					value = actualValue==null ? this.item : actualValue;
					this.item.set(value);
				}
			}
		return (Item) (value==this.item ? null : value);
	}

	@Override
	public String toString() {
		return String.format("VisualinkBlocks [id=%s, provider=%s]", this.id, this.provider);
	}

	public static void setStandardList() {
		EnderStorageModule.registerItems(items);
		JABBAModule.registerItems(items);
	}

	public static void init() {
		setStandardList();
	}
}