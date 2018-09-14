package net.teamfruit.visualink.asm;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import net.teamfruit.visualink.asm.lib.DescHelper;
import net.teamfruit.visualink.asm.lib.MethodMatcher;
import net.teamfruit.visualink.asm.lib.RefName;
import net.teamfruit.visualink.asm.lib.VisitorHelper;

public class BSpaceStorageHandlerVisitor extends ClassVisitor {
	private static class HookMethodVisitor extends MethodVisitor {
		public HookMethodVisitor(final @Nullable MethodVisitor mv) {
			super(Opcodes.ASM5, mv);
		}

		@Override
		public void visitCode() {
			/*
			    0  invokestatic net.teamfruit.visualink.jabba.BSpaceStorageHandlerHook() : void [21]
			 */
			visitMethodInsn(Opcodes.INVOKESTATIC, "net/teamfruit/visualink/jabba/BSpaceStorageHandlerHook", "writeToNBT",
					DescHelper.toDescMethod(void.class), false);
			super.visitCode();
		}
	}

	private final MethodMatcher matcher;

	public BSpaceStorageHandlerVisitor(final @Nonnull String obfClassName, final @Nonnull ClassVisitor cv) {
		super(Opcodes.ASM5, cv);
		// void mcp.mobius.betterbarrels.bspace.BSpaceStorageHandler.linkStorages(int sourceID, int targetID)
		this.matcher = new MethodMatcher(VisitorHelper.getMappedName("mcp/mobius/betterbarrels/bspace/BSpaceStorageHandler"),
				DescHelper.toDescMethod(void.class, "net/minecraft/nbt/NBTTagCompound"), RefName.name("writeToNBT"));
	}

	@Override
	public @Nullable MethodVisitor visitMethod(final int access, final @Nullable String name, final @Nullable String desc, final @Nullable String signature, final @Nullable String[] exceptions) {
		final MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);
		if (name==null||desc==null)
			return parent;
		return this.matcher.match(name, desc) ? new HookMethodVisitor(parent) : parent;
	}
}