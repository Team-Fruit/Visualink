package net.teamfruit.visualink.addons;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IItemIdentifierProvider {
	@Nullable
	String provide(@Nonnull IItemAccessor accessor);
}
