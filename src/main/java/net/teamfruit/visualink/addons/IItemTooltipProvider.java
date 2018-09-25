package net.teamfruit.visualink.addons;

import java.util.List;

import javax.annotation.Nonnull;

public interface IItemTooltipProvider {
	void provide(@Nonnull IItemAccessor accessor, List<String> tooltip);
}
