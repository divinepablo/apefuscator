package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.utils.ASMUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;

public class WrapperMethodTransformer extends me.divine.apefuscator.transformers.Transformer {

    Logger LOGGER = LogManager.getLogger(WrapperMethodTransformer.class);

    public WrapperMethodTransformer() {
        super("WrapperMethod", "Generates methods with same instructions");
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        // TODO Auto-generated method stub
        obfuscator.getClasses().forEach(classNode -> {
//            classNode.fields.forEach(fieldNode -> {
//                if (fieldNode.desc.equals("Ljava/lang/String;")) {
////                    fieldNode.desc = "Ljava/lang/Object;";
//                }
//            });
            ArrayList<MethodNode> betterMethods = new ArrayList<>();
            classNode.methods.forEach(methodNode -> {
//                int rng = ThreadLocalRandom.current().nextInt(1, 3);
                int rng = 2;
                if (rng == 2) {

                    if (!methodNode.name.equals("<init>") && !methodNode.name.equals("<clinit>") && !methodNode.name.equals("<cinit>")) {
                        LOGGER.info("Making wrapper method for {}", methodNode.name);
                        MethodNode normalMethod = ASMUtils.copyMethod(methodNode);
                        normalMethod.name += "xd";
                        betterMethods.add(normalMethod);
                        methodNode.instructions.clear();
                        InsnList il = new InsnList();
//                        LOGGER.info("Method Descriptor: {}", methodNode.desc);


                        int i = 1;
                        if (!ASMUtils.isStatic(methodNode.access)) {
                            il.insert(new VarInsnNode(Opcodes.ALOAD, 0));

                        }
                        for (Type argumentType : ASMUtils.getArgumentTypes(methodNode.desc)) {
                            int opcode = Opcodes.ALOAD;
                            switch (argumentType.getSort()) {
                                case Type.BOOLEAN:
                                case Type.BYTE:
                                case Type.CHAR:
                                case Type.INT:
                                case Type.SHORT:
                                    opcode = Opcodes.ILOAD;
                                    break;
                                case Type.DOUBLE:
                                    opcode = Opcodes.DLOAD;
                                    break;
                                case Type.FLOAT:
                                    opcode = Opcodes.FLOAD;
                                    break;
                                case Type.LONG:
                                    opcode = Opcodes.LLOAD;
                                    break;
                            }
                            il.add(new VarInsnNode(opcode, i));

                            i++;
                        }

                        if (ASMUtils.isStatic(methodNode.access)) {
                            il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, classNode.name, normalMethod.name, normalMethod.desc));
                        } else {

                            il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, classNode.name, normalMethod.name, normalMethod.desc));
                        }

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
                            case Type.VOID:
                                il.add(new InsnNode(Opcodes.RETURN));
                                break;
                            default:
                                il.add(new InsnNode(Opcodes.ARETURN));
                                break;
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
