package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.MethodNode;

import java.util.concurrent.ThreadLocalRandom;

public class NameTransformer extends me.divine.apefuscator.transformers.Transformer {
    private final Logger LOGGER = LogManager.getLogger(NameTransformer.class);
    public NameTransformer() {
        super("NameTransformer", "Transformer that changes method names");
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        obfuscator.classes().forEach(classNode -> {
            LOGGER.debug("Transforming class {}", classNode.name);
            classNode.methods.forEach(methodNode -> {
                LOGGER.debug("Transforming method {}", methodNode.name);
            });
        });
    }

    private String getName(int length) {
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < length; i++) {
            name.append('ඞ');
        }
        return name.toString();
    }
}
