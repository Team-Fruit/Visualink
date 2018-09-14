package com.kamesuta.mc.tooltip.asm;

import javax.annotation.Nullable;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import com.kamesuta.mc.tooltip.Reference;
import com.kamesuta.mc.tooltip.asm.lib.VisitorHelper;
import com.kamesuta.mc.tooltip.asm.lib.VisitorHelper.TransformProvider;

import net.minecraft.launchwrapper.IClassTransformer;

public class VisualinkTransformer implements IClassTransformer {
	@Override
	public @Nullable byte[] transform(final @Nullable String name, final @Nullable String transformedName, final @Nullable byte[] bytes) {
		if (bytes==null||name==null||transformedName==null)
			return bytes;

		if (transformedName.equals("mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel"))
			return VisitorHelper.apply(bytes, name, new TransformProvider(ClassWriter.COMPUTE_FRAMES) {
				@Override
				public ClassVisitor createVisitor(final String name, final ClassVisitor cv) {
					Reference.logger.info(String.format("Patching TileEntityBarrel.sendContentSyncPacket (class: %s)", name));
					return new TileEntityBarrelVisitor(name, cv);
				}
			});

		return bytes;
	}
}