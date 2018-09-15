package net.teamfruit.visualink.asm;

import javax.annotation.Nullable;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import net.minecraft.launchwrapper.IClassTransformer;
import net.teamfruit.visualink.Log;
import net.teamfruit.visualink.asm.lib.VisitorHelper;
import net.teamfruit.visualink.asm.lib.VisitorHelper.TransformProvider;

public class VisualinkTransformer implements IClassTransformer {
	@Override
	public @Nullable byte[] transform(final @Nullable String name, final @Nullable String transformedName, final @Nullable byte[] bytes) {
		if (bytes==null||name==null||transformedName==null)
			return bytes;

		if (transformedName.equals("mcp.mobius.betterbarrels.bspace.BSpaceStorageHandler"))
			return VisitorHelper.apply(bytes, name, new TransformProvider(ClassWriter.COMPUTE_FRAMES) {
				@Override
				public ClassVisitor createVisitor(final String name, final ClassVisitor cv) {
					Log.log.info(String.format("Patching BSpaceStorageHandler.linkStorages (class: %s)", name));
					return new BSpaceStorageHandlerVisitor(name, cv);
				}
			});

		if (transformedName.equals("codechicken.enderstorage.common.TileFrequencyOwner"))
			return VisitorHelper.apply(bytes, name, new TransformProvider(ClassWriter.COMPUTE_FRAMES) {
				@Override
				public ClassVisitor createVisitor(final String name, final ClassVisitor cv) {
					Log.log.info(String.format("Patching TileFrequencyOwner.handleDescriptionPacket (class: %s)", name));
					return new TileFrequencyOwnerVisitor(name, cv);
				}
			});

		if (transformedName.equals("mcp.mobius.betterbarrels.network.Message0x00FulleTileEntityNBT"))
			return VisitorHelper.apply(bytes, name, new TransformProvider(ClassWriter.COMPUTE_FRAMES) {
				@Override
				public ClassVisitor createVisitor(final String name, final ClassVisitor cv) {
					Log.log.info(String.format("Patching mcp.mobius.betterbarrels.network.Message0x00FulleTileEntityNBT.channelRead0 (class: %s)", name));
					return new Message0x00FulleTileEntityNBTVisitor(name, cv);
				}
			});
		if (transformedName.equals("mcp.mobius.betterbarrels.network.Message0x08LinkUpdate"))
			return VisitorHelper.apply(bytes, name, new TransformProvider(ClassWriter.COMPUTE_FRAMES) {
				@Override
				public ClassVisitor createVisitor(final String name, final ClassVisitor cv) {
					Log.log.info(String.format("Patching mcp.mobius.betterbarrels.network.Message0x08LinkUpdate.channelRead0 (class: %s)", name));
					return new Message0x08LinkUpdateVisitor(name, cv);
				}
			});

		return bytes;
	}
}