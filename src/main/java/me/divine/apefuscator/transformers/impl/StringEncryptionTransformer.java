package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.Transformer;
import me.divine.apefuscator.utils.ASMUtils;
import me.divine.apefuscator.utils.ClassUtil;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.concurrent.atomic.AtomicReference;

public class StringEncryptionTransformer extends Transformer implements Opcodes {



    public StringEncryptionTransformer() {
        super("String Encryption", "Encrypts strings with a key");
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        try {
            AtomicReference<ClassNode> stringEncrypt = new AtomicReference<>();
            obfuscator.getClasses().forEach(classNode -> {
                if (stringEncrypt.get() == null) {
                    ClassNode classNode1 = ClassUtil.copy(classNode);
                    stringEncrypt.set(classNode1);
                    classNode1.access =  Opcodes.ACC_PUBLIC;
                    classNode1.name = "lol/smd/ShaEKFather";
                    classNode1.sourceFile = "ShaEKFather.java";
                    classNode1.superName = null;
                    classNode1.outerClass = null;
                    classNode1.innerClasses.clear();
                    classNode1.fields.clear();
                    classNode1.methods.clear();
                    obfuscator.getClasses2().put("lol/smd/ShaEKFather", classNode1);
                    MethodNode method = new MethodNode(Opcodes.ACC_STATIC + Opcodes.ACC_PUBLIC, "monkey", ASMUtils.makeDescriptor(Type.getType(String.class), Type.getType(String.class)), null, null);
                    classNode1.methods.add(method);
                    makeDecryptFunction(method);
//                    classNode.methods.add(method);
                }



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
//                                    obfuscator.getLogger().info("before: {} after: {}", str, decrypt(str));
                                    MethodInsnNode methodCall = new MethodInsnNode(Opcodes.INVOKESTATIC, stringEncrypt.get().name, "monkey", ASMUtils.makeDescriptor(Type.getType(String.class), Type.getType(String.class)));
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

    private void makeDecryptFunction(MethodNode methodVisitor)  {
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C", false);
        methodVisitor.visitVarInsn(ASTORE, 2);
        methodVisitor.visitInsn(ICONST_0);
        methodVisitor.visitVarInsn(ISTORE, 3);
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        methodVisitor.visitVarInsn(ILOAD, 3);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitInsn(ARRAYLENGTH);
        Label label1 = new Label();
        methodVisitor.visitJumpInsn(IF_ICMPGE, label1);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitVarInsn(ILOAD, 3);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitVarInsn(ILOAD, 3);
        methodVisitor.visitInsn(CALOAD);
        methodVisitor.visitIntInsn(BIPUSH, 8);
        methodVisitor.visitInsn(IUSHR);
        methodVisitor.visitInsn(I2C);
        methodVisitor.visitInsn(CASTORE);
        methodVisitor.visitIincInsn(3, 1);
        methodVisitor.visitJumpInsn(GOTO, label0);
        methodVisitor.visitLabel(label1);
        methodVisitor.visitTypeInsn(NEW, "java/lang/String");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitVarInsn(ALOAD, 2);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([C)V", false);
        methodVisitor.visitInsn(ARETURN);
        methodVisitor.visitMaxs(4, 4);
        methodVisitor.visitEnd();
    }


    public String encrypt(String input) {
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
