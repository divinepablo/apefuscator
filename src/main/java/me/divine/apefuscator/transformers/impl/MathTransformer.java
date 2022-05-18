package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.Transformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;

public class MathTransformer extends Transformer {

    private Logger LOGGER = LogManager.getLogger(MathTransformer.class);

    public MathTransformer() {
        super("Operations", "Converts some math functions to their equivalent in java and converts some operations to bitwise operations."); // copilot wrote most of this sentence lmao
    }


    @Override
    public void transform(Apefuscator var1) {
        var1.classes().forEach(clazz -> {
            clazz.methods.forEach(method -> {
                method.instructions.forEach(instruction -> {
                    if (instruction.getOpcode() == Opcodes.IMUL) {
                        InsnNode node = new InsnNode(Opcodes.ISHL);
                        AbstractInsnNode prevNode = instruction.getPrevious();
                        if (prevNode instanceof IntInsnNode) {
                            if (((IntInsnNode) prevNode).operand % 2 == 0) {
                                ((IntInsnNode) prevNode).operand = ((IntInsnNode) prevNode).operand / 4;
                                method.instructions.insert(prevNode, node);
                                method.instructions.remove(instruction);
                            }
                        }
                    }else if (instruction.getOpcode() == Opcodes.IDIV) {
                        InsnNode node = new InsnNode(Opcodes.ISHR);
                        AbstractInsnNode prevNode = instruction.getPrevious();
                        if (prevNode instanceof IntInsnNode) {
                            if (((IntInsnNode) prevNode).operand % 2 == 0) {
                                ((IntInsnNode) prevNode).operand =  toBitThing(((IntInsnNode) prevNode).operand);
                                method.instructions.insert(prevNode, node);
                                method.instructions.remove(instruction);
                            }
                        }
                    }
                });
            });
        });
    }

    private int toBitThing(int i) {
        int asd = 0;
        for (int j = 1; j < i + 1; j++) {
            if (j % 2 == 0) {
                asd++;
            }
        }
        return asd;
    }
}
