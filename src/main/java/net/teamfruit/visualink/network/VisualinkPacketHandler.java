package net.teamfruit.visualink.network;

import java.util.EnumMap;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.FMLOutboundHandler.OutboundTarget;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.teamfruit.visualink.addons.jabba.Message0x00UpdateBarrelLinks;

public enum VisualinkPacketHandler {
	INSTANCE;

	public EnumMap<Side, FMLEmbeddedChannel> channels;

	private class VisualinkCodec extends FMLIndexedMessageToMessageCodec<IVisualinkMessage> {
		public VisualinkCodec() {
			addDiscriminator(0, Message0x00UpdateBarrelLinks.class);
		}

		@Override
		public void encodeInto(final ChannelHandlerContext ctx, final IVisualinkMessage msg, final ByteBuf target)
				throws Exception {
			msg.encodeInto(ctx, msg, target);
		}

		@Override
		public void decodeInto(final ChannelHandlerContext ctx, final ByteBuf dat, final IVisualinkMessage msg) {
			msg.decodeInto(ctx, dat, msg);
		}
	}

	private VisualinkPacketHandler() {
		this.channels = NetworkRegistry.INSTANCE.newChannel("Visualink", new ChannelHandler[] { new VisualinkCodec() });
		if (FMLCommonHandler.instance().getSide()==Side.CLIENT)
			addClientHandlers();

	}

	private void addClientHandlers() {
		final FMLEmbeddedChannel channel = this.channels.get(Side.CLIENT);
		final String codec = channel.findChannelHandlerNameForType(VisualinkCodec.class);

		channel.pipeline().addAfter(codec, "ClientHandler", new Message0x00UpdateBarrelLinks());
	}

	public void sendTo(final IVisualinkMessage message, final EntityPlayerMP player) {
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.PLAYER);
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		this.channels.get(Side.SERVER).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
	}

	public void sendToDimension(final IVisualinkMessage message, final int dimensionId) {
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.DIMENSION);
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(Integer.valueOf(dimensionId));
		this.channels.get(Side.SERVER).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
	}

	public void sendToAllAround(final IVisualinkMessage message, final TargetPoint point) {
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.ALLAROUNDPOINT);
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
		this.channels.get(Side.SERVER).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
	}

	public void sendToAll(final IVisualinkMessage message) {
		this.channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.ALL);
		this.channels.get(Side.SERVER).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
	}
}