package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;

import java.util.ArrayList;

public class InvokeDynamicTransformer extends me.divine.apefuscator.transformers.Transformer {
    Logger LOGGER = LogManager.getLogger(InvokeDynamicTransformer.class);

    public InvokeDynamicTransformer() {
        super("InvokeDynamic", "Fucks up method calls");
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        ArrayList<InvokeDynamicInsnNode> test = new ArrayList<>();
        obfuscator.classes().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                methodNode.instructions.forEach(instruction -> {
                    if (instruction instanceof InvokeDynamicInsnNode) {
                        InvokeDynamicInsnNode invokeDynamicInsnNode = (InvokeDynamicInsnNode) instruction;
                        test.add(invokeDynamicInsnNode);

                    }
                });
                test.forEach(invokeDynamicInsnNode -> {
                    methodNode.instructions.add(invokeDynamicInsnNode);
                });
            });
        });
    }
}
