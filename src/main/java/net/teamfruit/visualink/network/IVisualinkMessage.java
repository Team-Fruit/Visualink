package net.teamfruit.visualink.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface IVisualinkMessage {
	void encodeInto(ChannelHandlerContext arg0, IVisualinkMessage arg1, ByteBuf arg2) throws Exception;

	void decodeInto(ChannelHandlerContext arg0, ByteBuf arg1, IVisualinkMessage arg2);
}