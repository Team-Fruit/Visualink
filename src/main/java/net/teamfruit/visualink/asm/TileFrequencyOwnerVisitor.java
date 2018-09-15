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

public class TileFrequencyOwnerVisitor extends ClassVisitor {
	private static class HookMethodVisitor extends MethodVisitor {
		public HookMethodVisitor(final @Nullable MethodVisitor mv) {
			super(Opcodes.ASM5, mv);
		}

		@Override
		public void visitInsn(final int opcode) {
			if (opcode==Opcodes.RETURN) {
				visitVarInsn(Opcodes.ALOAD, 0);
				visitMethodInsn(Opcodes.INVOKESTATIC, "net/teamfruit/visualink/addons/enderstorage/TileFrequencyOwnerHook", "handleDescriptionPacket",
						DescHelper.toDescMethod(void.class, "net/minecraft/tileentity/TileEntity"), false);
			}
			super.visitInsn(opcode);
		}
	}

	private final MethodMatcher matcher;

	public TileFrequencyOwnerVisitor(final @Nonnull String obfClassName, final @Nonnull ClassVisitor cv) {
		super(Opcodes.ASM5, cv);
		this.matcher = new MethodMatcher(VisitorHelper.getMappedName("mcp/mobius/betterbarrels/network/Message0x08LinkUpdate"),
				DescHelper.toDescMethod(void.class, "codechicken/lib/packet/PacketCustom"), RefName.name("handleDescriptionPacket"));
	}

	@Override
	public @Nullable MethodVisitor visitMethod(final int access, final @Nullable String name, final @Nullable String desc, final @Nullable String signature, final @Nullable String[] exceptions) {
		final MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);
		if (name==null||desc==null)
			return parent;
		return this.matcher.match(name, desc) ? new HookMethodVisitor(parent) : parent;
	}
}