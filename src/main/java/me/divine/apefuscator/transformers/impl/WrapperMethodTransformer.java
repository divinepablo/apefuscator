package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.utils.ASMUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;

public class WrapperMethodTransformer extends me.divine.apefuscator.transformers.Transformer {

    Logger LOGGER = LogManager.getLogger(WrapperMethodTransformer.class);

    public WrapperMethodTransformer() {
        super("JunkCode", "Generates junk funny");
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        // TODO Auto-generated method stub
        obfuscator.getClasses().forEach(classNode -> {
            List<MethodNode> nigger = new ArrayList<>();
            classNode.methods.forEach(methodNode -> {
                for (int z = 0; z < 5; z++) {
                    StringBuilder a = new StringBuilder();
                    for (int i = 0; i < 50; i++) {
                        for (int j = 0; j < 12; j++) {
                            a.append(RandomStringUtils.random(RandomUtils.nextInt(0, 14))).append("\n");
                            a.append(RandomStringUtils.random(RandomUtils.nextInt(0, 12))).append("\t");
                        }
                        a.append(RandomStringUtils.random(RandomUtils.nextInt(0, 7))).append("\t");
                        a.append(RandomStringUtils.random(RandomUtils.nextInt(0, 13))).append("\n");
                    }
                    StringBuilder b = new StringBuilder();
                    for (int i = 0; i < 124; i++) {
                        b.append("[");
                    }
                    MethodNode mv = new MethodNode(ASMUtils.ACC_ABSTRACT |
                            ASMUtils.ACC_NATIVE |
                            ASMUtils.ACC_STATIC |
                            ASMUtils.ACC_PRIVATE |
                            ASMUtils.ACC_PUBLIC |
                            ASMUtils.ACC_SYNCHRONIZED |
                            ASMUtils.ACC_FINAL |
                            ASMUtils.ACC_STRICT |
                            ASMUtils.ACC_VOLATILE |
                            ASMUtils.ACC_PROTECTED |
                            ASMUtils.ACC_TRANSIENT,
                            a.toString(), "(" + b + "Ljava/lang/Object;)V", null, null);
                    mv.visitCode();
                    mv.visitFieldInsn(ASMUtils.GETSTATIC, "java/lang/System",
                            "out", "Ljava/io/PrintStream;");
                    mv.visitLdcInsn("fart sex");
                    mv.visitMethodInsn(ASMUtils.INVOKEVIRTUAL, "java/io/PrintStream",
                            "println", "(Ljava/lang/String;)V", false);
                    mv.visitInsn(ASMUtils.RETURN);
                    mv.visitMaxs(3, 3);
                    mv.visitEnd();
                    nigger.add(mv);
                }
            });
            classNode.methods.addAll(nigger);
        });
    }

    private InsnList randomPrintln() {
        InsnList instructions = new InsnList();
        MethodInsnNode instruction02 = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        instructions.add(instruction02);
        return instructions;
    }

}
