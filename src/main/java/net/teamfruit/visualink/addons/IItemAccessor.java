package net.teamfruit.visualink.addons;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IItemAccessor {
	Item getItem();

	ItemStack getItemStack();

	String getItemID();
}