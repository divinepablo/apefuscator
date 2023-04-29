package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.Transformer;
import me.divine.apefuscator.utils.ASMUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class DortCodeObfuscation extends Transformer implements Opcodes {
    public DortCodeObfuscation() {
        super("CodeNoRun", "if (true && !true) {}");
    } // dort dont hurt me pls

    @Override
    public void transform(Apefuscator obfuscator) {
        obfuscator.classes().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {

                LabelNode labelNode = new LabelNode();
                InsnList list = new InsnList();
                list.add(new InsnNode(Opcodes.ICONST_1));
                list.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
                list.add(labelNode);
                list.add(new FieldInsnNode(ASMUtils.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                list.add(new LdcInsnNode("fart sex"));
                list.add(new MethodInsnNode(ASMUtils.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
                list.add(new MethodInsnNode(ASMUtils.INVOKEVIRTUAL, classNode.name, "crasher", "()V", false));

                list.add(new InsnNode(ASMUtils.RETURN));
                methodNode.instructions.insert(list);
            });
            classNode.methods.add(crasher());
        });
    }

    public MethodNode crasher() {
        MethodNode methodVisitor = new MethodNode(ACC_PUBLIC, "crasher", "()V", null, null);
        methodVisitor.visitCode();
        Label label0 = new Label();
        Label label1 = new Label();
        Label label2 = new Label();
        methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/Exception");
        methodVisitor.visitLdcInsn("asd");
        methodVisitor.visitTypeInsn(NEW, "java/lang/String");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitInsn(ICONST_3);
        methodVisitor.visitIntInsn(NEWARRAY, T_CHAR);
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitIntInsn(BIPUSH, 97);
        methodVisitor.visitInsn(CASTORE);
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitInsn(ICONST_1);
        methodVisitor.visitIntInsn(BIPUSH, 115);
        methodVisitor.visitInsn(CASTORE);
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitInsn(ICONST_2);
        methodVisitor.visitIntInsn(BIPUSH, 100);
        methodVisitor.visitInsn(CASTORE);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([C)V", false);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toString", "()Ljava/lang/String;", false);
        Label label3 = new Label();
        methodVisitor.visitJumpInsn(IF_ACMPNE, label3);
        methodVisitor.visitLabel(label0);
        methodVisitor.visitLdcInsn(Type.getType("Lsun/misc/Unsafe;"));
        methodVisitor.visitLdcInsn("theUnsafe");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getDeclaredField", "(Ljava/lang/String;)Ljava/lang/reflect/Field;", false);
        methodVisitor.visitVarInsn(ASTORE, 2);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitInsn(ICONST_1);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "setAccessible", "(Z)V", false);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitInsn(ACONST_NULL);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
        methodVisitor.visitTypeInsn(CHECKCAST, "sun/misc/Unsafe");
        methodVisitor.visitVarInsn(ASTORE, 1);
        methodVisitor.visitLabel(label1);
        methodVisitor.visitJumpInsn(GOTO, label3);
        methodVisitor.visitLabel(label2);
        methodVisitor.visitVarInsn(ASTORE, 1);
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/System", "exit", "(I)V", false);
        methodVisitor.visitLabel(label3);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(7, 3);
        methodVisitor.visitEnd();
        return methodVisitor;
    }
}
