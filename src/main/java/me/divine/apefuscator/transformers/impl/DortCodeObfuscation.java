package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.Transformer;
import me.divine.apefuscator.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class DortCodeObfuscation extends Transformer {
    public DortCodeObfuscation() {
        super("CodeNoRun", "if (true && !true) {}");
    } // dort dont hurt me pls

    @Override
    public void transform(Apefuscator obfuscator) {
        obfuscator.getClasses().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                LabelNode labelNode = new LabelNode();
                methodNode.instructions.add(new InsnNode(Opcodes.ICONST_1));
                methodNode.instructions.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
                methodNode.instructions.add(labelNode);
                methodNode.instructions.add(new InsnNode(ASMUtils.RETURN));
                methodNode.instructions.add(new MethodInsnNode(ASMUtils.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                methodNode.instructions.add(new LdcInsnNode("fart sex"));
                methodNode.instructions.add(new MethodInsnNode(ASMUtils.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
                methodNode.instructions.add(new InsnNode(ASMUtils.RETURN));
            });
        });
    }
}
