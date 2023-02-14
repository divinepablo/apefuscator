package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.utils.ASMUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class FlowTransformer extends me.divine.apefuscator.transformers.Transformer implements Opcodes {

    private final int LEVEL = 1;
    private Logger LOGGER = LogManager.getLogger(FlowTransformer.class);

    public FlowTransformer() {
        super("Flow", "weird thing");
    }

//    public void hi() {
//        try {
//            try {
//                try {
//                    try {
//                        int n = 25;
//                        if (n != 24) {
//                            --n;
//                            System.out.println("hg");
//                        }
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//
//        } catch (Exception e) {
//            System.exit(-1);
//        }
//    }

    @Override
    public void transform(Apefuscator obfuscator) {
        obfuscator.getClasses().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
//                if (ASMUtils.getArgumentSize(methodNode.desc) > 0) return;
                int argSize = ASMUtils.getArgumentSize(methodNode.desc);
                InsnList insnList = new InsnList();
                int bro = (int) (Math.random() * 5000);
                LabelNode labelNode = new LabelNode();
                LabelNode labelNode2 = new LabelNode();
                LabelNode labelNode5 = new LabelNode();

                insnList.add(labelNode2);
                insnList.add(new IntInsnNode(BIPUSH, bro));
                insnList.add(new VarInsnNode(ISTORE, argSize + 1337));
                insnList.add(labelNode);
                insnList.add(new VarInsnNode(ILOAD, argSize + 1337));
                insnList.add(new IntInsnNode(BIPUSH, bro - 1));
                insnList.add(new JumpInsnNode(IF_ICMPEQ, labelNode5));

                InsnList hihi = new InsnList();
                methodNode.instructions.forEach(instruction -> {

                    if (instruction instanceof MethodInsnNode) {
//                        MethodInsnNode methodCall = ((MethodInsnNode) instruction);
//                        if (ASMUtils.getArgumentSize(methodCall.desc) > 0) return;
//                        int rng = (int) (Math.random() * 1000);
//                        LabelNode hi = new LabelNode();
//                        LabelNode hi2 = new LabelNode();
//                        hihi.add(hi2);
//                        hihi.add(new IntInsnNode(BIPUSH, bro));
//                        hihi.add(new VarInsnNode(ISTORE, argSize + 6969 + rng));
//
//                        hihi.add(new VarInsnNode(ILOAD, argSize + 6969 + rng));
//                        hihi.add(new IntInsnNode(BIPUSH, bro - 1));
//                        hihi.add(new JumpInsnNode(IF_ICMPEQ, hi));
//                        hihi.add(instruction);
//                        insnList.add(new IincInsnNode(argSize + 6969 + rng, -1));
//                        insnList.add(new JumpInsnNode(GOTO, hi2));
//                        insnList.add(hi);
                    } else {
                        hihi.add(instruction);
                    }
                });
                insnList.add(hihi);
                insnList.add(new IincInsnNode(argSize + 1337, -1));
                insnList.add(new JumpInsnNode(GOTO, labelNode));

//                final int TRYCATCH = LEVEL * 5;
//                LabelNode[] his = new LabelNode[TRYCATCH + 1];
//                his[TRYCATCH] = labelNode5;
//
//                for (int i = 0; i < TRYCATCH; i++) {
//                    his[i] = new LabelNode();
//                }
//                for (int i = 0; i < TRYCATCH; i++) {
//                    LabelNode hi3 = new LabelNode();
//
//                    methodNode.tryCatchBlocks.add(new TryCatchBlockNode(
//                            // start label
//                            labelNode2,
//                            // end label
//                            his[i],
//                            // handler label
//                            hi3,
//                            // exception type
//                            "java/lang/Exception"));
//
//                    insnList.add(his[i]);
//                    insnList.add(new JumpInsnNode(GOTO, his[i + 1]));
//                    insnList.add(hi3);
//                    insnList.add(new VarInsnNode(ASTORE, i + argSize));
//                    if (i == TRYCATCH - 1) {
//                        insnList.add(new VarInsnNode(ALOAD, i + argSize));
//                        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false));
////                        insnList.add(new IntInsnNode(BIPUSH, -1337));
////                        insnList.add(new MethodInsnNode(INVOKESTATIC, "java/lang/System", "exit", "(I)V", false));
//                    } else {
//                        insnList.add(new TypeInsnNode(NEW, "java/lang/RuntimeException"));
//                        insnList.add(new InsnNode(DUP));
//                        insnList.add(new VarInsnNode(ALOAD, i + argSize));
//                        insnList.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/Throwable;)V", false));
//                        insnList.add(new InsnNode(ATHROW));
//                    }
//                }



//                insnList.add(labelNode1);
                insnList.add(labelNode5);
                switch (Type.getType(methodNode.desc).getSort()) {

                    case Type.BOOLEAN:
                    case Type.CHAR:
                    case Type.INT:
                    case Type.BYTE:
                        insnList.add(new InsnNode(ICONST_0));
                        insnList.add(new InsnNode(IRETURN));
                        break;
                    case Type.LONG:
                        insnList.add(new InsnNode(LCONST_0));
                        insnList.add(new InsnNode(LRETURN));
                        break;
                    case Type.DOUBLE:
                        insnList.add(new InsnNode(DCONST_0));
                        insnList.add(new InsnNode(DRETURN));
                        break;
                    case Type.FLOAT:
                        insnList.add(new InsnNode(FCONST_0));
                        insnList.add(new InsnNode(FRETURN));
                        break;
                    case Type.OBJECT:
                        insnList.add(new InsnNode(ACONST_NULL));
                        insnList.add(new InsnNode(ARETURN));
                        break;
                    default:
                        insnList.add(new InsnNode(RETURN));
                        break;
                }
                methodNode.instructions.clear();
                methodNode.instructions.add(insnList);

            });
        });
    }
}
