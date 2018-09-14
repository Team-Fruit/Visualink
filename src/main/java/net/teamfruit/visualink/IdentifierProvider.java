package net.teamfruit.visualink;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IdentifierProvider {
	@Nullable
	String provide(@Nonnull IAccessor accessor);
}
