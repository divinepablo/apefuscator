package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.utils.ASMUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class TestTransformer extends me.divine.apefuscator.transformers.Transformer implements Opcodes {


    private final int TRYCATCH = 15;
    private Logger LOGGER = LogManager.getLogger(TestTransformer.class);

    public TestTransformer() {
        super("Flow", "weird thing");
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        obfuscator.getClasses().forEach(classNode -> {
            MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, "hi", "()V", null, null);
            mv.visitCode();
            LabelNode hi2 = new LabelNode();

            LabelNode[] his = new LabelNode[TRYCATCH + 1];
            his[TRYCATCH] = hi2;
            for (int i = 0; i < TRYCATCH; i++) {
                his[i] = new LabelNode();
            }
            LabelNode hi3 = new LabelNode();
            mv.visitLabel(hi3.getLabel());
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("hg");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            
            for (int i = 0; i < TRYCATCH; i++) {


                LabelNode labelNode3 = new LabelNode();

                mv.visitTryCatchBlock(
                        // start label
                        hi3.getLabel(),
                        // end label
                        his[i].getLabel(),
                        // handler label
                       labelNode3.getLabel(),
                        // exception type
                        "java/lang/Exception"
                );
                mv.visitLabel(his[i].getLabel());
                mv.visitJumpInsn(GOTO, his[i + 1].getLabel());
                mv.visitLabel(labelNode3.getLabel());
                mv.visitVarInsn(ASTORE, i);
                if (i == TRYCATCH - 1) {
                    mv.visitInsn(ICONST_M1);
                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "exit", "(I)V", false);
                } else {
                    mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
                    mv.visitInsn(DUP);
                    mv.visitVarInsn(ALOAD, i);
                    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/Throwable;)V", false);
                    mv.visitInsn(ATHROW);
                }
            }
            mv.visitLabel(hi2.getLabel());
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        });
    }
}
