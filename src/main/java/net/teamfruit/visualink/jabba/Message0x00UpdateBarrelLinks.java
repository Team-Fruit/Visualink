package net.teamfruit.visualink.jabba;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mcp.mobius.betterbarrels.network.BarrelPacketHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.teamfruit.visualink.network.IVisualinkMessage;

public class Message0x00UpdateBarrelLinks extends SimpleChannelInboundHandler<Message0x00UpdateBarrelLinks> implements IVisualinkMessage {
	public NBTTagCompound fullLinksTag = new NBTTagCompound();

	public Message0x00UpdateBarrelLinks() {
	}

	public Message0x00UpdateBarrelLinks(final BarrelLinkManager barrel) {
		barrel.writeToNBT(this.fullLinksTag);
	}

	@Override
	public void encodeInto(final ChannelHandlerContext ctx, final IVisualinkMessage msg, final ByteBuf target) throws Exception {
		BarrelPacketHandler.INSTANCE.writeNBTTagCompoundToBuffer(target, this.fullLinksTag);
	}

	@Override
	public void decodeInto(final ChannelHandlerContext ctx, final ByteBuf dat, final IVisualinkMessage rawmsg) {
		final Message0x00UpdateBarrelLinks msg = (Message0x00UpdateBarrelLinks) rawmsg;
		try {
			msg.fullLinksTag = BarrelPacketHandler.INSTANCE.readNBTTagCompoundFromBuffer(dat);
		} catch (final Exception arg5) {
			;
		}
	}

	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final Message0x00UpdateBarrelLinks msg) throws Exception {
		final BarrelLinkManager manager = BarrelLinkManager.instance;
		if (manager!=null)
			manager.readFromNBT(msg.fullLinksTag);
	}
}