package com.kamesuta.mc.tooltip;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IdentifierProvider {
	@Nullable
	String provide(@Nonnull IAccessor accessor);
}
