package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class FlowTransformer extends me.divine.apefuscator.transformers.Transformer implements Opcodes {

    private final int LEVEL = 1;
    private Logger LOGGER = LogManager.getLogger(FlowTransformer.class);

    public FlowTransformer() {
        super("Flow", "weird thing");
    }

    public void hi() {
        try {
            try {
                try {
                    try {
                        if (Math.random() >= 0.0) {
                            System.out.println("hg");
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } catch (Exception e) {
            System.exit(-1);
        }
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        obfuscator.classes().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                InsnList insnList = new InsnList();
                LabelNode labelNode5 = new LabelNode();

                insnList.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Math", "random", "()D"));
                insnList.add(new InsnNode(DCONST_0));
                insnList.add(new InsnNode(DCMPL));
                insnList.add(new JumpInsnNode(IFLT, labelNode5));
                InsnList list = new InsnList();
                methodNode.instructions.forEach(instruction -> {
                    if (instruction instanceof MethodInsnNode) {
                        LabelNode label = new LabelNode();
                        list.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Math", "random", "()D"));
                        list.add(new InsnNode(DCONST_0));
                        list.add(new InsnNode(DCMPL));
                        list.add(new JumpInsnNode(IFLT, labelNode5));
                        list.add(instruction);
                        list.add(label);

                    } else {
                        list.add(instruction);
                    }
                });

                insnList.add(list);
                insnList.add(labelNode5);
                insnList.add(methodNode.instructions.getLast());
//                switch (Type.getType(methodNode.desc).getSort()) {
//
//                    case Type.BOOLEAN:
//                    case Type.CHAR:
//                    case Type.INT:
//                    case Type.BYTE:
//                        insnList.add(new InsnNode(ICONST_0));
//                        insnList.add(new InsnNode(IRETURN));
//                        break;
//                    case Type.LONG:
//                        insnList.add(new InsnNode(LCONST_0));
//                        insnList.add(new InsnNode(LRETURN));
//                        break;
//                    case Type.DOUBLE:
//                        insnList.add(new InsnNode(DCONST_0));
//                        insnList.add(new InsnNode(DRETURN));
//                        break;
//                    case Type.FLOAT:
//                        insnList.add(new InsnNode(FCONST_0));
//                        insnList.add(new InsnNode(FRETURN));
//                        break;
//                    case Type.OBJECT:
//                        insnList.add(new InsnNode(ACONST_NULL));
//                        insnList.add(new InsnNode(ARETURN));
//                        break;
//                    default:
//                        insnList.add(new InsnNode(RETURN));
//                        break;
//                }
                methodNode.instructions.clear();
                methodNode.instructions.add(insnList);
            });
        });
    }
}
