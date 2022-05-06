package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.VarInsnNode;

public class TestTransformer extends me.divine.apefuscator.transformers.Transformer {


    private Logger LOGGER = LogManager.getLogger(TestTransformer.class);

    public TestTransformer() {
        super("test", "Test");
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        obfuscator.getClasses().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                LOGGER.info("Found method: {} {}", methodNode.name, methodNode.desc);
            });
        });
    }
}
