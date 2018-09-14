package com.kamesuta.mc.tooltip;

public class TileEntityBarrelHook {
	public static void sendContentSyncPacket(final TileEntityBarrelHook tileEntityBarrelHook, final boolean force) {

	}

	public boolean sendGhostSyncPacket(final boolean force) {
		sendContentSyncPacket(this, force);
		return false;
	}
}
