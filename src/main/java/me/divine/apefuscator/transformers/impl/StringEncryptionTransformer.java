package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.Transformer;
import me.divine.apefuscator.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class StringEncryptionTransformer extends Transformer {





    public StringEncryptionTransformer() {
        super("String Encryption", "Encrypts strings with a key");
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        try {

            obfuscator.getClasses().forEach(classNode -> {
                LabelNode node = new LabelNode();
                LabelNode node1 = new LabelNode();
                MethodNode method = new MethodNode(Opcodes.ACC_STATIC + Opcodes.ACC_PUBLIC, "monkey", ASMUtils.makeDescriptor(Type.getType(String.class), Type.getType(String.class)), null, null);
                try {
                    makeDecryptFunction(node, node1, method);
                } catch (Exception e) {
                    return;
                }
                classNode.methods.add(method);
                classNode.methods.forEach(methodNode -> {
                    methodNode.instructions.forEach(instruction -> {
                        if (instruction.getOpcode() == Opcodes.LDC) {
                            LdcInsnNode ldc = (LdcInsnNode) instruction;
                            if (ASMUtils.isString(ldc)) {
                                String str = ASMUtils.getString(ldc);
                                try {
//                                    str = encrypt("AES/CBC/PKCS5Padding", str, key, ivParameterSpec);
                                    str = encrypt(str);
                                    ldc.cst = str;
                                    obfuscator.getLogger().info("before: {} after: {}", str, decrypt(str));
                                    MethodInsnNode methodCall = new MethodInsnNode(Opcodes.INVOKESTATIC, classNode.name, "monkey", ASMUtils.makeDescriptor(Type.getType(String.class), Type.getType(String.class)));
                                    methodNode.instructions.insert(instruction, methodCall);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeDecryptFunction(LabelNode node, LabelNode node1, MethodNode method) throws NoSuchMethodException {
        InsnList instructions = new InsnList();
        instructions.insert(new VarInsnNode(Opcodes.ALOAD, 0));
//        instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/nio/charset/StandardCharsets", "UTF_8", "Ljava/nio/charset/Charset;"));
        instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "toCharArray", Type.getMethodDescriptor(String.class.getMethod("toCharArray"))));
        instructions.add(new VarInsnNode(Opcodes.ASTORE, 2));
        instructions.add(new IntInsnNode(Opcodes.ICONST_0, 0));
        instructions.add(new VarInsnNode(Opcodes.ISTORE, 3));

        instructions.add(node);
//                ILOAD i
        instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
//                ALOAD byArray
        instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
//                ARRAYLENGTH
        instructions.add(new InsnNode(Opcodes.ARRAYLENGTH));
//                IF_ICMPGE E
        instructions.add(new JumpInsnNode(Opcodes.IF_ICMPGE, node1));

//                ALOAD byArray
        instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
//                ILOAD i
        instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
//                ALOAD byArray
        instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
//                ILOAD i
        instructions.add(new VarInsnNode(Opcodes.ILOAD, 3));
//                BALOAD
        instructions.add(new InsnNode(Opcodes.CALOAD));
//                BIPUSH 16
        instructions.add(new IntInsnNode(Opcodes.BIPUSH, 8));
//                ISUB
        instructions.add(new InsnNode(Opcodes.ISHR));
//                I2B
        instructions.add(new InsnNode(Opcodes.I2B));
//                BASTORE
        instructions.add(new InsnNode(Opcodes.CASTORE));


        instructions.add(new IincInsnNode(3, 1));
        instructions.add(new JumpInsnNode(Opcodes.GOTO, node));
        instructions.add(node1);
        instructions.add(new TypeInsnNode(Opcodes.NEW, "java/lang/String"));
        instructions.add(new InsnNode(Opcodes.DUP));
        instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
        instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/String", "<init>", "([B)V"));
        instructions.add(new InsnNode(Opcodes.ARETURN));
        method.instructions.add(instructions);
    }


    public String encrypt(String input) {
//        Cipher cipher = Cipher.getInstance(algorithm);
//        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
//        byte[] cipherText = cipher.doFinal(input.getBytes());
//        return Base64.getEncoder().encodeToString(cipherText);
        char[] a = input.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] << 8);
        }
        return new String(a);
    }

    public String decrypt(String input) {

        char[] a = input.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] >>> 8);
        }
        return new String(a);
    }
}
