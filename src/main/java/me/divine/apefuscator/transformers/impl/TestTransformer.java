package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.utils.ASMUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Objects;

public class TestTransformer extends me.divine.apefuscator.transformers.Transformer {


    private Logger LOGGER = LogManager.getLogger(TestTransformer.class);

    public TestTransformer() {
        super("MethodWrapper", "weird thing");
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        obfuscator.getClasses().forEach(classNode -> {
            ArrayList<MethodNode> newMethods = new ArrayList<>();
            classNode.methods.forEach(methodNode -> {

                if (!Objects.equals(methodNode.name, "<init>") && ASMUtils.getArgumentSize(methodNode.desc) < 1) {
                    MethodNode method = ASMUtils.copyMethod(methodNode);
                    method.name = "new_" + method.name;
                    newMethods.add(method);
                    methodNode.instructions.clear();
                    methodNode.instructions.insert(new VarInsnNode(Opcodes.ALOAD, 0));
                    methodNode.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, classNode.name, method.name, method.desc));
                    switch (Type.getType(method.desc).getSort()) {
                        case Type.BOOLEAN:
                        case Type.CHAR:
                        case Type.INT:
                        case Type.BYTE:
                            methodNode.instructions.add(new InsnNode(Opcodes.IRETURN));
                            break;
                        case Type.LONG:
                            methodNode.instructions.add(new InsnNode(Opcodes.LRETURN));
                            break;
                        case Type.DOUBLE:
                            methodNode.instructions.add(new InsnNode(Opcodes.DRETURN));
                            break;
                        case Type.FLOAT:
                            methodNode.instructions.add(new InsnNode(Opcodes.FRETURN));
                            break;
                        default:
                            methodNode.instructions.add(new InsnNode(Opcodes.ARETURN));
                            break;
                    }

                }
            });
            classNode.methods.addAll(newMethods);
        });
    }
}
