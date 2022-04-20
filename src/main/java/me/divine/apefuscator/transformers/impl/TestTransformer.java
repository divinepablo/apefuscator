package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class TestTransformer extends me.divine.apefuscator.transformers.Transformer implements Opcodes {
    private final Logger LOGGER = LogManager.getLogger(TestTransformer.class);
    private Map<MethodNode, String[]> methodMap = new HashMap<>();
    private Map<String, String> localVariableMap = new HashMap<>();
    char primaryChar = '\u0D9E';
    char secondaryChar = '\u0D9D';
    public TestTransformer() {
        super("TestTransformer", "Allows me to test ASM");
    }

    public TestTransformer(char primaryChar, char secondaryChar) {
        this();
        this.primaryChar = primaryChar;
        this.secondaryChar = secondaryChar;
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        obfuscator.classes().forEach(classNode -> {
            if (classNode.name.contains("/")) {
                System.out.println(classNode.name);
            }
            LOGGER.info("Transforming class: {}", classNode.name);
            classNode.methods.forEach(methodNode -> {
                LOGGER.info("Found method: {}", methodNode.name);
                if (methodNode.localVariables != null) {
                    methodNode.localVariables.forEach(localVariableNode -> {
                        if (localVariableNode.name != null) {
                            LOGGER.info("Local variable: {} has name {} in method {}", localVariableNode.index, localVariableNode.name, methodNode.name);
                            String newName = getName(localVariableNode.name.length() + ThreadLocalRandom.current().nextInt(1, 10));

                            localVariableMap.put(localVariableNode.name, newName);
                            localVariableNode.name = newName;
                            LOGGER.info("Renamed local variable: {} to {}", localVariableNode.index, localVariableNode.name);
                        }
                    });
                }
//                if (methodNode.instructions != null) {
//                    LOGGER.info("Method: {} has {} instructions", methodNode.name, methodNode.instructions.size());
//                }
//                if (methodNode.tryCatchBlocks != null) {
//                    LOGGER.info("Method: {} has {} try catch blocks", methodNode.name, methodNode.tryCatchBlocks.size());
//                }
//                if (methodNode.exceptions != null) {
//                    LOGGER.info("Method: {} has {} exceptions", methodNode.name, methodNode.exceptions.size());
//                }
//                if (methodNode.visibleAnnotations != null) {
//                    LOGGER.info("Method: {} has {} visible annotations", methodNode.name, methodNode.visibleAnnotations.size());
//                }
//                if (methodNode.invisibleAnnotations != null) {
//                    LOGGER.info("Method: {} has {} invisible annotations", methodNode.name, methodNode.invisibleAnnotations.size());
//                }
//                if (methodNode.visibleTypeAnnotations != null) {
//                    LOGGER.info("Method: {} has {} visible type annotations", methodNode.name, methodNode.visibleTypeAnnotations.size());
//                }
//                if (methodNode.invisibleTypeAnnotations != null) {
//                    LOGGER.info("Method: {} has {} invisible type annotations", methodNode.name, methodNode.invisibleTypeAnnotations.size());
//                }
//                if (methodNode.visibleParameterAnnotations != null) {
//                    LOGGER.info("Method: {} has {} visible parameter annotations", methodNode.name, methodNode.visibleParameterAnnotations.length);
//                }
//                if (methodNode.invisibleParameterAnnotations != null) {
//                    LOGGER.info("Method: {} has {} invisible parameter annotations", methodNode.name, methodNode.invisibleParameterAnnotations.length);
//                }
//                LOGGER.info("Method: {} has {} visible annotable parameter count", methodNode.name, methodNode.visibleAnnotableParameterCount);
//                LOGGER.info("Method: {} has {} invisible annotable parameter count", methodNode.name, methodNode.invisibleAnnotableParameterCount);
//

            });
        });
    }

    private String getName(int length) {
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < length; i++) {
            name.append(ThreadLocalRandom.current().nextInt(1, 6) == 5 ? '\u0D9D' : '\u0D9E');
        }
        if (localVariableMap.containsValue(name.toString())) {
            return getName(name.length() + ThreadLocalRandom.current().nextInt(1, 5));
        }
        return name.toString();
    }
}
