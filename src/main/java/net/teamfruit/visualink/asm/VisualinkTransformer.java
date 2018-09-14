package net.teamfruit.visualink.asm;

import javax.annotation.Nullable;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import net.minecraft.launchwrapper.IClassTransformer;
import net.teamfruit.visualink.Reference;
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
					Reference.logger.info(String.format("Patching BSpaceStorageHandler.linkStorages (class: %s)", name));
					return new BSpaceStorageHandlerVisitor(name, cv);
				}
			});

		return bytes;
	}
}