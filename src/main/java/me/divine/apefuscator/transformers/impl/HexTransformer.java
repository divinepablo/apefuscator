package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.Transformer;
import me.divine.apefuscator.utils.ASMUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.lang.reflect.Method;

public class HexTransformer extends Transformer {
    public HexTransformer() {
        super("Hex", "Converts all numbers to hexadecimal");
    }

    Logger LOGGER = LogManager.getLogger(HexTransformer.class);

    @Override
    public void transform(Apefuscator obfuscator) {
        obfuscator.classes().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                try {

                    Method m = Integer.class.getMethod("parseInt", String.class);
                    methodNode.instructions.forEach(instruction -> {
                        try {

                            if (ASMUtils.isInteger(instruction)) {
                                if (instruction instanceof LdcInsnNode) {
                                    LdcInsnNode integer = (LdcInsnNode) instruction;
                                    integer.cst = getHex((Integer) integer.cst);
                                    MethodInsnNode methodInsnNode = new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "parseInt", Type.getMethodDescriptor(m));
                                    methodNode.instructions.insert(instruction, methodInsnNode);
                                } else if (instruction instanceof IntInsnNode) {
                                    IntInsnNode integer = (IntInsnNode) instruction;
                                    integer.operand = 0x12;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private String getHex(int i) {
        return Integer.toHexString(i);
    }

}
