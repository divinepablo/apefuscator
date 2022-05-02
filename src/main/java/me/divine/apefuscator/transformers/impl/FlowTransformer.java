package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.utils.ASMUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;

public class FlowTransformer extends me.divine.apefuscator.transformers.Transformer {

    Logger LOGGER = LogManager.getLogger(FlowTransformer.class);

    public FlowTransformer() {
        super("Flow", "Transforms flow control structures");
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        // TODO Auto-generated method stub
        obfuscator.getClasses().forEach(classNode -> {
            classNode.fields.forEach(fieldNode -> {
                if (fieldNode.desc.equals("Ljava/lang/String;")) {
//                    fieldNode.desc = "Ljava/lang/Object;";
                }
            });
            ArrayList<MethodNode> betterMethods = new ArrayList<>();
            classNode.methods.forEach(methodNode -> {
//                int rng = ThreadLocalRandom.current().nextInt(1, 3);
                int rng = 2;
                if (rng == 2) {

                    if (!methodNode.name.equals("<init>") && !methodNode.name.equals("<clinit>") && !methodNode.name.equals("<cinit>") && !methodNode.name.equals("main")) {
                        LOGGER.info("Making wrapper method for {}", methodNode.name);
                        MethodNode normalMethod = new MethodNode(Opcodes.ASM5, methodNode.access, String.format("aped_%s", methodNode.name), methodNode.desc, methodNode.signature, methodNode.exceptions.toArray(new String[]{}));
                        normalMethod.instructions.add(methodNode.instructions);
                        betterMethods.add(normalMethod);
                        methodNode.instructions.clear();
                        InsnList il = new InsnList();
                        LOGGER.info("Method Descriptor: {}", methodNode.desc);
                        for (int i = 0; i < ASMUtils.getArgumentSize(methodNode.desc); i++) {


                        }
                        int i = 0;
                        for (Type argumentType : ASMUtils.getArgumentTypes(methodNode.desc)) {
                            switch (argumentType.getSort()) {
                                case Type.BOOLEAN:
                                case Type.BYTE:
                                case Type.CHAR:
                                case Type.INT:
                                case Type.SHORT:
                                    il.add(new VarInsnNode(Opcodes.ILOAD, i));
                                    break;
                                case Type.DOUBLE:
                                    il.add(new VarInsnNode(Opcodes.DLOAD, i));
                                    break;
                                case Type.FLOAT:
                                    il.add(new VarInsnNode(Opcodes.FLOAD, i));
                                    break;
                                case Type.LONG:
                                    il.add(new VarInsnNode(Opcodes.LLOAD, i));
                                    break;
                                default:
                                    il.add(new VarInsnNode(Opcodes.ALOAD, i));
                                    break;
                            }
                            i++;
                        }

                        if (ASMUtils.isStatic(methodNode.access)) {
                            il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, classNode.name, normalMethod.name, normalMethod.desc));
                        } else {
                            if (ASMUtils.getArgumentSize(methodNode.desc) == 0) {
                                il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            } else {
                                il.add(new VarInsnNode(Opcodes.ALOAD, -1));
                            }
                            il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, classNode.name, normalMethod.name, normalMethod.desc));
                        }
                        if (ASMUtils.getReturnType(methodNode.desc).equals(Type.VOID_TYPE)) {
                            il.add(new InsnNode(Opcodes.RETURN));
                        } else {
                            switch (ASMUtils.getReturnType(methodNode.desc).getSort()) {
                                case Type.BOOLEAN:
                                case Type.INT:
                                case Type.BYTE:
                                case Type.CHAR:
                                    il.add(new InsnNode(Opcodes.IRETURN));
                                    break;
                                case Type.DOUBLE:
                                    il.add(new InsnNode(Opcodes.DRETURN));
                                    break;
                                case Type.FLOAT:
                                    il.add(new InsnNode(Opcodes.FRETURN));
                                    break;
                                case Type.LONG:
                                    il.add(new InsnNode(Opcodes.LRETURN));
                                    break;
                                default:
                                    il.add(new InsnNode(Opcodes.ARETURN));
                                    break;
                            }

                        }
                        methodNode.instructions.add(il);
                    }
                }

            });
            classNode.methods.addAll(betterMethods);
        });
    }

//    private InsnList randomPrintln() {
//        InsnList instructions = new InsnList();
//        MethodInsnNode instruction02 = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
//        instructions.add(instruction02);
//        return instructions;
//    }
}
