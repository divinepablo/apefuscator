package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.Transformer;
import me.divine.apefuscator.utils.ASMUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

public class StringEncryptionTransformer extends Transformer {

    private String pass;

    public StringEncryptionTransformer() {
        this("monkeyapeballs");
    }

    public StringEncryptionTransformer(String pass) {
        super("String Encryption", "Encrypts strings with a key");
        this.pass = pass;
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        try {
            SecretKey key = getKeyFromPassword(pass, "asdfNiggaBallz");
            IvParameterSpec ivParameterSpec = generateIv();

            obfuscator.getClasses().forEach(classNode -> {
                classNode.methods.forEach(methodNode -> {
                    methodNode.instructions.forEach(instruction -> {
                        if (instruction.getOpcode() == Opcodes.LDC) {
                            LdcInsnNode ldc = (LdcInsnNode) instruction;
                            if (ASMUtils.isString(ldc)) {
                                String str = ASMUtils.getString(ldc);
                                try {
                                    str = encrypt("AES/CBC/PKCS5Padding", str, key, ivParameterSpec);
                                    ldc.cst = str;
                                } catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                });
                MethodNode methodNode = new MethodNode(Opcodes.ACC_PRIVATE, "decrypt", "(Ljava/lang/String; 0, Ljavax/crypto/SecretKey; 1, Ljavax/crypto/spec/IvParameterSpec; 2)Ljava/lang/String;", null, null);
                InsnList decryptInstructions = new InsnList();

                LabelNode a = new LabelNode();
                LabelNode b = new LabelNode();
                LocalVariableNode local = new LocalVariableNode("cipher", "Ljavax/crypto/Cipher;", null, a, b, 4);
                methodNode.localVariables.add(local);
                decryptInstructions.insert(a);
//                decryptInstructions.add(new VarInsnNode(Opcodes.ASTORE, 1));
//                decryptInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "javax/crypto/Cipher", "init", "(ILjava/security/Key;Ljava/security/spec/AlgorithmParameterSpec;)V"));
//                methodNode.instructions.insert(decryptInstructions);
//                methodNode.visitLabel(b.getLabel());
                VarInsnNode node1 = new VarInsnNode(Opcodes.ALOAD, 1);
                InsnNode node2 = new InsnNode(Opcodes.ARETURN);
                decryptInstructions.insert(a, node1);
                decryptInstructions.insert(node1, b);
                decryptInstructions.insert(b, node2);
                methodNode.instructions.add(decryptInstructions);
                classNode.methods.add(methodNode);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SecretKey getKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
        return secret;
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public static String encrypt(String algorithm, String input, SecretKey key, IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String algorithm, String cipherText, SecretKey key, IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(plainText);
    }
}
