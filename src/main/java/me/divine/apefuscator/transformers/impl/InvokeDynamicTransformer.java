package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.ArrayList;

public class InvokeDynamicTransformer extends me.divine.apefuscator.transformers.Transformer {
    Logger LOGGER = LogManager.getLogger(InvokeDynamicTransformer.class);

    public InvokeDynamicTransformer() {
        super("InvokeDynamic", "Fucks up method calls");
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        ArrayList<InvokeDynamicInsnNode> test = new ArrayList<>();
        obfuscator.getClasses().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                methodNode.instructions.forEach(instruction -> {
                    if (instruction instanceof MethodInsnNode) {
                        MethodInsnNode methodInsnNode = ((MethodInsnNode) instruction);
                        if (methodInsnNode.owner.startsWith("java/") && (methodInsnNode.getOpcode() & Opcodes.ACC_STATIC) != 0) {
                            InvokeDynamicInsnNode invokeDynamic = new InvokeDynamicInsnNode(methodInsnNode.name, methodInsnNode.desc, new Handle(Opcodes.H_INVOKESTATIC, methodInsnNode.owner, methodInsnNode.name, methodInsnNode.desc, false));
                            methodNode.instructions.insertBefore(methodInsnNode, invokeDynamic);
                        }
                    }

                });
            });
        });
    }
}
