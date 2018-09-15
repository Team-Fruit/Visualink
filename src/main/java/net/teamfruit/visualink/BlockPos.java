package net.teamfruit.visualink;

public class BlockPos {
	public final int dim, x, y, z;

	public BlockPos(final int dim, final int x, final int y, final int z) {
		this.dim = dim;
		this.x = x;
		this.y = y;
		this.z = z;
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