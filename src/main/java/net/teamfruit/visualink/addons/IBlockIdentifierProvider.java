package net.teamfruit.visualink.addons;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IBlockIdentifierProvider {
	@Nullable
	String provide(@Nonnull IBlockAccessor accessor);
}
