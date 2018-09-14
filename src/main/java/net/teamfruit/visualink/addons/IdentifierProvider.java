package net.teamfruit.visualink.addons;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IdentifierProvider {
	@Nullable
	String provide(@Nonnull IAccessor accessor);
}
