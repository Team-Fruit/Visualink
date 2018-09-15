package net.teamfruit.visualink;

import org.apache.commons.lang3.math.NumberUtils;

public class BlockPos {
	public final int dim, x, y, z;

	public BlockPos(final int dim, final int x, final int y, final int z) {
		this.dim = dim;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return String.format("%d_%d_%d_%d", this.dim, this.x, this.y, this.z);
	}

	public static BlockPos fromString(final String string) {
		if (string==null)
			return null;
		final String[] split = string.split("_");
		if (split.length<4)
			return null;
		return new BlockPos(NumberUtils.toInt(split[0]), NumberUtils.toInt(split[1]), NumberUtils.toInt(split[2]), NumberUtils.toInt(split[3]));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime*result+this.dim;
		result = prime*result+this.x;
		result = prime*result+this.y;
		result = prime*result+this.z;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this==obj)
			return true;
		if (obj==null)
			return false;
		if (!(obj instanceof BlockPos))
			return false;
		final BlockPos other = (BlockPos) obj;
		if (this.dim!=other.dim)
			return false;
		if (this.x!=other.x)
			return false;
		if (this.y!=other.y)
			return false;
		if (this.z!=other.z)
			return false;
		return true;
	}
}