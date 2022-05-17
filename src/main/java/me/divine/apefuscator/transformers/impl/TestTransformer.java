package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.utils.ASMUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

public class TestTransformer extends me.divine.apefuscator.transformers.Transformer {


    private Logger LOGGER = LogManager.getLogger(TestTransformer.class);

    public TestTransformer() {
        super("test", "Test");
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        obfuscator.classes().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                methodNode.instructions.forEach(instruction -> {
                    if (instruction instanceof LdcInsnNode) {
                        if (ASMUtils.isString(instruction)) {
                            InsnList toStringList = new InsnList();
                            for (int i = 0; i < 1000; i++) {
                                MethodInsnNode methodInsnNode = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;");
                                toStringList.insert(methodInsnNode);
                            }
                            methodNode.instructions.insert(instruction, toStringList);
                        }
                    }
                    methodNode.localVariables.forEach(localVariableNode -> {
                        localVariableNode.desc = "Ljava/lang/Object;";
                    });
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
