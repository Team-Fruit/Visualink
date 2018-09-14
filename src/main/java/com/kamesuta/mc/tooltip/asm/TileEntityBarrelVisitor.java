package com.kamesuta.mc.tooltip.asm;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.kamesuta.mc.tooltip.asm.lib.DescHelper;
import com.kamesuta.mc.tooltip.asm.lib.MethodMatcher;
import com.kamesuta.mc.tooltip.asm.lib.RefName;
import com.kamesuta.mc.tooltip.asm.lib.VisitorHelper;

public class TileEntityBarrelVisitor extends ClassVisitor {
	private static class HookMethodVisitor extends MethodVisitor {
		public HookMethodVisitor(final @Nullable MethodVisitor mv) {
			super(Opcodes.ASM5, mv);
		}

		@Override
		public void visitCode() {
			/*
				0  aload_0 [this]
			    1  iload_1 [force]
			    2  invokestatic com.kamesuta.mc.tooltip.TileEntityBarrelHook.sendContentSyncPacket(com.kamesuta.mc.tooltip.TileEntityBarrelHook, boolean) : void [21]
			 */
			visitVarInsn(Opcodes.ALOAD, 0);
			visitVarInsn(Opcodes.ILOAD, 1);
			visitMethodInsn(Opcodes.INVOKESTATIC, "com/kamesuta/mc/tooltip/TileEntityBarrelHook", "sendContentSyncPacket",
					DescHelper.toDesc(void.class, "mcp/mobius/betterbarrels/common/blocks/TileEntityBarrel", boolean.class), false);
			super.visitCode();
		}
	}

	private final MethodMatcher matcher;

	public TileEntityBarrelVisitor(final @Nonnull String obfClassName, final @Nonnull ClassVisitor cv) {
		super(Opcodes.ASM5, cv);
		this.matcher = new MethodMatcher(VisitorHelper.getMappedName("mcp/mobius/betterbarrels/common/blocks/TileEntityBarrel"),
				DescHelper.toDesc(boolean.class, boolean.class), RefName.name("sendContentSyncPacket"));
	}

	@Override
	public @Nullable MethodVisitor visitMethod(final int access, final @Nullable String name, final @Nullable String desc, final @Nullable String signature, final @Nullable String[] exceptions) {
		final MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);
		if (name==null||desc==null)
			return parent;
		return this.matcher.match(name, desc) ? new HookMethodVisitor(parent) : parent;
	}
}