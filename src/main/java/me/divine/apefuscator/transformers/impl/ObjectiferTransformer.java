package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.Transformer;
import me.divine.apefuscator.utils.ASMUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

public class ObjectiferTransformer extends Transformer {

    private Logger LOGGER = LogManager.getLogger(ObjectiferTransformer.class);

    public ObjectiferTransformer() {
        super("Objectifer", "Converts everything to objects bc funnit");
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        obfuscator.getClasses().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                methodNode.instructions.forEach(instruction -> {
//                    if (instruction.getOpcode() == Opcodes.NEW) {
//                        LOGGER.info("{}", instruction);
//                    }
//                    if (instruction instanceof LdcInsnNode) {
//                        if (ASMUtils.isString(instruction)) {
//                            InsnList toStringList = new InsnList();
//                            for (int i = 0; i < 1000; i++) {
//                                MethodInsnNode methodInsnNode = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;");
//                                toStringList.insert(methodInsnNode);
//                            }
//                            methodNode.instructions.insert(instruction, toStringList);
//                        }
//                    }
//                    methodNode.localVariables.forEach(localVariableNode -> {
//                        localVariableNode.desc = "Ljava/lang/Object;";
//                    });
                });
                if (ASMUtils.getReturnType(methodNode.desc) != Type.VOID_TYPE) {
                    methodNode.desc = ASMUtils.changeReturnType(methodNode.desc, "Ljava/lang/Object;");
                }
            });
            classNode.fields.forEach(fieldNode -> {
                fieldNode.desc = "Ljava/lang/Object;";
            });
        });
    }
}
