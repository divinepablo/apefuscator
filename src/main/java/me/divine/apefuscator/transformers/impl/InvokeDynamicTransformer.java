package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Handle;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

public class InvokeDynamicTransformer extends me.divine.apefuscator.transformers.Transformer {
    Logger LOGGER = LogManager.getLogger(InvokeDynamicTransformer.class);

    public InvokeDynamicTransformer() {
        super("InvokeDynamic", "Fucks up method calls");
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        obfuscator.getClasses().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                methodNode.instructions.forEach(instruction -> {
                    if (instruction instanceof InvokeDynamicInsnNode) {
                        InvokeDynamicInsnNode invokeDynamicInsnNode = (InvokeDynamicInsnNode) instruction;
                        LOGGER.info("Found invoke dynamic instruction {} in method {}", invokeDynamicInsnNode.name, methodNode.name);
                        LOGGER.info("Invoke dynamic method name: {}", invokeDynamicInsnNode.name);
                        LOGGER.info("Invoke dynamic method desc: {}", invokeDynamicInsnNode.desc);
                        LOGGER.info("Invoke dynamic bootstrap method: {}", invokeDynamicInsnNode.bsm);
                        LOGGER.info("Invoke dynamic bootstrap method args: {}", invokeDynamicInsnNode.bsmArgs);
                        LOGGER.info("Invoke dynamic bootstrap method handle owner: {}", invokeDynamicInsnNode.bsm.getOwner());
                        LOGGER.info("Invoke dynamic bootstrap method handle name: {}", invokeDynamicInsnNode.bsm.getName());
                        LOGGER.info("Invoke dynamic bootstrap method handle desc: {}", invokeDynamicInsnNode.bsm.getDesc());
                    }
                });
            });
        });
    }
}
